package playground.pieter.singapore.hits;
//TODO: make persons and trips calculate everything when they are instantiated, remove all transients
import java.io.Serializable;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.management.timer.Timer;

public class HITSPerson implements Serializable{
	static HashMap<String, Integer> actSwitcher;
	static {
		HITSPerson.actSwitcher = new HashMap<String, Integer>();
		actSwitcher.put("home", 1);
		actSwitcher.put("work", 2);
		actSwitcher.put("edu",3);
		
		actSwitcher.put("biz", 4);
		actSwitcher.put("errand", 4);
		actSwitcher.put("medi", 4);
		
		actSwitcher.put("shop", 5);
		actSwitcher.put("eat", 5);
		actSwitcher.put("social", 5);
		actSwitcher.put("relig", 5);
		actSwitcher.put("sport", 5);
		actSwitcher.put("fun", 5);
		actSwitcher.put("rec", 5);
		
		actSwitcher.put("pikUpDrop", 0);
		actSwitcher.put("accomp", 0);
		actSwitcher.put("xfer", 0);
		actSwitcher.put("homeOther", 0);
	}
	HITSHousehold household;
	String h1_hhid;
	int pax_id;
	String pax_idx;
	String p1_age;
	String p2_gender;
	String p3c_nolic;
	String p3_car_lic;
	String p3a_moto_lic;
	String p3b_vanbus_lic;
	String p4_mobility;
	String p4a_aids;
	String p4b_aidsoth;
	String p5a_edu;
	String p5_econactivity;
	String p5i_econactoth;
	String p6_occup;
	String p6i_occupoth;
	String p6a_fixedwkpl;
	int p6c_fwkplpcode;
	String p7_workhrs;
	String p8_income;
	String p9_day;
	Date p9_date;
	String p10_maketrip;
	String p11_notripreason;
	String p11a_notripreasoth;
	String p12_lasttravelday;
	String p13_1sttriporig_home;
	int p13b_1storigpcode;
	int p14_1sttripstarttime;
	String p29_futrptuse;
	double p30_spendpt;
	double p31_spendschshtlbus;
	int p32_ptreimb;

