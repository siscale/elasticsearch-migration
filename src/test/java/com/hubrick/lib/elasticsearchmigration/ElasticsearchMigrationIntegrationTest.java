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
package com.hubrick.lib.elasticsearchmigration;

import com.hubrick.lib.elasticsearchmigration.model.es.MigrationEntry;
import com.hubrick.lib.elasticsearchmigration.model.es.MigrationEntryMeta;
import com.hubrick.lib.elasticsearchmigration.model.es.State;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class ElasticsearchMigrationIntegrationTest extends AbstractESTest {

    @Test
    public void testMigrate() throws IOException, InterruptedException, ExecutionException {
        final ElasticsearchMigration elasticsearchMigration = new ElasticsearchMigration(
                ElasticsearchMigrationConfig.builder( "test", ElasticsearchConfig.builder(new URL("http://localhost:9200")).build()).basePackage("changeset").build()
        );

        elasticsearchMigration.migrate();

        final MigrationEntry migrationEntry100 = getFromIndex(MigrationEntryMeta.INDEX, MigrationEntryMeta.TYPE, "test-1.0.0", MigrationEntry.class);
        final MigrationEntry migrationEntry110 = getFromIndex(MigrationEntryMeta.INDEX, MigrationEntryMeta.TYPE, "test-1.1.0", MigrationEntry.class);
        final MigrationEntry migrationEntry111 = getFromIndex(MigrationEntryMeta.INDEX, MigrationEntryMeta.TYPE, "test-1.1.1", MigrationEntry.class);

        final Instant now = Instant.now();
        assertThat(migrationEntry100.getIdentifier(), is("test"));
        assertThat(migrationEntry100.getVersion(), is("1.0.0"));
        assertThat(migrationEntry100.getName(), is("migration_one"));
        assertThat(migrationEntry100.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry100.getFailureMessage(), is(""));
        assertThat(migrationEntry100.getState(), is(State.SUCCESS));
        assertThat(migrationEntry100.getSha256Checksum(), is("5f18dfd0d16bd10b481e84e7254b6f485f31524e292dc226ec737ed300e98894"));

        assertThat(migrationEntry110.getIdentifier(), is("test"));
        assertThat(migrationEntry110.getVersion(), is("1.1.0"));
        assertThat(migrationEntry110.getName(), is("migration_two"));
        assertThat(migrationEntry110.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry110.getFailureMessage(), is(""));
        assertThat(migrationEntry110.getState(), is(State.SUCCESS));
        assertThat(migrationEntry110.getSha256Checksum(), is("0f4b920b0c5e14d8a5bef6a1086c2191b383aa18b19f4e88ae0e53a169ffbad2"));

        assertThat(migrationEntry111.getIdentifier(), is("test"));
        assertThat(migrationEntry111.getVersion(), is("1.1.1"));
        assertThat(migrationEntry111.getName(), is("migration_three"));
        assertThat(migrationEntry111.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry111.getFailureMessage(), is(""));
        assertThat(migrationEntry111.getState(), is(State.SUCCESS));
        assertThat(migrationEntry111.getSha256Checksum(), is("ad48c1788a74d58a5bcb561b5f1656e0b296ada1f966ab76521248de320401d3"));
    }
}
