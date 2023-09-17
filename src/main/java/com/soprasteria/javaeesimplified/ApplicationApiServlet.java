package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.SampleModelData;
import com.soprasteria.generated.javaeesimplified.model.TodoDto;
import com.soprasteria.generated.javaeesimplified.model.UpdateTaskStatusRequestDto;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jsonbuddy.parse.JsonParser;
import org.jsonbuddy.pojo.JsonGenerator;
import org.jsonbuddy.pojo.PojoMapper;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class ApplicationApiServlet extends HttpServlet {

    private final JsonGenerator jsonb = new JsonGenerator();
    private final PojoMapper mapper = new PojoMapper();
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var tasksPattern = Pattern.compile("/tasks/(?<id>[0-9a-fA-F-]+)");
        Matcher matcher;
        if ((matcher = tasksPattern.matcher(req.getPathInfo())).matches()) {
            var id = UUID.fromString(matcher.group("id"));
            var requestJson = JsonParser.parse(req.getInputStream());
            var requestBody = (UpdateTaskStatusRequestDto) mapper.mapToPojo(requestJson, UpdateTaskStatusRequestDto.class);
            var task = tasks.get(id);
            if (task == null) {
                resp.sendError(404);
                return;
            }
            if (requestBody.getStatus() == null) {
                resp.sendError(400);
                return;
            }
            task.setStatus(requestBody.getStatus());
            resp.setStatus(204);
        } else {
            resp.sendError(404);
        }
    }
}
