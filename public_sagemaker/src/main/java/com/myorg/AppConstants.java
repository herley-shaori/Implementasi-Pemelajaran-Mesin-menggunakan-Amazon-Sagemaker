package com.myorg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class AppConstants {

    private static final String ENV_FILE_PATH = "src/main/resources/env.json";
    public static final String VPC_CIDR = getValueFromJson("vpc_cidr");
    public static final String REGION = getValueFromJson("region");
    public static final String PROJECT_DESCRIPTION = getValueFromJson("project_description");

    private static String getValueFromJson(String key) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(new File(ENV_FILE_PATH));
            JsonNode valueNode = rootNode.get(key);
            if (valueNode != null) {
                return valueNode.asText();
            } else {
                System.err.println("Key '" + key + "' not found in env.json");
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error reading env.json: " + e.getMessage());
            return null;
        }
    }
}