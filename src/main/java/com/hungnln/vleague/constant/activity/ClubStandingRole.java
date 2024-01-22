package com.hungnln.vleague.constant.activity;

public enum ClubStandingRole {
    WIN,
    NOT_PLAYED,
    LOSS,
    DRAW;
    public static ClubStandingRole lookup(int input){
        for (ClubStandingRole type: values()){
            if (type.ordinal() == input) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid input: " + input);
    }
}
