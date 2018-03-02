package com.stratio.fbi.mafia.config.converters;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;
import com.stratio.fbi.mafia.model.org.tree.TreeMafiaOrganization;

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
        TreeMafiaOrganization tree = (TreeMafiaOrganization) t;
        new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValue(outputMessage.getBody(), tree.getCupulaForSerialization());
    }

}
