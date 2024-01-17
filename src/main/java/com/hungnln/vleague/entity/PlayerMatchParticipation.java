package com.hungnln.vleague.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hungnln.vleague.constant.activity.PlayerMatchRole;
import com.hungnln.vleague.entity.key.PlayerMatchParticipationKey;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "playermatchparticipation")
@Builder
@Data
@ToString
public class PlayerMatchParticipation implements Serializable {
    @EmbeddedId
    @JsonIgnore
    private PlayerMatchParticipationKey id;
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("playerContractId")
    @JoinColumn(name = "playercontractid")
    private PlayerContract playerContract;
    @MapsId("matchId")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Include
    @ToString.Include
    @JoinColumn(name = "matchid")
    private Match match;
    @Column(name = "inlineups")
    private boolean inLineups;
    @Column(name = "role")
    private PlayerMatchRole role;
    @ManyToMany(mappedBy = "playerMatchParticipations",cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Collection<MatchActivity> activities;
}
