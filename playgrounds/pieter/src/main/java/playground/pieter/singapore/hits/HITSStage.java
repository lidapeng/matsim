package playground.pieter.singapore.hits;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sun.xml.bind.v2.runtime.RuntimeUtil.ToStringAdapter;


public class HITSStage implements Serializable {
	/**
	 * 
	 */
	HITSTrip trip;
	int msno_main;
	String h1_hhid;
	int pax_id;
	int trip_id;
	int stage_id;
	String t10_mode;
	int t10a_walktime;
	String t10i_modeother;
	String t11_boardsvcstn;
	String t12_alightstn;
	int t12a_paxinveh;
	int t13_waittime;
	int t14_invehtime;
	double t15_taxifare;
	String t16_taxireimb;
	double t17_erpcost;
	String t18_erpreimb;
	double t19_parkfee;
	String t19a_parkftyp;
	String t20_parkreimb;
	int stageDuration;

//	String mainmode;
	
	public HITSStage(ResultSet srs, HITSTrip trip) {
		try {
			this.trip = trip;
			this.msno_main = srs.getInt("msno_main");
			this.h1_hhid = srs.getString("h1_hhid");
			this.pax_id = srs.getInt("pax_id");
			this.trip_id = srs.getInt("trip_id");
			this.stage_id = srs.getInt("stage_id");
			this.t10_mode = srs.getString("t10_mode");
			this.t10a_walktime = srs.getInt("t10a_walktime");
			this.t10i_modeother = srs.getString("t10i_modeother");
			this.t11_boardsvcstn = srs.getString("t11_boardsvcstn");
			this.t12_alightstn = srs.getString("t12_alightstn");
			this.t12a_paxinveh = srs.getInt("t12a_paxinveh");
			this.t13_waittime = srs.getInt("t13_waittime");
			this.t14_invehtime = srs.getInt("t14_invehtime");
			this.t15_taxifare = srs.getInt("t15_taxifare");
			this.t16_taxireimb = srs.getString("t16_taxireimb");
			this.t17_erpcost = srs.getInt("t17_erpcost");
			this.t18_erpreimb = srs.getString("t18_erpreimb");
			this.t19_parkfee = srs.getInt("t19_parkfee");
			this.t19a_parkftyp = srs.getString("t19a_parkftyp");
			this.t20_parkreimb = srs.getString("t20_parkreimb");
			this.stageDuration = t10a_walktime + t13_waittime + t14_invehtime;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HITSStage(){
		
	}
	public String toString(){
	     String format = "|%1$6d|%2$10s|%3$3d|%4$3d|%5$3d|%6$10s|%7$3d|\n";
	     return String.format(format,msno_main, h1_hhid, pax_id, trip_id, stage_id, t10_mode,t14_invehtime );
	}
}
