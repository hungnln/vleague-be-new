package com.hungnln.vleague.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hungnln.vleague.constant.activity.StaffMatchRole;
import com.hungnln.vleague.entity.key.StaffMatchParticipationKey;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "staffmatchparticipation")
@Builder
@Data
@ToString
public class StaffMatchParticipation implements Serializable {
    @EmbeddedId
    @JsonIgnore
    private StaffMatchParticipationKey id;
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("staffContractId")
    @JoinColumn(name = "staffcontractid")
    private StaffContract staffContract;
    @MapsId("matchId")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Include
    @ToString.Include
    @JoinColumn(name = "matchid")
    private Match match;
    @Column(name = "role")
    private StaffMatchRole role;
}
