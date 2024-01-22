package com.hungnln.vleague.entity;

import com.hungnln.vleague.constant.activity.ActivityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "matchactivities")
@Builder
@Data
@ToString
public class  MatchActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name ="matchid")
    private Match match;
    @Column(name = "type")
    private ActivityType type;
    @Column(name = "minuteinmatch")
    private int minuteInMatch;
//    @Temporal(TemporalType.TIME)
//    @Column(name = "time")
//    private Date time;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "matchactivityplayermatchparticipation",
            joinColumns = @JoinColumn(name = "activitiesid"),
            inverseJoinColumns = {
                    @JoinColumn(name = "playerinvolvedmatchid"),
                    @JoinColumn(name = "playerinvolvedplayercontractid")

            })
    private Collection<PlayerMatchParticipation> playerMatchParticipations;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "matchactivitystaffmatchparticipation",
            joinColumns = @JoinColumn(name = "activitiesid"),
            inverseJoinColumns = {
                    @JoinColumn(name = "staffinvolvedmatchid"),
                    @JoinColumn(name = "staffinvolvedstaffcontractid")
            })
    private Collection<StaffMatchParticipation> staffMatchParticipations;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "matchactivityrefereematchparticipation",
            joinColumns = @JoinColumn(name = "activitiesid"),
            inverseJoinColumns = {
                    @JoinColumn(name = "refereeinvolvedmatchid"),
                    @JoinColumn(name = "refereeinvolvedrefereeid")
            })
    private Collection<RefereeMatchParticipation> refereeMatchParticipations;
}
