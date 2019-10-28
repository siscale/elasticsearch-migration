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
package com.github.eemmiirr.lib.elasticsearchmigration.model.es;

import lombok.experimental.UtilityClass;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@UtilityClass
public class MigrationEntryMeta {

    public static final String INDEX = "elasticsearch_migration_version";

    public static final String IDENTIFIER_FIELD = "identifier";
    public static final String VERSION_FIELD = "version";
    public static final String NAME_FIELD = "name";
    public static final String SHA_256_CHECKSUM_FIELD = "sha256Checksum";
    public static final String STATE_FIELD = "state";
    public static final String FAUILURE_MESSAGE_FIELD = "failureMessage";
    public static final String CREATED_FIELD = "created";
}
