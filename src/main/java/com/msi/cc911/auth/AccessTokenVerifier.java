package com.msi.cc911.auth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.security.access.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * CC911 Access Token Verifier
 */
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
     * Verifies an auth token and returns an auth context.
     * @param tokenType The token type.
     * @param token The access token.
     * @return An auth context for the verified token.
     */
    public CC911AuthContext verify(TokenType tokenType, String token) {
        if (tokenType != TokenType.Bearer) {
            throw new UnsupportedOperationException("Only bearer tokens are supported.");
        }

        verifyBearerToken(token);
        return enrichBearerToken(token);
    }

    // TODO: Decide whether to return something more interesting than void here (probably should!)
    private void verifyBearerToken(String token) throws AccessDeniedException {
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

    private CC911AuthContext enrichBearerToken(String token) {
        URL userInfoUri = securityProperties.getUserInfoUri();
        log.trace("verifyBearerToken| accessTokenUri: {}", userInfoUri);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAuthorization("Bearer " + token);
            headers.setAccept("application/json");

            HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(userInfoUri));
            request.setHeaders(headers);

            try {
                HttpResponse response = request.execute();

                ObjectMapper objectMapper = new ObjectMapper(new MappingJsonFactory());
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                JsonNode root = objectMapper.readTree(response.parseAsString());
                JsonNode cc911 = root.get("services").get("cc911");

                List<Permission> permissions = new ArrayList<Permission>();
                for (Iterator<JsonNode> it = cc911.get("permissions").elements(); it.hasNext(); ) {
                    JsonNode jp = it.next();
                    log.trace("enrichBearerToken| permission:\n" + jp);
                    permissions.add(new Permission(jp.get("systemName").asText(), jp.get("name").asText()));
                }

                CC911AuthContext authContext = new CC911AuthContext(
                        root.get("id").asText(),
                        root.get("email").asText(),
                        permissions);
                return authContext;
            }
            catch (HttpResponseException ex) {
                GenericJson json = jsonFactory.fromString(ex.getContent(), GenericJson.class);
                log.trace("enrichBearerToken| response error:\n" + json.toPrettyString());
                throw new AccessDeniedException("Enrich token error: " + json.get("error") + " (" + json.get("error_description") + ")");
            }
        }
        catch (IOException ex) {
            throw new AccessDeniedException("Unexpected failure while enriching token.", ex);
        }
    }
}


