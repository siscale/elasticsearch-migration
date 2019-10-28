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

import com.github.eemmiirr.lib.elasticsearchmigration.model.migration.*;
import com.google.common.collect.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
import org.junit.*;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
        final MigrationSet migrationSet = yamlDirectoryMigrationSetProvider.getMigrationSet("com.github.eemmiirr.lib.elasticsearchmigration.service.impl");

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
                "e874b5f9c634b1b0058887b5d9bcdde6bae0850d83b89b17dcbf062fb579c739",
                "348c126c00c989b521c50d64018d2579d76ce26a9c00b9a99eaeb3073f43b847",
                "ba4d3f7585a354d407805cbaa6443ba77b9db63990944c74dbe485e3591bc494"
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
