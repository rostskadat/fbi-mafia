package com.stratio.fbi.mafia.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;

import com.stratio.fbi.mafia.managers.ICosaNostraManager;
import com.stratio.fbi.mafia.managers.IMafiosoManager;
import com.stratio.fbi.mafia.model.MafiaCell;
import com.stratio.fbi.mafia.model.Mafioso;

@TestComponent
public class RandomCosaNostra {

    private static final Log LOG = LogFactory.getLog(RandomCosaNostra.class);

    @Value("${firstNames}")
    private String firstNamesParam;
    private List<String> firstNames = new ArrayList<>();

    @Value("${lastNames}")
    private String lastNamesParam;
    private List<String> lastNames = new ArrayList<>();

    @Value("${maxLevel}")
    private Integer MAX_LEVEL;

    @Value("${maxSibling}")
    private Integer MAX_SIBLING;

    @Autowired
    IMafiosoManager mafiosoManager;

    @Autowired
    ICosaNostraManager cosaNostraManager;

    @PostConstruct
    public void postConstruct() {
        if (StringUtils.isNotBlank(firstNamesParam)) {
            firstNames.addAll(Arrays.asList(StringUtils.split(firstNamesParam, ", ; ")));
        }
        if (StringUtils.isNotBlank(lastNamesParam)) {
            lastNames.addAll(Arrays.asList(StringUtils.split(lastNamesParam, ", ; ")));
        }
        if (!mafiosoManager.exists("1")) {
            Mafioso alCapone = new Mafioso();
            alCapone.setFirstName("Al");
            alCapone.setLastName("Capone");
            alCapone.setAge(48);
            mafiosoManager.add(alCapone);

            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 1; i < 1000; i++) {
                int firstName = random.nextInt(0, firstNames.size());
                int lastName = random.nextInt(0, lastNames.size());
                int age = random.nextInt(18, 100);
                Mafioso mafioso = new Mafioso();
                mafioso.setFirstName(firstNames.get(firstName));
                mafioso.setLastName(lastNames.get(lastName));
                mafioso.setAge(age);
                mafiosoManager.add(mafioso);
                LOG.debug(String.format("Added mafioso %s %s aged %d (#%s)", mafioso.getFirstName(),
                        mafioso.getLastName(),
                        mafioso.getAge(), mafioso.getId()));
            }

            // Once I created all the Mafiosi, I linked them together.
            // Al Capone is always the boss!
            MafiaCell cupula = cosaNostraManager.getCupula();
            LOG.debug("Setting Al Capone as the Godfather.");
            cupula.setMafioso(alCapone);
            Map<Mafioso, MafiaCell> map = new HashMap<>();
            mafiosoManager.findAll().forEach(new Consumer<Mafioso>() {
                @Override
                public void accept(Mafioso t) {
                    // TODO: Add the mafioso randomly in the tree
                }
            });


        } else {
            LOG.debug("We already have all the bad guys we need");
        }
    }

}
