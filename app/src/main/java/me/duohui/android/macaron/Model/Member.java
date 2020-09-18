package me.duohui.android.macaron.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Member {
    private int memberNumber;
    private int type;
    private String id;
    private String password;
}
