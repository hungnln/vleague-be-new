package com.hungnln.vleague.service;

import com.hungnln.vleague.DTO.TournamentCreateDTO;
import com.hungnln.vleague.DTO.TournamentUpdateDTO;
import com.hungnln.vleague.constant.activity.ActivityType;
import com.hungnln.vleague.constant.activity.ClubStandingRole;
import com.hungnln.vleague.constant.tournament.TournamentFailMessage;
import com.hungnln.vleague.constant.tournament.TournamentSuccessMessage;
import com.hungnln.vleague.entity.Club;
import com.hungnln.vleague.entity.Match;
import com.hungnln.vleague.entity.MatchActivity;
import com.hungnln.vleague.entity.Tournament;
import com.hungnln.vleague.exceptions.ExistException;
import com.hungnln.vleague.exceptions.ListEmptyException;
import com.hungnln.vleague.exceptions.NotFoundException;
import com.hungnln.vleague.repository.TournamentRepository;
import com.hungnln.vleague.response.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private MatchService matchService;
    private final ModelMapper modelMapper;

    public ResponseWithTotalPage<TournamentResponse> getAllTournaments(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        Page<Tournament> pageResult = tournamentRepository.findAll(pageable);
        ResponseWithTotalPage<TournamentResponse> response = new ResponseWithTotalPage<>();
        List<TournamentResponse> tournamentList = new ArrayList<>();
        if (pageResult.hasContent()) {
            for (Tournament tournament :
                    pageResult.getContent()) {
                TournamentResponse tournamentResponse = modelMapper.map(tournament, TournamentResponse.class);
                tournamentList.add(tournamentResponse);
            }

        }
        response.setData(tournamentList);
        PaginationResponse paginationResponse = PaginationResponse.builder()
                .pageIndex(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalCount((int) pageResult.getTotalElements())
                .totalPage(pageResult.getTotalPages())
                .build();
        response.setPagination(paginationResponse);
        return response;
    }

    public TournamentResponse addTournament(TournamentCreateDTO tournamentCreateDTO) {
        Optional<Tournament> tournament = tournamentRepository.findTournamentByName(tournamentCreateDTO.getName());
        if (tournament.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            Tournament tmp = Tournament.builder()
                    .id(uuid)
                    .start(tournamentCreateDTO.getStart())
                    .name(tournamentCreateDTO.getName())
                    .end(tournamentCreateDTO.getEnd())
                    .build();
            tournamentRepository.save(tmp);
            return modelMapper.map(tmp, TournamentResponse.class);
        } else {
            throw new ExistException(TournamentFailMessage.TOURNAMENT_EXIST);
        }
    }

    public TournamentResponse getTournamentById(UUID id) {
        Tournament tournament = tournamentRepository.findTournamentById(id).orElseThrow(() -> new NotFoundException(TournamentFailMessage.TOURNAMENT_NOT_FOUND));
        return modelMapper.map(tournament, TournamentResponse.class);
    }

    public String deleteTournament(UUID id) {
        boolean exists = tournamentRepository.existsById(id);
        if (exists) {
            tournamentRepository.deleteById(id);
            return TournamentSuccessMessage.REMOVE_TOURNAMENT_SUCCESSFULL;
        } else {
            return TournamentFailMessage.DELETE_TOURNAMENT_FAIL;
        }

    }

    public TournamentResponse updateTournament(UUID id, TournamentUpdateDTO tournamentUpdateDTO) {
        Tournament tournament = tournamentRepository.findTournamentById(id).orElseThrow(() -> new NotFoundException(TournamentFailMessage.TOURNAMENT_NOT_FOUND));
        tournament.setName(tournamentUpdateDTO.getName());
        tournament.setStart(tournamentUpdateDTO.getStart());
        tournament.setEnd(tournamentUpdateDTO.getEnd());
        tournamentRepository.save(tournament);
        return modelMapper.map(tournament, TournamentResponse.class);
    }

    public List<TournamentStandingResponse> getTournamentStanding(UUID id) {
        List<Match> matchList = matchService.getMatchListByTournament(id);
        List<TournamentStandingResponse> list = new ArrayList<>();
        matchList.forEach(match -> {
            MatchStatisticResponse matchStatisticResponse = matchService.getMatchStatisticById(match.getId());
            int homeGoal = matchStatisticResponse.getMatchStatistic().get(match.getHomeClub().getId()).get(ActivityType.Goal);
            int awayGoal = matchStatisticResponse.getMatchStatistic().get(match.getAwayClub().getId()).get(ActivityType.Goal);
            if (list.stream().noneMatch(response -> response.getClub().getId().equals(match.getHomeClub().getId()))) {
                list.add(new TournamentStandingResponse(match.getHomeClub()));
            }

            // Check if awayClub is in the list, if not, add a new TournamentStandingResponse
            if (list.stream().noneMatch(response -> response.getClub().getId().equals(match.getAwayClub().getId()))) {
                list.add(new TournamentStandingResponse(match.getAwayClub()));
            }
            list.forEach(tournamentStandingResponse -> {
                if (tournamentStandingResponse.getClub().getId() == match.getHomeClub().getId()) {
                    int point = getMatchPoint(homeGoal, awayGoal, true);
                    tournamentStandingResponse.setMp(tournamentStandingResponse.getMp() + 1);
                    if (point == 3) {
                        tournamentStandingResponse.setW(tournamentStandingResponse.getW() + 1);
                        tournamentStandingResponse.addToLast5(ClubStandingRole.WIN);
                    } else if (point == 1) {
                        tournamentStandingResponse.setD(tournamentStandingResponse.getD() + 1);
                        tournamentStandingResponse.addToLast5(ClubStandingRole.DRAW);
                    } else {
                        tournamentStandingResponse.setL(tournamentStandingResponse.getL() + 1);
                        tournamentStandingResponse.addToLast5(ClubStandingRole.LOSS);
                    }
                    tournamentStandingResponse.setGf(tournamentStandingResponse.getGf() + homeGoal);
                    tournamentStandingResponse.setGa(tournamentStandingResponse.getGd() + awayGoal);
                    tournamentStandingResponse.setGd(tournamentStandingResponse.getGf() - tournamentStandingResponse.getGa());
                    tournamentStandingResponse.setPts(tournamentStandingResponse.getPts() + point);

                } else if (tournamentStandingResponse.getClub().getId() == match.getAwayClub().getId()) {
                    int point = getMatchPoint(homeGoal, awayGoal, false);
                    tournamentStandingResponse.setMp(tournamentStandingResponse.getMp() + 1);
                    if (point == 3) {
                        tournamentStandingResponse.setW(tournamentStandingResponse.getW() + 1);
                        tournamentStandingResponse.addToLast5(ClubStandingRole.WIN);
                    } else if (point == 1) {
                        tournamentStandingResponse.setD(tournamentStandingResponse.getD() + 1);
                        tournamentStandingResponse.addToLast5(ClubStandingRole.DRAW);
                    } else {
                        tournamentStandingResponse.setL(tournamentStandingResponse.getL() + 1);
                        tournamentStandingResponse.addToLast5(ClubStandingRole.LOSS);
                    }
                    tournamentStandingResponse.setGf(tournamentStandingResponse.getGf() + awayGoal);
                    tournamentStandingResponse.setGa(tournamentStandingResponse.getGd() + homeGoal);
                    tournamentStandingResponse.setGd(tournamentStandingResponse.getGf() - tournamentStandingResponse.getGa());
                    tournamentStandingResponse.setPts(tournamentStandingResponse.getPts() + point);
                }
            });
        });
        return list;
    }

    private int getMatchPoint(int homeGoal, int awayGoal, boolean isHome) {
        if (homeGoal < awayGoal) {
            if (isHome) return 0;
            return 3;
        } else if (homeGoal == awayGoal) {
            return 1;
        } else {
            if (isHome) return 3;
            return 0;
        }
    }

    public TournamentRankingResponse getTournamentRanking(UUID id) {
        List<PlayerRankingResponse> yellowCards = new ArrayList<>();
        List<PlayerRankingResponse> redCards = new ArrayList<>();
        List<PlayerRankingResponse> goals = new ArrayList<>();
        TournamentRankingResponse tournamentRankingResponse = new TournamentRankingResponse();
        List<Match> matchList = matchService.getMatchListByTournament(id);
        List<TournamentStandingResponse> list = new ArrayList<>();
        matchList.forEach(match -> match.getActivities().forEach(matchActivity -> {
                    if (matchActivity.getType() == ActivityType.RedCard) {
                        matchActivity.getPlayerMatchParticipations().forEach(playerMatchParticipation -> {
                            if (redCards.stream().noneMatch(response -> response.getPlayer().getId().equals(playerMatchParticipation.getPlayerContract().getPlayer().getId()))) {
                                redCards.add(new PlayerRankingResponse(playerMatchParticipation.getPlayerContract().getPlayer(), 0));
                            }
                            redCards.stream().filter(response -> response.getPlayer().getId().equals(playerMatchParticipation.getPlayerContract().getPlayer().getId()))
                                    .findFirst().ifPresent(redCard -> {
                                        redCard.setCount(redCard.getCount() + 1);
                                        redCard.setPlayer(playerMatchParticipation.getPlayerContract().getPlayer());
                                    });
                        });
                    }
                    if (matchActivity.getType() == ActivityType.Goal) {
                        matchActivity.getPlayerMatchParticipations().forEach(playerMatchParticipation -> {
                            if (goals.stream().noneMatch(response -> response.getPlayer().getId().equals(playerMatchParticipation.getPlayerContract().getPlayer().getId()))) {
                                goals.add(new PlayerRankingResponse(playerMatchParticipation.getPlayerContract().getPlayer(), 0));
                            }
                            goals.stream().filter(response -> response.getPlayer().getId().equals(playerMatchParticipation.getPlayerContract().getPlayer().getId()))
                                    .findFirst().ifPresent(goal -> {
                                        goal.setCount(goal.getCount() + 1);
                                        goal.setPlayer(playerMatchParticipation.getPlayerContract().getPlayer());
                                    });
                        });
                    }
                    if (matchActivity.getType() == ActivityType.YellowCard) {
                        matchActivity.getPlayerMatchParticipations().forEach(playerMatchParticipation -> {
                            if (yellowCards.stream().noneMatch(response -> response.getPlayer().getId().equals(playerMatchParticipation.getPlayerContract().getPlayer().getId()))) {
                                yellowCards.add(new PlayerRankingResponse(playerMatchParticipation.getPlayerContract().getPlayer(), 0));
                            }
                            yellowCards.stream().filter(response -> response.getPlayer().getId().equals(playerMatchParticipation.getPlayerContract().getPlayer().getId()))
                                    .findFirst().ifPresent(yellowCard -> {
                                        yellowCard.setCount(yellowCard.getCount() + 1);
                                        yellowCard.setPlayer(playerMatchParticipation.getPlayerContract().getPlayer());
                                    });
                        });
                    }
                }
        ));
        tournamentRankingResponse.setRedCards(redCards);
        tournamentRankingResponse.setGoals(goals);
        tournamentRankingResponse.setYellowCards(yellowCards);
        return tournamentRankingResponse;
    };

}
