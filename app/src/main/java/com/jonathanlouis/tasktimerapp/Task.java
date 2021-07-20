package com.jonathanlouis.tasktimerapp;

import java.io.Serializable;

class Task implements Serializable {

    //--fields--
    public static final long serialVersionUID = 20200503L;

    private long id;
    private final String name;
    private final String description;
    private final int sortOrder;

    //--constructor--
    public Task(long id, String name, String description, int sortOrder) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    //--methods--
    long getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    int getSortOrder() {
        return sortOrder;
    }

    void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
