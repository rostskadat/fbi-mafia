package com.stratio.fbi.mafia.managers.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.exception.ResourceNotFoundException;
import com.stratio.fbi.mafia.managers.ICosaNostraManager;
import com.stratio.fbi.mafia.managers.IJailManager;
import com.stratio.fbi.mafia.managers.IMafiosoManager;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;
import com.stratio.fbi.mafia.model.org.MafiosoPosition;

/**
 * 
 * @author rostskadat
 *
 */
@Component
public class CosaNostraManager implements ICosaNostraManager {

	private static final Logger LOG = LoggerFactory.getLogger(CosaNostraManager.class);

	@Value("${watchThreshold}")
	private Integer watchThreshold;

	@Value("${isSubordinateCountDeep:false}")
	private Boolean isDeep;

	@Value("${organizationType:NONE}")
	private String organizationType;

	@Autowired
	private CosaNostraFactory factory;

	@Autowired
	IMafiosoManager mafiosoManager;

	@Autowired
	IJailManager jailManager;

	MafiaOrganization organization;

	@PostConstruct
	private void postConstruct() {
		switch (OrganizationType.valueOf(organizationType)) {
		case TREE:
			LOG.info("Creating random Treee MafiaOrganization");
			organization = factory.getTreeOrganization(true);
			break;
		case PATH:
			LOG.info("Creating random Path MafiaOrganization");
			organization = factory.getPathListOrganization(true);
			break;
		case RELATION:
			LOG.info("Creating random Relation MafiaOrganization");
			organization = factory.getRelationListOrganization(true);
			break;
		case NONE:
			LOG.info("Not creating any MafiaOrganization");
			organization = null;
			break;
		default:
			throw new BeanInitializationException("Invalid organizationType parameter in your application.properties");
		}
	}

	@Override
	public OrganizationType getOrganizationType() {
		return OrganizationType.valueOf(organizationType);
	}

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
		if (mafiosoManager.exists(id)) {
			Mafioso mafioso = mafiosoManager.get(id);
			LOG.info(String.format("Sending %s %s to jail...", mafioso.getFirstName(), mafioso.getLastName()));
			jailManager.sendToJail(organization.getMafiosoPosition(mafioso));
			organization.removeFromOrganization(mafioso);
		} else {
			throw new ResourceNotFoundException("'id' doesn't exists: " + id);
		}
	}

	@Override
	public void releaseFromJail(String id) {
		MafiosoPosition position = jailManager.releaseFromJail(id);
		Mafioso boss = StringUtils.isNotBlank(position.getBossId()) ? mafiosoManager.get(position.getBossId()) : null;
		Mafioso mafioso = mafiosoManager.get(position.getMafiosoId());
		final List<Mafioso> directSubordinates = new ArrayList<>();
		position.getDirectSubordinateIds().forEach(new Consumer<String>() {
			@Override
			public void accept(String t) {
				directSubordinates.add(mafiosoManager.get(t));
			}
		});
		LOG.info(String.format("Releasing %s %s from jail...", mafioso.getFirstName(), mafioso.getLastName()));
		organization.reinstateMafioso(boss, mafioso, directSubordinates);
	}

	@Override
	public List<Mafioso> getWatchList() {
		List<Mafioso> listToWatch = new ArrayList<>();
		Iterator<Mafioso> i = organization.iterator();
		while (i.hasNext()) {
			Mafioso mafioso = i.next();
			int subordinatesCount = 0;
			Iterator<Mafioso> j = organization.getSubordinates(mafioso);
			while (j.hasNext()) {
				j.next();
				subordinatesCount++;
			}
			if (subordinatesCount >= watchThreshold) {
				listToWatch.add(mafioso);
			}
		}
		return listToWatch;
	}

	public Integer getWatchThreshold() {
		return watchThreshold;
	}

	public void setWatchThreshold(Integer watchThreshold) {
		this.watchThreshold = watchThreshold;
	}

}
