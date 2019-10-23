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
        assertThat(migrationSet.getMigrations().stream().map(e -> e.getMigrationMeta().getSha256Checksums()).collect(Collectors.toList()), contains(
                ImmutableSet.of("add4031e5df0c7b8426426019b6c1b7031443661da4639c41db583d7ecac3336", "e874b5f9c634b1b0058887b5d9bcdde6bae0850d83b89b17dcbf062fb579c739"),
                ImmutableSet.of("d9a0430c8fe25ef4809a6e456b53effbc1b3b44ad4db8f50dc69b4167ad39f1e", "348c126c00c989b521c50d64018d2579d76ce26a9c00b9a99eaeb3073f43b847"),
                ImmutableSet.of("b5694b48c166bf79cb69d4dc058461b5c3312ae7fad8ada60c617ed26c5501d7", "ba4d3f7585a354d407805cbaa6443ba77b9db63990944c74dbe485e3591bc494")
        ));
        assertThat(migrationSet.getMigrations().stream().flatMap(e -> e.getMigration().stream()).collect(Collectors.toList()), contains(
                new CreateIndexMigration("test_index_1", "{}"),
                new CreateIndexMigration("test_index_2", "{}"),
                new CreateOrUpdateIndexTemplateMigration("test_template", "{}"),
                new UpdateMappingMigration(ImmutableSet.of("test_index_1", "test_index_2"), "{}"),
                new IndexDocumentMigration("test_index_1", Optional.of("1"), Optional.empty(), "{}"),
                new UpdateDocumentMigration("test_index_1", "1", "{}"),
                new DeleteDocumentMigration("test_index_1", "1"),
                new DeleteIndexTemplateMigration("test_template"),
                new DeleteIndexMigration("test_index_1"),
                new DeleteIndexMigration("test_index_2")
        ));

    }
}
