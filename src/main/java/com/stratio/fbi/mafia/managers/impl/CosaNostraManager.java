package com.stratio.fbi.mafia.managers.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stratio.fbi.mafia.managers.ICosaNostraManager;
import com.stratio.fbi.mafia.model.MafiaCell;
import com.stratio.fbi.mafia.model.Mafioso;

@Component
public class CosaNostraManager implements ICosaNostraManager {

    @Value("${capo.threshold}")
    private Integer capoThreshold;

    @Autowired
    MafiosoManager mafiosoManager;

    @Autowired
    CemeteryManager cemeteryManager;

    @Autowired
    JailManager jailManager;

    MafiaCell cupula = new MafiaCell();

    @Override
    public List<Mafioso> getCapos() {
        List<Mafioso> capos = new ArrayList<>();
        getCaposInCell(getCupula(), capos);
        return capos;
    }

    private void getCaposInCell(MafiaCell mafiaCell, List<Mafioso> capos) {
        for (MafiaCell member : mafiaCell.getSubordinates()) {
            if (member.getSubordinates().size() >= capoThreshold) {
                capos.add(member.getMafioso());
            }
        }
    }

    @Override
    public MafiaCell getCupula() {
        return cupula;
    }

    @Override
    public void addSubordinate(Mafioso boss, Mafioso mafioso) {
        if (mafioso == null) {
            throw new IllegalArgumentException("Mafioso can't be null");
        }
    }

    @Override
    public void sendToJail(String id) {
        Iterator<MafiaCell> i = cupula.getSubordinates().iterator();
        while (i.hasNext()) {
            Mafioso mafioso = i.next().getMafioso();
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
        Iterator<MafiaCell> i = cupula.getSubordinates().iterator();
        while (i.hasNext()) {
            Mafioso mafioso = i.next().getMafioso();
            if (StringUtils.equals(mafioso.getId(), id)) {
                cemeteryManager.sendToCemetery(mafioso);
                i.remove();
                // TODO: reshuffle my subordinate
                return;
            }
        }
    }

}
