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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.net.URL;
import java.util.Collections;
import java.util.Set;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@Getter
@Builder(builderMethodName = "hiddenBuilder")
public class ElasticsearchConfig {

    @NonNull
    private final Set<URL> urls;
    private final Integer maxRetryTimeoutMillis;
    private final String pathPrefix;

    @NonNull
    @Builder.Default
    private final Multimap<String, String> headers = HashMultimap.create();

    public static ElasticsearchConfigBuilder builder(@NonNull URL url) {
        return hiddenBuilder().urls(Collections.singleton(url));
    }

    public static ElasticsearchConfigBuilder builder(@NonNull URL... urls) {
        return hiddenBuilder().urls(Sets.newHashSet(urls));
    }

    public static ElasticsearchConfigBuilder builder(@NonNull Set<URL> urls) {
        return hiddenBuilder().urls(urls);
    }
}
