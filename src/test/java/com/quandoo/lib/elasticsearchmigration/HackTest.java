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
package com.quandoo.lib.elasticsearchmigration;

import com.quandoo.lib.elasticsearchmigration.model.es.MigrationEntry;
import com.quandoo.lib.elasticsearchmigration.model.es.MigrationEntryMeta;
import com.quandoo.lib.elasticsearchmigration.model.es.State;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class HackTest extends AbstractESTest {

    @Test
    public void testMigrate() throws IOException, InterruptedException, ExecutionException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        final ElasticsearchMigration elasticsearchMigration = new ElasticsearchMigration(
                ElasticsearchMigrationConfig.builder(
                        "test",
                        ElasticsearchConfig.builder(new URL("https://localhost:9200"))
                                .username("hack")
                                .password("hackhack")
                                .build()).basePackage("hack").build()
        );

        elasticsearchMigration.migrate();

        final MigrationEntry migrationEntry100 = getFromIndex(MigrationEntryMeta.INDEX, "test-1.0.0", MigrationEntry.class);
    }
}
