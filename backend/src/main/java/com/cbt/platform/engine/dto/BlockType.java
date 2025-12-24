package com.cbt.platform.engine.dto;

/**
 * Types of blocks in a CBT course scenario
 * Each block type has its own handler and behavior
 */
public enum BlockType {
    /**
     * Static content block - displays text/images without user interaction
     */
    STATIC,

    /**
     * Text input block - collects text input from user
     */
    INPUT,

    /**
     * Slider input block - collects numeric value via slider
     */
    SLIDER,

    /**
     * Single select block - user selects one option from multiple choices
     */
    SINGLE_SELECT,

    /**
     * Multi select block - user selects multiple options
     */
    MULTI_SELECT,

    /**
     * LLM conversation block - interactive chat with Claude AI
     */
    LLM_CONVERSATION,

    /**
     * LLM response block - generated response based on user data
     */
    LLM_RESPONSE,

    /**
     * Exercise block - interactive CBT exercise (breathing, grounding, etc.)
     */
    EXERCISE,

    /**
     * Visualization block - data visualization (charts, progress graphs)
     */
    VISUALIZATION,

    /**
     * Calculation block - performs calculations on user data
     */
    CALCULATION,

    /**
     * Session complete block - marks session as completed
     */
    SESSION_COMPLETE,

    /**
     * Paywall block - prompts user to subscribe to continue
     */
    PAYWALL
}
