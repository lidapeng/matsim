package playground.pieter.singapore.hits;

import java.util.Date;

public class HITSEntry {
/*	msno_main					 int
	h1_hhid					 varchar
	h1_pcode					 int
	h2_dwell					 varchar
	h3_ethnic					 varchar
	h3a_other					 varchar
	h4_totpax					 int
	h4b_u4yrs					 varchar
	h4c_eligible					 varchar
	h4d_nereason					 varchar
	h5_vehavail					 varchar
	xqty_carncom					 int
	xqty_carnind					 int
	xqty_carnrent					 int
	xqty_caropcom					 int
	xqty_caropind					 int
	xqty_caroprent					 varchar
	xqty_buscoach					 int
	xqty_hgv					 int
	xqty_lgvgoods					 int
	xqty_lgvpsgr					 int
	xqty_minibus					 int
	xqty_motocom					 int
	xqty_motoind					 int
	xqty_taxi					 int
	h7_bike					 varchar
	h8_bikeqty					 int
	pax_id					 int
	p1_age					 varchar
	p2_gender					 varchar
	p3c_nolic					 varchar
	p3_car_lic					 varchar
	p3a_moto_lic					 varchar
	p3b_vanbus_lic					 varchar
	p4_mobility					 varchar
	p4a_aids					 varchar
	p4b_aidsoth					 varchar
	p5a_edu					 varchar
	p5_econactivity					 varchar
	p5i_econactoth					 varchar
	p6_occup					 varchar
	p6i_occupoth					 varchar
	p6a_fixedwkpl					 varchar
	p6c_fwkplpcode					 int
	p7_workhrs					 varchar
	p8_income					 varchar
	p9_day					 varchar
	p9_date					 varchar
	p9_datefull					 datetime
	p10_maketrip					 varchar
	p11_notripreason					 varchar
	p11a_notripreasoth					 varchar
	p12_lasttravelday					 varchar
	trip_id					 int
	stage_id					 int
	p13_1sttriporig_home					 varchar
	p13b_1storigpcode					 int
	p13d_origpcode					 int
	p13e_deptime					 varchar
	p14_1sttripstarttime					 int
	t2_destpcode					 int
	t3_starttime					 int
	t3_starttime_24h					 datetime
	t4_endtime					 int
	t4_endtime_24h					 datetime
	t5_placetype					 varchar
	t6_purpose					 varchar
	t10_mode					 varchar
	t10a_walktime					 int
	t10i_modeother					 varchar
	t11_boardsvcstn					 varchar
	t12_alightstn					 varchar
	t12a_paxinveh					 int
	t13_waittime					 int
	t14_invehtime					 int
	t15_taxifare					 double
	t16_taxireimb					 varchar
	t17_erpcost					 double
	t18_erpreimb					 varchar
	t19_parkfee					 double
	t19a_parkftyp					 varchar
	t20_parkreimb					 varchar
	t22_lastwlktime					 double
	t23_tripfreq					 int
	t24_compjtime					 int
	t25_estjtime					 int
	p28a_fastrbypt					 varchar
	p28b_cheaprbypt					 varchar
	p28c_easrtoacc					 varchar
	p28d_needntpark					 varchar
	p28e_lessstress					 varchar
	p28f_othrmmbrusecar					 varchar
	p28g_opconly					 varchar
	p28h_envfrndly					 varchar
	p28i_othrreasnchck					 varchar
	p29_futrptuse					 varchar
	p30_spendpt					 double
	p31_spendschshtlbus					 double
	p32_ptreimb					 int
	t27_flextimetyp					 varchar
	hhfactors_200409					 double
	tripfactorsstgfinal					 double
*/
	 int				msno_main;
	 String				h1_hhid;
	 int				h1_pcode;
	 String				h2_dwell;
	 String				h3_ethnic;
	 String				h3a_other;
	 int				h4_totpax;
	 String				h4b_u4yrs;
	 String				h4c_eligible;
	 String				h4d_nereason;
	 String				h5_vehavail;
	 int				xqty_carncom;
	 int				xqty_carnind;
	 int				xqty_carnrent;
	 int				xqty_caropcom;
	 int				xqty_caropind;
	 String				xqty_caroprent;
	 int				xqty_buscoach;
	 int				xqty_hgv;
	 int				xqty_lgvgoods;
	 int				xqty_lgvpsgr;
	 int				xqty_minibus;
	 int				xqty_motocom;
	 int				xqty_motoind;
	 int				xqty_taxi;
	 String				h7_bike;
	 int				h8_bikeqty;
	 int				pax_id;
	 String				p1_age;
	 String				p2_gender;
	 String				p3c_nolic;
	 String				p3_car_lic;
	 String				p3a_moto_lic;
	 String				p3b_vanbus_lic;
	 String				p4_mobility;
	 String				p4a_aids;
	 String				p4b_aidsoth;
	 String				p5a_edu;
	 String				p5_econactivity;
	 String				p5i_econactoth;
	 String				p6_occup;
	 String				p6i_occupoth;
	 String				p6a_fixedwkpl;
	 int				p6c_fwkplpcode;
	 String				p7_workhrs;
	 String				p8_income;
	 String				p9_day;
	 String				p9_date;
	 Date				p9_datefull;
	 String				p10_maketrip;
	 String				p11_notripreason;
	 String				p11a_notripreasoth;
	 String				p12_lasttravelday;
	 int				trip_id;
	 int				stage_id;
	 String				p13_1sttriporig_home;
	 int				p13b_1storigpcode;
	 int				p13d_origpcode;
	 String				p13e_deptime;
	 int				p14_1sttripstarttime;
	 int				t2_destpcode;
	 int				t3_starttime;
	 Date				t3_starttime_24h;
	 int				t4_endtime;
	 Date				t4_endtime_24h;
	 String				t5_placetype;
	 String				t6_purpose;
	 String				t10_mode;
	 int				t10a_walktime;
	 String				t10i_modeother;
	 String				t11_boardsvcstn;
	 String				t12_alightstn;
	 int				t12a_paxinveh;
	 int				t13_waittime;
	 int				t14_invehtime;
	 double				t15_taxifare;
	 String				t16_taxireimb;
	 double				t17_erpcost;
	 String				t18_erpreimb;
	 double				t19_parkfee;
	 String				t19a_parkftyp;
	 String				t20_parkreimb;
	 double				t22_lastwlktime;
	 int				t23_tripfreq;
	 int				t24_compjtime;
	 int				t25_estjtime;
	 String				p28a_fastrbypt;
	 String				p28b_cheaprbypt;
	 String				p28c_easrtoacc;
	 String				p28d_needntpark;
	 String				p28e_lessstress;
	 String				p28f_othrmmbrusecar;
	 String				p28g_opconly;
	 String				p28h_envfrndly;
	 String				p28i_othrreasnchck;
	 String				p29_futrptuse;
	 double				p30_spendpt;
	 double				p31_spendschshtlbus;
	 int				p32_ptreimb;
	 String				t27_flextimetyp;
	 double				hhfactors_200409;
	 double				tripfactorsstgfinal;
}
