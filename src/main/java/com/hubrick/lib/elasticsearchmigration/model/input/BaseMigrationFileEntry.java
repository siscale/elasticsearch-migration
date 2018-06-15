/**
 * Copyright (C) 2018 Etaia AS (oss@hubrick.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hubrick.lib.elasticsearchmigration.model.input;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateIndexMigrationFileEntry.class, name = "CREATE_INDEX"),
        @JsonSubTypes.Type(value = DeleteIndexMigrationFileEntry.class, name = "DELETE_INDEX"),
        @JsonSubTypes.Type(value = CreateOrUpdateIndexTemplateMigrationFileEntry.class, name = "CREATE_OR_UPDATE_INDEX_TEMPLATE"),
        @JsonSubTypes.Type(value = DeleteIndexTemplateMigrationFileEntry.class, name = "DELETE_INDEX_TEMPLATE"),
        @JsonSubTypes.Type(value = UpdateMappingMigrationFileEntry.class, name = "UPDATE_MAPPING"),
        @JsonSubTypes.Type(value = IndexDocumentMigrationFileEntry.class, name = "INDEX_DOCUMENT"),
        @JsonSubTypes.Type(value = DeleteDocumentMigrationFileEntry.class, name = "DELETE_DOCUMENT"),
        @JsonSubTypes.Type(value = UpdateDocumentMigrationFileEntry.class, name = "UPDATE_DOCUMENT")
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true, property = "type")
public abstract class BaseMigrationFileEntry {
    private MigrationType type;
}
