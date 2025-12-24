package com.cbt.platform.user.mapper;

import com.cbt.platform.user.dto.RegisterRequest;
import com.cbt.platform.user.dto.UpdateProfileRequest;
import com.cbt.platform.user.dto.UserResponse;
import com.cbt.platform.user.entity.User;
import com.cbt.platform.user.entity.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-23T22:05:05+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String email = null;
        String name = null;
        String phone = null;
        String timezone = null;
        String preferredLanguage = null;
        UserRole role = null;
        boolean isActive = false;
        LocalDateTime createdAt = null;
        LocalDateTime lastLoginAt = null;

        id = user.getId();
        email = user.getEmail();
        name = user.getName();
        phone = user.getPhone();
        timezone = user.getTimezone();
        preferredLanguage = user.getPreferredLanguage();
        role = user.getRole();
        if ( user.getIsActive() != null ) {
            isActive = user.getIsActive();
        }
        createdAt = user.getCreatedAt();
        lastLoginAt = user.getLastLoginAt();

        UserResponse userResponse = new UserResponse( id, email, name, phone, timezone, preferredLanguage, role, isActive, createdAt, lastLoginAt );

        return userResponse;
    }

    @Override
    public User toEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( request.email() );
        user.name( request.name() );
        user.phone( request.phone() );
        user.timezone( request.timezone() );
        user.preferredLanguage( request.preferredLanguage() );

        return user.build();
    }

    @Override
    public void updateEntity(User user, UpdateProfileRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.name() != null ) {
            user.setName( request.name() );
        }
        if ( request.phone() != null ) {
            user.setPhone( request.phone() );
        }
        if ( request.timezone() != null ) {
            user.setTimezone( request.timezone() );
        }
        if ( request.preferredLanguage() != null ) {
            user.setPreferredLanguage( request.preferredLanguage() );
        }
    }
}
