package com.api.demo.project.storage;

import com.api.demo.project.engine.RestResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class MainResponseStorage {

    private String payload;
    private RestResponse<?> restResponse;

}
