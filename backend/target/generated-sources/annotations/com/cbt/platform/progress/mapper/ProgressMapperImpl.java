package com.cbt.platform.progress.mapper;

import com.cbt.platform.progress.dto.ProgressResponse;
import com.cbt.platform.progress.dto.UpdateProgressRequest;
import com.cbt.platform.progress.entity.UserProgress;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-23T22:05:05+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class ProgressMapperImpl implements ProgressMapper {

    @Override
    public ProgressResponse toResponse(UserProgress progress) {
        if ( progress == null ) {
            return null;
        }

        UUID id = null;
        UUID userId = null;
        UUID courseId = null;
        String currentSessionId = null;
        Integer currentBlockIndex = null;
        JsonNode userData = null;
        List<String> completedSessions = null;
        List<String> completedBlocks = null;
        Integer completionPercentage = null;
        boolean isCompleted = false;
        LocalDateTime startedAt = null;
        LocalDateTime completedAt = null;
        LocalDateTime lastActivityAt = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = progress.getId();
        userId = progress.getUserId();
        courseId = progress.getCourseId();
        currentSessionId = progress.getCurrentSessionId();
        currentBlockIndex = progress.getCurrentBlockIndex();
        userData = progress.getUserData();
        List<String> list = progress.getCompletedSessions();
        if ( list != null ) {
            completedSessions = new ArrayList<String>( list );
        }
        List<String> list1 = progress.getCompletedBlocks();
        if ( list1 != null ) {
            completedBlocks = new ArrayList<String>( list1 );
        }
        completionPercentage = progress.getCompletionPercentage();
        if ( progress.getIsCompleted() != null ) {
            isCompleted = progress.getIsCompleted();
        }
        startedAt = progress.getStartedAt();
        completedAt = progress.getCompletedAt();
        lastActivityAt = progress.getLastActivityAt();
        createdAt = progress.getCreatedAt();
        updatedAt = progress.getUpdatedAt();

        ProgressResponse progressResponse = new ProgressResponse( id, userId, courseId, currentSessionId, currentBlockIndex, userData, completedSessions, completedBlocks, completionPercentage, isCompleted, startedAt, completedAt, lastActivityAt, createdAt, updatedAt );

        return progressResponse;
    }

    @Override
    public void updateEntity(UserProgress progress, UpdateProgressRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.currentSessionId() != null ) {
            progress.setCurrentSessionId( request.currentSessionId() );
        }
        if ( request.currentBlockIndex() != null ) {
            progress.setCurrentBlockIndex( request.currentBlockIndex() );
        }
        if ( request.userData() != null ) {
            progress.setUserData( request.userData() );
        }
        if ( request.completionPercentage() != null ) {
            progress.setCompletionPercentage( request.completionPercentage() );
        }
    }
}
