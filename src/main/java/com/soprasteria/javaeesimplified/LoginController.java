package com.soprasteria.javaeesimplified;

import com.soprasteria.generated.javaeesimplified.model.SampleModelData;
import com.soprasteria.generated.javaeesimplified.model.UserinfoDto;

public class LoginController {
    public UserinfoDto getUserinfo() {
        var sampleData = new SampleModelData(2);
        return sampleData.sampleUserinfoDto()
                .displayName(sampleData.randomPersonName());
    }
}
