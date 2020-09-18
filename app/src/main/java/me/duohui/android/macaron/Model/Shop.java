package me.duohui.android.macaron.Model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Shop extends Member implements Serializable{

    private int shopNumber;
    private String shopName;
    private String phoneNumber;
    private String sns;
    private String ownerName;
    private String email;
    private int col;
    private int row;
    private byte[] shopImage;
    private int open;
    private double lat;
    private double lon;
    private String address;
    private String gu;

    //추가된 부분
    private int bookmark; //현재 member의 북마크 여부
    private int distance; //현재 member 위치로부터의 거리(m)

}


