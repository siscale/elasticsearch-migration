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
package com.github.eemmiirr.lib.elasticsearchmigration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public abstract class AbstractESTest {

    protected RestHighLevelClient client;

    private static final ObjectMapper esObjectMapper = new ObjectMapper();

    @BeforeClass
    public static final void initESClass() {
        esObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        esObjectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        esObjectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        esObjectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        esObjectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        esObjectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        esObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        esObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        esObjectMapper.registerModule(new Jdk8Module());
        esObjectMapper.registerModule(new JavaTimeModule());
    }

    @Before
    public final void initES() throws InterruptedException, IOException {
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://localhost:9200")));
        refreshIndices();

        try {
            final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("_all");
            deleteIndexRequest.indicesOptions(IndicesOptions.strictExpand());
            client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        } catch (IndexNotFoundException indexNotFoundException) {
            // Do nothing
        }

        try {
            final DeleteIndexTemplateRequest deleteIndexTemplateRequest = new DeleteIndexTemplateRequest("*");
            client.indices().deleteTemplate(deleteIndexTemplateRequest, RequestOptions.DEFAULT);
        } catch (IndexNotFoundException indexNotFoundException) {
            // Do nothing
        }

        final DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest("_all").setQuery(QueryBuilders.matchAllQuery());
        final BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);

        assertThat(bulkByScrollResponse.isTimedOut(), is(false));
        refreshIndices();

        final SearchRequest searchRequest = new SearchRequest("_all");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        assertThat(searchResponse.getHits().getTotalHits().value, is(0L));
    }

    @SneakyThrows
    protected void indexDocument(final String index, final String id, final String definition) {

        final IndexRequest indexRequest = new IndexRequest(index).id(id).source(definition, XContentType.JSON);
        client.index(indexRequest, RequestOptions.DEFAULT);

        refreshIndices();
    }

    @SneakyThrows
    protected void deleteDocument(final String index, final String id) {

        final DeleteRequest deleteRequest = new DeleteRequest(index).id(id);
        client.delete(deleteRequest, RequestOptions.DEFAULT);
        refreshIndices();
    }

    @SneakyThrows
    protected void createIndex(final String index, final String definition) {

        final CreateIndexRequest createIndexRequest = new CreateIndexRequest(index).source(definition, XContentType.JSON);
        final CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        assertThat(createIndexResponse.isAcknowledged(), is(true));
        refreshIndices();
    }

    @SneakyThrows
    protected void createTemplate(final String template, final String definition) {

        final PutIndexTemplateRequest putIndexTemplateRequest = new PutIndexTemplateRequest(template).source(definition, XContentType.JSON);
        final AcknowledgedResponse acknowledgedResponse = client.indices().putTemplate(putIndexTemplateRequest, RequestOptions.DEFAULT);

        assertThat(acknowledgedResponse.isAcknowledged(), is(true));
    }

    @SneakyThrows
    protected String loadResource(String fileName) {
        try (final InputStream resourceStream = this.getClass().getResourceAsStream(fileName)) {
            if (resourceStream == null) {
                throw new IllegalStateException("No such file: " + fileName);
            }
            return IOUtils.toString(resourceStream);
        }
    }

    @SneakyThrows
    protected boolean checkDocumentExists(String indexName, String id) {

        final GetRequest getRequest = new GetRequest(indexName).id(id);
        final GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        refreshIndices();

        return getResponse.isExists();
    }

    @SneakyThrows
    protected boolean checkIndexExists(String indexName) {
        try {
            final GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
            client.indices().get(getIndexRequest, RequestOptions.DEFAULT);
            return true;
        } catch (ElasticsearchStatusException e) {
            if(e.status() == RestStatus.NOT_FOUND) {
                return false;
            } else {
                throw e;
            }
        }
    }

    @SneakyThrows
    protected boolean checkTemplateExists(String name) {
        try {
            final GetIndexTemplatesRequest getIndexTemplatesRequest = new GetIndexTemplatesRequest(name);
            client.indices().getIndexTemplate(getIndexTemplatesRequest, RequestOptions.DEFAULT);
            return true;
        } catch (ElasticsearchStatusException e) {
            if(e.status() == RestStatus.NOT_FOUND) {
                return false;
            } else {
                throw e;
            }
        }
    }

    @SneakyThrows
    protected String getFromIndex(String indexName, String id) {

        final GetRequest getRequest = new GetRequest(indexName).id(id);
        final GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        refreshIndices();

        assertThat("Get " + id + " should exist (" + indexName + ")", getResponse.isExists(), is(true));
        assertThat("Source field should not be empty", getResponse.isSourceEmpty(), is(false));

        final String sourceAsString = getResponse.getSourceAsString();

        assertThat("response source should not be null", sourceAsString, notNullValue());
        return sourceAsString;
    }

    @SneakyThrows
    protected <T> T getFromIndex(String indexName, String id, Class<T> clazz) {
        final GetRequest getRequest = new GetRequest(indexName).id(id);
        final GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        refreshIndices();

        final String sourceAsString = getResponse.getSourceAsString();

        return esObjectMapper.readValue(sourceAsString, clazz);
    }

    @SneakyThrows
    protected void refreshIndices() {
        final RefreshRequest refreshRequest = new RefreshRequest();
        client.indices().refresh(refreshRequest, RequestOptions.DEFAULT);
    }
}
