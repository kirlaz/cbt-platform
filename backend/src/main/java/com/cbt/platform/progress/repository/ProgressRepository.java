package com.cbt.platform.progress.repository;

import com.cbt.platform.progress.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserProgress entity operations
 */
@Repository
public interface ProgressRepository extends JpaRepository<UserProgress, UUID> {

    /**
     * Find progress by user ID and course ID
     */
    Optional<UserProgress> findByUserIdAndCourseId(UUID userId, UUID courseId);

    /**
     * Find all progress records for a user
     */
    List<UserProgress> findByUserId(UUID userId);

    /**
     * Find all users taking a specific course
     */
    List<UserProgress> findByCourseId(UUID courseId);

    /**
     * Find all active (incomplete) progress for a user
     */
    @Query("SELECT p FROM UserProgress p WHERE p.userId = :userId AND p.isCompleted = false ORDER BY p.lastActivityAt DESC")
    List<UserProgress> findActiveProgressByUserId(@Param("userId") UUID userId);

    /**
     * Find all completed progress for a user
     */
    @Query("SELECT p FROM UserProgress p WHERE p.userId = :userId AND p.isCompleted = true ORDER BY p.completedAt DESC")
    List<UserProgress> findCompletedProgressByUserId(@Param("userId") UUID userId);

    /**
     * Check if user has started a course
     */
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);

    /**
     * Count active courses for a user
     */
    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.userId = :userId AND p.isCompleted = false")
    long countActiveByUserId(@Param("userId") UUID userId);

    /**
     * Find users who completed a specific course
     */
    @Query("SELECT p FROM UserProgress p WHERE p.courseId = :courseId AND p.isCompleted = true")
    List<UserProgress> findCompletedByCourseId(@Param("courseId") UUID courseId);

    /**
     * Find progress with completion percentage above threshold
     */
    @Query("SELECT p FROM UserProgress p WHERE p.userId = :userId AND p.completionPercentage >= :threshold")
    List<UserProgress> findByUserIdWithMinCompletion(@Param("userId") UUID userId, @Param("threshold") Integer threshold);
}
