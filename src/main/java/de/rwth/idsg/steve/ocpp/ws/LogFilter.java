package de.rwth.idsg.steve.ocpp.ws;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.rwth.idsg.steve.utils.LogFileRetriever;

@Controller
// @RequestMapping("")
public class LogFilter {

    @GetMapping("/log/charger/{id}")
    public String filterLogMessages(@PathVariable("id") String chargeBoxId, Model model) {
        String filePath = LogFileRetriever.INSTANCE.getLogFilePathOrErrorMessage();
        List<String> filteredMessages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Normalize line for searching
                String normalizedLine = line.replaceAll("\\s+", "").toLowerCase();
                String normalizedChargeBoxId = chargeBoxId.replaceAll("\\s+", "").toLowerCase();

                // Check if the line matches the search criteria
                if (normalizedLine.contains("[info]") &&
                    normalizedLine.contains("ws.websocketlogger") &&
                    normalizedLine.contains(normalizedChargeBoxId) &&
                    (normalizedLine.contains("sending:") || normalizedLine.contains("received:"))) {

                    // Remove the class name, session ID, and strings in parentheses
                    String cleanedLine = line
                        .replaceAll("de\\.rwth\\.idsg\\.steve\\.ocpp\\.ws\\.WebSocketLogger", "") // Remove class name
                        .replaceAll("sessionId=[^\\s]*", "") // Remove session ID
                        .replaceAll("\\(qtp[\\w-]+\\)", "") // Remove strings like (qtp905080434-45)
                        .trim();

                    filteredMessages.add(cleanedLine); // Add cleaned line to the filtered list
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the filtered messages to the model for JSP rendering
        model.addAttribute("filteredMessages", filteredMessages);

        // Return the JSP page name
        return "log-results";
    }

}
