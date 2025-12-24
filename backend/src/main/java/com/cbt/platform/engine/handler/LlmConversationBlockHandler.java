package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.cbt.platform.llm.dto.LlmMessage;
import com.cbt.platform.llm.dto.LlmResponse;
import com.cbt.platform.llm.service.LlmService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for LLM_CONVERSATION blocks
 * Interactive chat with LLM (Claude, GPT, etc.)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LlmConversationBlockHandler implements BlockHandler {

    private final LlmService llmService;
    private final ObjectMapper objectMapper;

    @Override
    public BlockType getBlockType() {
        return BlockType.LLM_CONVERSATION;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing LLM_CONVERSATION block: {}", blockId);

        if (!llmService.isAvailable()) {
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.LLM_CONVERSATION)
                    .content(blockData)
                    .requiresInput(true)
                    .isComplete(false)
                    .error("LLM service is not configured")
                    .updatedUserData(userData)
                    .build();
        }

        // First time - show initial prompt
        if (userInput == null || userInput.isNull()) {
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.LLM_CONVERSATION)
                    .content(blockData)
                    .requiresInput(true)
                    .isComplete(false)
                    .updatedUserData(userData)
                    .build();
        }

        try {
            // Get user message from input
            String userMessage = userInput.get("message").asText();

            // Get conversation history from userData
            String conversationKey = "conversation_" + blockId;
            List<LlmMessage> conversationHistory = loadConversationHistory(userData, conversationKey);

            // Add user message to history
            conversationHistory.add(LlmMessage.user(userMessage));

            // Get system prompt
            String systemPrompt = blockData.has("system_prompt") ?
                    blockData.get("system_prompt").asText() : "";

            // Send to LLM
            LlmResponse llmResponse = llmService.sendConversation(
                    systemPrompt,
                    conversationHistory,
                    userData
            );

            // Add assistant response to history
            conversationHistory.add(LlmMessage.assistant(llmResponse.content()));

            // Update userData with conversation history
            ObjectNode updatedUserData = userData.deepCopy();
            ArrayNode historyArray = objectMapper.createArrayNode();
            for (LlmMessage msg : conversationHistory) {
                ObjectNode msgNode = objectMapper.createObjectNode();
                msgNode.put("role", msg.role());
                msgNode.put("content", msg.content());
                historyArray.add(msgNode);
            }
            updatedUserData.set(conversationKey, historyArray);

            // Create response content
            ObjectNode content = objectMapper.createObjectNode();
            content.put("type", "llm_conversation");
            content.put("message", llmResponse.content());
            content.put("model", llmResponse.model());
            content.set("history", historyArray);

            log.debug("LLM conversation: {} messages, {} tokens",
                    conversationHistory.size(), llmResponse.tokensUsed());

            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.LLM_CONVERSATION)
                    .content(content)
                    .requiresInput(true) // Continue conversation
                    .isComplete(false)   // Can continue chatting
                    .updatedUserData(updatedUserData)
                    .build();

        } catch (Exception e) {
            log.error("Error in LLM conversation", e);
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.LLM_CONVERSATION)
                    .content(blockData)
                    .requiresInput(true)
                    .isComplete(false)
                    .error("Failed to process conversation: " + e.getMessage())
                    .updatedUserData(userData)
                    .build();
        }
    }

    /**
     * Load conversation history from userData
     */
    private List<LlmMessage> loadConversationHistory(JsonNode userData, String key) {
        List<LlmMessage> history = new ArrayList<>();

        if (userData.has(key)) {
            JsonNode historyNode = userData.get(key);
            if (historyNode.isArray()) {
                for (JsonNode msgNode : historyNode) {
                    String role = msgNode.get("role").asText();
                    String content = msgNode.get("content").asText();
                    history.add(new LlmMessage(role, content));
                }
            }
        }

        return history;
    }
}
