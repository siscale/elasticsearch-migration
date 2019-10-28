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
package com.github.eemmiirr.lib.elasticsearchmigration.model.migration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
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
public class IndexDocumentMigration implements Migration {

    @NonNull
    private final String index;
    @NonNull
    private final Optional<String> id;
    @NonNull
    private final Optional<OpType> opType;
    @NonNull
    private final String definition;

    @Override
    public Method getMethod() {
        return id.isPresent() ? Method.PUT : Method.POST;
    }

    @Override
    public String getUrl() {
        return "/" + index + "/_doc/" + id.orElse("");
    }

    @Override
    public Map<String, String> getParameters() {
        return ImmutableMap.of(
                "op_type", opType.map(e -> e.name().toLowerCase()).orElse(OpType.CREATE.name().toLowerCase()),
                "refresh", "wait_for"
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
