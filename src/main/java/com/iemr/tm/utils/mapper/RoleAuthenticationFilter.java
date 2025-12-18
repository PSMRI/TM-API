package com.iemr.tm.utils.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.iemr.tm.utils.CookieUtil;
import com.iemr.tm.utils.JwtAuthenticationUtil;
import com.iemr.tm.utils.JwtUtil;
import com.iemr.tm.utils.redis.RedisStorage;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RoleAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger
            = LoggerFactory.getLogger(RoleAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisStorage redisService;

    @Autowired
    private JwtAuthenticationUtil userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, java.io.IOException {

        try {
            Long userId = null;

            /* =======================
             * TRY JWT TOKEN FIRST
             * ======================= */
            String jwtToken
                    = CookieUtil.getJwtTokenFromCookie(request) != null
                    ? CookieUtil.getJwtTokenFromCookie(request)
                    : request.getHeader("Jwttoken");

            if (jwtToken != null && !jwtToken.isBlank()) {
                Claims claims = jwtUtil.validateToken(jwtToken);
                if (claims != null && claims.get("userId") != null) {
                    userId = Long.valueOf(claims.get("userId").toString());
                    logger.info("UserId resolved from JWT: {}", userId);
                }
            }

            /* =================================
             * FALLBACK → LEGACY AUTH + REDIS
             * ================================= */
            if (userId == null) {
                String authToken = resolveAuthToken(request);
                logger.info("Resolved authToken: {}", authToken);

                if (authToken != null && !authToken.isBlank()) {
                    String sessionJson = null;
                    try {
                        sessionJson = redisService.getObject(authToken, true, 100000);
                    } catch (Exception ex) {
                        logger.warn("No Redis session found for authToken: {}", authToken);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    if (sessionJson != null && !sessionJson.isBlank()) {
                        JSONObject json = new JSONObject(sessionJson);

                        if (json.has("userID")) {
                            userId = json.getLong("userID");
                            logger.info("UserId resolved from Redis: {}", userId);
                        }
                    }
                }
            }

            /* =======================
             * NO USER → SKIP
             * ======================= */
            if (userId == null) {
                logger.debug("No userId resolved, skipping authentication");
                filterChain.doFilter(request, response);
                return;
            }

            /* =======================
             * LOAD USER ROLES
             * ======================= */
            List<String> authRoles
                    = redisService.getUserRoleFromCache(userId);

            if (authRoles == null || authRoles.isEmpty()) {
                authRoles = userService.getUserRoles(userId)
                        .stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .map(r -> "ROLE_" + r.toUpperCase().replace(" ", "_"))
                        .collect(Collectors.toList());

                redisService.cacheUserRoles(userId, authRoles);
            }

            /* =======================
             * SET SECURITY CONTEXT
             * ======================= */
            List<GrantedAuthority> authorities = authRoles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(
                            userId, null, authorities);

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            logger.info("Authentication set for userId {}", userId);

        } catch (Exception e) {
            logger.error("Authentication error", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /* =======================
     * AUTH TOKEN RESOLVER
     * ======================= */
    private String resolveAuthToken(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token == null || token.isBlank()) {
            token = request.getHeader("AuthToken");
        }
        if (token == null || token.isBlank()) {
            token = request.getHeader("X-Auth-Token");
        }
        if (token == null || token.isBlank()) {
            token = CookieUtil.getCookieValue(request, "Authorization")
                    .orElse(null);
        }
        return token;
    }
}
