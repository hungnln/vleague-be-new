package com.hungnln.vleague.service;

import com.hungnln.vleague.DTO.ClubCreateDTO;
import com.hungnln.vleague.DTO.ClubUpdateDTO;
import com.hungnln.vleague.constant.club.ClubFailMessage;
import com.hungnln.vleague.constant.club.ClubSuccessMessage;
import com.hungnln.vleague.constant.stadium.StadiumFailMessage;
import com.hungnln.vleague.entity.Club;
import com.hungnln.vleague.entity.Stadium;
import com.hungnln.vleague.exceptions.ExistException;
import com.hungnln.vleague.exceptions.ListEmptyException;
import com.hungnln.vleague.exceptions.NotFoundException;
import com.hungnln.vleague.repository.ClubRepository;
import com.hungnln.vleague.repository.StadiumRepository;
import com.hungnln.vleague.response.ClubResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private StadiumRepository stadiumRepository;

    private final ModelMapper modelMapper;

    public List<ClubResponse> getAllClubs(int pageNo,int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        Page<Club> pageResult = clubRepository.findAll(pageable);
        List<ClubResponse> clubList = new ArrayList<>();
        if(pageResult.hasContent()) {
            for (Club club :
                    pageResult.getContent()) {
                ClubResponse clubResponse = modelMapper.map(club, ClubResponse.class);
                clubList.add(clubResponse);
            }
        }else{
            throw new ListEmptyException(ClubFailMessage.LIST_CLUB_IS_EMPTY);

        }
        return clubList;
    }

    public ClubResponse addClub(ClubCreateDTO clubCreateDTO) {
        Optional<Stadium> stadium = stadiumRepository.findStadiumById(clubCreateDTO.getStadiumID());
        if (!stadium.isPresent()){
            throw new NotFoundException(StadiumFailMessage.STADIUM_NOT_FOUND);
        }
        Optional<Club> club = clubRepository.findClubByName(clubCreateDTO.getName());
        if(club.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            Club tmp = Club.builder()
                    .id(uuid)
                    .headQuarter(clubCreateDTO.getHeadQuarter())
                    .name(clubCreateDTO.getName())
                    .stadium(stadium.get())
                    .imageURL(clubCreateDTO.getImageURL())
                    .build();
            clubRepository.save(tmp);
            return modelMapper.map(tmp, ClubResponse.class);
        }else {
            throw new ExistException(ClubFailMessage.CLUB_EXIST);
        }
    }
    public ClubResponse getClubById(UUID id){
        Club club = clubRepository.findClubById(id).orElseThrow(()-> new NotFoundException(ClubFailMessage.CLUB_NOT_FOUND));
        return modelMapper.map(club,ClubResponse.class);
    }
    public String deleteClub(UUID id){
        boolean exists = clubRepository.existsById(id);
        if(exists){
            clubRepository.deleteById(id);
            return ClubSuccessMessage.REMOVE_CLUB_SUCCESSFULL;
        }else{
            return ClubFailMessage.DELETE_CLUB_FAIL;
        }

    }
    public ClubResponse updateClub(UUID id, ClubUpdateDTO clubUpdateDTO){
        Club club = clubRepository.findClubById(id).orElseThrow(()-> new NotFoundException(ClubFailMessage.CLUB_NOT_FOUND));
        Optional<Stadium> stadium = stadiumRepository.findStadiumById(clubUpdateDTO.getStadiumID());
        if(stadium.isPresent()){
            club.setName(clubUpdateDTO.getName());
            club.setHeadQuarter(clubUpdateDTO.getHeadQuarter());
            club.setStadium(stadium.get());
            club.setImageURL(clubUpdateDTO.getImageURL());
            clubRepository.save(club);
        }
        return  modelMapper.map(club,ClubResponse.class);
    }
}
