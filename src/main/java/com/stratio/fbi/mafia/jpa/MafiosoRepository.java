package com.stratio.fbi.mafia.jpa;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stratio.fbi.mafia.model.Mafioso;

@Transactional
public interface MafiosoRepository extends JpaRepository<Mafioso, String> {

}