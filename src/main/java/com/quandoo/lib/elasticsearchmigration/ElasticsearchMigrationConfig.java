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

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@Getter
@Builder(builderMethodName = "hiddenBuilder")
public class ElasticsearchMigrationConfig {

    @NonNull
    private final String identifier;
    @NonNull
    @Builder.Default
    private final String basePackage = "es.migration";
    @NonNull
    @Builder.Default
    private final Boolean ignorePreviousFailures = false;
    @NonNull
    @Builder.Default
    private final Boolean allowOlderVersions = false;
    @NonNull
    @Builder.Default
    private final Integer backoffPeriodInMillis = 30000;
    @NonNull
    @Builder.Default
    private final Integer retryCount = 5;

    @NonNull
    private final ElasticsearchConfig elasticsearchConfig;

    public static ElasticsearchMigrationConfig.ElasticsearchMigrationConfigBuilder builder(@NonNull final String identifier, @NonNull final ElasticsearchConfig elasticsearchConfig) {
        return hiddenBuilder().identifier(identifier).elasticsearchConfig(elasticsearchConfig);
    }
}
