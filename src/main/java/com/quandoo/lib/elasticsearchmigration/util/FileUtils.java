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
package com.quandoo.lib.elasticsearchmigration.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class FileUtils {

    private static final int INITIAL_SIZE = 16384;

    public String readFully(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

            String line;
            final StringBuilder stringBuilder = new StringBuilder(INITIAL_SIZE);
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            log.error("Error calculating sha256", e);
            throw new IllegalStateException("Error calculating sha256", e);
        }
    }
}
