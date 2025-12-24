package com.cbt.platform.editor.mapper;

import com.cbt.platform.editor.dto.BlockTemplateResponse;
import com.cbt.platform.editor.dto.CreateBlockTemplateRequest;
import com.cbt.platform.editor.dto.UpdateBlockTemplateRequest;
import com.cbt.platform.editor.entity.BlockTemplate;
import com.fasterxml.jackson.databind.JsonNode;
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
public class BlockTemplateMapperImpl implements BlockTemplateMapper {

    @Override
    public BlockTemplateResponse toResponse(BlockTemplate template) {
        if ( template == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String description = null;
        String blockType = null;
        String category = null;
        JsonNode blockJson = null;
        boolean isSystem = false;
        boolean isActive = false;
        Integer usageCount = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = template.getId();
        name = template.getName();
        description = template.getDescription();
        blockType = template.getBlockType();
        category = template.getCategory();
        blockJson = template.getBlockJson();
        if ( template.getIsSystem() != null ) {
            isSystem = template.getIsSystem();
        }
        if ( template.getIsActive() != null ) {
            isActive = template.getIsActive();
        }
        usageCount = template.getUsageCount();
        createdAt = template.getCreatedAt();
        updatedAt = template.getUpdatedAt();

        BlockTemplateResponse blockTemplateResponse = new BlockTemplateResponse( id, name, description, blockType, category, blockJson, isSystem, isActive, usageCount, createdAt, updatedAt );

        return blockTemplateResponse;
    }

    @Override
    public BlockTemplate toEntity(CreateBlockTemplateRequest request) {
        if ( request == null ) {
            return null;
        }

        BlockTemplate.BlockTemplateBuilder blockTemplate = BlockTemplate.builder();

        blockTemplate.name( request.name() );
        blockTemplate.description( request.description() );
        blockTemplate.blockType( request.blockType() );
        blockTemplate.category( request.category() );
        blockTemplate.blockJson( request.blockJson() );

        return blockTemplate.build();
    }

    @Override
    public void updateEntity(BlockTemplate template, UpdateBlockTemplateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.name() != null ) {
            template.setName( request.name() );
        }
        if ( request.description() != null ) {
            template.setDescription( request.description() );
        }
        if ( request.blockType() != null ) {
            template.setBlockType( request.blockType() );
        }
        if ( request.category() != null ) {
            template.setCategory( request.category() );
        }
        if ( request.blockJson() != null ) {
            template.setBlockJson( request.blockJson() );
        }
        if ( request.isActive() != null ) {
            template.setIsActive( request.isActive() );
        }
    }
}
