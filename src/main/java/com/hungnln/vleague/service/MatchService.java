package com.hungnln.vleague.service;

import com.hungnln.vleague.DTO.*;
import com.hungnln.vleague.constant.activity.ActivityType;
import com.hungnln.vleague.constant.activity.PlayerMatchRole;
import com.hungnln.vleague.constant.activity.RefereeMatchRole;
import com.hungnln.vleague.constant.activity.StaffMatchRole;
import com.hungnln.vleague.constant.club.ClubFailMessage;
import com.hungnln.vleague.constant.match.MatchFailMessage;
import com.hungnln.vleague.constant.match.MatchSuccessMessage;
import com.hungnln.vleague.constant.player_contract.PlayerContractFailMessage;
import com.hungnln.vleague.constant.referee.RefereeFailMessage;
import com.hungnln.vleague.constant.round.RoundFailMessage;
import com.hungnln.vleague.constant.stadium.StadiumFailMessage;
import com.hungnln.vleague.constant.staff_contract.StaffContractFailMessage;
import com.hungnln.vleague.constant.tournament.TournamentFailMessage;
import com.hungnln.vleague.entity.*;
import com.hungnln.vleague.entity.key.PlayerMatchParticipationKey;
import com.hungnln.vleague.entity.key.RefereeMatchParticipationKey;
import com.hungnln.vleague.entity.key.StaffMatchParticipationKey;
import com.hungnln.vleague.exceptions.ListEmptyException;
import com.hungnln.vleague.exceptions.NotFoundException;
import com.hungnln.vleague.exceptions.NotValidException;
import com.hungnln.vleague.helper.MatchSpecification;
import com.hungnln.vleague.helper.RoundSpecification;
import com.hungnln.vleague.helper.SearchCriteria;
import com.hungnln.vleague.helper.SearchOperation;
import com.hungnln.vleague.repository.*;
import com.hungnln.vleague.response.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MatchService {
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private StadiumRepository stadiumRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private MatchActivityRepository matchActivityRepository;
    @Autowired
    private PlayerMatchParticipationRepository playerMatchParticipationRepository;
    @Autowired
    private StaffMatchParticipationRepository staffMatchParticipationRepository;
    @Autowired
    private RefereeMatchParticipationRepository refereeMatchParticipationRepository;
    @Autowired
    private PlayerContractRepository playerContractRepository;
    @Autowired
    private StaffContractRepository staffContractRepository;
    @Autowired
    private RefereeRepository refereeRepository;
    private final ModelMapper modelMapper;

    public ResponseWithTotalPage<MatchResponse> getAllMatch(int pageNumber, int pageSize,UUID tournamentId,UUID stadiumId, UUID roundId){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));

        List<Specification<Match>> specificationList = new ArrayList<>();
        List<Specification<Match>> specificationListSpecial = new ArrayList<>();

        if(tournamentId != null){
            Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(()-> new NotFoundException(TournamentFailMessage.TOURNAMENT_NOT_FOUND));
            RoundSpecification roundSpecification = new RoundSpecification(new SearchCriteria("tournament",SearchOperation.EQUALITY,tournament));
            List<Round> roundList =  roundRepository.findAll(roundSpecification);
            if (!roundList.isEmpty()){
                for (Round round : roundList){
                    MatchSpecification matchSpecification = new MatchSpecification(new SearchCriteria("round",SearchOperation.EQUALITY,round));
                    specificationListSpecial.add(matchSpecification);
                }
            }else{
                ResponseWithTotalPage<MatchResponse> response = new ResponseWithTotalPage<>();
                List<MatchResponse> matchList = new ArrayList<>();
                response.setData(matchList);
                PaginationResponse paginationResponse = PaginationResponse.builder()
                        .pageIndex(pageNumber)
                        .pageSize(pageSize)
                        .totalCount(0)
                        .totalPage(0)
                        .build();
                response.setPagination(paginationResponse);
                return  response;
            }

        }
        if (stadiumId != null) {
            Stadium stadium = stadiumRepository.findById(stadiumId).orElseThrow(()-> new NotFoundException(StadiumFailMessage.STADIUM_NOT_FOUND));
            MatchSpecification specification = new MatchSpecification(new SearchCriteria("stadium",SearchOperation.EQUALITY,stadium));
            specificationList.add(specification);
        }
        if (roundId != null) {
            Round round = roundRepository.findById(roundId).orElseThrow(()-> new NotFoundException(RoundFailMessage.ROUND_NOT_FOUND));
            MatchSpecification specification = new MatchSpecification(new SearchCriteria("round",SearchOperation.EQUALITY,round));
            specificationList.add(specification);
        }
        Page<Match> pageResult = matchRepository.findAll(Specification.anyOf(specificationListSpecial).and(Specification.allOf(specificationList)),pageable);
        ResponseWithTotalPage<MatchResponse> response = new ResponseWithTotalPage<>();
        List<MatchResponse> matchList = new ArrayList<>();
        if(pageResult.hasContent()) {
            for (Match match :
                    pageResult.getContent()) {
                MatchResponse matchResponse = modelMapper.map(match, MatchResponse.class);
                List<MatchActivity> matchActivityList = matchActivityRepository.findAllByMatch(match);
                int homeGoal = getMatchStatistic(matchActivityList,match.getHomeClub()).getOrDefault(ActivityType.Goal,0);
                int awayGoal = getMatchStatistic(matchActivityList,match.getAwayClub()).getOrDefault(ActivityType.Goal,0);
                matchResponse.setHomeGoals(homeGoal);
                matchResponse.setAwayGoals(awayGoal);
                matchList.add(matchResponse);
            }
        }
        response.setData(matchList);
        PaginationResponse paginationResponse = PaginationResponse.builder()
                .pageIndex(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalCount((int) pageResult.getTotalElements())
                .totalPage(pageResult.getTotalPages())
                .build();
        response.setPagination(paginationResponse);
        return response;
    }
    public MatchResponse addMatch(MatchCreateDTO matchCreateDTO){
        Club homeClub =clubRepository.findById(matchCreateDTO.getHomeClubId()).orElseThrow(()->new NotFoundException(ClubFailMessage.HOME_CLUB_NOT_FOUND));
        Club awayClub =clubRepository.findById(matchCreateDTO.getAwayClubId()).orElseThrow(()->new NotFoundException(ClubFailMessage.AWAY_CLUB_NOT_FOUND));
        Stadium stadium = stadiumRepository.findById(matchCreateDTO.getStadiumId()).orElseThrow(()-> new NotFoundException(StadiumFailMessage.STADIUM_NOT_FOUND));
        Round round = roundRepository.findById(matchCreateDTO.getRoundId()).orElseThrow(()-> new NotFoundException(RoundFailMessage.ROUND_NOT_FOUND));
        Match match =Match.builder()
                .homeClub(homeClub)
                .awayClub(awayClub)
                .stadium(stadium)
                .round(round)
                .startDate(matchCreateDTO.getStartDate())
                .build();
        matchRepository.save(match);
        return modelMapper.map(match,MatchResponse.class);
    }
