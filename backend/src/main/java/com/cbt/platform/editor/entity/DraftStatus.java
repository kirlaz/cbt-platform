package com.cbt.platform.editor.entity;

/**
 * Status of a scenario draft in the editing workflow
 */
public enum DraftStatus {
    /**
     * Draft is being edited
     */
    DRAFT,

    /**
     * Draft is being validated
     */
    VALIDATING,

    /**
     * Draft passed validation and ready for publication
     */
    READY,

    /**
     * Draft has been published to a Course
     */
    PUBLISHED
}
