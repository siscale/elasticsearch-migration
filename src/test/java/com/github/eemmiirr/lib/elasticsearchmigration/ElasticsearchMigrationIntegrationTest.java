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
package com.github.eemmiirr.lib.elasticsearchmigration;

import com.github.eemmiirr.lib.elasticsearchmigration.model.es.MigrationEntry;
import com.github.eemmiirr.lib.elasticsearchmigration.model.es.MigrationEntryMeta;
import com.github.eemmiirr.lib.elasticsearchmigration.model.es.State;
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
                ElasticsearchMigrationConfig.builder("test", ElasticsearchConfig.builder(new URL("http://localhost:9200")).build()).basePackage("changeset").build()
        );

        elasticsearchMigration.migrate();

        final MigrationEntry migrationEntry100 = getFromIndex(MigrationEntryMeta.INDEX, "test-1.0.0", MigrationEntry.class);
        final MigrationEntry migrationEntry110 = getFromIndex(MigrationEntryMeta.INDEX, "test-1.1.0", MigrationEntry.class);
        final MigrationEntry migrationEntry111 = getFromIndex(MigrationEntryMeta.INDEX, "test-1.1.1", MigrationEntry.class);
        final MigrationEntry migrationEntry112 = getFromIndex(MigrationEntryMeta.INDEX, "test-1.1.2", MigrationEntry.class);

        final Instant now = Instant.now();
        assertThat(migrationEntry100.getIdentifier(), is("test"));
        assertThat(migrationEntry100.getVersion(), is("1.0.0"));
        assertThat(migrationEntry100.getName(), is("creation"));
        assertThat(migrationEntry100.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry100.getFailureMessage(), is(""));
        assertThat(migrationEntry100.getState(), is(State.SUCCESS));
        assertThat(migrationEntry100.getSha256Checksum(), is(
                "37163ccf9cafa67838446b1b5aeed228108e6a5a14fc905f901a58819279d1c9"
        ));

        assertThat(migrationEntry110.getIdentifier(), is("test"));
        assertThat(migrationEntry110.getVersion(), is("1.1.0"));
        assertThat(migrationEntry110.getName(), is("meta_altering"));
        assertThat(migrationEntry110.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry110.getFailureMessage(), is(""));
        assertThat(migrationEntry110.getState(), is(State.SUCCESS));
        assertThat(migrationEntry110.getSha256Checksum(), is(
                "d2a5aa74d14d3f24e1520e584792c10f8afbe3e8aebad1b81b2e07b3944b0eb8"
        ));

        assertThat(migrationEntry111.getIdentifier(), is("test"));
        assertThat(migrationEntry111.getVersion(), is("1.1.1"));
        assertThat(migrationEntry111.getName(), is("data_altering"));
        assertThat(migrationEntry111.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry111.getFailureMessage(), is(""));
        assertThat(migrationEntry111.getState(), is(State.SUCCESS));
        assertThat(migrationEntry111.getSha256Checksum(), is(
                "a8e24dc8351a214b8823001e940e494a8db9446fc3fa9854f8fe68041ce6f6f9"
        ));

        assertThat(migrationEntry112.getIdentifier(), is("test"));
        assertThat(migrationEntry112.getVersion(), is("1.1.2"));
        assertThat(migrationEntry112.getName(), is("delete"));
        assertThat(migrationEntry112.getCreated(), lessThanOrEqualTo(now));
        assertThat(migrationEntry112.getFailureMessage(), is(""));
        assertThat(migrationEntry112.getState(), is(State.SUCCESS));
        assertThat(migrationEntry112.getSha256Checksum(), is(
                "706b413aefb36d7cbb34ac810249db1f8e70e2bc953f4183f4ffdc082614af61"
        ));
    }
}
