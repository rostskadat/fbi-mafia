package com.stratio.fbi.mafia.config.converters;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

/**
 * 
 */
public final class MafiaOrganizationConverter extends AbstractHttpMessageConverter<MafiaOrganization> {

    public MafiaOrganizationConverter() {
        super(MediaType.APPLICATION_JSON_UTF8);
    }

    @Override
    protected boolean supports(Class<?> classz) {
        return MafiaOrganization.class.isAssignableFrom(classz);
    }

    @Override
    protected MafiaOrganization readInternal(Class<? extends MafiaOrganization> arg0, HttpInputMessage arg1)
            throws IOException, HttpMessageNotReadableException {
        throw new HttpMessageNotReadableException("Import of MafiaOrganization not supported yet");
    }

    @Override
    protected void writeInternal(MafiaOrganization t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        new ObjectMapper().writeValue(outputMessage.getBody(), t);
    }

}
