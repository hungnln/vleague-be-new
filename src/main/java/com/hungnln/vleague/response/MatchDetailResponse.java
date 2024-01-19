package com.hungnln.vleague.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hungnln.vleague.entity.Club;
import com.hungnln.vleague.entity.MatchActivity;
import com.hungnln.vleague.entity.Round;
import com.hungnln.vleague.entity.Stadium;
import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchDetailResponse {
    private UUID id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private UUID homeClubId;
    private Club homeClub;
    private UUID awayClubId;
    private Club awayClub;
    private UUID stadiumId;
    private Stadium stadium;
    private UUID roundId;
    private Round round;
    private int homeGoals;
    private int awayGoals;
    private Collection<MatchActivity> activities;
}
