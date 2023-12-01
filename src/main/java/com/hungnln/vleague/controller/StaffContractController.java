package com.hungnln.vleague.controller;

import com.hungnln.vleague.DTO.StaffContractCreateDTO;
import com.hungnln.vleague.DTO.StaffContractUpdateDTO;
import com.hungnln.vleague.constant.staff_contract.StaffContractSuccessMessage;
import com.hungnln.vleague.constant.response.ResponseStatusDTO;
import com.hungnln.vleague.repository.StaffContractRepository;
import com.hungnln.vleague.response.ListResponseDTO;
import com.hungnln.vleague.response.StaffContractResponse;
import com.hungnln.vleague.response.ResponseDTO;
import com.hungnln.vleague.service.StaffContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1.0/staffs-contracts")
@Tag(name = "staff contract", description = "staff api")
public class StaffContractController {
    public static Logger logger = LoggerFactory.getLogger(StaffContractController.class);
    @Autowired
    StaffContractRepository staffContractRepository;
    @Autowired
    StaffContractService staffContractService;

    @GetMapping("")
    @Operation(summary ="Get staffs contract list", description = "Get staffs contract list")
    ResponseEntity<ListResponseDTO<StaffContractResponse>> getAllStaffContracts(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ){
        ListResponseDTO<StaffContractResponse> responseDTO = new ListResponseDTO<>();
        List<StaffContractResponse> list = staffContractService.getAllStaffContracts(pageNo, pageSize);
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        responseDTO.setData(list);
        responseDTO.setMessage(StaffContractSuccessMessage.GET_STAFF_CONTRACT_SUCCESSFUL);
        return ResponseEntity.ok().body(responseDTO);
    }
    @GetMapping("/{id}")
    ResponseEntity<ResponseDTO<StaffContractResponse>> getStaffContractById(@PathVariable UUID id){
        ResponseDTO<StaffContractResponse> responseDTO = new ResponseDTO<>();
        StaffContractResponse staffContract = staffContractService.getStaffContractById(id);
        responseDTO.setData(staffContract);
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        responseDTO.setMessage(StaffContractSuccessMessage.GET_STAFF_CONTRACT_SUCCESSFUL);
        return ResponseEntity.ok().body(responseDTO);
    }
    @PostMapping("")
    ResponseEntity<ResponseDTO<StaffContractResponse>> addStaffContract(@RequestBody @Valid StaffContractCreateDTO dto) throws BindException {
        ResponseDTO<StaffContractResponse> responseDTO = new ResponseDTO<>();
        StaffContractResponse staffContract = staffContractService.addStaffContract(dto);
        responseDTO.setData(staffContract);
        responseDTO.setMessage(StaffContractSuccessMessage.CREATE_STAFF_CONTRACT_SUCCESSFUL);
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseDTO<StaffContractResponse>> updateStaffContract(@PathVariable UUID id, @RequestBody @Valid StaffContractUpdateDTO dto) throws BindException{
        ResponseDTO<StaffContractResponse> responseDTO = new ResponseDTO<>();
        StaffContractResponse staffContract = staffContractService.updateStaffContract(id,dto);
        responseDTO.setData(staffContract);
        responseDTO.setMessage(StaffContractSuccessMessage.UPDATE_STAFF_CONTRACT_SUCCESSFUL);
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        return ResponseEntity.ok().body(responseDTO);

    }
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseDTO<StaffContractResponse>> deleteStaffContract(@PathVariable UUID id){
        ResponseDTO<StaffContractResponse> responseDTO = new ResponseDTO<>();
        String msg = staffContractService.deleteStaffContract(id);
        responseDTO.setMessage(msg);
        responseDTO.setStatus(ResponseStatusDTO.SUCCESS);
        return ResponseEntity.ok().body(responseDTO);
    }
}
