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

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class VersionComparator<T> implements Comparator<T> {

    private final Pattern versionRegex;
    private final Integer versionRegexGroup;
    private final String versionDelimiter;
    private final Function<T, String> lookupFunction;

    @Override
    public int compare(T version1, T version2) {
        final Matcher version1Matcher = versionRegex.matcher(lookupFunction.apply(version1));
        final Matcher version2Matcher = versionRegex.matcher(lookupFunction.apply(version2));

        if (!version1Matcher.matches() || !version2Matcher.matches()) {
            throw new IllegalStateException("Version invalid");
        } else {
            return compareInternal(
                    Lists.newArrayList(version1Matcher.toMatchResult().group(versionRegexGroup).split(versionDelimiter)),
                    Lists.newArrayList(version2Matcher.toMatchResult().group(versionRegexGroup).split(versionDelimiter))
            );
        }

    }

    private int compareInternal(final List<String> versions1, final List<String> versions2) {
        if (versions1.isEmpty() && versions2.isEmpty()) {
            return 0;
        } else if (versions1.isEmpty() && !versions2.isEmpty()) {
            return 1;
        } else if (versions2.isEmpty() && !versions1.isEmpty()) {
            return -1;
        } else if (Integer.valueOf(versions1.get(0)) > Integer.valueOf(versions2.get(0))) {
            return 1;
        } else if (Integer.valueOf(versions1.get(0)) < Integer.valueOf(versions2.get(0))) {
            return -1;
        } else {
            return compareInternal(versions1.subList(1, versions1.size()), versions2.subList(1, versions2.size()));
        }
    }
}