//    public MatchResponse updateMatch(UUID matchId,MatchUpdateDTO matchUpdateDTO){
//        Match match = matchRepository.findById(matchId).orElseThrow(()->new NotFoundException(MatchFailMessage.ROUND_NOT_FOUND));
//        Tournament tournament =tournamentRepository.findTournamentById(matchUpdateDTO.getTournamentId()).orElseThrow(()->new NotFoundException(TournamentFailMessage.TOURNAMENT_NOT_FOUND));
//        match.setTournament(tournament);
//        match.setName(matchUpdateDTO.getName());
//        matchRepository.save(match);
//        return modelMapper.map(match,MatchResponse.class);
//    }
    public MatchDetailResponse findMatchById(UUID matchId){
        Match match = matchRepository.findById(matchId).orElseThrow(()->new NotFoundException(MatchFailMessage.MATCH_NOT_FOUND));
//        List<MatchActivity> matchActivityList = matchActivityRepository.findAllByMatch(match);
        MatchDetailResponse matchResponse = modelMapper.map(match,MatchDetailResponse.class);
//        matchResponse.setActivities(matchActivityList);
//        int homeGoal = getMatchStatistic(matchActivityList,match.getHomeClub()).getOrDefault(ActivityType.Goal,0);
//        int awayGoal = getMatchStatistic(matchActivityList,match.getAwayClub()).getOrDefault(ActivityType.Goal,0);
//        Map<UUID, Map<ActivityType, Integer>> matchStatistic =getMatchStatisticNew(match);
//        int homeGoal = matchStatistic.getOrDefault(match.getHomeClub().getId(),new HashMap<>()).getOrDefault(ActivityType.Goal,0);
//        int awayGoal = matchStatistic.getOrDefault(match.getAwayClub().getId(),new HashMap<>()).getOrDefault(ActivityType.Goal,0);

        MatchStatisticResponse matchStatisticResponse = getMatchStatisticById(match.getId());
        int homeGoal = matchStatisticResponse.getMatchStatistic().get(match.getHomeClub().getId()).get(ActivityType.Goal);
        int awayGoal = matchStatisticResponse.getMatchStatistic().get(match.getAwayClub().getId()).get(ActivityType.Goal);

        matchResponse.setHomeGoals(homeGoal);
        matchResponse.setAwayGoals(awayGoal);
        return matchResponse;
    }
    public MatchParticipationResponse getMatchParticipationById(UUID matchId){
        Match match = matchRepository.findById(matchId).orElseThrow(()->new NotFoundException(MatchFailMessage.MATCH_NOT_FOUND));
        return modelMapper.map(match,MatchParticipationResponse.class);
    }
    public MatchActivityResponse addMatchActivity(UUID matchId,MatchActivityCreateDTO dto){
        Match match = matchRepository.findById(matchId).orElseThrow(()->new NotFoundException(MatchFailMessage.MATCH_NOT_FOUND));
        List<PlayerMatchParticipationKey> playerMatchParticipationKeyList = new ArrayList<>();
        List<StaffMatchParticipationKey> staffMatchParticipationKeyList = new ArrayList<>();
        List<RefereeMatchParticipationKey> refereeMatchParticipationKeyList = new ArrayList<>();
        if(ActivityType.lookup(dto.getType()) == ActivityType.StartFirstHalf ||
                ActivityType.lookup(dto.getType()) == ActivityType.EndFirstHalf ||
                ActivityType.lookup(dto.getType()) == ActivityType.StartSecondHalf ||
                ActivityType.lookup(dto.getType()) == ActivityType.EndSecondHalf ||
                ActivityType.lookup(dto.getType()) == ActivityType.EndMatch
        ){
            int count = matchActivityRepository.countAllByMatchAndType(match,ActivityType.lookup(dto.getType()));
            if (count >0){
                String msq =  ActivityType.lookup(dto.getType())+ " is duplicate. Try again" ;
                throw new NotValidException(msq);
            }
        }
        if(ActivityType.lookup(dto.getType())==ActivityType.EndMatch){
            List<MatchActivity> matchActivityList = matchActivityRepository.findAllByMatchAndType(match,ActivityType.ExtraTime);
            LocalDateTime startDate = match.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            int extraMinute =0;
            for (MatchActivity matchActivity:
                 matchActivityList) {
                extraMinute+=matchActivity.getMinuteInMatch();
            }
            LocalDateTime endDate = startDate.plus(extraMinute, ChronoUnit.MINUTES);
            match.setEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
        }
        if(dto.getPlayerContractIds() != null && !dto.getPlayerContractIds().isEmpty()){
            for (UUID playerContractId:dto.getPlayerContractIds()){
                PlayerMatchParticipationKey playerMatchParticipationKey =PlayerMatchParticipationKey.builder()
                        .matchId(matchId)
                        .playerContractId(playerContractId)
                        .build();
                playerMatchParticipationKeyList.add(playerMatchParticipationKey);
            }
        }
        List<PlayerMatchParticipation> playerMatchParticipationList = playerMatchParticipationRepository.findAllById(playerMatchParticipationKeyList);
        checkValidPlayerParticipation(playerMatchParticipationList,dto);

        if(dto.getStaffContractIds()!= null && !dto.getStaffContractIds().isEmpty()){
            for (UUID staffContractId:dto.getStaffContractIds()){
                StaffMatchParticipationKey staffMatchParticipationKey =StaffMatchParticipationKey.builder()
                        .matchId(matchId)
                        .staffContractId(staffContractId)
                        .build();
                staffMatchParticipationKeyList.add(staffMatchParticipationKey);
            }
        }
        List<StaffMatchParticipation> staffMatchParticipationList = staffMatchParticipationRepository.findAllById(staffMatchParticipationKeyList);

        if(dto.getRefereeIds()!= null && !dto.getRefereeIds().isEmpty()){
            for (UUID refereeId:dto.getRefereeIds()){
                RefereeMatchParticipationKey refereeMatchParticipationKey =RefereeMatchParticipationKey.builder()
                        .matchId(matchId)
                        .refereeId(refereeId)
                        .build();
                refereeMatchParticipationKeyList.add(refereeMatchParticipationKey);
            }
        }
        List<RefereeMatchParticipation> refereeMatchParticipationList = refereeMatchParticipationRepository.findAllById(refereeMatchParticipationKeyList);


        MatchActivity matchActivity = MatchActivity.builder()
                .match(match)
                .minuteInMatch(dto.getMinuteInMatch())
                .type(ActivityType.lookup(dto.getType()))
                .playerMatchParticipations(playerMatchParticipationList)
                .refereeMatchParticipations(refereeMatchParticipationList)
                .staffMatchParticipations(staffMatchParticipationList)
                .build();
        matchActivityRepository.save(matchActivity);
        return modelMapper.map(matchActivity,MatchActivityResponse.class);
    }
    public String deleteMatchById(UUID matchId){
        boolean exist = matchRepository.existsById(matchId);
        if (exist){
            matchRepository.deleteById(matchId);
            return MatchSuccessMessage.DELETE_MATCH_SUCCESSFUL;
        }else {
            return MatchFailMessage.DELETE_MATCH_FAIL;
        }
    }
    public MatchParticipationResponse addMatchLineups(UUID matchId, MatchLineupsCreateDTO dto){
        Match match = matchRepository.findById(matchId).orElseThrow(()->new NotFoundException(MatchFailMessage.MATCH_NOT_FOUND));
        List<PlayerMatchParticipation> playerMatchParticipationList = new ArrayList<>();
        List<StaffMatchParticipation> staffMatchParticipationList = new ArrayList<>();
        List<RefereeMatchParticipation> refereeMatchParticipationList =new ArrayList<>();
        Club homeClub = match.getHomeClub();
        Club awayClub = match.getAwayClub();
        String homePlayerMsg = checkValidPlayer(dto.getPlayerParticipation(),homeClub);
        String awayPlayerMsg = checkValidPlayer(dto.getPlayerParticipation(),awayClub);
        String homeStaffMsg = checkValidStaff(dto.getStaffParticipation(),homeClub);
        String awayStaffMsg = checkValidStaff(dto.getStaffParticipation(),awayClub);
        String refereeMsg = checkValidReferee(dto.getRefereeParticipation());
        String msg = homePlayerMsg + homeStaffMsg + awayPlayerMsg + awayStaffMsg + refereeMsg;
        if (msg.isEmpty()){
            for (PlayerParticipationDTO playerParticipationDTO : dto.getPlayerParticipation()){
                PlayerContract playerContract = playerContractRepository.findPlayerContractById(playerParticipationDTO.getPlayerContractId()).orElseThrow(()-> new NotFoundException(PlayerContractFailMessage.PLAYER_CONTRACT_NOT_FOUND));
                PlayerMatchParticipationKey participationKey = PlayerMatchParticipationKey.builder()
                        .matchId(match.getId())
                        .playerContractId(playerContract.getId())
                        .build();
                PlayerMatchParticipation playerMatchParticipation = PlayerMatchParticipation.builder()
                        .id(participationKey)
                        .playerContract(playerContract)
                        .match(match)
                        .inLineups(playerParticipationDTO.isInLineups())
                        .role(PlayerMatchRole.lookup(playerParticipationDTO.getRole()))
                        .build();
               playerMatchParticipationList.add(playerMatchParticipation);
            }
            for (StaffParticipationDTO staffParticipationDTO : dto.getStaffParticipation()){
                StaffContract staffContract = staffContractRepository.findStaffContractById(staffParticipationDTO.getStaffContractId()).orElseThrow(()-> new NotFoundException(StaffContractFailMessage.STAFF_CONTRACT_NOT_FOUND));
                StaffMatchParticipationKey participationKey = StaffMatchParticipationKey.builder()
                        .matchId(match.getId())
                        .staffContractId(staffContract.getId())
                        .build();
                StaffMatchParticipation staffMatchParticipation = StaffMatchParticipation.builder()
                        .id(participationKey)
                        .staffContract(staffContract)
                        .match(match)
                        .role(StaffMatchRole.lookup(staffParticipationDTO.getRole()))
                        .build();
                staffMatchParticipationList.add(staffMatchParticipation);
            }
            for (RefereeParticipationDTO refereeParticipationDTO : dto.getRefereeParticipation()){
                Referee referee = refereeRepository.findRefereeById(refereeParticipationDTO.getRefereeId()).orElseThrow(()-> new NotFoundException(RefereeFailMessage.REFEREE_NOT_FOUND));
                RefereeMatchParticipationKey participationKey = RefereeMatchParticipationKey.builder()
                        .matchId(match.getId())
                        .refereeId(referee.getId())
                        .build();
                RefereeMatchParticipation refereeMatchParticipation = RefereeMatchParticipation.builder()
                        .id(participationKey)
                        .referee(referee)
                        .match(match)
                        .role(RefereeMatchRole.lookup(refereeParticipationDTO.getRole()))
                        .build();
                refereeMatchParticipationList.add(refereeMatchParticipation);
            }
        }else {
            throw new NotValidException(msg);
        }
//        match.setStaffMatchParticipations(staffMatchParticipationList);
//        match.setRefereeMatchParticipations(refereeMatchParticipationList);
//        match.setPlayerMatchParticipations(playerMatchParticipationList);
//        playerMatchParticipationRepository.saveAll(playerMatchParticipationList);
//        staffMatchParticipationRepository.saveAll(staffMatchParticipationList);
//        refereeMatchParticipationRepository.saveAll(refereeMatchParticipationList);
        match.setStaffMatchParticipations(staffMatchParticipationList);
        match.setRefereeMatchParticipations(refereeMatchParticipationList);
        match.setPlayerMatchParticipations(playerMatchParticipationList);
        matchRepository.save(match);
        return modelMapper.map(match,MatchParticipationResponse.class);
    }
    private long countPlayers(List<PlayerParticipationDTO> playerParticipation, Club club, boolean inLineups) {
        return playerParticipation.stream().filter(playerParticipationDTO -> {
            PlayerContract playerContract = playerContractRepository
                    .findPlayerContractById(playerParticipationDTO.getPlayerContractId())
                    .orElseThrow(() -> new NotFoundException(PlayerContractFailMessage.PLAYER_CONTRACT_NOT_FOUND));
            return playerContract.getClub().getId() == club.getId() && playerParticipationDTO.isInLineups() == inLineups;
        }).count();
    }
    private long countGoalKeeper(List<PlayerParticipationDTO> playerParticipation, Club club, boolean inLineups) {
        return playerParticipation.stream().filter(playerParticipationDTO -> {
            PlayerContract playerContract = playerContractRepository
                    .findPlayerContractById(playerParticipationDTO.getPlayerContractId())
                    .orElseThrow(() -> new NotFoundException(PlayerContractFailMessage.PLAYER_CONTRACT_NOT_FOUND));
            return playerContract.getClub().getId() == club.getId()&& playerParticipationDTO.isInLineups() == inLineups && PlayerMatchRole.lookup(playerParticipationDTO.getRole()) == PlayerMatchRole.GoalKeeper ;
        }).count();
    }

    private long countStaff(List<StaffParticipationDTO> staffParticipation, Club club) {
        return staffParticipation.stream().filter(staffParticipationDTO -> {
            StaffContract staffContract = staffContractRepository
                    .findStaffContractById(staffParticipationDTO.getStaffContractId())
                    .orElseThrow(() -> new NotFoundException(StaffContractFailMessage.STAFF_CONTRACT_NOT_FOUND));
            return staffContract.getClub().getId() == club.getId();
        }).count();
    }
    private long countHeadCoach(List<StaffParticipationDTO> staffParticipation, Club club) {
        return staffParticipation.stream().filter(staffParticipationDTO -> {
            StaffContract staffContract = staffContractRepository
                    .findStaffContractById(staffParticipationDTO.getStaffContractId())
                    .orElseThrow(() -> new NotFoundException(StaffContractFailMessage.STAFF_CONTRACT_NOT_FOUND));
            return staffContract.getClub().getId() == club.getId() && StaffMatchRole.lookup(staffParticipationDTO.getRole()) == StaffMatchRole.HeadCoach;
        }).count();
    }
    private long countAssistantCoach(List<StaffParticipationDTO> staffParticipation, Club club) {
        return staffParticipation.stream().filter(staffParticipationDTO -> {
            StaffContract staffContract = staffContractRepository
                    .findStaffContractById(staffParticipationDTO.getStaffContractId())
                    .orElseThrow(() -> new NotFoundException(StaffContractFailMessage.STAFF_CONTRACT_NOT_FOUND));
            return staffContract.getClub().getId() == club.getId() && StaffMatchRole.lookup(staffParticipationDTO.getRole()) == StaffMatchRole.AssistantCoach;
        }).count();
    }
    private long countHeadReferee(List<RefereeParticipationDTO> refereeParticipation) {
        return refereeParticipation.stream().filter(refereeParticipationDTO -> RefereeMatchRole.lookup(refereeParticipationDTO.getRole()) == RefereeMatchRole.HeadReferee).count();
    }
    private long countAssistantReferee(List<RefereeParticipationDTO> refereeParticipation) {
        return refereeParticipation.stream().filter(refereeParticipationDTO -> RefereeMatchRole.lookup(refereeParticipationDTO.getRole()) == RefereeMatchRole.AssistantReferee).count();
    }
    private String checkValidPlayer(List<PlayerParticipationDTO> playerParticipation, Club club){
        String msg ="";
        long lineupsCount = countPlayers(playerParticipation,club,true);
        long reverseCount = countPlayers(playerParticipation,club,false);
        long goalKeeperCount = countGoalKeeper(playerParticipation,club,true);
        if (lineupsCount == 11 & (reverseCount>=0 && reverseCount <=19) && goalKeeperCount==1 ){
            return msg;
        }else if (lineupsCount != 11){
            msg+=club.getName()+" Lineups must have 11 players\n";
        }else if (reverseCount<0 || reverseCount >19){
            msg+=club.getName()+" Reverse must have 0 - 19 players\n";
        }else {
            msg+=club.getName()+" Goal keeper must have only 1 player\n";
        }
        return msg;
    }
    private String checkValidStaff(List<StaffParticipationDTO> staffParticipationDTO, Club club){
        String msg ="";
        long headCoachCount = countHeadCoach(staffParticipationDTO,club);
        long assistantCoachCount = countAssistantCoach(staffParticipationDTO,club);
        if (headCoachCount == 1 & assistantCoachCount >= 1 ){
            return msg;
        }else if (headCoachCount != 1){
            msg+=club.getName()+" Head Coach must have only 1 staff\n";
        }else {
            msg+=club.getName()+" Assistant Coach must have staff \n";
        }
        return msg;
    }
    private String checkValidReferee(List<RefereeParticipationDTO> refereeParticipationDTO){
        String msg ="";
        long headRefereeCount = countHeadReferee(refereeParticipationDTO);
        long assistantRefereeCount = countAssistantReferee(refereeParticipationDTO);
        if (headRefereeCount == 1 & assistantRefereeCount >= 1 ){
            return msg;
        }else if (headRefereeCount != 1){
            msg+=" Head Referee must have only 1 referee\n";
        }else {
            msg+=" Assistant Referee must have referee \n";
        }
        return msg;
    }
    private Map<ActivityType, Integer> getMatchStatistic (List<MatchActivity> matchActivityList, Club club){
        Map<ActivityType,Integer> typeCount = new HashMap<>();
        matchActivityList.forEach(matchActivity -> {
            int playerSize = matchActivity.getPlayerMatchParticipations().size();
//            int staffSize = matchActivity.getStaffMatchParticipations().size();
//            int refereeSize = matchActivity.getRefereeMatchParticipations().size();
            if(playerSize > 0){
                Optional<PlayerMatchParticipation> playerMatchParticipation = matchActivity.getPlayerMatchParticipations().stream().findFirst();
                PlayerContract playerContract = playerMatchParticipation.get().getPlayerContract();
                if(club.getId() == playerContract.getClub().getId()){
                    typeCount.put(matchActivity.getType(),typeCount.getOrDefault(matchActivity.getType(),0)+1);
                }

            }
        });
        return typeCount;
    }
    public MatchStatisticResponse getMatchStatisticById(UUID matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(()->new NotFoundException(MatchFailMessage.MATCH_NOT_FOUND));

//        Map<UUID, Map<ActivityType, Integer>> matchStatistic = new HashMap<>();
        MatchStatisticResponse response = new MatchStatisticResponse(match);
        List<MatchActivity> matchActivityList = (List<MatchActivity>) match.getActivities();
        matchActivityList.forEach(matchActivity -> {
            int playerSize = matchActivity.getPlayerMatchParticipations().size();
            if (playerSize > 0) {
                Optional<PlayerMatchParticipation> playerMatchParticipation =
                        matchActivity.getPlayerMatchParticipations().stream().findFirst();
                PlayerContract playerContract = playerMatchParticipation.get().getPlayerContract();
                UUID clubId = playerContract.getClub().getId();
                response.getMatchStatistic().putIfAbsent(clubId, new HashMap<>());

                response.getMatchStatistic().get(clubId)
                        .put(matchActivity.getType(),response.getMatchStatistic().get(clubId)
                                        .getOrDefault(matchActivity.getType(), 0) + 1);
            }
        });
//        response.setDefaultValues();
        return response;
    }
    public List<Match> getMatchListByTournament(UUID tournamentId){
        List<Match> matchList = new ArrayList<>();
        List<Specification<Match>> specificationList = new ArrayList<>();
        List<Specification<Match>> specificationListSpecial = new ArrayList<>();
        if(tournamentId != null){
            Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(()-> new NotFoundException(TournamentFailMessage.TOURNAMENT_NOT_FOUND));
            RoundSpecification roundSpecification = new RoundSpecification(new SearchCriteria("tournament",SearchOperation.EQUALITY,tournament));
            List<Round> roundList =  roundRepository.findAll(roundSpecification);
            if (!roundList.isEmpty()){
                for (Round round : roundList){
                    MatchSpecification matchSpecification = new MatchSpecification(new SearchCriteria("round",SearchOperation.EQUALITY,round));
                    specificationListSpecial.add(matchSpecification);
                }
            }else{
                return  matchList;
            }

        }
//            MatchSpecification specificationEnd = new MatchSpecification(new SearchCriteria("endDate",SearchOperation.NEGATION,null));
        matchList = matchRepository.findAll(Specification.anyOf(specificationListSpecial)
//                .and(specificationEnd)
                , Sort.by(Sort.Direction.ASC, "startDate"));
        return matchList;

    }
    private void checkValidPlayerParticipation(List<PlayerMatchParticipation> playerMatchParticipationList,MatchActivityCreateDTO dto){
        List<MatchActivity> matchActivityList = matchActivityRepository.findAllByPlayerMatchParticipationsInAndMinuteInMatchBefore(playerMatchParticipationList, dto.getMinuteInMatch());
        for (UUID playerContractId:dto.getPlayerContractIds()) {
            int yellowCard=0;
            int redCard =0;
            boolean isInlineups =false;
            boolean isSubstitution = false;
            for (MatchActivity matchActivity: matchActivityList) {
                for (PlayerMatchParticipation playerMatchParticipation: playerMatchParticipationList){
                    if (playerContractId.equals(playerMatchParticipation.getPlayerContract().getId())){
                       if (playerMatchParticipation.isInLineups()){
                           isInlineups =true;
                           if (matchActivity.getType() == ActivityType.YellowCard){
                               yellowCard +=1;
                           }
                           else if (matchActivity.getType() == ActivityType.RedCard){
                               redCard +=1;
                           }
                           else if (matchActivity.getType() == ActivityType.Substitution){
                               isSubstitution = true;
                           }
                       }else {
                           isInlineups = false;
                           if (matchActivity.getType() == ActivityType.Substitution){
                               isSubstitution=true;
                           }
                           else if (matchActivity.getType() == ActivityType.YellowCard){
                               yellowCard +=1;
                           }
                           else if (matchActivity.getType() == ActivityType.RedCard){
                               redCard +=1;
                           }
                       }

                    }
                }
            }
            if (yellowCard == 2 || redCard >0){
                throw new NotValidException(playerContractId+ " got "+yellowCard+" yellow cards and "+redCard+" red card");
            }
            else if (isInlineups && isSubstitution){
                throw new NotValidException(playerContractId+ " is substitution already");
            }else if (!isInlineups && !isSubstitution){
                throw new NotValidException(playerContractId+ " is not substitution");
            }
        }
    }
}
