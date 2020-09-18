package me.duohui.android.macaron.Model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Showcase implements Serializable{
    private int shopNumber;
    private int row;
    private int col;
}
