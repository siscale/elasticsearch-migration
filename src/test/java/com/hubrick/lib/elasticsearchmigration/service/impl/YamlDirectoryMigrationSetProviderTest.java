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

import com.google.common.collect.ImmutableSet;
import com.hubrick.lib.elasticsearchmigration.model.migration.CreateIndexMigration;
import com.hubrick.lib.elasticsearchmigration.model.migration.CreateOrUpdateIndexTemplateMigration;
import com.hubrick.lib.elasticsearchmigration.model.migration.DeleteDocumentMigration;
import com.hubrick.lib.elasticsearchmigration.model.migration.DeleteIndexMigration;
import com.hubrick.lib.elasticsearchmigration.model.migration.DeleteIndexTemplateMigration;
import com.hubrick.lib.elasticsearchmigration.model.migration.IndexDocumentMigration;
import com.hubrick.lib.elasticsearchmigration.model.migration.MigrationSet;
import com.hubrick.lib.elasticsearchmigration.model.migration.UpdateDocumentMigration;
import com.hubrick.lib.elasticsearchmigration.model.migration.UpdateMappingMigration;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class YamlDirectoryMigrationSetProviderTest {

    private YamlDirectoryMigrationSetProvider yamlDirectoryMigrationSetProvider;

    @Before
    public void setUp() throws Exception {
        yamlDirectoryMigrationSetProvider = new YamlDirectoryMigrationSetProvider();
    }

    @Test
    public void getMigrationSet() throws URISyntaxException {
        final MigrationSet migrationSet = yamlDirectoryMigrationSetProvider.getMigrationSet("com.hubrick.lib.elasticsearchmigration.service.impl");

        assertThat(migrationSet.getMigrations(), hasSize(3));
        assertThat(migrationSet.getMigrations().stream().map(e -> e.getMigrationMeta().getVersion()).collect(Collectors.toList()), contains(
                "1.0.0",
                "1.1.0",
                "1.1.1"
        ));
        assertThat(migrationSet.getMigrations().stream().map(e -> e.getMigrationMeta().getName()).collect(Collectors.toList()), contains(
                "migration_one",
                "migration_two",
                "migration_three"
        ));
        assertThat(migrationSet.getMigrations().stream().map(e -> e.getMigrationMeta().getSha256Checksum()).collect(Collectors.toList()), contains(
                "125a71e37ca846d26814ac8063831ab1c1fe19cf3a5e9d06916a8e8bace450c2",
                "653bff037b49688eca9e7d33312fc4eec12e48d701d037becb3ead5a874e4ecd",
                "f8dd60c73ae0b455724e38a4bf75c20a423c506731dd771bc81fd0981e42519c"
        ));
        assertThat(migrationSet.getMigrations().stream().flatMap(e -> e.getMigration().stream()).collect(Collectors.toList()), contains(
                new CreateIndexMigration("test_index", "{}"),
                new CreateOrUpdateIndexTemplateMigration("test_template", "{}"),
                new UpdateMappingMigration(ImmutableSet.of("test_index"), "test", "{}"),
                new IndexDocumentMigration("test_index", "test", Optional.of("1"), Optional.empty(), "{}"),
                new UpdateDocumentMigration("test_index", "test", "1", "{}"),
                new DeleteDocumentMigration("test_index", "test", "1"),
                new DeleteIndexTemplateMigration("test_template"),
                new DeleteIndexMigration("test_index")
        ));

    }
}
