package com.stratio.fbi.mafia.managers;

import com.stratio.fbi.mafia.model.org.MafiaOrganization;

public interface ICosaNostraManager {

	void setOrganization(MafiaOrganization organization);

	MafiaOrganization getOrganization();

	void sendToJail(String id);

	void releaseFromJail(String id);

	void sendToCemetery(String id);

}
