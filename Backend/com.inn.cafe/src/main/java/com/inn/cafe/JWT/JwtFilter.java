package com.inn.cafe.JWT;

import com.inn.cafe.serviceimple.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.JwsException;
import io.jsonwebtoken.JwsHeader;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    @Autowired
    private JwtUtils jwtUtils;



    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    Claims claims = null;
    private String username = null;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String path = httpServletRequest.getServletPath();
        log.info("Processing path: {}", path);

        if (path.matches("/user/login|/user/signup|/user/forgotPassword")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        else {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String token = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);

                username = jwtUtils.extractUsername(token);
                claims = jwtUtils.extractAllClaims(token);
                log.info("Claims extracted: {}", claims);

            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
                if (jwtUtils.validateToken(token, userDetails)) {
                    log.info("Token is valid: {}", jwtUtils.validateToken(token, userDetails));
                    log.info("User role: {}", isAdmin() ? "admin" : "not admin");

//                    String role = jwtUtils.extractRole(token);
//                    log.info("Extracted role from token: {}", role);
//
//                    if (path.startsWith("/user/get") && !"admin".equalsIgnoreCase(role)) {
//                        log.warn("Access Denied. Non-admin role tried accessing admin endpoint.");
//                        httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
//                        return;
//                    }

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);


                }else{
                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                    clearClaims();
                    return;
                }

            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        clearClaims();
    }
//----function to  check either it is admin or not
    public boolean isAdmin() {
        if (claims != null && claims.get("role") != null) {
            log.info("Role in claims: {}", claims.get("role"));
            return "admin".equalsIgnoreCase((String) claims.get("role"));
        }
        log.warn("No role found in claims or claims is null.");
        return false;
    }

    //----function to  check either it is user or not
    public boolean isUser() {
        if (claims != null && claims.get("role") != null) {
            return "user".equalsIgnoreCase((String) claims.get("role"));
        }
        return false;
    }

    public String getCurrentUser(){
        return username;

    }

    private void clearClaims() {
        claims = null;
        username = null;
    }
}