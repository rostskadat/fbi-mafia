package com.stratio.fbi.mafia.jpa;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stratio.fbi.mafia.model.Mafioso;

@Transactional
public interface JailRepository extends JpaRepository<Mafioso, String> {

    public Iterable<Mafioso> getMafiosoById(@NotNull final String id);

}
