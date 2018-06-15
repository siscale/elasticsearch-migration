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
package com.hubrick.lib.elasticsearchmigration.model.migration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@EqualsAndHashCode
@AllArgsConstructor
public class CreateIndexMigration implements Migration {

    @NonNull
    private final String index;
    @NonNull
    private final String definition;

    @Override
    public Method getMethod() {
        return Method.PUT;
    }

    @Override
    public String getUrl() {
        return "/" + index;
    }

    @Override
    public Map<String, String> getParameters() {
        final Integer numberOfReplicas = (Integer) Optional.ofNullable(JsonPath.parse(definition, com.jayway.jsonpath.Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS)).read("$.settings.number_of_replicas")).orElse(0) + 1;
        return ImmutableMap.of(
                "wait_for_active_shards", numberOfReplicas.toString()
        );
    }

    @Override
    public Multimap<String, String> getHeaders() {
        return HashMultimap.create();
    }

    @Override
    public String getBody() {
        return definition;
    }
}
