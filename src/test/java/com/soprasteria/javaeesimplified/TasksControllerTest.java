package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.SampleModelData;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class TasksControllerTest {

    private final SampleModelData sampleTaskData = new SampleModelData(System.currentTimeMillis());

    private final DbContext dbContext = new DbContext();
    private DbContextConnection dbContextConnection;

    @BeforeEach
    void startConnection() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUser("postgres");
        Flyway.configure().dataSource(dataSource).load().migrate();
        dbContextConnection = dbContext.startConnection(dataSource);
    }

    @AfterEach
    void endConnection() {
        dbContextConnection.close();
    }


    @Test
    void shouldIncludeCreatedTask() {
        var controller = new TasksController(new TasksDao(new HashMap<>(), dbContext));
        var task = sampleTaskData.sampleTodoDto();
        controller.createTask(task);
        assertThat(controller.listTasks())
                .filteredOn(id -> id.getId().equals(task.getId()))
                .singleElement()
                .usingRecursiveComparison()
                .isEqualTo(task);
    }
  
}