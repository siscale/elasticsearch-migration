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

import com.quandoo.lib.elasticsearchmigration.exception.*;
import com.quandoo.lib.elasticsearchmigration.model.input.*;
import java.net.*;

import com.quandoo.lib.elasticsearchmigration.model.migration.UpdateIndexSettingsMigration;
import org.junit.*;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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

    @Test(expected = InvalidSchemaException.class)
    public void parseFailure() throws URISyntaxException {
        final YamlParser yamlParser = new YamlParser();
        yamlParser.parse("failure.yaml");
    }
}
