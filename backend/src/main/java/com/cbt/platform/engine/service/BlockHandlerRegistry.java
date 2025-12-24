package com.cbt.platform.engine.service;

import com.cbt.platform.engine.dto.BlockType;
import com.cbt.platform.engine.handler.BlockHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for block handlers (Strategy pattern)
 * Maps BlockType to BlockHandler implementation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlockHandlerRegistry {

    private final List<BlockHandler> handlers;
    private final Map<BlockType, BlockHandler> handlerMap = new HashMap<>();

    /**
     * Register all handlers after bean construction
     * Spring auto-injects all BlockHandler beans via constructor
     */
    @PostConstruct
    public void registerHandlers() {
        log.info("Registering block handlers...");

        for (BlockHandler handler : handlers) {
            BlockType blockType = handler.getBlockType();
            handlerMap.put(blockType, handler);
            log.debug("Registered handler for block type: {}", blockType);
        }

        log.info("Registered {} block handlers", handlerMap.size());

        // Validate all block types have handlers
        for (BlockType blockType : BlockType.values()) {
            if (!handlerMap.containsKey(blockType)) {
                log.warn("No handler registered for block type: {}", blockType);
            }
        }
    }

    /**
     * Get handler for a block type
     *
     * @param blockType Block type
     * @return Handler for this block type
     * @throws IllegalArgumentException if no handler found
     */
    public BlockHandler getHandler(BlockType blockType) {
        BlockHandler handler = handlerMap.get(blockType);
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for block type: " + blockType);
        }
        return handler;
    }

    /**
     * Check if handler exists for a block type
     */
    public boolean hasHandler(BlockType blockType) {
        return handlerMap.containsKey(blockType);
    }
}
