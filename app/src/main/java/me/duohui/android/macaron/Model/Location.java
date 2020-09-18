package me.duohui.android.macaron.Model;

import lombok.Data;

@Data
public class Location {
    private int customerNumber;
    private double lat;
    private double lon;
    private String address;
    private String gu;

}
