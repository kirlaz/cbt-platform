package com.cbt.platform.course.repository;

import com.cbt.platform.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Course entity operations
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    /**
     * Find course by slug
     */
    Optional<Course> findBySlug(String slug);

    /**
     * Find all active and published courses (not soft-deleted)
     */
    @Query("SELECT c FROM Course c WHERE c.isActive = true AND c.isPublished = true AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<Course> findAllActiveAndPublished();

    /**
     * Find all active courses (not soft-deleted)
     */
    @Query("SELECT c FROM Course c WHERE c.isActive = true AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<Course> findAllActive();

    /**
     * Find courses by category
     */
    @Query("SELECT c FROM Course c WHERE c.category = :category AND c.isActive = true AND c.isPublished = true AND c.deletedAt IS NULL")
    List<Course> findByCategory(@Param("category") String category);

    /**
     * Find active course by slug
     */
    @Query("SELECT c FROM Course c WHERE c.slug = :slug AND c.deletedAt IS NULL")
    Optional<Course> findActiveBySlug(@Param("slug") String slug);

    /**
     * Check if course exists by slug
     */
    boolean existsBySlug(String slug);

    /**
     * Soft delete course
     */
    @Modifying
    @Query("UPDATE Course c SET c.deletedAt = CURRENT_TIMESTAMP, c.isActive = false WHERE c.id = :id")
    void softDelete(@Param("id") UUID id);
}
