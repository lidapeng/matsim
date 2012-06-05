/* *********************************************************************** *
 * project: org.matsim.*
 * JointChooseModeForSubtourStrategy.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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
package playground.thibautd.jointtrips.replanning.strategies;

import org.matsim.core.controler.Controler;

import playground.thibautd.jointtrips.replanning.JointPlanStrategy;
import playground.thibautd.jointtrips.replanning.modules.ExecuteModuleOnAllPlansModule;
import playground.thibautd.jointtrips.replanning.modules.jointchoosemodeforsubtour.JointChooseModeForSubtourModule;
import playground.thibautd.jointtrips.replanning.modules.reroute.JointReRouteModule;
import playground.thibautd.jointtrips.replanning.selectors.RandomPlanSelectorWithoutCasts;
import playground.thibautd.router.replanning.TripsToLegModule;

/**
 * @author thibautd
 */
public class JointChooseModeForSubtourStrategy extends JointPlanStrategy {

	public JointChooseModeForSubtourStrategy(final Controler controler) {
		super( new RandomPlanSelectorWithoutCasts() );
		addStrategyModule(
				new ExecuteModuleOnAllPlansModule(
					controler.getConfig(),
					new TripsToLegModule( controler ) ) );
		addStrategyModule(
				new JointChooseModeForSubtourModule(
					controler.getConfig() ) );
		addStrategyModule(
				new JointReRouteModule(
					controler ) );
	}
}
