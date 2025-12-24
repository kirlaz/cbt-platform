package com.cbt.platform.llm.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Builds prompts from templates and user data
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromptBuilder {

    private final TemplateEngine templateEngine;

    /**
     * Build prompt from template, resolving {{userData.field}} placeholders
     *
     * @param template Prompt template with {{placeholders}}
     * @param userData User data for template resolution
     * @return Resolved prompt
     */
    public String buildPrompt(String template, JsonNode userData) {
        if (template == null || template.isEmpty()) {
            log.warn("Empty template provided");
            return "";
        }

        return templateEngine.resolve(template, userData);
    }

    /**
     * Build system prompt combining global config + block config
     *
     * @param globalSystemPrompt System prompt from course global config
     * @param blockSystemPrompt  System prompt from block config (optional)
     * @param userData           User data for template resolution
     * @return Combined system prompt
     */
    public String buildSystemPrompt(
            String globalSystemPrompt,
            String blockSystemPrompt,
            JsonNode userData
    ) {
        StringBuilder prompt = new StringBuilder();

        // Add global prompt
        if (globalSystemPrompt != null && !globalSystemPrompt.isEmpty()) {
            String resolvedGlobal = templateEngine.resolve(globalSystemPrompt, userData);
            prompt.append(resolvedGlobal);
        }

        // Add block-specific prompt
        if (blockSystemPrompt != null && !blockSystemPrompt.isEmpty()) {
            if (prompt.length() > 0) {
                prompt.append("\n\n");
            }
            String resolvedBlock = templateEngine.resolve(blockSystemPrompt, userData);
            prompt.append(resolvedBlock);
        }

        return prompt.toString();
    }

    /**
     * Build user context from userData
     * Creates a summary of user information for inclusion in prompts
     *
     * @param userData User data
     * @return Formatted user context
     */
    public String buildUserContext(JsonNode userData) {
        if (userData == null || userData.isNull()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        context.append("User Information:\n");

        // Add common fields if present
        addFieldIfPresent(context, "Name", userData, "name");
        addFieldIfPresent(context, "Primary Issue", userData, "primary_issue");
        addFieldIfPresent(context, "Anxiety Level", userData, "gad7_score");

        return context.toString();
    }

    /**
     * Add field to context if present in userData
     */
    private void addFieldIfPresent(
            StringBuilder context,
            String label,
            JsonNode userData,
            String fieldName
    ) {
        if (userData.has(fieldName)) {
            JsonNode field = userData.get(fieldName);
            if (!field.isNull()) {
                context.append("- ")
                        .append(label)
                        .append(": ")
                        .append(field.asText())
                        .append("\n");
            }
        }
    }
}
