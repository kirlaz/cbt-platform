package com.cbt.platform.engine.handler;

import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for PAYWALL blocks
 * Prompts user to subscribe to continue (if not subscribed)
 */
@Component
@Slf4j
public class PaywallBlockHandler implements BlockHandler {

    @Override
    public BlockType getBlockType() {
        return BlockType.PAYWALL;
    }

    @Override
    public BlockResult handle(JsonNode blockData, JsonNode userData, JsonNode userInput) {
        String blockId = blockData.get("id").asText();
        log.debug("Processing PAYWALL block: {}", blockId);

        // TODO: Check user subscription status from userData or database
        // For now, always show paywall
        boolean hasSubscription = false;

        if (hasSubscription) {
            // User has subscription, skip paywall
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.PAYWALL)
                    .content(null)
                    .requiresInput(false)
                    .isComplete(true)
                    .updatedUserData(userData)
                    .build();
        } else {
            // Show paywall
            return BlockResult.builder()
                    .blockId(blockId)
                    .blockType(BlockType.PAYWALL)
                    .content(blockData) // Contains pricing info, subscribe button
                    .requiresInput(true) // Wait for subscription
                    .isComplete(false)
                    .updatedUserData(userData)
                    .build();
        }
    }
}
