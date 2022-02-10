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

import com.google.common.base.Strings;
import com.quandoo.lib.elasticsearchmigration.model.migration.MigrationSet;
import com.quandoo.lib.elasticsearchmigration.service.MigrationClient;
import com.quandoo.lib.elasticsearchmigration.service.MigrationSetProvider;
import com.quandoo.lib.elasticsearchmigration.service.impl.DefaultMigrationClient;
import com.quandoo.lib.elasticsearchmigration.service.impl.YamlDirectoryMigrationSetProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.stream.Collectors;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@Slf4j
public class ElasticsearchMigration {

    private final ElasticsearchMigrationConfig elasticsearchMigrationConfig;
    private final MigrationClient migrationClient;
    private final MigrationSetProvider migrationSetProvider;

    public ElasticsearchMigration(@NonNull final ElasticsearchMigrationConfig elasticsearchMigrationConfig) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        this.elasticsearchMigrationConfig = elasticsearchMigrationConfig;
        this.migrationClient = new DefaultMigrationClient(
                elasticsearchMigrationConfig.getIdentifier(),
                createElasticsearchClient(elasticsearchMigrationConfig.getElasticsearchConfig()),
                elasticsearchMigrationConfig.getIgnorePreviousFailures(),
                elasticsearchMigrationConfig.getAllowOlderVersions(),
                elasticsearchMigrationConfig.getBackoffPeriodInMillis(),
                elasticsearchMigrationConfig.getRetryCount()
        );
        this.migrationSetProvider = new YamlDirectoryMigrationSetProvider();
    }

    private RestHighLevelClient createElasticsearchClient(ElasticsearchConfig elasticsearchConfig) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {


        final RestClientBuilder builder = RestClient.builder(
                elasticsearchConfig.getUrls().stream().map(e -> new HttpHost(e.getHost(), e.getPort(), e.getProtocol())).distinct().toArray(HttpHost[]::new)
        );

        if (!Strings.isNullOrEmpty(elasticsearchConfig.getUsername())) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchConfig.getUsername(), elasticsearchConfig.getPassword()));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider));
        }

        builder.setDefaultHeaders(elasticsearchConfig.getHeaders().entries().stream().map(e -> new BasicHeader(e.getKey(), e.getValue())).toArray(Header[]::new));

        Path caCertificatePath = Paths.get("/Users/popescub/personal/siscale/elasticsearch-migration/src/test/resources/ca.crt");
        CertificateFactory factory =
                CertificateFactory.getInstance("X.509");
        Certificate trustedCa;
        try (InputStream is = Files.newInputStream(caCertificatePath)) {
            trustedCa = factory.generateCertificate(is);
        }
        KeyStore trustStore = KeyStore.getInstance("pkcs12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", trustedCa);
        SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(trustStore, null);
        final SSLContext sslContext = sslContextBuilder.build();
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setSSLContext(sslContext);
                    }
                });

        if (elasticsearchConfig.getPathPrefix() != null) {
            builder.setPathPrefix(elasticsearchConfig.getPathPrefix());
        }

        return new RestHighLevelClient(builder);
    }

    public void migrate() {
        log.info("Starting ES schema migration...");
        final MigrationSet migrationSet = migrationSetProvider.getMigrationSet(elasticsearchMigrationConfig.getBasePackage());
        migrationClient.applyMigrationSet(migrationSet);
        log.info("Finished ES schema migration");
    }
}
