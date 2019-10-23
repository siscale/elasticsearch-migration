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

import static org.hamcrest.Matchers.containsInAnyOrder;
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
                ElasticsearchMigrationConfig.builder("test", ElasticsearchConfig.builder(new URL("http://localhost:9200")).build()).basePackage("changeset").build()
        );

        elasticsearchMigration.migrate();

        final MigrationEntry migrationEntry100 = getFromIndex(MigrationEntryMeta.INDEX, "test-1.0.0", MigrationEntry.class);
        final MigrationEntry migrationEntry110 = getFromIndex(MigrationEntryMeta.INDEX, "test-1.1.0", MigrationEntry.class);
        final MigrationEntry migrationEntry111 = getFromIndex(MigrationEntryMeta.INDEX, "test-1.1.1", MigrationEntry.class);

        final Instant now = Instant.now();
        assertThat(migrationEntry100.getIdentifier(), is("test"));
        assertThat(migrationEntry100.getVersion(), is("1.0.0"));
        assertThat(migrationEntry100.getName(), is("migration_one"));
        assertThat(migrationEntry100.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry100.getFailureMessage(), is(""));
        assertThat(migrationEntry100.getState(), is(State.SUCCESS));
        assertThat(migrationEntry100.getSha256Checksum(), containsInAnyOrder(
                "24e88874e494349b13562cbb706745424b7c661cc92be15c8cf00cf24a50ff0b",
                "e48d69ff3d07bb0e90bb4942f316d2166b0b46359467d176052e201f61583c77"
        ));

        assertThat(migrationEntry110.getIdentifier(), is("test"));
        assertThat(migrationEntry110.getVersion(), is("1.1.0"));
        assertThat(migrationEntry110.getName(), is("migration_two"));
        assertThat(migrationEntry110.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry110.getFailureMessage(), is(""));
        assertThat(migrationEntry110.getState(), is(State.SUCCESS));
        assertThat(migrationEntry110.getSha256Checksum(), containsInAnyOrder(
                "863e13aaaadf7faa8ae3499ed4f4f51e7319fae385f9ae0ecb12cec079753997",
                "f67f164931c53499915056d3220c56c2b036c282f0f7c9821b8d08453040c9c7"
        ));

        assertThat(migrationEntry111.getIdentifier(), is("test"));
        assertThat(migrationEntry111.getVersion(), is("1.1.1"));
        assertThat(migrationEntry111.getName(), is("migration_three"));
        assertThat(migrationEntry111.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry111.getFailureMessage(), is(""));
        assertThat(migrationEntry111.getState(), is(State.SUCCESS));
        assertThat(migrationEntry111.getSha256Checksum(), containsInAnyOrder(
                "cb8fc0b8bd4325807a6a16a480aa80a453659a3d90da8329023f600461d094ed",
                "4b69f5084b608d6cd3dd058fdcbabf84f89c4c918d0a406186d21656061c8dd9"
        ));
    }
}
