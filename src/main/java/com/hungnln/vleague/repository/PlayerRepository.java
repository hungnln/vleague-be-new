package com.hungnln.vleague.repository;

import com.hungnln.vleague.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    List<Player> findAll();
    Optional<Player> findPlayerById(UUID id);
    Optional<Player> findPlayerByName(String name);
}
