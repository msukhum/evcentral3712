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
package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.ChargePoint.Overview;
import de.rwth.idsg.steve.service.ChargeBoxDescriptionService;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import de.rwth.idsg.steve.utils.ConnectorStatusFilter;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 *
 */
@Controller
@RequestMapping(value = "/manager", method = RequestMethod.GET)
public class HomeController {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private ChargePointHelperService chargePointHelperService;

    private static final String PARAMS = "params";
    // private static final String PARAMS2 = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String HOME_PREFIX = "/dashboard";

    private static final String OCPP_JSON_STATUS = HOME_PREFIX + "/ocppJsonStatus";
    private static final String CONNECTOR_STATUS_PATH = HOME_PREFIX + "/connectorStatus";
    private static final String CONNECTOR_STATUS_QUERY_PATH = HOME_PREFIX + "/connectorStatus/query";

    private final ChargeBoxDescriptionService descriptionService;

    public HomeController(ChargeBoxDescriptionService descriptionService,
                          ChargePointHelperService chargePointHelperService,
                          ChargePointRepository chargePointRepository) {
        this.descriptionService = descriptionService;
        this.chargePointHelperService = chargePointHelperService;
        this.chargePointRepository = chargePointRepository;
    }
    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(value = {"", HOME_PREFIX})
    public String getHome(Model model,
                          @ModelAttribute("chargePointParams") ChargePointQueryForm chargePointParams, 
                          @ModelAttribute("connectorStatusParams") ConnectorStatusForm connectorStatusParams) {
        
        // Fetch the charge point overview list
        List<ChargePoint.Overview> cpList = chargePointRepository.getOverview(chargePointParams);

        // Step 1: Retrieve the list of ConnectorStatus objects
        List<ConnectorStatus> latestList = chargePointHelperService.getChargePointConnectorStatus(connectorStatusParams);

        // Step 2: Filter the list as needed
        List<ConnectorStatus> filteredList = ConnectorStatusFilter.filterAndPreferZero(latestList);

        // Example usage: Retrieve description by ChargeBoxId
        for(ConnectorStatus cs : filteredList) {
            // System.out.println(cs.getConnectorId());
        	Optional<String> description = descriptionService.getDescriptionByChargeBoxId(cs.getChargeBoxId(), cpList, filteredList);
        	description.ifPresent(cs::setDescription);
        }

        // Add attributes to the model
        model.addAttribute("stats", chargePointHelperService.getStats());
        model.addAttribute("cpList", cpList);
        model.addAttribute("connectorStatusList", filteredList);

        return "dashboard";
    }
    

    @RequestMapping(value = CONNECTOR_STATUS_PATH)
    public String getConnectorStatus(Model model) {
        return getConnectorStatusQuery(new ConnectorStatusForm(), model);
    }

    @RequestMapping(value = CONNECTOR_STATUS_QUERY_PATH)
    public String getConnectorStatusQuery(@ModelAttribute(PARAMS) ConnectorStatusForm params, Model model) {
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
        model.addAttribute("statusValues", ConnectorStatusCountFilter.ALL_STATUS_VALUES);
        model.addAttribute(PARAMS, params);

        List<ConnectorStatus> latestList = chargePointHelperService.getChargePointConnectorStatus(params);
        List<ConnectorStatus> filteredList = ConnectorStatusFilter.filterAndPreferZero(latestList);
        model.addAttribute("connectorStatusList", filteredList);
        return "connectorStatus";
    }

    @RequestMapping(value = OCPP_JSON_STATUS)
    public String getOcppJsonStatus(Model model) {
        model.addAttribute("ocppJsonStatusList", chargePointHelperService.getOcppJsonStatus());
        return "ocppJsonStatus";
    }
    private void initList(Model model, ChargePointQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("cpList", chargePointRepository.getOverview(params));
        model.addAttribute("unknownList", chargePointHelperService.getUnknownChargePoints());
    }
}
