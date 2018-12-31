package com.msi.cc911;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.msi.cc911.auth.AccessTokenVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.msi.cc911.SecurityConstants.HEADER_STRING;
import static com.msi.cc911.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);
    private final AccessTokenVerifier accessTokenVerifier;

    public JWTAuthorizationFilter(AccessTokenVerifier accessTokenVerifier, AuthenticationManager authManager) {
        super(authManager);
        this.accessTokenVerifier = accessTokenVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        log.warn("JWTAuthorizationFilter.doFilterInternal");

        String header = req.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null && token.toLowerCase().startsWith("bearer")) {
            token = token.split(" ", 2)[1];
        }
        log.warn("JWTAuthorizationFilter.getAuthentication| token: {}", token);

        try {
            accessTokenVerifier.verifyBearerToken(token);
        }
        catch (Exception ex) {
            log.warn("Error while verifying bearer token.", ex);
        }

        if (token != null) {
            DecodedJWT jwt = JWT.decode(token);
            try {
                String claimsText = "";
                for (Map.Entry<String, Claim> claim: jwt.getClaims().entrySet()) {
                    claimsText += "\n    " + claim.getKey() + ": " + claim.getValue().asString();
                }
                log.warn("jwt|\nheader: {}\npayload: {}\nsignature: {}\nalgorithm: {}\nsubject: {}\naudience: {}\nissuer: {}\nclaims: {}", jwt.getHeader(), jwt.getPayload(), jwt.getSignature(), jwt.getAlgorithm(), jwt.getSubject(), jwt.getAudience(), jwt.getIssuer(), claimsText);

                ArrayList<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("Call Taker"));
                authorities.add(new SimpleGrantedAuthority("Call Taker Supervisor"));

                UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(jwt.getSubject(), jwt, authorities);
//                authtoken.setDetails(new UserDetails() {
//                    @Override
//                    public Collection<? extends GrantedAuthority> getAuthorities() {
//                        return authorities;
//                    }
//
//                    @Override
//                    public String getPassword() {
//                        return null;
//                    }
//
//                    @Override
//                    public String getUsername() {
//                        return null;
//                    }
//
//                    @Override
//                    public boolean isAccountNonExpired() {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean isAccountNonLocked() {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean isCredentialsNonExpired() {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean isEnabled() {
//                        return true;
//                    }
//                });
                return authtoken;
            }
            catch (JWTVerificationException ex) {
                log.warn("Exception while parsing auth token:", ex);
            }
        }
        return null;
    }
}