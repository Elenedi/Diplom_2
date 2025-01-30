package org.example.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

public class OrderModel {
    @Getter
    @Setter
    List<String> components;

    public OrderModel() { }

    public OrderModel(List<String> components) {
        this.components = this.components;
    }
}

