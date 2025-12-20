package com.cbt.platform.user.repository;

import com.cbt.platform.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find all active users (not soft-deleted)
     */
    List<User> findAllByIsActiveTrueAndDeletedAtIsNull();

    /**
     * Find user by email and ensure not deleted
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.deletedAt IS NULL")
    Optional<User> findActiveByEmail(@Param("email") String email);

    /**
     * Find user by id and ensure not deleted
     */
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<User> findActiveById(@Param("id") UUID id);

    /**
     * Soft delete user by setting deletedAt timestamp
     */
    @Modifying
    @Query("UPDATE User u SET u.deletedAt = CURRENT_TIMESTAMP, u.isActive = false WHERE u.id = :id")
    void softDelete(@Param("id") UUID id);
}
