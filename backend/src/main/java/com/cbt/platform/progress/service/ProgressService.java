package com.cbt.platform.progress.service;

import com.cbt.platform.progress.dto.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for user progress management operations
 */
public interface ProgressService {

    /**
     * Start a new course for a user
     * Creates initial UserProgress record with empty userData
     *
     * @param userId  user ID
     * @param request course start request
     * @return created progress
     */
    ProgressResponse startCourse(UUID userId, StartCourseRequest request);

    /**
     * Get user's progress in a course
     *
     * @param userId   user ID
     * @param courseId course ID
     * @return progress data
     */
    ProgressResponse getProgress(UUID userId, UUID courseId);

    /**
     * Get user's progress by progress ID
     *
     * @param progressId progress ID
     * @return progress data
     */
    ProgressResponse getProgressById(UUID progressId);

    /**
     * Get all progress records for a user
     *
     * @param userId user ID
     * @return list of progress records
     */
    List<ProgressResponse> getAllProgressForUser(UUID userId);

    /**
     * Get active (incomplete) courses for a user
     *
     * @param userId user ID
     * @return list of active progress
     */
    List<ProgressResponse> getActiveProgressForUser(UUID userId);

    /**
     * Update user's progress (position, completion, etc.)
     *
     * @param userId   user ID
     * @param courseId course ID
     * @param request  update data
     * @return updated progress
     */
    ProgressResponse updateProgress(UUID userId, UUID courseId, UpdateProgressRequest request);

    /**
     * Update user data (merge or replace)
     *
     * @param userId   user ID
     * @param courseId course ID
     * @param request  user data update
     * @return updated progress
     */
    ProgressResponse updateUserData(UUID userId, UUID courseId, UpdateUserDataRequest request);

    /**
     * Mark a session as completed
     *
     * @param userId   user ID
     * @param courseId course ID
     * @param request  session completion data
     * @return updated progress
     */
    ProgressResponse completeSession(UUID userId, UUID courseId, CompleteSessionRequest request);

    /**
     * Mark a block as completed
     *
     * @param userId    user ID
     * @param courseId  course ID
     * @param sessionId session ID
     * @param blockId   block ID
     * @return updated progress
     */
    ProgressResponse completeBlock(UUID userId, UUID courseId, String sessionId, String blockId);

    /**
     * Mark entire course as completed
     *
     * @param userId   user ID
     * @param courseId course ID
     * @return updated progress
     */
    ProgressResponse completeCourse(UUID userId, UUID courseId);

    /**
     * Delete user progress (for testing or reset)
     *
     * @param userId   user ID
     * @param courseId course ID
     */
    void deleteProgress(UUID userId, UUID courseId);
}
