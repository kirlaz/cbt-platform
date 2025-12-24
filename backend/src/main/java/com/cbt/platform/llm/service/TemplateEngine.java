package com.cbt.platform.llm.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Template engine for resolving {{userData.field}} placeholders
 * Supports:
 * - Simple fields: {{name}} → userData.name
 * - Nested fields: {{user.profile.age}} → userData.user.profile.age
 * - Array access: {{triggers[0]}} → userData.triggers[0]
 */
@Component
@Slf4j
public class TemplateEngine {

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

    /**
     * Resolve all {{placeholders}} in template using userData
     *
     * @param template Template string with {{placeholders}}
     * @param userData User data JSON
     * @return Resolved string
     */
    public String resolve(String template, JsonNode userData) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        if (userData == null || userData.isNull()) {
            log.warn("userData is null, cannot resolve template");
            return template;
        }

        Matcher matcher = TEMPLATE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1).trim();
            String value = resolveField(placeholder, userData);
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Resolve single field from userData
     * Examples:
     * - "name" → userData.get("name")
     * - "triggers[0]" → userData.get("triggers").get(0)
     * - "user.profile.age" → userData.get("user").get("profile").get("age")
     */
    private String resolveField(String field, JsonNode userData) {
        try {
            JsonNode current = userData;

            // Split by '.' for nested fields
            String[] parts = field.split("\\.");

            for (String part : parts) {
                // Check for array access: fieldName[index]
                if (part.contains("[")) {
                    int bracketIndex = part.indexOf('[');
                    String fieldName = part.substring(0, bracketIndex);
                    int arrayIndex = Integer.parseInt(
                            part.substring(bracketIndex + 1, part.length() - 1)
                    );

                    current = current.get(fieldName);
                    if (current == null || !current.isArray()) {
                        log.warn("Field {} is not an array", fieldName);
                        return "";
                    }

                    current = current.get(arrayIndex);
                } else {
                    current = current.get(part);
                }

                if (current == null || current.isNull()) {
                    log.debug("Field {} not found in userData", field);
                    return "";
                }
            }

            // Return text value
            if (current.isTextual()) {
                return current.asText();
            } else if (current.isNumber()) {
                return String.valueOf(current.asInt());
            } else if (current.isBoolean()) {
                return String.valueOf(current.asBoolean());
            } else {
                // Complex object/array - return JSON string
                return current.toString();
            }

        } catch (Exception e) {
            log.error("Error resolving field: {}", field, e);
            return "";
        }
    }

    /**
     * Check if string contains templates
     */
    public boolean hasTemplates(String text) {
        return text != null && TEMPLATE_PATTERN.matcher(text).find();
    }
}
