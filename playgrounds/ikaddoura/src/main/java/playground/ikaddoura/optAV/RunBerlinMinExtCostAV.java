/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package playground.ikaddoura.optAV;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.av.robotaxi.scoring.TaxiFareConfigGroup;
import org.matsim.contrib.av.robotaxi.scoring.TaxiFareHandler;
import org.matsim.contrib.dvrp.data.FleetImpl;
import org.matsim.contrib.dvrp.data.file.VehicleReader;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.noise.NoiseCalculationOnline;
import org.matsim.contrib.noise.NoiseConfigGroup;
import org.matsim.contrib.noise.data.NoiseContext;
import org.matsim.contrib.noise.utils.MergeNoiseCSVFile;
import org.matsim.contrib.noise.utils.ProcessNoiseImmissions;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.contrib.taxi.optimizer.DefaultTaxiOptimizerProvider;
import org.matsim.contrib.taxi.run.TaxiConfigConsistencyChecker;
import org.matsim.contrib.taxi.run.TaxiConfigGroup;
import org.matsim.contrib.taxi.run.TaxiOptimizerModules;
import org.matsim.contrib.taxi.run.TaxiOutputModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

import playground.ikaddoura.agentSpecificActivityScheduling.AgentSpecificActivityScheduling;
import playground.ikaddoura.analysis.detailedPersonTripAnalysis.PersonTripAnalysisModule;
import playground.ikaddoura.decongestion.DecongestionConfigGroup;
import playground.ikaddoura.decongestion.DecongestionControlerListener;
import playground.ikaddoura.decongestion.data.DecongestionInfo;
import playground.ikaddoura.decongestion.handler.IntervalBasedTolling;
import playground.ikaddoura.decongestion.handler.IntervalBasedTollingAll;
import playground.ikaddoura.decongestion.handler.PersonVehicleTracker;
import playground.ikaddoura.decongestion.tollSetting.DecongestionTollSetting;
import playground.ikaddoura.decongestion.tollSetting.DecongestionTollingPID;
import playground.ikaddoura.moneyTravelDisutility.MoneyEventAnalysis;
import playground.ikaddoura.moneyTravelDisutility.MoneyTimeDistanceTravelDisutilityFactory;

/**
* @author ikaddoura
*/

public class RunBerlinMinExtCostAV {

	private static final Logger log = Logger.getLogger(RunBerlinMinExtCostAV.class);

	private static String configFile;
	private static String outputDirectory;	
	private static double kP;	
	private static boolean internalizeNoise;
	private static boolean agentBasedActivityScheduling;

	private static boolean otfvis;
	
	public static void main(String[] args) {
		if (args.length > 0) {
			
			configFile = args[0];		
			log.info("configFile: "+ configFile);
			
			outputDirectory = args[1];
			log.info("outputDirectory: "+ outputDirectory);
			
			kP = Double.parseDouble(args[2]);
			log.info("kP: "+ kP);
			
			internalizeNoise = Boolean.parseBoolean(args[3]);
			log.info("internalizeNoise: "+ internalizeNoise);
			
			agentBasedActivityScheduling = Boolean.parseBoolean(args[4]);
			log.info("agentBasedActivityScheduling: "+ agentBasedActivityScheduling);
			
			otfvis = false;
			
		} else {
			configFile = "/Users/ihab/Documents/workspace/runs-svn/optAV/input/config_be_10pct.xml";
			outputDirectory = "/Users/ihab/Documents/workspace/runs-svn/optAV/output/minExtCostAV/";
			kP = 2 * 12./3600.;
			internalizeNoise = false;
			agentBasedActivityScheduling = false;
			otfvis = false;
		}
		
		RunBerlinMinExtCostAV runBerlinOptAV = new RunBerlinMinExtCostAV();
		runBerlinOptAV.run();
	}

	private void run() {
		
		Config config = ConfigUtils.loadConfig(configFile,
				new TaxiConfigGroup(),
				new DvrpConfigGroup(),
				new TaxiFareConfigGroup(),
				new OTFVisConfigGroup(),
				new NoiseConfigGroup());
		
		config.controler().setOutputDirectory(outputDirectory);
		
		DvrpConfigGroup.get(config).setMode(TaxiOptimizerModules.TAXI_MODE);
		
		TaxiConfigGroup taxiCfg = TaxiConfigGroup.get(config);
		config.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
		config.checkConsistency();
		
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);
		
		if (agentBasedActivityScheduling) {
			AgentSpecificActivityScheduling aa = new AgentSpecificActivityScheduling(controler);
			controler = aa.prepareControler(false);
		}
				
		// #############################
		// noise pricing
		// #############################

		if (internalizeNoise) {
			NoiseConfigGroup noiseParams = (NoiseConfigGroup) controler.getConfig().getModules().get(NoiseConfigGroup.GROUP_NAME);
			noiseParams.setInternalizeNoiseDamages(true);
			
			if (agentBasedActivityScheduling) {
				List<String> consideredActivitiesForSpatialFunctionality = new ArrayList<>();
				for (ActivityParams params : controler.getConfig().planCalcScore().getActivityParams()) {
					if (!params.getActivityType().contains("interaction")) {
						consideredActivitiesForSpatialFunctionality.add(params.getActivityType());
					}
				}
				String[] consideredActivitiesForSpatialFunctionalityArray = new String[consideredActivitiesForSpatialFunctionality.size()];
				noiseParams.setConsideredActivitiesForDamageCalculationArray(consideredActivitiesForSpatialFunctionality.toArray(consideredActivitiesForSpatialFunctionalityArray));
			}
			
			log.info(noiseParams.toString());
			controler.addControlerListener(new NoiseCalculationOnline(new NoiseContext(controler.getScenario())));
		}
		
