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
package com.quandoo.lib.elasticsearchmigration.model.migration;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@Getter
public class MigrationMeta {

    private static final Pattern VERSION_PATTERN = Pattern.compile("^([0-9]{1}(\\.{1}[0-9]+)*)$");

    private final String sha256Checksum;
    private final String version;
    private final String name;

    public MigrationMeta(final String sha256Checksum, final String version, final String name) {
        checkNotNull(StringUtils.trimToNull(sha256Checksum), "sha256Checksum must not be null");
        checkNotNull(StringUtils.trimToNull(version), "version must not be null");
        checkArgument(VERSION_PATTERN.matcher(version).matches(), "version must be a valid version number like 1.0.0");
        checkNotNull(StringUtils.trimToNull(name), "name must not be null");

        this.sha256Checksum = sha256Checksum;
        this.version = version;
        this.name = name;
    }
}
