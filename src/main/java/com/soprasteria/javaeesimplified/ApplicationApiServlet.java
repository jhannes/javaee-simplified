package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.TodoDto;
import com.soprasteria.generated.javaeesimplified.model.UpdateTaskStatusRequestDto;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.actioncontroller.exceptions.HttpActionException;
import org.jsonbuddy.parse.JsonParser;
import org.jsonbuddy.pojo.JsonGenerator;
import org.jsonbuddy.pojo.PojoMapper;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ApplicationApiServlet extends HttpServlet {

    private final TasksController tasksController;
    private final LoginController loginController = new LoginController();

    private final JsonGenerator jsonb = new JsonGenerator();
    private final PojoMapper mapper = new PojoMapper();

    public ApplicationApiServlet(Map<UUID, TodoDto> tasks) {
        tasksController = new TasksController(tasks);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getPathInfo().equals("/tasks")) {
            var response = tasksController.listTasks();
            resp.setContentType("application/json");
            jsonb.generateNode(response).toJson(resp.getWriter());
        } else if (req.getPathInfo().equals("/login")) {
            var response = loginController.getUserinfo();
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
            try {
                tasksController.updateTask(id, requestBody);
                resp.setStatus(204);
            } catch (HttpActionException ex) {
                resp.sendError(ex.getStatusCode(), ex.getMessage());
            }
        } else {
            resp.sendError(404);
        }
    }
}
