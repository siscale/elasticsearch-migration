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

import static org.hamcrest.Matchers.*;
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

        assertThat(checksumedMigrationFile.getSha256Checksum(), is(
                "809ea86173d206af07cf1d6403524ff9b7ad41534b268a93c13c14dde6d952ba"
        ));
    }

    @Test(expected = InvalidSchemaException.class)
    public void parseFailure() throws URISyntaxException {
        final YamlParser yamlParser = new YamlParser();
        yamlParser.parse("failure.yaml");
    }
}
