package org.example.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import org.example.request.Components;

public class ComponentsResponse {
    @Getter
    @Setter
    public String success;

    @Getter
    @Setter
    public List<Components> data;

    public ComponentsResponse() { }

    public ComponentsResponse(String success, List<Components> data) {
        this.success = success;
        this.data = data;
    }
}

