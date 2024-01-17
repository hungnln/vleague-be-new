package com.hungnln.vleague.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hungnln.vleague.constant.activity.RefereeMatchRole;
import com.hungnln.vleague.entity.key.RefereeMatchParticipationKey;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "refereematchparticipation")
@Builder
@Data
@ToString
public class RefereeMatchParticipation implements Serializable {
    @EmbeddedId
    @JsonIgnore
    private RefereeMatchParticipationKey id;
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("refereeId")
    @JoinColumn(name = "refereeid")
    private Referee referee;
    @MapsId("matchId")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Include
    @ToString.Include
    @JoinColumn(name = "matchid")
    private Match match;
    @Column(name = "role")
    private RefereeMatchRole role;
}
