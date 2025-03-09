package com.myorg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppConstants {

    private static final String ENV_FILE_PATH = "src/main/resources/env.json";
    public static final String VPC_CIDR = getValueFromJson("vpc_cidr");
    public static final String REGION = getValueFromJson("region");
    public static final String PROJECT_DESCRIPTION = getValueFromJson("project_description");
    public static final Map<String, String> TAGS = getTagsFromJson();

    /**
     * Retrieves the value associated with the specified key from the JSON file.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key, or null if the key is not found or an error occurs
     */
    private static String getValueFromJson(String key) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Read the JSON file into a JsonNode
            JsonNode rootNode = mapper.readTree(new File(ENV_FILE_PATH));
            // Retrieve the value associated with the specified key
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

    /**
     * Retrieves the tags from the JSON file and returns them as a Map.
     *
     * @return a Map containing the tags from the JSON file, or an empty Map if the tags are not found or an error occurs
     */
    private static Map<String, String> getTagsFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> tagsMap = new HashMap<>();
        try {
            // Read the JSON file into a JsonNode
            JsonNode rootNode = mapper.readTree(new File(ENV_FILE_PATH));
            // Retrieve the tags node from the JSON
            JsonNode tagsNode = rootNode.get("tags");
            if (tagsNode != null && tagsNode.isObject()) {
                // Iterate over the fields of the tags node and populate the tagsMap
                tagsNode.fields().forEachRemaining(entry -> tagsMap.put(entry.getKey(), entry.getValue().asText()));
            } else {
                System.err.println("Tags not found or not an object in env.json");
            }
        } catch (IOException e) {
            System.err.println("Error reading env.json: " + e.getMessage());
        }
        return tagsMap;
    }
}