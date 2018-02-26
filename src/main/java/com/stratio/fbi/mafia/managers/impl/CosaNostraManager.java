package com.stratio.fbi.mafia.managers.impl;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stratio.fbi.mafia.managers.ICosaNostraManager;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

/**
 * 
 * @author rostskadat
 *
 */
@Component
public class CosaNostraManager implements ICosaNostraManager {

    @Value("${capo.threshold}")
    private Integer capoThreshold;

    @Value("${isSubordinateCountDeep:false}")
    private Boolean isDeep;

    @Autowired
    MafiosoManager mafiosoManager;

    @Autowired
    CemeteryManager cemeteryManager;

    @Autowired
    JailManager jailManager;

    MafiaOrganization organization;

    @Override
    public void setOrganization(MafiaOrganization organization) {
        this.organization = organization;
    }

    @Override
    public MafiaOrganization getOrganization() {
        return organization;
    }

    @Override
    public void sendToJail(String id) {
        Iterator<Mafioso> i = organization.iterator();
        while (i.hasNext()) {
            Mafioso mafioso = i.next();
            if (StringUtils.equals(mafioso.getId(), id)) {
                jailManager.sendToJail(mafioso);
                i.remove();
                // TODO: reshuffle subordinates
                return;
            }
        }
    }

    @Override
    public void releaseFromJail(String id) {
        Mafioso mafioso = jailManager.releaseFromJail(id);
        // TODO: reshuflle subordinates
    }

    @Override
    public void sendToCemetery(String id) {
        Iterator<Mafioso> i = organization.iterator();
        while (i.hasNext()) {
            Mafioso mafioso = i.next();
            if (StringUtils.equals(mafioso.getId(), id)) {
                cemeteryManager.sendToCemetery(mafioso);
                i.remove();
                // TODO: reshuffle my subordinate
                return;
            }
        }
    }

}
