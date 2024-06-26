package com.university.mcmaster.services;

import com.university.mcmaster.enums.VerificationStatus;
import com.university.mcmaster.models.dtos.request.AddUpdateRentalUnitRequestDto;
import com.university.mcmaster.models.dtos.request.ApiResponse;
import com.university.mcmaster.models.dtos.request.SearchRentalUnitRequestDto;
import com.university.mcmaster.models.entities.RentalUnit;
import com.university.mcmaster.models.entities.RequestedVisitingSchedule;
import com.university.mcmaster.models.entities.TimeSlotCommonProps;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

public interface RentalUnitService {
    ResponseEntity<ApiResponse<?>> getRentalUnits(boolean fetchLiveOnly,int limit,String lastSeen,String requestId, HttpServletRequest request);
    ResponseEntity<ApiResponse<?>> addRentalUnit(AddUpdateRentalUnitRequestDto requestDto, String requestId, HttpServletRequest request);
    ResponseEntity<ApiResponse<?>> updateRentalUnits(String rentalUnitId,AddUpdateRentalUnitRequestDto requestDto,String requestId, HttpServletRequest request);
    ResponseEntity<ApiResponse<?>> deleteRentalUnits(String rentalUnitId, String requestId, HttpServletRequest request);
    void updateRentalUnitPosterImage(String rentalUnitId, String imageId,String imagePath);
    List<RentalUnit> getPaginatedRentalUnitsByVerificationStatusAndDeletedFalseForAdmin(VerificationStatus verificationStatus, int limit, String lastSeen);

    RentalUnit findRentalUnitById(String rentalUnitId);

    void updateRentalUnit(String rentalUnitId, HashMap<String, Object> hashMap);

    void increamentLikeCountForRentalUnit(String rentalUnitId);

    void decreamentLikeCountForRentalUnit(String rentalUnitId);

    void increamentRatingCountForRentalUnit(String rentalUnitId, int star);

    void decreamentRatingCountForRentalUnit(String rentalUnitId, int rating);

    RentalUnit getRentalUnitById(String rentalUnitId);

    void decrementOrIncrementRatingCountForRentalUnit(String rentalUnitId, int rating, String op);

    void decrementOrIncrementGeneralCountForRentalUnit(String rentalUnitId, String count, int vale, String op);

    void decrementOrIncrementBookedSlotsCountForRentalUnit(String rentalUnitId, String key, int vale, String op);

    void decrementOrIncrementBookedSlotsCountForRentalUnit(String rentalUnitId, RequestedVisitingSchedule requestedVisitingSchedule, int vale, String op);

    void decrementOrIncrementBookedSlotsCountForRentalUnit(String rentalUnitId, String date, TimeSlotCommonProps timeSlot, int vale, String op);

    ResponseEntity<ApiResponse<?>> getRentalUnitFeaturesStaticData(String requestId, HttpServletRequest request);

    ResponseEntity<ApiResponse<?>> getRentalUnitById(String rentalUnitId, String requestId, HttpServletRequest request);

    ResponseEntity<ApiResponse<?>> searchRentalUnits(SearchRentalUnitRequestDto requestDto, int limit, String lastSeen, String requestId, HttpServletRequest request);
}
