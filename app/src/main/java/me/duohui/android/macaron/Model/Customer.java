package me.duohui.android.macaron.Model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Customer extends Member implements Serializable {

    private int customerNumber;
    private String customerName;
    private String nickname;
    private String email;
    private double lat;
    private double lon;
    private String address;
    private String gu;

}

