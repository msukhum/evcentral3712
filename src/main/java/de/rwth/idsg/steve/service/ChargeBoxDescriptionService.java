package de.rwth.idsg.steve.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;

@Service
public class ChargeBoxDescriptionService {
    public Optional<String> getDescriptionByChargeBoxId(
            String chargeBoxId, 
            List<ChargePoint.Overview> chargePointOverviews, 
            List<ConnectorStatus> connectorStatuses) {

        // Guard clause for null inputs
        if (chargeBoxId == null || chargePointOverviews == null || connectorStatuses == null) {
            return Optional.empty();
        }

        // Find a match in ChargePoint.Overview by chargeBoxId
        Optional<String> descriptionFromOverview = chargePointOverviews.stream()
            .filter(cp -> chargeBoxId.equals(cp.getChargeBoxId())) // Null-safe comparison
            .map(ChargePoint.Overview::getDescription)
            .filter(Objects::nonNull) // Avoid null descriptions
            .findFirst();

        // If not found in ChargePoint.Overview, search in ConnectorStatus
        Optional<String> descriptionFromConnector = connectorStatuses.stream()
            .filter(cs -> chargeBoxId.equals(cs.getChargeBoxId())) // Null-safe comparison
            .map(ConnectorStatus::getDescription)
            .filter(Objects::nonNull) // Avoid null descriptions
            .findFirst();

        // Combine results
        return descriptionFromOverview.or(() -> descriptionFromConnector);
    }
}


