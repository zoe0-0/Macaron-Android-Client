package me.duohui.android.macaron.Model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Lineup implements Serializable {
    private int lineupNumber;
    private int row;
    private int col;
    private int level;
    private int menuNumber;
    private int shopNumber;
}

