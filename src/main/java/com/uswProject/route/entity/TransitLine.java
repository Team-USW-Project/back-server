package com.uswProject.route.entity;

import lombok.Getter;

@Getter
public class TransitLine {
    private final String name;
    private final String direction;
    private final String providerId;

    public TransitLine(String name, String direction, String providerId) {
        this.name = validate(name);
        this.direction = validate(direction);
        this.providerId = validate(providerId);
    }

    private String validate(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        return input.trim();
    }
}
