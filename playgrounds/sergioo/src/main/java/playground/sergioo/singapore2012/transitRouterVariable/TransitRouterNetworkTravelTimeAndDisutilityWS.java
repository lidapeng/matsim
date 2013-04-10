/* *********************************************************************** *
 * project: org.matsim.*
 * TransitRouterNetworkTravelTimeAndDisutilityVariableWW.java
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

package playground.sergioo.singapore2012.transitRouterVariable;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.trafficmonitoring.TravelTimeCalculatorConfigGroup;
import org.matsim.pt.router.CustomDataManager;
import org.matsim.pt.router.PreparedTransitSchedule;
import org.matsim.pt.router.TransitRouterConfig;
import org.matsim.pt.router.TransitRouterNetwork.TransitRouterNetworkNode;
import org.matsim.pt.router.TransitRouterNetworkTravelTimeAndDisutility;
import org.matsim.vehicles.Vehicle;

import playground.sergioo.singapore2012.transitRouterVariable.TransitRouterNetworkWW.TransitRouterNetworkLink;

/**
 * TravelTime and TravelDisutility calculator to be used with the transit network used for transit routing.
 *
 * @author sergioo
 */
public class TransitRouterNetworkTravelTimeAndDisutilityWS extends TransitRouterNetworkTravelTimeAndDisutility implements TravelDisutility {
	
	private final WaitTime waitTime;
	private final StopStopTime stopStopTime;
	
	public TransitRouterNetworkTravelTimeAndDisutilityWS(final TransitRouterConfig config, Network network, TransitRouterNetworkWW routerNetwork, WaitTime waitTime, StopStopTime stopStopTime, TravelTimeCalculatorConfigGroup tTConfigGroup, QSimConfigGroup qSimConfigGroup, PreparedTransitSchedule preparedTransitSchedule) {
		this(config, network, routerNetwork, waitTime, stopStopTime, tTConfigGroup, qSimConfigGroup.getStartTime(), qSimConfigGroup.getEndTime(), preparedTransitSchedule);
	}
	public TransitRouterNetworkTravelTimeAndDisutilityWS(final TransitRouterConfig config, Network network, TransitRouterNetworkWW routerNetwork, WaitTime waitTime, StopStopTime stopStopTime, TravelTimeCalculatorConfigGroup tTConfigGroup, double startTime, double endTime, PreparedTransitSchedule preparedTransitSchedule) {
		super(config, preparedTransitSchedule);
		this.waitTime = waitTime;
		this.stopStopTime = stopStopTime;
	}
	
	@Override
	public double getLinkTravelTime(final Link link, final double time, Person person, Vehicle vehicle) {
		TransitRouterNetworkLink wrapped = (TransitRouterNetworkLink) link;
		if (wrapped.route != null)
			//in line link
			return stopStopTime.getStopStopTime(((TransitRouterNetworkNode)link.getFromNode()).stop.getStopFacility().getId(), ((TransitRouterNetworkNode)link.getToNode()).stop.getStopFacility().getId(), time);
		else if(wrapped.toNode.route!=null)
			//wait link
			return waitTime.getRouteStopWaitTime(wrapped.toNode.line.getId(), wrapped.toNode.route.getId(), wrapped.fromNode.stop.getStopFacility().getId(), time);
		else if(wrapped.fromNode.route==null)
			//walking link
			return wrapped.getLength()/this.config.getBeelineWalkSpeed();
		else
			//inside link
			return 0;
	}
	@Override
	public double getLinkTravelDisutility(final Link link, final double time, final Person person, final Vehicle vehicle, final CustomDataManager dataManager) {
		TransitRouterNetworkLink wrapped = (TransitRouterNetworkLink) link;
		if (wrapped.route != null)
			return - stopStopTime.getStopStopTime(((TransitRouterNetworkNode)link.getFromNode()).stop.getStopFacility().getId(), ((TransitRouterNetworkNode)link.getToNode()).stop.getStopFacility().getId(), time)*this.config.getMarginalUtilityOfTravelTimePt_utl_s() 
				       - link.getLength() * (this.config.getMarginalUtilityOfTravelDistancePt_utl_m()-2.7726/100000);
		else if (wrapped.toNode.route!=null)
			// it's a wait link
			return - waitTime.getRouteStopWaitTime(wrapped.toNode.line.getId(), wrapped.toNode.route.getId(), wrapped.fromNode.stop.getStopFacility().getId(), time) * this.config.getMarginalUtilityOfWaitingPt_utl_s()
					- this.config.getUtilityOfLineSwitch_utl();
		else if(wrapped.fromNode.route==null)
			// it's a transfer link (walk)
			return - wrapped.getLength()*this.config.getMarginalUtilityOfTravelTimeWalk_utl_s()/this.config.getBeelineWalkSpeed();
		else
			//inside link
			return 0;
	}
	@Override
	public double getLinkTravelDisutility(Link link, double time, Person person, Vehicle vehicle) {
		TransitRouterNetworkLink wrapped = (TransitRouterNetworkLink) link;
		if (wrapped.route != null)
			return - stopStopTime.getStopStopTime(((TransitRouterNetworkNode)link.getFromNode()).stop.getStopFacility().getId(), ((TransitRouterNetworkNode)link.getToNode()).stop.getStopFacility().getId(), time) * this.config.getMarginalUtilityOfTravelTimePt_utl_s() 
				       - link.getLength() * (this.config.getMarginalUtilityOfTravelDistancePt_utl_m()-2.7726/100000);
		else if (wrapped.toNode.route!=null)
			// it's a wait link
			return - waitTime.getRouteStopWaitTime(wrapped.toNode.line.getId(), wrapped.toNode.route.getId(), wrapped.fromNode.stop.getStopFacility().getId(), time) * this.config.getMarginalUtilityOfWaitingPt_utl_s()
					- this.config.getUtilityOfLineSwitch_utl();
		else if(wrapped.fromNode.route==null)
			// it's a transfer link (walk)
			return -wrapped.getLength()/this.config.getBeelineWalkSpeed() * this.config.getMarginalUtilityOfTravelTimeWalk_utl_s();
		else
			//inside link
			return 0;
	}
	@Override
	public double getLinkMinimumTravelDisutility(Link link) {
		return 0;
	}

}
