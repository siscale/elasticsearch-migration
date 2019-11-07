/**
 * Copyright (C) 2019 Emir Dizdarevic (4640075+eemmiirr@users.noreply.github.com)
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
package com.github.eemmiirr.lib.elasticsearchmigration.service.impl;

import com.github.eemmiirr.lib.elasticsearchmigration.exception.*;
import com.github.eemmiirr.lib.elasticsearchmigration.model.input.*;
import java.net.*;

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
                "8a2f6654cb772e142e0d7f5d03b15449cdfffd6428567be1c2018fbbf8faf58b"
        ));
    }

    @Test(expected = InvalidSchemaException.class)
    public void parseFailure() throws URISyntaxException {
        final YamlParser yamlParser = new YamlParser();
        yamlParser.parse("failure.yaml");
    }
}
