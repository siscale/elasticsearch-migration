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
package com.quandoo.lib.elasticsearchmigration.model.input;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMappingMigrationFileEntry extends BaseMigrationFileEntry {

    @NonNull
    private Set<String> indices;
    @NonNull
    private String definition;
}
