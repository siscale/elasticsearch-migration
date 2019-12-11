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

import com.google.common.io.BaseEncoding;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class HashUtils {

    private static final int CHUNK_SIZE = 4096;

    public String hashSha256(InputStream inputStream) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            int chunkLen = 0;
            final byte[] chunk = new byte[CHUNK_SIZE];
            while ((chunkLen = inputStream.read(chunk)) != -1) {
                messageDigest.update(chunk, 0, chunkLen);
            }

            final byte[] hashesValueByteArray = messageDigest.digest();
            return new String(BaseEncoding.base16().lowerCase().encode(hashesValueByteArray));
        } catch (IOException e) {
            log.error("Error calculating sha256", e);
            throw new IllegalStateException("Error calculating sha256", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("This should never happen. Hash algorithms are type safe", e);
            throw new IllegalStateException("This should never happen. HashAlgorithm is type safe", e);
        }
    }

    public static String hashSha256(ByteBuffer value) {
        checkNotNull(value, "value must not be null");
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(value.asReadOnlyBuffer());
            final byte[] hashesValueByteArray = messageDigest.digest();
            return new String(BaseEncoding.base16().lowerCase().encode(hashesValueByteArray));
        } catch (NoSuchAlgorithmException e) {
            log.error("This should never happen. Hash algorithms are type safe", e);
            throw new IllegalStateException("This should never happen. HashAlgorithm is type safe", e);
        }
    }
}
