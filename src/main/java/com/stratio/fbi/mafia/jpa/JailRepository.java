package com.stratio.fbi.mafia.jpa;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stratio.fbi.mafia.model.org.MafiosoPosition;

@Transactional
public interface JailRepository extends JpaRepository<MafiosoPosition, String> {

}
