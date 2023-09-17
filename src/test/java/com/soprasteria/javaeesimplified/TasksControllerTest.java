package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.SampleModelData;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class TasksControllerTest {

    private final SampleModelData sampleTaskData = new SampleModelData(System.currentTimeMillis());

    @Test
    void shouldIncludeCreatedTask() {
        var controller = new TasksController(new TasksDao(new HashMap<>()));
        var task = sampleTaskData.sampleTodoDto();
        controller.createTask(task);
        assertThat(controller.listTasks())
                .filteredOn(id -> id.getId().equals(task.getId()))
                .singleElement()
                .usingRecursiveComparison()
                .isEqualTo(task);
    }
  
}