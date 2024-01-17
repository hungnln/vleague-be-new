package com.hungnln.vleague.constant.activity;

public enum PlayerMatchRole {
    Forward,
    Midfielder,
    Defender,
    GoalKeeper;
    public static PlayerMatchRole lookup(int input){
        for (PlayerMatchRole type: values()){
            if (type.ordinal() == input) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid input: " + input);
    }
}
