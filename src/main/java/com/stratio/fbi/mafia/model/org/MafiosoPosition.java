package com.stratio.fbi.mafia.model.org;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.stratio.fbi.mafia.config.JPAConfig;

/**
 * 
 * 
 * @author rostskadat
 *
 */
@Entity
@Table(name = "jail_positions", schema = JPAConfig.SCHEMA)
public class MafiosoPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column
    private String mafiosoId;

    @Column
    private String bossId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> directSubordinateIds = new ArrayList<>();

    public MafiosoPosition() {
        super();
    }

    public MafiosoPosition(String bossId, String mafiosoId, List<String> directSubordinateIds) {
        super();
        this.bossId = bossId;
        this.mafiosoId = mafiosoId;
        this.directSubordinateIds.addAll(directSubordinateIds);
    }

    public String getBossId() {
        return bossId;
    }

    public void setBossId(String bossId) {
        this.bossId = bossId;
    }

    public String getMafiosoId() {
        return mafiosoId;
    }

    public void setMafiosoId(String mafiosoId) {
        this.mafiosoId = mafiosoId;
    }

    public List<String> getDirectSubordinateIds() {
        return directSubordinateIds;
    }

    public void setDirectSubordinateIds(List<String> directSubordinateIds) {
        this.directSubordinateIds = directSubordinateIds;
    }
    
	@Override
	public String toString() {
		try {
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return e.getMessage();
		}
	}

}
