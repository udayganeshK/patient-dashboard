package com.storypublisher.patientdashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class LlmAgentService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String openAIApiKey;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ClaimService claimService;

    public Map<String, Object> answerQuestion(String question) {
        StringBuilder context = new StringBuilder();
        boolean handled = false;
        // Example rule: most expensive claim (global)
        if (question.toLowerCase().contains("most expensive claim")) {
            var claims = claimService.getAllClaims();
            var maxClaim = claims.stream().max(java.util.Comparator.comparingDouble(c -> c.getTotalCost())).orElse(null);
            if (maxClaim != null) {
                var patient = maxClaim.getPatient();
                context.append("Most expensive claim: $")
                    .append(String.format("%.2f", maxClaim.getTotalCost()))
                    .append(", Patient: ").append(patient.getName())
                    .append(", Birthplace: ").append(patient.getBirthPlace() != null ? patient.getBirthPlace() : "Unknown")
                    .append(", Hospital: ").append(maxClaim.getHospital() != null ? maxClaim.getHospital() : "Unknown")
                    .append("\n");
                handled = true;
            }
        }
        // Example rule: patient by name and dob
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("([A-Za-z ]+) born ([0-9]{4}-[0-9]{2}-[0-9]{2})");
        java.util.regex.Matcher m = p.matcher(question);
        if (m.find()) {
            String name = m.group(1).trim();
            String dob = m.group(2).trim();
            var patients = patientService.getAllPatients().stream()
                .filter(pt -> pt.getName() != null && pt.getName().equalsIgnoreCase(name))
                .filter(pt -> pt.getBirthDate() != null && pt.getBirthDate().toString().equals(dob))
                .toList();
            if (!patients.isEmpty()) {
                var patient = patients.get(0);
                var claims = claimService.getClaimsByPatient(patient);
                var maxClaim = claims.stream().max(java.util.Comparator.comparingDouble(c -> c.getTotalCost())).orElse(null);
                if (maxClaim != null) {
                    context.append("Patient: ").append(name)
                        .append(", DOB: ").append(dob)
                        .append(", Most expensive claim: $")
                        .append(String.format("%.2f", maxClaim.getTotalCost()))
                        .append(", Hospital: ").append(maxClaim.getHospital() != null ? maxClaim.getHospital() : "Unknown")
                        .append("\n");
                    handled = true;
                }
            }
        }
        // Rule: patient with highest total claim cost (highest claimer)
        if (question.toLowerCase().contains("highest claimer") || question.toLowerCase().contains("highest total claim") || question.toLowerCase().contains("most claims")) {
            var patients = patientService.getAllPatients();
            var patientTotals = new java.util.HashMap<Object, Double>();
            for (var patient : patients) {
                var claims = claimService.getClaimsByPatient(patient);
                double total = claims.stream().mapToDouble(c -> c.getTotalCost()).sum();
                patientTotals.put(patient, total);
            }
            var maxEntry = patientTotals.entrySet().stream().max(java.util.Map.Entry.comparingByValue()).orElse(null);
            if (maxEntry != null) {
                var patient = (com.storypublisher.patientdashboard.model.Patient) maxEntry.getKey();
                double total = maxEntry.getValue();
                context.append("Highest claimer: ")
                    .append(patient.getName())
                    .append(", DOB: ").append(patient.getBirthDate() != null ? patient.getBirthDate().toString() : "Unknown")
                    .append(", Total claim cost: $").append(String.format("%.2f", total))
                    .append(", Birthplace: ").append(patient.getBirthPlace() != null ? patient.getBirthPlace() : "Unknown")
                    .append(", Hospital: ");
                var claims = claimService.getClaimsByPatient(patient);
                var maxClaim = claims.stream().max(java.util.Comparator.comparingDouble(c -> c.getTotalCost())).orElse(null);
                context.append(maxClaim != null && maxClaim.getHospital() != null ? maxClaim.getHospital() : "Unknown");
                context.append("\n");
                handled = true;
            }
        }
        // Rule: hospital with most unique patients
        if (question.toLowerCase().contains("hospital has most patients") || question.toLowerCase().contains("most patients hospital") || question.toLowerCase().contains("hospital with most patients")) {
            var claims = claimService.getAllClaims();
            java.util.Map<String, java.util.Set<Long>> hospitalPatients = new java.util.HashMap<>();
            for (var claim : claims) {
                String hospital = claim.getHospital() != null ? claim.getHospital() : "Unknown";
                Long patientId = claim.getPatient() != null ? claim.getPatient().getId() : null;
                if (patientId != null) {
                    hospitalPatients.computeIfAbsent(hospital, k -> new java.util.HashSet<>()).add(patientId);
                }
            }
            String maxHospital = null;
            int maxCount = 0;
            for (var entry : hospitalPatients.entrySet()) {
                int count = entry.getValue().size();
                if (count > maxCount) {
                    maxCount = count;
                    maxHospital = entry.getKey();
                }
            }
            if (maxHospital != null) {
                context.append("Hospital with most patients: ")
                    .append(maxHospital)
                    .append(" (Unique patients: ")
                    .append(maxCount)
                    .append(")\n");
                handled = true;
            }
        }
        // Rule: top N hospitals with most patients, claims, and total cost
        java.util.regex.Pattern topHospitalsPattern = java.util.regex.Pattern.compile("top (\\d+) hospitals?", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher topHospitalsMatcher = topHospitalsPattern.matcher(question);
        if (topHospitalsMatcher.find()) {
            int topN = Integer.parseInt(topHospitalsMatcher.group(1));
            var claims = claimService.getAllClaims();
            java.util.Map<String, java.util.Set<Object>> hospitalPatients = new java.util.HashMap<>();
            java.util.Map<String, Integer> hospitalClaims = new java.util.HashMap<>();
            java.util.Map<String, Double> hospitalCosts = new java.util.HashMap<>();
            for (var claim : claims) {
                String hospital = claim.getHospital() != null ? claim.getHospital() : "Unknown";
                Object patient = claim.getPatient();
                hospitalPatients.computeIfAbsent(hospital, k -> new java.util.HashSet<>()).add(patient);
                hospitalClaims.put(hospital, hospitalClaims.getOrDefault(hospital, 0) + 1);
                hospitalCosts.put(hospital, hospitalCosts.getOrDefault(hospital, 0.0) + claim.getTotalCost());
            }
            var sortedHospitals = hospitalPatients.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .limit(topN)
                .toList();
            context.append("Top ").append(topN).append(" hospitals with most patients, claims, and total cost:\n");
            int rank = 1;
            for (var entry : sortedHospitals) {
                String hospital = entry.getKey();
                int patientCount = entry.getValue().size();
                int claimCount = hospitalClaims.getOrDefault(hospital, 0);
                double totalCost = hospitalCosts.getOrDefault(hospital, 0.0);
                context.append(rank).append(". ").append(hospital).append(":\n")
                    .append("   Number of patients: ").append(patientCount).append("\n")
                    .append("   Number of claims: ").append(claimCount).append("\n")
                    .append("   Total cost of claims: $").append(String.format("%.2f", totalCost)).append("\n");
                rank++;
            }
            handled = true;
        }
        // Fallback: global summary
        if (!handled) {
            long patientCount = patientService.getAllPatients().size();
            long claimCount = claimService.getAllClaims().size();
            double totalCost = claimService.getAllClaims().stream().mapToDouble(c -> c.getTotalCost()).sum();
            Map<String, Long> claimsByType = new HashMap<>();
            claimService.getAllClaims().forEach(c -> {
                String type = c.getClaimType() != null ? c.getClaimType() : "Unknown";
                claimsByType.put(type, claimsByType.getOrDefault(type, 0L) + 1);
            });
            context.append("Database summary:\n");
            context.append("Patients: ").append(patientCount).append("\n");
            context.append("Claims: ").append(claimCount).append("\n");
            context.append("Total claim cost: $").append(String.format("%.2f", totalCost)).append("\n");
            context.append("Claims by type: ").append(claimsByType.toString()).append("\n");
        }
        String prompt = "You are a medical analytics assistant. Use the following database context to answer the user's question.\n" + context + "Question: " + question;
        String openAIResponse = callOpenAI(prompt);
        Map<String, Object> response = new HashMap<>();
        response.put("answer", openAIResponse);
        response.put("context", context.toString());
        return response;
    }

    private String callOpenAI(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAIApiKey);
        try {
            // Build request body using Jackson for valid JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> bodyMap = new java.util.HashMap<>();
            bodyMap.put("model", "gpt-3.5-turbo");
            java.util.List<java.util.Map<String, String>> messages = new java.util.ArrayList<>();
            java.util.Map<String, String> userMsg = new java.util.HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);
            bodyMap.put("messages", messages);
            String body = mapper.writeValueAsString(bodyMap);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String respBody = response.getBody();
            if (respBody != null) {
                if (respBody.trim().startsWith("<")) {
                    System.err.println("OpenAI API returned HTML error: " + respBody);
                    return "Error: OpenAI API returned HTML error. Check your API key, endpoint, or network connectivity.";
                }
                try {
                    java.util.Map respMap = mapper.readValue(respBody, java.util.Map.class);
                    if (respMap.containsKey("choices")) {
                        Object choices = respMap.get("choices");
                        if (choices instanceof java.util.List && !((java.util.List) choices).isEmpty()) {
                            java.util.Map firstChoice = (java.util.Map) ((java.util.List) choices).get(0);
                            java.util.Map message = (java.util.Map) firstChoice.get("message");
                            return message.get("content").toString();
                        }
                    }
                } catch (Exception jsonEx) {
                    System.err.println("Failed to parse OpenAI response as JSON: " + respBody);
                    return "Error: OpenAI API response is not valid JSON.";
                }
            }
        } catch (Exception e) {
            System.err.println("Exception calling OpenAI: " + e.getMessage());
            return "Error calling OpenAI: " + e.getMessage();
        }
        return "No response from OpenAI.";
    }
}
