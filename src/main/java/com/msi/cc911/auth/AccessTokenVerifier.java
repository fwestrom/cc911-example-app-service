package com.msi.cc911.auth;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

/**
 * CC911 Access Token Verifier
 */
@Service
public class AccessTokenVerifier {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenVerifier.class);
    private static final String utf8 = "UTF-8";
    private final Base64.Encoder base64Encoder = Base64.getEncoder();
    private final HttpRequestFactory httpRequestFactory = new NetHttpTransport().createRequestFactory();
    private final JsonFactory jsonFactory = new JacksonFactory();
    private final SecurityProperties securityProperties;

    public AccessTokenVerifier(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * Verifies a bearer token.
     * @param token The access token
     */
    // TODO: Decide whether to return something more interesting than void here (probably should!)
    public void verifyBearerToken(String token) throws AccessDeniedException {
        URL accessTokenUri = securityProperties.getAccessTokenUri();
        log.trace("verifyBearerToken| accessTokenUri: {}", accessTokenUri);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAuthorization("Basic " + base64Encoder.encodeToString((securityProperties.getClientId() + ":" + securityProperties.getClientSecret()).getBytes(utf8)));
            headers.setAccept("application/json");

            HashMap<String, String> content = new HashMap<>();
            content.put("grant_type", "urn:pingidentity.com:oauth2:grant_type:validate_bearer");
            content.put("token", token);

            HttpRequest request = httpRequestFactory.buildPostRequest(
                    new GenericUrl(securityProperties.getAccessTokenUri()),
                    new UrlEncodedContent(content));
            request.setHeaders(headers);

            try {
                HttpResponse response = request.execute();
                GenericJson json = jsonFactory.fromString(response.parseAsString(), GenericJson.class);
                log.trace("verifyBearerToken| response:\n" + json.toPrettyString());

                String tokenType = json.get("token_type").toString();
                if (!tokenType.equals("urn:pingidentity.com:oauth2:validated_token"))
                    throw new AccessDeniedException("Invalid token type: " + tokenType);
            }
            catch (HttpResponseException ex) {
                if (ex.getStatusCode() == 400) {
                    GenericJson json = jsonFactory.fromString(ex.getContent(), GenericJson.class);
                    log.trace("verifyBearerToken| response error:\n" + json.toPrettyString());
                    throw new AccessDeniedException("Validate token error: " + json.get("error") + " (" + json.get("error_description") + ")");
                }
            }
        }
        catch (IOException ex) {
            throw new AccessDeniedException("Unexpected failure while verifying token.", ex);
        }
    }
}
