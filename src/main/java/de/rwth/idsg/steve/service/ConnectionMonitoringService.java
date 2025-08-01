/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
 * All Rights Reserved.
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced WebSocket connection monitoring service for tracking connection patterns,
 * disconnections, and heartbeat status of charge points.
 * 
 * @author SteVe Community
 * @since 2024
 */
@Slf4j
@Service
public class ConnectionMonitoringService {

    @Autowired
    private ChargePointHelperService chargePointHelperService;

    // Track connection history
    private final Map<String, ConnectionStats> connectionHistory = new ConcurrentHashMap<>();

    public static class ConnectionStats {
        public String chargeBoxId;
        public int totalConnections = 0;
        public int totalDisconnections = 0;
        public DateTime lastConnected;
        public DateTime lastDisconnected;
        public Duration longestConnection = Duration.ZERO;
        public Duration shortestConnection;
        public long totalConnectionTime = 0; // milliseconds
        public boolean currentlyConnected = false;

        public ConnectionStats(String chargeBoxId) {
            this.chargeBoxId = chargeBoxId;
        }

        public double getAverageConnectionDuration() {
            if (totalConnections == 0) return 0;
            return (double) totalConnectionTime / totalConnections / 1000.0; // seconds
        }

        public double getUptimePercentage(DateTime since) {
            if (since == null) return 0;
            long totalPeriod = new Duration(since, DateTime.now()).getMillis();
            if (totalPeriod == 0) return 0;
            return ((double) totalConnectionTime / totalPeriod) * 100.0;
        }
    }

    /**
     * Scheduled method to monitor connection status every 30 seconds
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void monitorConnections() {
        try {
            List<OcppJsonStatus> currentConnections = chargePointHelperService.getOcppJsonStatus();
            
            // Log connection summary
            if (!currentConnections.isEmpty()) {
                log.info("=== WebSocket Connection Monitor ===");
                log.info("Active connections: {}", currentConnections.size());
                
                for (OcppJsonStatus status : currentConnections) {
                    Duration connectionDuration = new Duration(status.getConnectedSinceDT(), DateTime.now());
                    
                    // Update connection stats
                    updateConnectionStats(status.getChargeBoxId(), status.getConnectedSinceDT(), true);
                    
                    // Log details for connections > 5 minutes (indicating stable connection)
                    if (connectionDuration.getStandardMinutes() >= 5) {
                        log.info("ChargeBox: {} | OCPP: {} | Connected: {} | Duration: {} mins", 
                                status.getChargeBoxId(), 
                                status.getVersion(), 
                                status.getConnectedSince(),
                                connectionDuration.getStandardMinutes());
                    }
                    
                    // Special monitoring for Test1234
                    if ("Test1234".equals(status.getChargeBoxId())) {
                        log.warn("MONITOR Test1234: Connected for {} mins, Since: {}", 
                                connectionDuration.getStandardMinutes(),
                                status.getConnectedSince());
                    }
                }
            } else {
                log.info("=== WebSocket Connection Monitor ===");
                log.info("No active WebSocket connections");
            }

            // Log connection statistics summary
            logConnectionStatistics();

        } catch (Exception e) {
            log.error("Error in connection monitoring", e);
        }
    }

    /**
     * Update connection statistics for a charge box
     */
    private void updateConnectionStats(String chargeBoxId, DateTime connectedSince, boolean isConnected) {
        ConnectionStats stats = connectionHistory.computeIfAbsent(chargeBoxId, ConnectionStats::new);
        
        if (isConnected && !stats.currentlyConnected) {
            // New connection detected
            stats.totalConnections++;
            stats.lastConnected = connectedSince;
            stats.currentlyConnected = true;
            log.info("NEW CONNECTION: {} connected at {}", chargeBoxId, connectedSince);
        } else if (!isConnected && stats.currentlyConnected) {
            // Disconnection detected
            stats.totalDisconnections++;
            stats.lastDisconnected = DateTime.now();
            stats.currentlyConnected = false;
            
            if (stats.lastConnected != null) {
                Duration connectionDuration = new Duration(stats.lastConnected, stats.lastDisconnected);
                stats.totalConnectionTime += connectionDuration.getMillis();
                
                if (connectionDuration.isLongerThan(stats.longestConnection)) {
                    stats.longestConnection = connectionDuration;
                }
                if (stats.shortestConnection == null || connectionDuration.isShorterThan(stats.shortestConnection)) {
                    stats.shortestConnection = connectionDuration;
                }
            }
            
            log.warn("DISCONNECTION: {} disconnected at {}", chargeBoxId, stats.lastDisconnected);
        }
        
        // Update current status based on actual connection state
        List<OcppJsonStatus> currentConnections = chargePointHelperService.getOcppJsonStatus();
        boolean actuallyConnected = currentConnections.stream()
            .anyMatch(conn -> chargeBoxId.equals(conn.getChargeBoxId()));
        stats.currentlyConnected = actuallyConnected;
    }

    /**
     * Log detailed connection statistics
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void logConnectionStatistics() {
        if (connectionHistory.isEmpty()) {
            return;
        }

        log.info("=== CONNECTION STATISTICS ===");
        for (ConnectionStats stats : connectionHistory.values()) {
            log.info("ChargeBox: {} | Connections: {} | Disconnections: {} | Current: {} | Avg Duration: {:.1f}s", 
                    stats.chargeBoxId,
                    stats.totalConnections,
                    stats.totalDisconnections,
                    stats.currentlyConnected ? "CONNECTED" : "DISCONNECTED",
                    stats.getAverageConnectionDuration());
            
            if (stats.longestConnection.isLongerThan(Duration.ZERO)) {
                log.info("  -> Longest: {}m | Shortest: {}m", 
                        stats.longestConnection.getStandardMinutes(),
                        stats.shortestConnection != null ? stats.shortestConnection.getStandardMinutes() : 0);
            }
        }
    }

    /**
     * Get connection statistics for a specific charge box
     */
    public ConnectionStats getConnectionStats(String chargeBoxId) {
        return connectionHistory.get(chargeBoxId);
    }

    /**
     * Get all connection statistics
     */
    public Map<String, ConnectionStats> getAllConnectionStats() {
        return new ConcurrentHashMap<>(connectionHistory);
    }

    /**
     * Method to be called when a connection is established
     */
    public void onConnectionEstablished(String chargeBoxId, DateTime connectedAt) {
        log.info("CONNECTION ESTABLISHED: {} at {}", chargeBoxId, connectedAt);
        updateConnectionStats(chargeBoxId, connectedAt, true);
    }

    /**
     * Method to be called when a connection is closed
     */
    public void onConnectionClosed(String chargeBoxId, DateTime disconnectedAt, String reason) {
        log.warn("CONNECTION CLOSED: {} at {} | Reason: {}", chargeBoxId, disconnectedAt, reason);
        ConnectionStats stats = connectionHistory.get(chargeBoxId);
        if (stats != null) {
            updateConnectionStats(chargeBoxId, disconnectedAt, false);
        }
    }
}
