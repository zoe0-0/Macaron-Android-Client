package me.duohui.android.macaron.Model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Menu implements Serializable {

    private int menuNumber;
    private String menuName;
    private int price;
    private int bookmark;
    private int shopNumber;

}