		// #############################
		// congestion pricing
		// #############################

		final DecongestionConfigGroup decongestionSettings = new DecongestionConfigGroup();
		decongestionSettings.setKp(kP);
		decongestionSettings.setKi(0.);
		decongestionSettings.setKd(0.);
		decongestionSettings.setMsa(true);
		decongestionSettings.setRUN_FINAL_ANALYSIS(false);
		decongestionSettings.setWRITE_LINK_INFO_CHARTS(false);
		
		DecongestionInfo info = new DecongestionInfo(decongestionSettings);
		
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				
				this.bind(DecongestionInfo.class).toInstance(info);
				this.bind(DecongestionTollSetting.class).to(DecongestionTollingPID.class);
				
//				this.bind(IntervalBasedTolling.class).to(IntervalBasedTollingAV.class);
//				this.bind(IntervalBasedTollingAV.class).asEagerSingleton();
//				this.addEventHandlerBinding().to(IntervalBasedTollingAV.class);
				
				this.bind(IntervalBasedTolling.class).to(IntervalBasedTollingAll.class);
				this.bind(IntervalBasedTollingAll.class).asEagerSingleton();
				this.addEventHandlerBinding().to(IntervalBasedTollingAll.class);
				
				this.bind(PersonVehicleTracker.class).asEagerSingleton();								
				this.addEventHandlerBinding().to(PersonVehicleTracker.class);
				
				this.addControlerListenerBinding().to(DecongestionControlerListener.class);
			}
		});
		
		// #############################
		// taxi
		// #############################

		FleetImpl fleet = new FleetImpl();
		new VehicleReader(scenario.getNetwork(), fleet).readFile(taxiCfg.getTaxisFileUrl(config.getContext()).getFile());
		
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addEventHandlerBinding().to(TaxiFareHandler.class).asEagerSingleton();
			}
		});
		controler.addOverridingModule(new TaxiOutputModule());
        controler.addOverridingModule(TaxiOptimizerModules.createDefaultModule(fleet));

        // #############################
        // travel disutility
        // #############################

		final MoneyTimeDistanceTravelDisutilityFactory dvrpTravelDisutilityFactory = new MoneyTimeDistanceTravelDisutilityFactory(null);

		controler.addOverridingModule(new AbstractModule(){
			@Override
			public void install() {
												
				addTravelDisutilityFactoryBinding(DefaultTaxiOptimizerProvider.TAXI_OPTIMIZER).toInstance(dvrpTravelDisutilityFactory);
				
//				this.bind(AgentFilter.class).to(AVAgentFilter.class);

				this.bind(MoneyEventAnalysis.class).asEagerSingleton();
				this.addControlerListenerBinding().to(MoneyEventAnalysis.class);
				this.addEventHandlerBinding().to(MoneyEventAnalysis.class);
			}
		}); 

		// #############################
		// welfare analysis
		// #############################
		
		controler.addOverridingModule(new PersonTripAnalysisModule());
		
		// #############################
		// run
		// #############################
		
		if (otfvis) controler.addOverridingModule(new OTFVisLiveModule());	
        controler.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		controler.run();
		
		// #############################
		// post processing
		// #############################
		
		if (internalizeNoise) {
			NoiseConfigGroup noiseParams = (NoiseConfigGroup) controler.getConfig().getModules().get(NoiseConfigGroup.GROUP_NAME);
			
			String immissionsDir = controler.getConfig().controler().getOutputDirectory() + "/ITERS/it." + controler.getConfig().controler().getLastIteration() + "/immissions/";
			String receiverPointsFile = controler.getConfig().controler().getOutputDirectory() + "/receiverPoints/receiverPoints.csv";
			
			ProcessNoiseImmissions processNoiseImmissions = new ProcessNoiseImmissions(immissionsDir, receiverPointsFile, noiseParams.getReceiverPointGap());
			processNoiseImmissions.run();
			
			final String[] labels = { "immission", "consideredAgentUnits" , "damages_receiverPoint" };
			final String[] workingDirectories = { controler.getConfig().controler().getOutputDirectory() + "/ITERS/it." + controler.getConfig().controler().getLastIteration() + "/immissions/" , controler.getConfig().controler().getOutputDirectory() + "/ITERS/it." + controler.getConfig().controler().getLastIteration() + "/consideredAgentUnits/" , controler.getConfig().controler().getOutputDirectory() + "/ITERS/it." + controler.getConfig().controler().getLastIteration()  + "/damages_receiverPoint/" };
	
			MergeNoiseCSVFile merger = new MergeNoiseCSVFile() ;
			merger.setReceiverPointsFile(receiverPointsFile);
			merger.setOutputDirectory(controler.getConfig().controler().getOutputDirectory() + "/ITERS/it." + controler.getConfig().controler().getLastIteration() + "/");
			merger.setTimeBinSize(noiseParams.getTimeBinSizeNoiseComputation());
			merger.setWorkingDirectory(workingDirectories);
			merger.setLabel(labels);
			merger.run();	
		}
	}
}

