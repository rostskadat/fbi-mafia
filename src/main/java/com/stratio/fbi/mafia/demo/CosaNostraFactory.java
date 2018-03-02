package com.stratio.fbi.mafia.demo;

import static com.stratio.fbi.mafia.model.org.OrganizationFactory.createPathList;
import static com.stratio.fbi.mafia.model.org.OrganizationFactory.createRelationList;
import static com.stratio.fbi.mafia.model.org.OrganizationFactory.createTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stratio.fbi.mafia.managers.IMafiosoManager;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

@Component
public class CosaNostraFactory {

	private static final Log LOG = LogFactory.getLog(CosaNostraFactory.class);

	@Value("${isSubordinateCountDeep:false}")
	private Boolean isDeep;

	@Value("${firstNames}")
	private String firstNamesParam;
	private List<String> firstNames = new ArrayList<>();

	@Value("${lastNames}")
	private String lastNamesParam;
	private List<String> lastNames = new ArrayList<>();

	@Value("${maxMafioso}")
	private Integer MAX_MAFIOSO;

	@Autowired
	IMafiosoManager mafiosoManager;

	@PostConstruct
	private void postConstruct() {
		if (StringUtils.isNotBlank(firstNamesParam)) {
			firstNames.addAll(Arrays.asList(StringUtils.split(firstNamesParam, ", ; ")));
		}
		if (StringUtils.isNotBlank(lastNamesParam)) {
			lastNames.addAll(Arrays.asList(StringUtils.split(lastNamesParam, ", ; ")));
		}
	}

    public MafiaOrganization createFixedOrganization(MafiaOrganization organization, boolean register) {
        organization.erase();
        organization.setCupula(createGodfather(register));
        return createRandomOrganization(organization, register);
        // Mafioso cupula = createGodfather(register);
        // Mafioso r1 = createRandomMafioso(register);
        // Mafioso r2 = createRandomMafioso(register);
        // Mafioso r11 = createRandomMafioso(register);
        // r11.setAge(40);
        // Mafioso r12 = createRandomMafioso(register);
        // r12.setAge(30);
        // Mafioso r13 = createRandomMafioso(register);
        // r13.setAge(20);
        // organization.setCupula(cupula);
        // organization.addSubordinate(cupula, r1);
        // organization.addSubordinate(cupula, r2);
        // organization.addSubordinate(r1, r11);
        // organization.addSubordinate(r1, r12);
        // organization.addSubordinate(r1, r13);
        // return organization;
    }

	public MafiaOrganization getTreeOrganization() {
        return getTreeOrganization(false);
	}

    public MafiaOrganization getTreeOrganization(boolean register) {
        return createRandomOrganization(createTree(createGodfather(register), isDeep), register);
    }

    public MafiaOrganization getPathListOrganization() {
        return getPathListOrganization(false);
    }

    public MafiaOrganization getPathListOrganization(boolean register) {
        return createRandomOrganization(createPathList(createGodfather(register), isDeep), register);
    }

    public MafiaOrganization getRelationListOrganization() {
        return getRelationListOrganization(false);
    }

    public MafiaOrganization getRelationListOrganization(boolean register) {
        return createRandomOrganization(createRelationList(createGodfather(register), isDeep), register);
    }

	public Mafioso createGodfather() {
        return createGodfather(false);
	}

    public Mafioso createGodfather(boolean register) {
        Mafioso godfather = new Mafioso();
        godfather.setFirstName("Al");
        godfather.setLastName("Capone");
        godfather.setAge(48);
        if (register) {
            return mafiosoManager.add(godfather);
        }
        godfather.setId("0");
        return godfather;
    }

	public Mafioso createRandomMafioso() {
        return createRandomMafioso(false);
	}

    public Mafioso createRandomMafioso(boolean register) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Mafioso mafioso = new Mafioso();
        mafioso.setFirstName(firstNames.get(random.nextInt(0, firstNames.size())));
        mafioso.setLastName(lastNames.get(random.nextInt(0, lastNames.size())));
        mafioso.setAge(random.nextInt(18, 100));
        if (register) {
            return mafiosoManager.add(mafioso);
        }
        mafioso.setId(UUID.randomUUID().toString());
        return mafioso;
    }

    private MafiaOrganization createRandomOrganization(MafiaOrganization organization, boolean register) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		List<Mafioso> mafiosos = new ArrayList<>();
		Mafioso boss = organization.getCupula();
		mafiosos.add(boss);
		for (int i = 1; i < MAX_MAFIOSO; i++) {
            Mafioso mafioso = createRandomMafioso(register);
			mafiosos.add(mafioso);
			organization.addSubordinate(boss, mafioso);
			int lowerBound = 0;
			int upperBound = mafiosos.size() - 1;
			if (lowerBound < upperBound) {
				// Spread the subordinates around the organization
				boss = mafiosos.get(random.nextInt(lowerBound, upperBound));
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Added mafioso #%s: %s %s aged %d as a subordinate to %s %s", mafioso.getId(),
				        mafioso.getFirstName(), mafioso.getLastName(), mafioso.getAge(), boss.getFirstName(),
				        boss.getLastName()));
			}
		}
		return organization;
	}

}
