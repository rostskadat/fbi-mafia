package com.stratio.fbi.mafia.config;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public class JPAConfig {

    public static final String SCHEMA = "fbi_mafia";
}
