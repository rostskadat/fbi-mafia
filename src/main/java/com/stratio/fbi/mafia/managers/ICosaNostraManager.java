package com.stratio.fbi.mafia.managers;

import java.util.List;

import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

public interface ICosaNostraManager {

	void setOrganization(MafiaOrganization organization);

	MafiaOrganization getOrganization();

    List<Mafioso> getWatchList();

	void sendToJail(String id);

	void releaseFromJail(String id);

}
