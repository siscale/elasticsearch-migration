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
package com.hubrick.lib.elasticsearchmigration.service.impl;

import com.hubrick.lib.elasticsearchmigration.exception.InvalidSchemaException;
import com.hubrick.lib.elasticsearchmigration.model.input.ChecksumedMigrationFile;
import com.hubrick.lib.elasticsearchmigration.model.input.CreateIndexMigrationFileEntry;
import com.hubrick.lib.elasticsearchmigration.model.input.CreateOrUpdateIndexTemplateMigrationFileEntry;
import com.hubrick.lib.elasticsearchmigration.model.input.DeleteDocumentMigrationFileEntry;
import com.hubrick.lib.elasticsearchmigration.model.input.DeleteIndexMigrationFileEntry;
import com.hubrick.lib.elasticsearchmigration.model.input.DeleteIndexTemplateMigrationFileEntry;
import com.hubrick.lib.elasticsearchmigration.model.input.IndexDocumentMigrationFileEntry;
import com.hubrick.lib.elasticsearchmigration.model.input.UpdateDocumentMigrationFileEntry;
import com.hubrick.lib.elasticsearchmigration.model.input.UpdateMappingMigrationFileEntry;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

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
                instanceOf(UpdateMappingMigrationFileEntry.class),
                instanceOf(IndexDocumentMigrationFileEntry.class),
                instanceOf(UpdateDocumentMigrationFileEntry.class),
                instanceOf(DeleteDocumentMigrationFileEntry.class),
                instanceOf(DeleteIndexTemplateMigrationFileEntry.class),
                instanceOf(DeleteIndexMigrationFileEntry.class)
        ));

        assertThat(checksumedMigrationFile.getSha256Checksums(), containsInAnyOrder(
                "53d16871b446d1f5db362f74c2f3b4a211e568b11736c6c1816a3d0107baa445",
                "8da26f8be997e97f320e8a63ffcc67d302c548e78606509f773b7fab5ffd920e"
        ));
    }

    @Test(expected = InvalidSchemaException.class)
    public void parseFailure() throws URISyntaxException {
        final YamlParser yamlParser = new YamlParser();
        yamlParser.parse("failure.yaml");
    }
}
