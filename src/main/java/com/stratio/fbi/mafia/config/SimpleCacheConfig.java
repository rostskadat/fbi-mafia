package com.stratio.fbi.mafia.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class SimpleCacheConfig {

    private static final Log LOG = LogFactory.getLog(SimpleCacheConfig.class);

    public static final String GENERIC_CACHE = "GenericCache";

    @Bean
    public CacheManager cacheManager() {
        LOG.info("Initializing CacheManager...");
        CacheManager cashManager = new JCacheCacheManager(jsr107CacheManager());
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(cashManager);
        compositeCacheManager.setFallbackToNoOpCache(true);
        return compositeCacheManager;
    }

    private javax.cache.CacheManager jsr107CacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        try {
            ClassLoader cl = getClass().getClassLoader();
            URI uri = getClass().getResource("/ehcache-jsr107.xml").toURI();
            LOG.debug("Loading cache definition from " + uri.toString());
            return cachingProvider.getCacheManager(uri, cl);
        } catch (URISyntaxException e) {
            LOG.error("Failed to configure cache: " + e.getMessage(), e);
            throw new BeanInstantiationException(CacheManager.class, e.getMessage());
        }
    }
}
