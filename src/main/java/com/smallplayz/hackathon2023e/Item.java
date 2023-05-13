package com.smallplayz.hackathon2023e;

public class Item {
        private String name;
    private int id;

    Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}