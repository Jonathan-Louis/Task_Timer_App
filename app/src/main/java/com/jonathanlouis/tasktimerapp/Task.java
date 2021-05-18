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
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setId(long id) {
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
