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

import org.apache.commons.lang3.ObjectUtils;

import com.stratio.fbi.mafia.config.JPAConfig;

@Entity
@Table(name = "mafiosos", schema = JPAConfig.SCHEMA)
public class Mafioso implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column
	private String id;

    @Column
	private String firstName;

    @Column
	private String lastName;
	
    @Column
	private Integer age;

    @ElementCollection
    private List<String> subordinates = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<String> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<String> subordinates) {
        this.subordinates = subordinates;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCodeMulti(id, firstName, lastName, age);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Mafioso other = (Mafioso) obj;
        return ObjectUtils.equals(id, other.id) && ObjectUtils.equals(firstName, other.firstName)
                && ObjectUtils.equals(lastName, other.lastName)
                && ObjectUtils.equals(age, other.age);
    }


}
