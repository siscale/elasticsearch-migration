/**
 * Copyright (C) 2019 Quandoo GmbH (account.oss@quandoo.com)
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
package com.quandoo.lib.elasticsearchmigration.service.impl;

import com.quandoo.lib.elasticsearchmigration.*;
import com.quandoo.lib.elasticsearchmigration.exception.*;
import com.quandoo.lib.elasticsearchmigration.model.es.*;
import com.quandoo.lib.elasticsearchmigration.model.migration.*;
import com.google.common.collect.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.http.*;
import org.elasticsearch.client.*;
import org.junit.*;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class DefaultMigrationClientIntegrationTest extends AbstractESTest {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final String IDENTIFIER = "test";

    @Test
    public void testInitIndexCreation() {
        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        assertThat(checkIndexExists(LockEntryMeta.INDEX), is(true));
        assertThat(checkIndexExists(MigrationEntryMeta.INDEX), is(true));
    }

    @Test
    public void testInitIndexCreationNotHappaned() {
        final DefaultMigrationClient defaultMigrationClient = createClient();

        assertThat(checkIndexExists(LockEntryMeta.INDEX), is(false));
        assertThat(checkIndexExists(MigrationEntryMeta.INDEX), is(false));
    }

    @Test
    public void testApplyCreateIndexMigration() throws ExecutionException, InterruptedException, IOException {

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkIndexExists("test_index"), is(true));
        assertMigrationEntry();
    }

    @Test
    public void testApplyDeleteIndexMigration() throws ExecutionException, InterruptedException, IOException {

        createIndex("test_index", loadResource("create_index.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new DeleteIndexMigration("test_index")),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkIndexExists("test_index"), is(false));
        assertMigrationEntry();
    }

    @Test
    public void testApplyCreateOrUpdateIndexTemplateMigration() throws ExecutionException, InterruptedException, IOException {

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateOrUpdateIndexTemplateMigration("test_template", loadResource("create_template.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkTemplateExists("test_template"), is(true));
        assertMigrationEntry();
    }

    @Test
    public void testApplyDeleteIndexTemplateMigration() throws ExecutionException, InterruptedException, IOException {

        createTemplate("test_template", loadResource("create_template.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new DeleteIndexTemplateMigration("test_template")),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkTemplateExists("test_template"), is(false));
        assertMigrationEntry();
    }

    @Test
    public void testApplyUpdateMappingMigration() throws ExecutionException, InterruptedException, IOException {

        createIndex("test_index", loadResource("create_index.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new UpdateMappingMigration(ImmutableSet.of("test_index"), loadResource("update_mapping.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkIndexExists("test_index"), is(true));
        assertMigrationEntry();
    }


    @Test
    public void testApplyIndexDocumentMigration() throws ExecutionException, InterruptedException, IOException {

        createIndex("test_index", loadResource("create_index.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new IndexDocumentMigration("test_index", Optional.of("1"), Optional.empty(), loadResource("index_document.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkDocumentExists("test_index", "1"), is(true));
        assertMigrationEntry();
    }

    @Test
    public void testApplyDeleteDocumentMigration() throws ExecutionException, InterruptedException, IOException {

        createIndex("test_index", loadResource("create_index.json"));
        indexDocument("test_index", "1", loadResource("index_document.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new DeleteDocumentMigration("test_index", "1")),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkDocumentExists("test_index", "1"), is(false));
        assertMigrationEntry();
    }

    @Test
    public void testApplyAliasesMigration() throws ExecutionException, InterruptedException, IOException {

        createIndex("test_index", loadResource("create_index.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new AliasesMigration(loadResource("create_alias.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkAliasExists("test_alias"), is(true));
        assertMigrationEntry();
    }

    @Test
    public void testCreatePipelineMigration() throws ExecutionException, InterruptedException, IOException {

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIngestPipelineMigration("test_pipeline", loadResource("create_pipeline.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkPipelineExists("test_pipeline"), is(true));
        assertMigrationEntry();
    }

    @Test
    public void testDeletePipelineMigration() throws ExecutionException, InterruptedException, IOException {

        createPipeline("test_pipeline", loadResource("create_pipeline.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new DeleteIngestPipelineMigration("test_pipeline")),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkPipelineExists("test_pipeline"), is(false));
        assertMigrationEntry();
    }

    @Test
    public void testReindexMigration() throws ExecutionException, InterruptedException, IOException {

        createIndex("test_index_1", loadResource("create_index.json"));
        createIndex("test_index_2", loadResource("create_index.json"));
        indexDocument("test_index_1", "1", loadResource("index_document.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new ReindexMigration(loadResource("reindex.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkDocumentExists("test_index_1", "1"), is(true));
        assertMigrationEntry();
    }

    @Test
    public void testReapplyMigration() throws ExecutionException, InterruptedException, IOException {

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(MigrationEntryMeta.INDEX, "test-1.0.0", loadResource("successful_elasticsearchmigration_version_entry.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkIndexExists("test_index"), is(false));
        assertMigrationEntry();
    }

    @Test
    public void testPreviousMigrationFailedIgnore() {

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(MigrationEntryMeta.INDEX, "test-1.0.0", loadResource("failed_elasticsearchmigration_version_entry.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);
    }

    @Test(expected = PreviousMigrationFailedException.class)
    public void testPreviousMigrationFailedException() {

        final DefaultMigrationClient defaultMigrationClient = createClient(false, 15000, 5);
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(MigrationEntryMeta.INDEX, "test-1.0.0", loadResource("failed_elasticsearchmigration_version_entry.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);
    }

    @Test(expected = MigrationFailedException.class)
    public void testVersionMismatchMigrationFailedException() {

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(MigrationEntryMeta.INDEX, "test-1.0.0", loadResource("successful_elasticsearchmigration_version_entry.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        ),
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "0.1.0",
                                        "singularity"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);
    }

    @Test
    public void testMigrationRetried() throws ExecutionException, InterruptedException, IOException {

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(LockEntryMeta.INDEX, "test-global", loadResource("lock_entry.json"));
        scheduler.schedule(() -> deleteDocument(LockEntryMeta.INDEX, "test-global"), 30, TimeUnit.SECONDS);

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkIndexExists("test_index"), is(true));
        assertMigrationEntry();
    }

    @Test(expected = MigrationLockedException.class)
    public void testMigrationFailedAfterAllRetries() throws ExecutionException, InterruptedException, IOException {

        final DefaultMigrationClient defaultMigrationClient = createClient(true, 5000, 3);
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(LockEntryMeta.INDEX, "test-global", loadResource("lock_entry.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);

        assertThat(checkIndexExists("test_index"), is(false));
    }

    @Test(expected = MigrationFailedException.class)
    public void testNameMismatchMigrationFailedException() {

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(MigrationEntryMeta.INDEX, "test-1.0.0", loadResource("successful_elasticsearchmigration_version_entry.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "wrong_name"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);
    }

    @Test(expected = MigrationFailedException.class)
    public void testChecksumMismatchMigrationFailedException() {

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(MigrationEntryMeta.INDEX, "test-1.0.0", loadResource("successful_elasticsearchmigration_version_entry.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "20d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);
    }

    @Test(expected = MigrationFailedException.class)
    public void testLocalChangeSetSmallerMigrationFailedException() {

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(MigrationEntryMeta.INDEX, "test-1.0.0", loadResource("successful_elasticsearchmigration_version_entry.json"));
        indexDocument(MigrationEntryMeta.INDEX, "test-1.1.0", loadResource("successful_elasticsearchmigration_version_entry.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "1.0.0",
                                        "singularity"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);
    }

    @Test(expected = MigrationFailedException.class)
    public void testSmallerChangeSetThenAlreadyAppliedMigrationFailedException() {

        final DefaultMigrationClient defaultMigrationClient = createClient();
        defaultMigrationClient.applyMigrationSet(new MigrationSet(Collections.emptyList()));

        indexDocument(MigrationEntryMeta.INDEX, "test-1.0.0", loadResource("successful_elasticsearchmigration_version_entry.json"));
        indexDocument(MigrationEntryMeta.INDEX, "test-1.1.0", loadResource("successful_elasticsearchmigration_version_entry.json"));

        final MigrationSet migrationSet = new MigrationSet(
                ImmutableList.of(
                        new MigrationSetEntry(
                                ImmutableList.of(new CreateIndexMigration("test_index", loadResource("create_index.json"))),
                                new MigrationMeta(
                                        "10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007",
                                        "0.1.0",
                                        "singularity"
                                )

                        )
                )
        );

        defaultMigrationClient.applyMigrationSet(migrationSet);
    }

    private void assertMigrationEntry() {
        final MigrationEntry migrationEntry = getFromIndex(MigrationEntryMeta.INDEX, "test-1.0.0", MigrationEntry.class);
        assertThat(migrationEntry.getName(), is("singularity"));
        assertThat(migrationEntry.getVersion(), is("1.0.0"));
        assertThat(migrationEntry.getIdentifier(), is(IDENTIFIER));
        assertThat(migrationEntry.getSha256Checksum(), is("10d798ee9a8265432b6b9c621adeec1eb5ae9a79a6d5c3a684e06e6021163007"));
        assertThat(migrationEntry.getState(), is(State.SUCCESS));
        assertThat(migrationEntry.getCreated(), notNullValue());
        assertThat(migrationEntry.getFailureMessage(), isEmptyString());
    }

    private DefaultMigrationClient createClient() {
        return createClient(true, 15000, 5);
    }

    private DefaultMigrationClient createClient(boolean ignorePreviousFailures, int backoffPeriodMillis, int retryCount) {
        final RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        return new DefaultMigrationClient("test", new RestHighLevelClient(builder), ignorePreviousFailures, backoffPeriodMillis, retryCount);
    }
}
