package com.stratio.fbi.mafia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.stratio.fbi.mafia.config.JPAConfig;

@Entity
@Table(name = "cemetery", schema = JPAConfig.SCHEMA)
public class Cemetery implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column
    private Integer id;

    @ElementCollection
    private List<Mafioso> guests = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Mafioso> getGuests() {
        return guests;
    }

    public void setGuests(List<Mafioso> guests) {
        this.guests = guests;
    }

    public void addGuest(Mafioso guest) {
        this.guests.add(guest);
    }

}
