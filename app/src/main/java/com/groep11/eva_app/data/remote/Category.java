package com.groep11.eva_app.data.remote;

public class Category {
    private String name;

    public Category() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return new StringBuilder()
                .append("name: ").append(name).toString();
    }
}
