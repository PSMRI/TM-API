/*
* AMRIT – Accessible Medical Records via Integrated Technology 
* Integrated EHR (Electronic Health Records) Solution 
*
* Copyright (C) "Piramal Swasthya Management and Research Institute" 
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/

package com.iemr.tm.service.health;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private static final Logger logger = LoggerFactory.getLogger(HealthService.class);

    private static final String STATUS_KEY = "status";
    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";
    
    private static final long MYSQL_TIMEOUT_SECONDS = 3;
    private static final long REDIS_TIMEOUT_SECONDS = 3;

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ExecutorService executorService;

    public HealthService(DataSource dataSource,
                        @Autowired(required = false) RedisTemplate<String, Object> redisTemplate) {
        this.dataSource = dataSource;
        this.redisTemplate = redisTemplate;
        this.executorService = Executors.newFixedThreadPool(2);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    public Map<String, Object> checkHealth() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        
        Map<String, Map<String, Object>> components = new LinkedHashMap<>();
        
        // Check MySQL
        Map<String, Object> mysqlStatus = new LinkedHashMap<>();
        performHealthCheck("MySQL", mysqlStatus, this::checkMySQLHealth);
        components.put("mysql", mysqlStatus);
        
        // Check Redis
        Map<String, Object> redisStatus = new LinkedHashMap<>();
        performHealthCheck("Redis", redisStatus, this::checkRedisHealth);
        components.put("redis", redisStatus);
        
        response.put("components", components);
        
        // Overall status
        boolean allUp = components.values().stream()
            .allMatch(this::isHealthy);
        response.put(STATUS_KEY, allUp ? STATUS_UP : STATUS_DOWN);
        
        return response;
    }

    private HealthCheckResult checkMySQLHealth() {
        CompletableFuture<HealthCheckResult> future = CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("SELECT 1 as health_check")) {
                
                stmt.setQueryTimeout((int) MYSQL_TIMEOUT_SECONDS);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new HealthCheckResult(true, null);
                    }
                }
                
                return new HealthCheckResult(false, "No result from health check query");
                
            } catch (Exception e) {
                logger.warn("MySQL health check failed: {}", e.getMessage());
                return new HealthCheckResult(false, "MySQL connection failed");
            }
        }, executorService);
        
        try {
            return future.get(MYSQL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            logger.warn("MySQL health check timed out");
            return new HealthCheckResult(false, "MySQL health check timed out");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new HealthCheckResult(false, "MySQL health check was interrupted");
        } catch (Exception e) {
            logger.warn("MySQL health check failed: {}", e.getMessage());
            return new HealthCheckResult(false, "MySQL connection failed");
        }
    }

    private HealthCheckResult checkRedisHealth() {
        if (redisTemplate == null) {
            return new HealthCheckResult(true, null);
        }
        
        CompletableFuture<String> future = null;
        try {
            future = CompletableFuture.supplyAsync(() -> {
                try {
                    return redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<String>) (connection) -> connection.ping());
                } catch (Exception e) {
                    return null;
                }
            }, executorService);
            
            String pong = future.get(REDIS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            if ("PONG".equals(pong)) {
                return new HealthCheckResult(true, null);
            }
            
            return new HealthCheckResult(false, "Redis PING failed");
            
        } catch (TimeoutException e) {
            if (future != null) {
                future.cancel(true);
            }
            logger.warn("Redis health check timed out");
            return new HealthCheckResult(false, "Redis health check timed out");
        } catch (InterruptedException e) {
            logger.warn("Redis health check was interrupted");
            Thread.currentThread().interrupt();
            return new HealthCheckResult(false, "Redis health check was interrupted");
        } catch (Exception e) {
            logger.warn("Redis health check failed: {}", e.getMessage());
            return new HealthCheckResult(false, "Redis connection failed");
        }
    }

    private Map<String, Object> performHealthCheck(String componentName,
                                                    Map<String, Object> status,
                                                    Supplier<HealthCheckResult> checker) {
        long startTime = System.currentTimeMillis();
        
        try {
            HealthCheckResult result = checker.get();
            long responseTime = System.currentTimeMillis() - startTime;
            
            status.put(STATUS_KEY, result.isHealthy ? STATUS_UP : STATUS_DOWN);
            status.put("responseTimeMs", responseTime);
            
            if (result.error != null) {
                status.put("error", result.error);
            }
            
            return status;
            
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            logger.error("{} health check failed with exception: {}", componentName, e.getMessage(), e);
            
            status.put(STATUS_KEY, STATUS_DOWN);
            status.put("responseTimeMs", responseTime);
            status.put("error", "Health check failed with an unexpected error");
            
            return status;
        }
    }

    private boolean isHealthy(Map<String, Object> componentStatus) {
        return STATUS_UP.equals(componentStatus.get(STATUS_KEY));
    }

    private static class HealthCheckResult {
        final boolean isHealthy;
        final String error;

        HealthCheckResult(boolean isHealthy, String error) {
            this.isHealthy = isHealthy;
            this.error = error;
        }
    }
}
