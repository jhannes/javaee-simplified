package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.SampleModelData;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jsonbuddy.pojo.JsonGenerator;

import java.io.IOException;

class ApplicationApiServlet extends HttpServlet {

    private final JsonGenerator jsonb = new JsonGenerator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var sampleData = new SampleModelData(-1);
        var response = sampleData.sampleListOfListTasks200ResponseDto();
        resp.setContentType("application/json");
        jsonb.generateNode(response).toJson(resp.getWriter());
    }
}
