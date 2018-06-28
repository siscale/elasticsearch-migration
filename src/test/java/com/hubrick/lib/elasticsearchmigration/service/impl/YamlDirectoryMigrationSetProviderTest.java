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
                "e70768434489d06d6b51d9bf1ac4f1804d8cd530a6af70e5eebd9994778c78d1",
                "c0b90eb5d8a427d36d709419418a95b6e5fd26fba7e16e7ea49204dbb7b86e9f",
                "f22917c73688ba7cc769fb6ca321f5ce008523a5a8857d1cf25c1016b6b65d7d"
        ));
        assertThat(migrationSet.getMigrations().stream().flatMap(e -> e.getMigration().stream()).collect(Collectors.toList()), contains(
                new CreateIndexMigration("test_index_1", "{}"),
                new CreateIndexMigration("test_index_2", "{}"),
                new CreateOrUpdateIndexTemplateMigration("test_template", "{}"),
                new UpdateMappingMigration(ImmutableSet.of("test_index_1", "test_index_2"), "test", "{}"),
                new IndexDocumentMigration("test_index_1", "test", Optional.of("1"), Optional.empty(), "{}"),
                new UpdateDocumentMigration("test_index_1", "test", "1", "{}"),
                new DeleteDocumentMigration("test_index_1", "test", "1"),
                new DeleteIndexTemplateMigration("test_template"),
                new DeleteIndexMigration("test_index_1"),
                new DeleteIndexMigration("test_index_2")
        ));

    }
}
