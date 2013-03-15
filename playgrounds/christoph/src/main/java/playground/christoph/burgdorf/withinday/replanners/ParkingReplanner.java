/* *********************************************************************** *
 * project: org.matsim.*
 * ParkingReplanner.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

package playground.christoph.burgdorf.withinday.replanners;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.mobsim.qsim.InternalInterface;
import org.matsim.core.mobsim.qsim.agents.PlanBasedWithinDayAgent;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.withinday.replanning.replanners.interfaces.WithinDayDuringLegReplanner;

import playground.christoph.burgdorf.ParkingInfrastructure;

/**
 * Take existing route and simply attach the remaining sub-route to the selected parking.
 * 
 * @author Christoph
 */
public class ParkingReplanner extends WithinDayDuringLegReplanner {

	/*package*/ ParkingReplanner(Id id, Scenario scenario, InternalInterface internalInterface) {
		super(id, scenario, internalInterface);
	}

	@Override
	public boolean doReplanning(PlanBasedWithinDayAgent withinDayAgent) {

		// If we don't have a valid Replanner.
		if (this.routeAlgo == null) return false;

		// If we don't have a valid PersonAgent
		if (withinDayAgent == null) return false;

		Plan executedPlan = withinDayAgent.getSelectedPlan();

		// If we don't have an executed plan
		if (executedPlan == null) return false;

		int currentLegIndex = withinDayAgent.getCurrentPlanElementIndex();

		Id parkingId = ParkingInfrastructure.selectedParkings.get(withinDayAgent.getId());
		List<Id> parkingSubRoute = ParkingInfrastructure.toParkingSubRoutes.get(parkingId);
		
		Leg currentLeg = (Leg) executedPlan.getPlanElements().get(currentLegIndex);
		NetworkRoute route = (NetworkRoute) currentLeg.getRoute(); 
		
		List<Id> routeLinkIds = new ArrayList<Id>();
		routeLinkIds.add(route.getStartLinkId());
		routeLinkIds.addAll(route.getLinkIds());
		routeLinkIds.add(route.getEndLinkId());
		routeLinkIds.addAll(parkingSubRoute);
		
		// update route
		route.setLinkIds(routeLinkIds.get(0), routeLinkIds.subList(1, routeLinkIds.size() - 1), routeLinkIds.get(routeLinkIds.size() - 1));

		// update location of next activity
		Activity nextActivity = (Activity) executedPlan.getPlanElements().get(currentLegIndex + 1);
		((ActivityImpl) nextActivity).setLinkId(route.getEndLinkId());
		
		// Finally reset the cached Values of the PersonAgent - they may have changed!
		withinDayAgent.resetCaches();

		return true;
	}

}