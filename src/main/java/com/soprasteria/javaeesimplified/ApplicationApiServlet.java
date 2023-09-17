package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.SampleModelData;
import com.soprasteria.generated.javaeesimplified.model.TodoDto;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jsonbuddy.pojo.JsonGenerator;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

class ApplicationApiServlet extends HttpServlet {

    private final JsonGenerator jsonb = new JsonGenerator();
    private final Map<UUID, TodoDto> tasks;

    public ApplicationApiServlet() {
        var sampleData = new SampleModelData(2);
        tasks = sampleData.sampleList(sampleData::sampleTodoDto, 5, 20)
                .stream().collect(Collectors.toMap(TodoDto::getId, todo -> todo));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getPathInfo().equals("/tasks")) {
            var response = tasks.values();
            resp.setContentType("application/json");
            jsonb.generateNode(response).toJson(resp.getWriter());
        } else if (req.getPathInfo().equals("/login")) {
            var sampleData = new SampleModelData(2);
            var response = sampleData.sampleUserinfoDto()
                    .displayName(sampleData.randomPersonName());
            resp.setContentType("application/json");
            jsonb.generateNode(response).toJson(resp.getWriter());
        }
    }
}
