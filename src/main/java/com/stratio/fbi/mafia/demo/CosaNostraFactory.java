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
import org.springframework.boot.test.context.TestComponent;

import com.stratio.fbi.mafia.managers.IMafiosoManager;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

@TestComponent
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

	public MafiaOrganization getTreeOrganization() {
        return getTreeOrganization(false);
	}

    public MafiaOrganization getTreeOrganization(boolean register) {
        return createOrganization(createTree(createGodfather(register), isDeep), register);
    }

    public MafiaOrganization getPathListOrganization() {
        return getPathListOrganization(false);
    }

    public MafiaOrganization getPathListOrganization(boolean register) {
        return createOrganization(createPathList(createGodfather(register), isDeep), register);
    }

    public MafiaOrganization getRelationListOrganization() {
        return getRelationListOrganization(false);
    }

    public MafiaOrganization getRelationListOrganization(boolean register) {
        return createOrganization(createRelationList(createGodfather(register), isDeep), register);
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

    private MafiaOrganization createOrganization(MafiaOrganization organization, boolean register) {
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
