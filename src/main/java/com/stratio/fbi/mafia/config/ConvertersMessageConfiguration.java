package com.stratio.fbi.mafia.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.stratio.fbi.mafia.config.converters.MafiaOrganizationConverter;

@Configuration
public class ConvertersMessageConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        converters.add(new MafiaOrganizationConverter());

    }
}