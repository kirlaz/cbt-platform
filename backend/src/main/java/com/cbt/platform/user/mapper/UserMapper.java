package com.cbt.platform.user.mapper;

import com.cbt.platform.user.dto.RegisterRequest;
import com.cbt.platform.user.dto.UpdateProfileRequest;
import com.cbt.platform.user.dto.UserResponse;
import com.cbt.platform.user.entity.User;
import org.mapstruct.*;

/**
 * MapStruct mapper for User entity and DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Convert User entity to UserResponse DTO
     */
    UserResponse toResponse(User user);

    /**
     * Convert RegisterRequest to User entity
     * Note: passwordHash and other fields should be set separately
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(RegisterRequest request);

    /**
     * Update existing User entity from UpdateProfileRequest
     * Only updates non-null fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(@MappingTarget User user, UpdateProfileRequest request);
}
