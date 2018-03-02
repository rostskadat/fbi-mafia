package com.stratio.fbi.mafia.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.stratio.fbi.mafia.config.JPAConfig;

@Entity
@Table(name = "jail", schema = JPAConfig.SCHEMA)
public class Jail implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column
    private Integer id;

    @ElementCollection(fetch = FetchType.EAGER)
	private List<Mafioso> prisoners;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Mafioso> getPrisoners() {
        return prisoners;
    }

    public void setPrisoners(List<Mafioso> prisoners) {
        this.prisoners = prisoners;
    }

}
