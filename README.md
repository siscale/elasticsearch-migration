Elasticsearch Migration
========

A simple and lightweight migration tool for Elasticsearch database that's based on [Axel Fontaine's Flyway project](https://github.com/flyway/flyway).
Elasticsearch Migration works just like Flyway but using yaml files for describing changessets.

## Requirements
* Java (Tested with JDK 8+)

| Elasticsearch version | Tested with | Library version   
| --------------------- |------------ | ----------------- 
| 7.x.x                 | 7.4.0       | 1.3.1

Latest version
```
<dependency>
    <groupId>com.quandoo.lib</groupId>
    <artifactId>elasticsearch-migration</artifactId>
    <version>1.3.1</version>
</dependency>
```

## Indexes
These indexes are created on the first run and are there too keep track of the migrations.

### Migration version index (elasticsearch_migration_version)
Keeping track of the executed changesets. If a migration fails it will be transitioned to state 'FAILED' and the failureMessage field will contain the reason.
The entry won't be removed and the changes applied to this point will stay in the cluster. There is no automatic rollback which means that the cleaneup has to be done manually.

```javascript
  {
    "settings": {
        "number_of_shards": 3
    },
    "mappings": {
        "dynamic": "strict",
        "_source": {
            "enabled": true
        },
        "properties": {
            "identifier": {
                "type": "keyword",
                "index": true
            },
            "version": {
                "type": "keyword",
                "index": true
            },
            "name": {
                "type": "keyword",
                "index": true
            },
            "sha256Checksum": {
                "type": "keyword",
                "index": true
            },
            "state": {
                "type": "keyword",
                "index": true
            },
            "failureMessage": {
                "type": "text",
                "index": true
            },
            "created": {
                "type": "date",
                "format": "date_time",
                "index": true
            }
        }
    }
}
```

### Migration lock index (elasticsearch_migration_lock)
Used to create a pessimistic lock during the migration so only one client makes changes at a time. 
In case the migration is aborted for any reason the lock won't be removed and has to be removed manually.

```javascript
{
    "settings": {
        "number_of_shards": 3
    },
    "mappings": {
        "dynamic": "strict",
        "_source": {
            "enabled": true
        },
        "properties": {
            "created": {
                "type": "date",
                "format": "date_time",
                "index": true
            }
        }
    }
}
```

## YAML changesets
The changesets are defined with versioned yaml files (V{version}__{name}.yaml)(example: V1_0_0__singularity.yaml). 
The yaml files have to conform to this schema [YAML Schema](src/main/resources/schema/yaml/schema.json). 

Currently the following migration types are supported: 
 * CREATE_INDEX
 * UPDATE_INDEX_SETTINGS
 * DELETE_INDEX
 * CREATE_OR_UPDATE_INDEX_TEMPLATE
 * DELETE_INDEX_TEMPLATE
 * UPDATE_MAPPING
 * INDEX_DOCUMENT
 * UPDATE_DOCUMENT
 * DELETE_DOCUMENT
 * CREATE_INGEST_PIPELINE
 * ALIASES
 * REINDEX
 * DELETE_INGEST_PIPELINE

### Example changeset
```yaml
migrations:
  - type: CREATE_INDEX
    index: 'test_index'
    definition: >
      {
          "settings": {
              "number_of_shards": 3
          },
          "mappings": {
              "dynamic": false,
              "_source": {
                  "enabled": true
              },
              "properties": {
                  "user": {
                      "type": "keyword",
                      "index": true
                  },
                  "post_date": {
                      "type": "keyword",
                      "index": true
                  },
                  "message": {
                      "type": "keyword",
                      "index": true
                  }
              }
          }
      }
  - type: UPDATE_INDEX_SETTINGS
    index: 'test_index'
    definition: >
      {
          "index" : {
              "number_of_replicas" : 1
          }
      }
  - type: CREATE_OR_UPDATE_INDEX_TEMPLATE
    template: 'test_template'
    definition: >
      {
        "index_patterns": ["foo*", "bar*"],
        "settings": {
          "number_of_shards": 1
        },
        "mappings": {
          "properties": {
            "host_name": {
              "type": "keyword"
            },
            "created_at": {
              "type": "date",
              "format": "EEE MMM dd HH:mm:ss Z YYYY"
            }
          }
        }
      }
  - type: CREATE_INGEST_PIPELINE
    id: 'test_pipeline'
    definition: >
        {
          "description" : "rename xxx",
          "processors" : [
            {
              "rename": {
                "field": "xxx",
                "target_field": "yyy"
              }
            }
          ]
        }
  - type: ALIASES
    definition: >
        {
          "actions": [
            {
              "add": {
                "index": "test_index_1",
                "alias": "test_index_alias"
              }
            },
            {
              "add": {
                "index": "test_index_2",
                "alias": "test_index_alias"
              }
            }
          ]
        }
  - type: REINDEX
    definition: >
      {
        "source": {
          "index": "test_index_1"
        },
        "dest": {
          "index": "test_index_2"
        }
      }
  - type: UPDATE_MAPPING
    indices:
      - 'test_index'
    definition: >
      {
        "properties": {
          "email": {
            "type": "keyword"
          }
        }
      }
  - type: INDEX_DOCUMENT
    index: 'test_index'
    id: '1'
    definition: >
      {
          "user" : "kimchy",
          "post_date" : "2009-11-15T14:12:12",
          "message" : "trying out Elasticsearch"
      }
  - type: UPDATE_DOCUMENT
    index: 'test_index'
    id: '1'
    definition: >
      {
          "doc" : {
              "user" : "new_user"
          }
      }
  - type: DELETE_DOCUMENT
    index: 'test_index'
    id: '1'
  - type: DELETE_INDEX_TEMPLATE
    template: 'test_template'
  - type: DELETE_INDEX
    index: 'test_index'
  - type: DELETE_INGEST_PIPELINE
    id: 'test_pipeline'
```

## Usage
Each service has to define an identifier which will identify the owner of the indexes, templates, documents etc. and locks in the ES cluster. 
The easiest way is to give the identifier the service name which owns it.

Example:
```java
final ElasticsearchMigration elasticsearchMigration = new ElasticsearchMigration(
  ElasticsearchMigrationConfig.builder( 
    "test-service", 
    ElasticsearchConfig.builder(new URL("http://localhost:9200"))
        .username("user")
        .password("password")
        .build()
  )
    .basePackage("migration.es")
    .build()
);

elasticsearchMigration.migrate();
```

## Migration from previous un-managed schema
1. Collect all your schema in one yaml changeset.
2. Create 'Migration version index' and 'Migration lock index' using the schemas from above or from the source tree
3. Startup your application manually. After it's started there will be one entry in the 'elasticsearch_migration_version' index. Copy this entry over to your staging/production ES cluster.

## Improvements
* In case a migration is aborted in the middle the lock stays there forever. Unlock it after a TTL.
* Figure out the number of shards and make use of wait_for_active_shards for maxiumum consistency
* Add more functionality

## Limitations
* The tool does not roll back the database upon migration failure. You're expected to manually restore backup.

## License
Apache License, Version 2.0
