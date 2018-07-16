package com.curisprofound.tddwebstack.assertions;

import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class AssertOnDb {
    public static H2Assertions ForH2(JdbcTemplate jdbcTemplate) {
        return new H2Assertions(jdbcTemplate);
    }

    ;

    public static MongoAssertions ForMongo(ReactiveMongoTemplate mongoTemplate) {
        return new MongoAssertions(mongoTemplate);
    }


    public static class MongoAssertions extends Assertions<MongoAssertions> {

        private final ReactiveMongoTemplate mongoTemplate;
        private Class<?> refClass;

        public MongoAssertions(ReactiveMongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }


        public MongoAssertions collectionExists(String collectionName) {
            return checkExistence(
                    "Collection " + collectionName,
                    mongoTemplate.collectionExists(collectionName).block()
            );
        }

        public Flux<?> getAll(){
            return mongoTemplate.findAll(refClass);
        }

        private MongoAssertions checkExistence(String subject, boolean actual) {
            String msg = subject +
                    (not ? " exists " : " doesn't exist ") +
                    "in the database";
            if (not)
                assertFalse(msg, actual);
            else
                assertTrue(msg, actual);
            return this;

        }

        public MongoAssertions collectionExists(Class<?> clazz) {
            refClass = clazz;
            return checkExistence(
                    "Collection " + clazz.getName(),
                    mongoTemplate.collectionExists(clazz).block()
            );
        }


        public <T> Flux<T> getDocuments(Class<T> clazz) {
            return mongoTemplate.findAll(clazz);
        }


        public static class CollectionAssertions extends Assertions<CollectionAssertions> {

            private final ReactiveMongoTemplate mongoTemplate;

            private final String collectionName;
            private MongoCollection<Document> collection;


            public CollectionAssertions(ReactiveMongoTemplate mongoTemplate, String name) {
                this.mongoTemplate = mongoTemplate;
                this.collectionName = name;
            }


        }

    }

    public static class H2Assertions {

        private final JdbcTemplate jdbcTemplate;

        public H2Assertions(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        public TableAssertions Table(String tableName) {
            return new TableAssertions(tableName, jdbcTemplate);
        }


        public static class TableAssertions extends Assertions<TableAssertions> {

            private final String tableName;
            private final JdbcTemplate db;

            public TableAssertions(String tableName, JdbcTemplate jdbcTemplate) {
                this.tableName = tableName;
                this.db = jdbcTemplate;
            }

            public TableAssertions exists() {
                String msg = tableName + (not ? "" : " not") + " Found in Database";
                boolean actual = checkExistence().stream().findAny().isPresent();
                if (not)
                    assertFalse(msg, actual);
                else
                    assertTrue(msg, actual);
                return chain();
            }

            public TableAssertions hasForeignKeyTo(String targetTable) {
                String msg = tableName +
                        (not ? " has" : " doesn't have") + " a foreign key to " + targetTable;
                boolean actual = getTableConstraints()
                        .stream()
                        .filter(c -> c.getOrDefault("CONSTRAINT_TYPE", "")
                                .equals("REFERENTIAL"))
                        .anyMatch(c -> ((String) c.getOrDefault("SQL", ""))
                                .contains(targetTable.toUpperCase()));
                if (not)
                    assertFalse(msg, actual);
                else
                    assertTrue(msg, actual);
                return chain();
            }

            public TableAssertions hasColumnsByName(String... names) {
                Optional<String> name = checkColumnsByName(not, names);
                assertFalse(
                        tableName + " has no column by name of " + name.orElse(""),
                        name.isPresent()
                );
                return this;
            }


            private Optional<String> checkColumnsByName(boolean not, String... names) {
                List<Map<String, Object>> columnNames = getColumns();
                for (String name : names)
                    if (!not && !hasColumnByName(columnNames, name))
                        return Optional.of(name);
                    else if (not && hasColumnByName(columnNames, name))
                        return Optional.of(name);
                return Optional.empty();
            }

            private boolean hasColumnByName(List<Map<String, Object>> columnNames, String name) {
                return columnNames
                        .stream()
                        .anyMatch(c -> ((String) c.getOrDefault("FIELD", ""))
                                .equalsIgnoreCase(name));
            }

            private List<Map<String, Object>> getTableConstraints() {
                return db.query(
                        "SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS where table_name = ?",
                        new Object[]{tableName.toUpperCase()},
                        new ColumnMapRowMapper()
                );
            }


            private List<Map<String, Object>> checkExistence() {
                return db.query(
                        "select * from information_schema.indexes where table_name = ?",
                        new Object[]{tableName.toUpperCase()},
                        new ColumnMapRowMapper()
                );
            }

            private List<Map<String, Object>> getColumns() {
                return db.query(
                        "show columns from " + tableName.toUpperCase(), new ColumnMapRowMapper());
            }


        }
    }
}
