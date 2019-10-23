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
package com.hubrick.lib.elasticsearchmigration.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonschema.core.load.Dereferencing;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.hubrick.lib.elasticsearchmigration.exception.InvalidSchemaException;
import com.hubrick.lib.elasticsearchmigration.model.input.ChecksumedMigrationFile;
import com.hubrick.lib.elasticsearchmigration.model.input.MigrationFile;
import com.hubrick.lib.elasticsearchmigration.service.Parser;
import com.hubrick.lib.elasticsearchmigration.util.HashUtils;
import com.hubrick.lib.elasticsearchmigration.util.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@Slf4j
public class YamlParser implements Parser {

    private static final String MIGRATION_SCHEMA;

    static {
        try {
            MIGRATION_SCHEMA = Resources.toString(Resources.getResource(YamlParser.class, "/schema/yaml/schema.json"), Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load yaml schema", e);
        }
    }

    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;
    private final JsonSchema jsonSchema;

    public YamlParser() {
        this.yamlMapper = createYamlMapper();
        this.jsonMapper = createJsonMapper();
        this.jsonSchema = createJsonSchema();
    }

    private ObjectMapper createYamlMapper() {
        final YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
        yamlFactory.configure(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID, false);
        yamlFactory.configure(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE, true);

        final ObjectMapper yamlMapper = new ObjectMapper(yamlFactory);
        yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        yamlMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        yamlMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        yamlMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        yamlMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        yamlMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        yamlMapper.registerModule(new Jdk8Module());
        yamlMapper.registerModule(new JavaTimeModule());

        return yamlMapper;
    }

    private JsonSchema createJsonSchema() {
        final LoadingConfiguration loadingConfiguration = LoadingConfiguration.newBuilder()
                .dereferencing(Dereferencing.INLINE).freeze();
        final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
                .setLoadingConfiguration(loadingConfiguration).freeze();

        try {
            final JsonNode schemaObject = jsonMapper.readTree(MIGRATION_SCHEMA);
            return factory.getJsonSchema(schemaObject);
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't parse yaml schema", e);
        }
    }

    private ObjectMapper createJsonMapper() {
        return new ObjectMapper();
    }

    private void checkSchema(String path) {
        try {
            final InputStream inputStream = ResourceUtils.getResourceAsStream(path, this);
            final JsonNode yaml = yamlMapper.readTree(inputStream);
            final ProcessingReport report = jsonSchema.validate(yaml);
            final List<String> errors = new LinkedList<>();
            if (!report.isSuccess()) {
                report.forEach(e -> errors.add(e.getMessage()));
                throw new InvalidSchemaException("Yaml file doesn't match the schema. Problems: " + Joiner.on(",").join(errors));
            }
        } catch (Exception e) {
            throw new InvalidSchemaException("Problem parsing yaml file " + path, e);
        }
    }

    @Override
    public ChecksumedMigrationFile parse(final String path) {
        checkNotNull(StringUtils.trimToNull(path), "path must be not null");

        try {
            log.info("Checking schema for file " + path);
            checkSchema(path);
            log.info("Parsing file " + path);
            final byte[] yaml = IOUtils.toByteArray(ResourceUtils.getResourceAsStream(path, this));
            final MigrationFile migrationFile = yamlMapper.readValue(new ByteArrayInputStream(yaml), MigrationFile.class);

            final byte[] normalizedYaml = yamlMapper.writeValueAsBytes(migrationFile);
            final String normalizedSha256Checksum = HashUtils.hashSha256(ByteBuffer.wrap(normalizedYaml));

            if(log.isDebugEnabled()) {
                log.debug("Original yaml: \n{}", new String(yaml, Charsets.UTF_8));
                log.debug("Normalized yaml: \n{}", new String(normalizedYaml, Charsets.UTF_8));
            }

            return new ChecksumedMigrationFile(migrationFile, normalizedSha256Checksum);
        } catch (IOException e) {
            throw new InvalidSchemaException("Problem parsing yaml file " + path, e);
        }
    }
}
