package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.SampleModelData;
import com.soprasteria.generated.javaeesimplified.model.UserinfoDto;
import org.actioncontroller.actions.GET;
import org.actioncontroller.values.json.JsonBody;

public class LoginController {

    @GET("/login")
    @JsonBody
    public UserinfoDto getUserinfo() {
        var sampleData = new SampleModelData(2);
        return sampleData.sampleUserinfoDto()
                .displayName(sampleData.randomPersonName());
    }
}
