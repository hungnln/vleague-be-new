package com.hungnln.vleague.repository;

import com.hungnln.vleague.entity.Match;
import com.hungnln.vleague.entity.StaffMatchParticipation;
import com.hungnln.vleague.entity.key.StaffMatchParticipationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffMatchParticipationRepository extends JpaRepository<StaffMatchParticipation, StaffMatchParticipationKey> {
List<StaffMatchParticipation> findAllByMatch(Match match);
}
