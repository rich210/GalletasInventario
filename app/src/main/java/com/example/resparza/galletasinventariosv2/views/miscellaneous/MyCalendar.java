package com.example.resparza.galletasinventariosv2.views.miscellaneous;

/**
 * Created by resparza on 07/06/2017.
 */

public class MyCalendar {
    private String name;
    private String id;
    private String accountType;
    private String accountOwner;

    public MyCalendar(String _name, String _id) {
        name = _name;
        id = _id;
    }

    public MyCalendar() {
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }

    @Override
    public String toString() {
        return "MyCalendar{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", accountType='" + accountType + '\'' +
                ", accountOwner='" + accountOwner + '\'' +
                '}';
    }
}
