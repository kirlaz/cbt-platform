package com.cbt.platform.progress.repository;

import com.cbt.platform.progress.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserProgress entity
 */
@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, UUID> {

    /**
     * Find user progress by user ID and course ID
     */
    Optional<UserProgress> findByUserIdAndCourseId(UUID userId, UUID courseId);

    /**
     * Find all progress records for a user
     */
    List<UserProgress> findByUserId(UUID userId);

    /**
     * Find all progress records for a course
     */
    List<UserProgress> findByCourseId(UUID courseId);

    /**
     * Check if user has started a course
     */
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);
}
