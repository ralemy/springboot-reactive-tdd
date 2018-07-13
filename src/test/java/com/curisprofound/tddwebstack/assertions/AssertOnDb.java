package com.curisprofound.tddwebstack.assertions;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssertOnDb {
    public static H2Assertions ForH2(JdbcTemplate jdbcTemplate){
        return new H2Assertions(jdbcTemplate);
    };


    public static class H2Assertions{

        private final JdbcTemplate jdbcTemplate;

        public H2Assertions(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        public TableAssertions Table(String tableName){
            return new TableAssertions(tableName, jdbcTemplate);
        }



        public static class TableAssertions{

            private final String tableName;
            private final JdbcTemplate db;

            public TableAssertions(String tableName, JdbcTemplate jdbcTemplate) {
                this.tableName = tableName;
                this.db = jdbcTemplate;
            }

            public TableAssertions exists(){
                assertTrue(
                        tableName + " not Found in Database",
                        checkExistence().stream().findAny().isPresent()
                );
                return this;
            }

            public TableAssertions hasForeignKeyTo(String targetTable){
                assertTrue(
                        tableName + " does not have a foreign key to " + targetTable,
                        getTableConstraints()
                                 .stream()
                                 .filter(c -> c.getOrDefault("CONSTRAINT_TYPE", "")
                                         .equals("REFERENTIAL"))
                                 .anyMatch(c -> ((String) c.getOrDefault("SQL", ""))
                                         .contains(targetTable.toUpperCase()))
                );
                return this;
            }

            public TableAssertions hasNoForeignKeysTo(String targetTable){
                assertFalse(
                        tableName + " has a foreign key to " + targetTable,
                        getTableConstraints()
                                .stream()
                                .filter(c -> c.getOrDefault("CONSTRAINT_TYPE", "")
                                        .equals("REFERENTIAL"))
                                .anyMatch(c -> ((String) c.getOrDefault("SQL", ""))
                                        .contains(targetTable.toUpperCase()))
                );
                return this;
            }

            public TableAssertions hasColumnsByName(String... names){
                Optional<String> name = checkColumnsByName(names);
                assertFalse(
                        tableName + " has no column by name of " + name.orElse(""),
                        name.isPresent()
                );
                return this;
            }


            private Optional<String> checkColumnsByName(String... names){
                List<Map<String, Object>> columnNames = getColumns();
                for(String name : names)
                    if(!hasColumnByName(columnNames, name))
                        return Optional.of(name);
                return Optional.empty();
            }

            private boolean hasColumnByName(List<Map<String, Object>> columnNames, String name) {
                return columnNames
                        .stream()
                        .anyMatch(c-> ((String) c.getOrDefault("FIELD", ""))
                                .equalsIgnoreCase(name));
            }

            private List<Map<String, Object>> getTableConstraints() {
                return db.query(
                        "SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS where table_name = ?",
                        new Object[]{tableName.toUpperCase()},
                        new ColumnMapRowMapper()
                );
            }


            private List<Map<String,Object>> checkExistence() {
                return db.query(
                        "select * from information_schema.indexes where table_name = ?",
                        new Object[]{tableName.toUpperCase()},
                        new ColumnMapRowMapper()
                );
            }

            private List<Map<String, Object>> getColumns(){
                return db.query(
                        "show columns from " + tableName.toUpperCase(), new ColumnMapRowMapper());
            }




        }
    }
}
