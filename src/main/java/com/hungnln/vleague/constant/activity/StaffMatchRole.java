package com.hungnln.vleague.constant.activity;

public enum StaffMatchRole {
    HeadCoach,
    AssistantCoach,
    MedicalTeam;
    public static StaffMatchRole lookup(int input){
        for (StaffMatchRole type: values()){
            if (type.ordinal() == input) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid input: " + input);
    }

}
