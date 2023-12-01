package com.hungnln.vleague.service;

import com.hungnln.vleague.DTO.StaffContractCreateDTO;
import com.hungnln.vleague.DTO.StaffContractUpdateDTO;
import com.hungnln.vleague.constant.club.ClubFailMessage;
import com.hungnln.vleague.constant.staff.StaffFailMessage;
import com.hungnln.vleague.constant.staff_contract.StaffContractFailMessage;
import com.hungnln.vleague.constant.staff_contract.StaffContractSuccessMessage;
import com.hungnln.vleague.constant.validation_message.ValidationMessage;
import com.hungnln.vleague.entity.Club;
import com.hungnln.vleague.entity.Staff;
import com.hungnln.vleague.entity.StaffContract;
import com.hungnln.vleague.exceptions.ExistException;
import com.hungnln.vleague.exceptions.ListEmptyException;
import com.hungnln.vleague.exceptions.NotFoundException;
import com.hungnln.vleague.exceptions.NotValidException;
import com.hungnln.vleague.repository.ClubRepository;
import com.hungnln.vleague.repository.StaffContractRepository;
import com.hungnln.vleague.repository.StaffRepository;
import com.hungnln.vleague.response.StaffContractResponse;
import com.hungnln.vleague.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
public class StaffContractService {
    @Autowired
    private StaffContractRepository staffContractRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private StaffRepository staffRepository;

    private final ModelMapper modelMapper;
    @Autowired
    private DateUtil dateUtil;

    public List<StaffContractResponse> getAllStaffContracts(int pageNo,int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        Page<StaffContract> pageResult = staffContractRepository.findAll(pageable);
        List<StaffContractResponse> staffContractList = new ArrayList<>();
        if(pageResult.hasContent()) {
            for (StaffContract staffContract :
                    pageResult.getContent()) {
                StaffContractResponse staffContractResponse = modelMapper.map(staffContract, StaffContractResponse.class);
                staffContractList.add(staffContractResponse);
            }
        }else{
            throw new ListEmptyException(StaffContractFailMessage.LIST_STAFF_CONTRACT_IS_EMPTY);

        }
        return staffContractList;
    }

    @SneakyThrows
    public StaffContractResponse addStaffContract(StaffContractCreateDTO staffContractCreateDTO) {
        if(!staffContractCreateDTO.isValidDateRange()){
            throw new NotValidException(ValidationMessage.DATE_VALID_MESSAGE);
        }
        Optional<Staff> staff = staffRepository.findStaffById(staffContractCreateDTO.getStaffId());
        System.out.println(staff);
        if (!staff.isPresent()){
            throw new NotFoundException(StaffFailMessage.STAFF_NOT_FOUND);
        }
        Optional<Club> club = clubRepository.findClubById(staffContractCreateDTO.getClubId());
        if (!club.isPresent()){
            throw new NotFoundException(ClubFailMessage.CLUB_NOT_FOUND);
        }
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateStartString =dateUtil.formatDate(staffContractCreateDTO.getStart());
        String dateEndString = dateUtil.formatDate(staffContractCreateDTO.getEnd());
        List<StaffContract> staffContractList = staffContractRepository
                .findAllByStartGreaterThanEqualAndEndLessThanEqualAndStaffId(
                dateStartString,
                dateEndString,
                staffContractCreateDTO.getStaffId());
        if(staffContractList.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            StaffContract tmp = StaffContract.builder()
                    .id(uuid)
                    .start(staffContractCreateDTO.getStart())
                    .end(staffContractCreateDTO.getEnd())
                    .staff(staff.get())
                    .club(club.get())
                    .description(staffContractCreateDTO.getDescription())
                    .salary(staffContractCreateDTO.getSalary())
                    .build();
            staffContractRepository.save(tmp);
            return modelMapper.map(tmp, StaffContractResponse.class);
        }else {
            throw new ExistException(StaffContractFailMessage.STAFF_CONTRACT_EXIST);
        }
    }
    public StaffContractResponse getStaffContractById(UUID id){
        StaffContract staffContract = staffContractRepository.findStaffContractById(id).orElseThrow(()-> new NotFoundException(StaffContractFailMessage.STAFF_CONTRACT_NOT_FOUND));
        return modelMapper.map(staffContract,StaffContractResponse.class);
    }
    public String deleteStaffContract(UUID id){
        boolean exists = staffContractRepository.existsById(id);
        if(exists){
            staffContractRepository.deleteById(id);
            return StaffContractSuccessMessage.REMOVE_STAFF_CONTRACT_SUCCESSFUL;
        }else{
            return StaffContractFailMessage.DELETE_STAFF_CONTRACT_FAIL;
        }

    }
    public StaffContractResponse updateStaffContract(UUID id, StaffContractUpdateDTO staffContractUpdateDTO){
        StaffContract staffContract = staffContractRepository.findStaffContractById(id).orElseThrow(()-> new NotFoundException(StaffContractFailMessage.STAFF_CONTRACT_NOT_FOUND));
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if(staffContract.getStart().before(staffContractUpdateDTO.getEnd())){
            throw new NotValidException(ValidationMessage.DATE_VALID_MESSAGE);
        }
        String dateStartString = dateUtil.formatDate(staffContract.getStart());
        String dateEndString = dateUtil.formatDate(staffContractUpdateDTO.getEnd());
        List<StaffContract> staffContractList = staffContractRepository
                .findAllByStartGreaterThanEqualAndEndLessThanEqualAndStaffId(
                        dateStartString,
                        dateEndString,
                        staffContract.getStaff().getId()) ;
      if(staffContractList.size() == 1 ) {
          if(staffContractList.contains(staffContract)){
              throw new NotFoundException(StaffContractFailMessage.STAFF_CONTRACT_NOT_FOUND);
          }
          staffContract.setEnd(staffContractUpdateDTO.getEnd());
          staffContract.setDescription(staffContractUpdateDTO.getDescription());
          staffContract.setSalary(staffContractUpdateDTO.getSalary());
          staffContractRepository.save(staffContract);
          return modelMapper.map(staffContract, StaffContractResponse.class);
        }else {
            throw new ExistException(StaffContractFailMessage.STAFF_CONTRACT_DATE_END_);
        }
    }
}
