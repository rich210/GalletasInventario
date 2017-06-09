package com.example.resparza.galletasinventariosv2.views.miscellaneous;

/**
 * Created by resparza on 07/06/2017.
 */

public class MyCalendar {
    private String name;
    private String id;

    public MyCalendar(String _name, String _id) {
        name = _name;
        id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
