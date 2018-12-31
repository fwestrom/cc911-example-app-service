package com.msi.cc911.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration("appSecurityProperties")
//@ConfigurationProperties(prefix = "security.oauth2")
public class AppSecurityProperties {

    @Value("${security.oauth2.client.accessTokenUri}")
    private URL accessTokenUri;

    @Value("${security.oauth2.client.clientId}")
    private String clientId;

    @Value("${security.oauth2.client.clientSecret}")
    private String clientSecret;

    public URL getAccessTokenUri() {
        return accessTokenUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
