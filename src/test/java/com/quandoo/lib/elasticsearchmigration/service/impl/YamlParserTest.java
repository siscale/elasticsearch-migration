/**
 * Copyright (C) 2019 Quandoo GmbH (account.oss@quandoo.com)
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
package com.quandoo.lib.elasticsearchmigration.service.impl;

import com.quandoo.lib.elasticsearchmigration.exception.InvalidSchemaException;
import com.quandoo.lib.elasticsearchmigration.model.input.AliasesMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.ChecksumedMigrationFile;
import com.quandoo.lib.elasticsearchmigration.model.input.CreateIndexMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.CreateIngestPipelineMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.CreateOrUpdateIndexTemplateMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.DeleteDocumentMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.DeleteIndexMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.DeleteIndexTemplateMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.DeleteIngestPipelineMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.IndexDocumentMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.ReindexMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.UpdateDocumentMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.UpdateIndexSettingsMigrationFileEntry;
import com.quandoo.lib.elasticsearchmigration.model.input.UpdateMappingMigrationFileEntry;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class YamlParserTest {

    @Test
    public void parseSuccess() throws URISyntaxException {
        final YamlParser yamlParser = new YamlParser();
        final ChecksumedMigrationFile checksumedMigrationFile = yamlParser.parse("success.yaml");

        assertThat(checksumedMigrationFile.getMigrationFile().getMigrations(), contains(
                instanceOf(CreateIndexMigrationFileEntry.class),
                instanceOf(UpdateIndexSettingsMigrationFileEntry.class),
                instanceOf(CreateOrUpdateIndexTemplateMigrationFileEntry.class),
                instanceOf(CreateIngestPipelineMigrationFileEntry.class),
                instanceOf(UpdateMappingMigrationFileEntry.class),
                instanceOf(AliasesMigrationFileEntry.class),
                instanceOf(IndexDocumentMigrationFileEntry.class),
                instanceOf(UpdateDocumentMigrationFileEntry.class),
                instanceOf(DeleteDocumentMigrationFileEntry.class),
                instanceOf(ReindexMigrationFileEntry.class),
                instanceOf(DeleteIndexTemplateMigrationFileEntry.class),
                instanceOf(DeleteIndexMigrationFileEntry.class),
                instanceOf(DeleteIngestPipelineMigrationFileEntry.class)
        ));

        assertThat(checksumedMigrationFile.getSha256Checksum(), is(
                "7647553f6c5cfa37934c6f8ec06f0fe778921311b2318d53de449ad1627cc9f6"
        ));
    }

    @Test
    public void parseFailure() throws URISyntaxException {
        assertThrows(InvalidSchemaException.class, () -> {
            final YamlParser yamlParser = new YamlParser();
            yamlParser.parse("failure.yaml");
        });
    }
}
