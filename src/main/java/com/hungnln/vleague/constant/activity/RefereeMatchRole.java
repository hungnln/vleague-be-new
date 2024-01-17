package com.hungnln.vleague.constant.activity;

public enum RefereeMatchRole {
    HeadReferee,
    AssistantReferee,
    MonitoringReferee;
    public static RefereeMatchRole lookup(int input){
        for (RefereeMatchRole type: values()){
            if (type.ordinal() == input) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid input: " + input);
    }
}