	private ArrayList<HITSTrip> trips;
	private ArrayList<HITSStage> stages;
	
	
	public HITSPerson(){
		
	}
	public HITSPerson(ResultSet prs, Connection conn, DateFormat dfm, HITSHousehold hh) {

		try {
			this.household = hh;
			this.h1_hhid = prs.getString("h1_hhid");
			this.pax_id = prs.getInt("pax_id");
			this.pax_idx = h1_hhid + "_" + pax_id;
			this.p1_age = prs.getString("p1_age");
			this.p2_gender = prs.getString("p2_gender");
			this.p3c_nolic = prs.getString("p3c_nolic");
			this.p3_car_lic = prs.getString("p3_car_lic");
			this.p3a_moto_lic = prs.getString("p3a_moto_lic");
			this.p3b_vanbus_lic = prs.getString("p3b_vanbus_lic");
			this.p4_mobility = prs.getString("p4_mobility");
			this.p4a_aids = prs.getString("p4a_aids");
			this.p4b_aidsoth = prs.getString("p4b_aidsoth");
			this.p5a_edu = prs.getString("p5a_edu");
			this.p5_econactivity = prs.getString("p5_econactivity");
			this.p5i_econactoth = prs.getString("p5i_econactoth");
			this.p6_occup = prs.getString("p6_occup");
			this.p6i_occupoth = prs.getString("p6i_occupoth");
			this.p6a_fixedwkpl = prs.getString("p6a_fixedwkpl");
			this.p6c_fwkplpcode = prs.getInt("p6c_fwkplpcode");
			this.p7_workhrs = prs.getString("p7_workhrs");
			this.p8_income = prs.getString("p8_income");
			this.p9_day = prs.getString("p9_day");
			this.p9_date = (Date) prs.getObject("p9_date");
			this.p10_maketrip = prs.getString("p10_maketrip");
			this.p11_notripreason = prs.getString("p11_notripreason");
			this.p11a_notripreasoth = prs.getString("p11a_notripreasoth");
			this.p12_lasttravelday = prs.getString("p12_lasttravelday");
			this.p13_1sttriporig_home = prs.getString("p13_1sttriporig_home");
			this.p13b_1storigpcode = prs.getInt("p13b_1storigpcode");
			this.p14_1sttripstarttime = prs.getInt("p14_1sttripstarttime");
			this.p29_futrptuse = prs.getString("p29_futrptuse");
			this.p30_spendpt = prs.getDouble("p30_spendpt");
			this.p31_spendschshtlbus = prs.getDouble("p31_spendschshtlbus");
			this.p32_ptreimb = prs.getInt("p32_ptreimb");

			setTrips(conn,dfm);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		serializeTimes();
		getStagesFromTrips();
		calcPersonInfo();
	}


	public void setTrips(Connection conn, DateFormat dfm) {
		ArrayList<HITSTrip> t = new ArrayList<HITSTrip>();
		Statement ts;
		try {
			ts = conn.createStatement();
			ts.executeQuery("select distinct " +
					"h1_hhid,"+
					"pax_id,"+
					"trip_id,"+
					"p13d_origpcode,"+
					"t2_destpcode,"+
					"t3_starttime_24h,"+
					"t4_endtime_24h,"+
					"t5_placetype,"+
					"t6_purpose,"+
					"t22_lastwlktime,"+
					"t23_tripfreq,"+
					"t24_compjtime,"+
					"t25_estjtime,"+
					"t27_flextimetyp,"+
					"p28a_fastrbypt,"+
					"p28b_cheaprbypt,"+
					"p28c_easrtoacc,"+
					"p28d_needntpark,"+
					"p28e_lessstress,"+
					"p28f_othrmmbrusecar,"+
					"p28g_opconly,"+
					"p28h_envfrndly,"+
					"p28i_othrreasnchck, "+
					"tripfactorsstgfinal "+
					" from hits.hitsshort where " +
					"h1_hhid = '"+ this.h1_hhid +
					"' and pax_id = " + this.pax_id + " and trip_id is not null;");
			ResultSet trs = ts.getResultSet();
			int currTripId;
			int prevTripId = 0 ;
			while (trs.next()) {
//				this section eliminates getting duplicate trip ids for a few cases where subsequent stages of a trip had different 
//				origin or destination postal codes than the first stage of the trip
				currTripId = trs.getInt("trip_id");
				if (currTripId != prevTripId){
					t.add(new HITSTrip(trs, conn,dfm,this));		
				}
				prevTripId = currTripId;
			}
			trs.close();
			ts.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		t.trimToSize();
		this.trips = t;
	}

	private void getStagesFromTrips() {
		this.stages = new ArrayList<HITSStage>();
		for(HITSTrip trip:this.trips){
			this.stages.addAll(trip.getStages());
		}
		this.stages.trimToSize();
	}
	
	public ArrayList<HITSStage> getStages(){
		return this.stages;
	}
	
	public ArrayList<HITSTrip> getTrips() {
		return trips;
	}

	public void serializeTimes(){
		for(int i=0; i<this.trips.size(); i++){
			if (i > 0){
				//				if the current trip's start time is before that of the previous trip, 
				//				increase the start time of all subsequent trips by one day
				if (this.trips.get(i).t3_starttime_24h.before(this.trips.get(i-1).t3_starttime_24h)){
					for(HITSTrip subtrip:this.trips.subList(i, this.trips.size())){
						subtrip.t3_starttime_24h.setTime(subtrip.t3_starttime_24h.getTime() + Timer.ONE_DAY);
						subtrip.t4_endtime_24h.setTime(subtrip.t4_endtime_24h.getTime() + Timer.ONE_DAY);
					}
				}
			}
		}
	}
	
	
	//  FIELDS
	 int numberOfTrips;
//	 ArrayList<Integer> regionChainPax; //12 - 32 - 12
//	 ArrayList<String> actChainPax; //home - work - home
//	 ArrayList<String> placeTypeChainPax; //hdb1 - office - hdb1
//	 ArrayList<Integer> durationChainPax; //6*3600 - 8*3600 - 6*3600
//	 ArrayList<String> mainModeChain; //publBus - mrt
	
	 String actMainModeChainPax; //e.g. home - bus - work - walk - shop - mrt - home
	 String actStageChainPax; //e.g. home - walk - BUS - walk - work - walk - shop - walk - mrt - walk - home
	 String actChainPax;
	
	 int numberOfWalkStagesPax;
	 double totalWalkTimePax;
	double scheduleFactor;
	
	 static public final String FORMATSTRING = "%s, %s, %d, %d, %f, %s, %s, %s \n"; 
	 static public final String HEADERSTRING = "h1_hhid, pax_idx, numberOfTrips, numberOfWalkStagesPax, totalWalkTimePax, actMainModeChainPax, actStageChainPax, actChainPax \n";
	
	
	private void calcPersonInfo() {
		try {
			int n = 0; //number of trips
			int nws = 0; //number of walk stages
			double twt = 0; //total walking time
			double tfs = 0; //trip factor sum, average this quantity to get the total schedule inflation factor
			
			boolean tripRepeater;
			
			String aMMC = ""; //e.g. home - bus - work - walk - shop - mrt - home
			String aSCM = ""; // motorised stage chain
			String aC = ""; // activity chain
			
//			ArrayList<Integer> rC = new ArrayList<Integer>();//12 - 32 - 12 (region2 chain from postal codes)
//			ArrayList<String> aC = new ArrayList<String>();//home - work - home (the activity chain for this person)
//			ArrayList<String> pTC = new ArrayList<String>();//hdb1 - office - hdb1 (the placeTypeChain for this person)
			
			for (HITSTrip t : this.trips) {
				if(t.mainmode == null) t.calcTransients(); //forces the trip object to calculate extra fields
				if(n>0 && t.trip_id == this.trips.get(n-1).trip_id) {
					tripRepeater = true; //check if this trip repeats, if anything prints, there's an error
					System.out.println(t.h1_hhid + " " + t.pax_id); 
				}
				n += 1;
				nws += t.numberOfWalkStagesTrip;
				twt += t.totalWalkTimeTrip;
				tfs += t.tripfactorsstgfinal;
				
				String purpose = "";
				int p = HITSPerson.actSwitcher.get(t.t6_purpose);
				switch(p){
					case 0:	purpose = ""; break;
					case 1:	purpose = "-h"; break;
					case 2:	purpose = "-w"; break;
					case 3:	purpose = "-s"; break;
					case 4:	purpose = "-b"; break;
					case 5:	purpose = "-l"; break;
				}
				
				if (n == 1) {
					aMMC += this.p13_1sttriporig_home + "_" + t.mainmode + "_"
							+ t.t6_purpose;
					aSCM += this.p13_1sttriporig_home + "_"
							+ t.stageChainSimple + "_" + t.t6_purpose;
					aC += this.p13_1sttriporig_home.substring(0, 1) +  purpose;
				} else {
					aMMC += "_" + t.mainmode + "_" + t.t6_purpose;
					aSCM += "_" + t.stageChainSimple + "_" + t.t6_purpose;
					aC +=  purpose;
				}
				
				
			}
			this.numberOfTrips = n;
			this.numberOfWalkStagesPax = nws;
			this.totalWalkTimePax = twt;
			this.actMainModeChainPax = aMMC;
			this.actStageChainPax = aSCM;
			this.actChainPax = aC;
			if(aC.equals("h--w-h")){
				System.out.println("h--w-h");
			}
			this.scheduleFactor = n>0 ? tfs/n : 0; //only calculate if this person made any trips
			
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public void calcStageInfo(){
		int n = 0;
		double inflStageCount = 0; //the number of motorized stages times the trip inflation factor for the trip
		double inflCarDrvStageCount = 0; //the number of stages where person is in car or other vehicle as driver
		double inflCarPsgrStageCount = 0; //the number of stages where person is in car or other vehicle as passenger
		double inflTransitStageCount = 0; //the number of stages where person is in transit vehicle
		double inflInVehTimeStage = 0; //the in-vehicle time for each stage times the trip inflation factor
		
		double inflTripCount = 0; //the sum of the trip inflation factor for this person's trips
		double inflCarDrvTripCount = 0; //the number of Trips where person is in car or other vehicle as driver (main mode)
		double inflCarPsgrTripCount = 0; //the number of Trips where person is in car or other vehicle as passenger (main mode)
		double inflTransitTripCount = 0; //the number of Trips where person is in transit vehicle (main mode)
		double inflInVehTimeTrip = 0; //the in-vehicle time across all modes for this trip times the trip inflation factor
		
		for(HITSTrip t:trips){
			ArrayList<HITSStage> stages = t.getStages();
			for(HITSStage s:stages){
				inflStageCount += s.t10_mode.equals("walk") || s.t10_mode.equals("cycle")? 
					0 : t.tripfactorsstgfinal ;
				inflCarDrvStageCount += s.t10_mode.equals("carDrv")
						|| s.t10_mode.equals("lhdvDrv")
						|| s.t10_mode.equals("motoDrv")
						? t.tripfactorsstgfinal : 0;
				inflCarPsgrStageCount += s.t10_mode.equals("carPsgr")
				|| s.t10_mode.equals("compBus")
				|| s.t10_mode.equals("lhdvPsgr")
				|| s.t10_mode.equals("motoPsgr")
				|| s.t10_mode.equals("schBus")
				|| s.t10_mode.equals("shutBus")
				|| s.t10_mode.equals("taxi")
				? t.tripfactorsstgfinal : 0;
				
			}
		}
	}
	
	public String toString(){
		if(this.numberOfTrips == 0 && !this.getTrips().equals(null)) calcPersonInfo();
	     return String.format(FORMATSTRING,
	    		 h1_hhid, pax_idx, 
	    		 numberOfTrips, numberOfWalkStagesPax, 
	    		 totalWalkTimePax, actMainModeChainPax, 
	    		 actStageChainPax, actChainPax );
	}
}
