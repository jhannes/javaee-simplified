package com.soprasteria.simplejavaee;

import com.soprasteria.simplejavaee.infrastructure.ApplicationDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.postgresql.ds.PGSimpleDataSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ExtendWith(TestDataSource.Extension.class)
public @interface TestDataSource {
    class Extension implements org.junit.jupiter.api.extension.Extension, BeforeEachCallback, AfterEachCallback {

        public static final PGSimpleDataSource PG_SIMPLE_DATA_SOURCE = new PGSimpleDataSource();
        static {
            PG_SIMPLE_DATA_SOURCE.setURL("jdbc:postgresql://localhost:5432/postgres");
            PG_SIMPLE_DATA_SOURCE.setUser("postgres");

            Flyway.configure().dataSource(PG_SIMPLE_DATA_SOURCE).load().migrate();
        }


        @Override
        public void beforeEach(ExtensionContext context) throws Exception {
            context.getStore(ExtensionContext.Namespace.GLOBAL)
                    .put("connectionContext", ApplicationDataSource.beginConnection(PG_SIMPLE_DATA_SOURCE));
        }

        @Override
        public void afterEach(ExtensionContext context) throws Exception {
            context.getStore(ExtensionContext.Namespace.GLOBAL)
                    .get("connectionContext", AutoCloseable.class)
                    .close();
        }
    }
}
