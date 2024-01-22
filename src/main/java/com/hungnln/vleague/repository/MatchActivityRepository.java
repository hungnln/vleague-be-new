package com.hungnln.vleague.repository;

import com.hungnln.vleague.constant.activity.ActivityType;
import com.hungnln.vleague.entity.Match;
import com.hungnln.vleague.entity.MatchActivity;
import com.hungnln.vleague.entity.PlayerMatchParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface MatchActivityRepository extends JpaRepository<MatchActivity, UUID> {
    List<MatchActivity> findAllByMatch(Match match);

    int countAllByMatchAndType(Match match, ActivityType type);

    List<MatchActivity> findAllByMatchAndType(Match match,ActivityType type);

    List<MatchActivity> findAllByPlayerMatchParticipationsInAndMinuteInMatchBefore(Collection<PlayerMatchParticipation> playerMatchParticipations,int minuteInMatch);
}

