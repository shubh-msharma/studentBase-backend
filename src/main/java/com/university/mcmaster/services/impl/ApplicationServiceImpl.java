package com.university.mcmaster.services.impl;

import com.university.mcmaster.enums.ApplicationStatus;
import com.university.mcmaster.enums.UserRole;
import com.university.mcmaster.exceptions.*;
import com.university.mcmaster.models.dtos.request.ApiResponse;
import com.university.mcmaster.models.dtos.request.CreateApplicationRequestDto;
import com.university.mcmaster.models.dtos.response.ApplicationForRentalUnitOwner;
import com.university.mcmaster.models.dtos.response.ApplicationForStudent;
import com.university.mcmaster.models.entities.Application;
import com.university.mcmaster.models.entities.CustomUserDetails;
import com.university.mcmaster.models.entities.RequestedVisitingSchedule;
import com.university.mcmaster.models.entities.User;
import com.university.mcmaster.repositories.ApplicationRepo;
import com.university.mcmaster.services.ApplicationService;
import com.university.mcmaster.services.UserService;
import com.university.mcmaster.utils.ResponseMapper;
import com.university.mcmaster.utils.Utility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepo applicationRepo;
    private final ResponseMapper responseMapper;
    private final UserService userService;

    @Override
    public ResponseEntity<?> getApplications(ApplicationStatus status,String rentalUnitId,String lastSeen,int limit,String requestId, HttpServletRequest request) {
        CustomUserDetails userDetails = Utility.customUserDetails(request);
        if(null == userDetails || null == userDetails.getRoles()) throw new UnAuthenticatedUserException();
        if(userDetails.getRoles().contains(UserRole.student)){
            return getApplicationsForStudent(userDetails.getId(),rentalUnitId,status,limit,lastSeen,requestId);
        }
        if(userDetails.getRoles().contains(UserRole.rental_unit_owner)){
            return getApplicationsForRentalUnitOwner(rentalUnitId,status,limit,lastSeen,requestId);
        }
        throw new UnAuthenticatedUserException();
    }

    private ResponseEntity<?> getApplicationsForRentalUnitOwner(String rentalUnitId, ApplicationStatus status,int limit, String lastSeen, String requestId) {
        if(false == ApplicationService.getAllowedRentalUnitStatusForOwner().contains(status)) throw new InvalidParamValueException("status",ApplicationService.getAllowedRentalUnitStatusForOwner().toString());
        List<Application> applications = applicationRepo.getPaginatedApplicationsByNullableRentalUnitIdAndStatusAndDeletedFalse(rentalUnitId,status,limit,lastSeen);
        List<ApplicationForRentalUnitOwner> res = responseMapper.getApplicationsForRentalUnitOwner(applications,requestId);
        return ResponseEntity.ok(ApiResponse.builder()
                        .data(res)
                        .build());
    }

    private ResponseEntity<?> getApplicationsForStudent(String userId,String rentalUnitId, ApplicationStatus status,int limit, String lastSeen, String requestId) {
        if(false == ApplicationService.getAllowedRentalUnitStatusForStudentToListApplication().contains(status)) throw new InvalidParamValueException("status",ApplicationService.getAllowedRentalUnitStatusForStudentToListApplication().toString());
        List<Application> applications = applicationRepo.getPaginatedApplicationsByStudentIdAndNullableRentalUnitIdAndStatusAndDeletedFalse(userId,rentalUnitId,status,limit,lastSeen);
        List<ApplicationForStudent> res = responseMapper.getApplicationsForStudent(userId,applications,requestId);
        return ResponseEntity.ok(ApiResponse.builder()
                        .data(res)
                        .build());
    }

    @Override
    public ResponseEntity<?> createApplication(CreateApplicationRequestDto requestDto, String requestId, HttpServletRequest request) {
        CustomUserDetails userDetails = Utility.customUserDetails(request);
        if(null == userDetails || null == userDetails.getRoles() || false == userDetails.getRoles().contains(UserRole.student)) throw new UnAuthenticatedUserException();
        verifyCreateApplicationRequest(requestDto);
        Application application = Application.builder()
                .id(UUID.randomUUID().toString())
                .rentalUnitId(requestDto.getRentalUnitId())
                .students(Arrays.asList(userDetails.getId()))
                .createdBy(userDetails.getId())
                .applicationStatus(ApplicationStatus.visit_requested)
                .createdOn(Instant.now().toEpochMilli())
                .visitingSchedule(RequestedVisitingSchedule.builder()
                        .timeZone(requestDto.getTimeZone())
                        .durationInMin(requestDto.getVisitDurationInMin())
                        .time(Utility.getTimeStamp(requestDto.getDate(),requestDto.getTime(),requestDto.getTimeZone()))
                        .build())
                .lastUpdatedOn(Instant.now().toEpochMilli())
                .build();
        applicationRepo.save(application);
        return ResponseEntity.status(200).body(ApiResponse.builder()
                        .msg("added applications")
                .build());
    }

    private void verifyCreateApplicationRequest(CreateApplicationRequestDto requestDto) {
        if(null == requestDto.getRentalUnitId() || requestDto.getRentalUnitId().trim().isEmpty()) throw new MissingRequiredParamException("rentalUnitId");
        if(null == requestDto.getTimeZone() || requestDto.getTimeZone().trim().isEmpty()) throw new MissingRequiredParamException("timeZone");
        if(null == requestDto.getDate() || requestDto.getDate().trim().isEmpty()) throw new MissingRequiredParamException("date");
        if(requestDto.getVisitDurationInMin() <= 0) throw new InvalidParamValueException("visitDurationInMin");
        if(false == Utility.verifyDateFormat(requestDto.getDate())) throw new InvalidParamValueException("date","yyyy-MM-dd");
    }

    @Override
    public ResponseEntity<?> updateApplication(String applicationId, String requestId, HttpServletRequest request) {
        CustomUserDetails userDetails = Utility.customUserDetails(request);
        if(null == userDetails || null == userDetails.getRoles() || false == userDetails.getRoles().contains(UserRole.student)) throw new UnAuthenticatedUserException();
        if(null == applicationId || applicationId.trim().isEmpty()) throw new MissingRequiredParamException();
        Application application = applicationRepo.findById(applicationId);
        if(null == application) throw new EntityNotFoundException();
//        if(userDetails.getRoles().contains(UserRole.rental_unit_owner)){
//            return updateApplicationForRentalUnitOwner(userDetails,application,requestId);
//        }
//        if(userDetails.getRoles().contains(UserRole.student)){
//            return updateApplicationForStudent(userDetails,application,requestId);
//        }
        throw new ActionNotAllowedException("update_application_status","invalid user role",401);
    }

    @Override
    public ResponseEntity<?> deleteApplication(String applicationId, String requestId, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateApplicationStatus(String applicationId, ApplicationStatus status, String requestId, HttpServletRequest request) {
        CustomUserDetails userDetails = Utility.customUserDetails(request);
        if(null == userDetails || null == userDetails.getRoles()) throw new UnAuthenticatedUserException();
        if(null == applicationId || applicationId.trim().isEmpty()) throw new MissingRequiredParamException();
        if(null == status) throw new MissingRequiredParamException();
        Application application = applicationRepo.findById(applicationId);
        if(null == application) throw new EntityNotFoundException();
        if(status == application.getApplicationStatus()) throw new ActionNotAllowedException("update_application_status","old and requested status are same");
        if(false == ApplicationStatus.isValidTransition(application.getApplicationStatus(),status)) throw new ActionNotAllowedException("update_application_status","invalid status update request");
        if(userDetails.getRoles().contains(UserRole.student)){
            return updateApplicationStatusForStudent(userDetails.getId(),application,status,requestId);
        }
        if(userDetails.getRoles().contains(UserRole.rental_unit_owner)){
            return updateApplicationStatusForRentalUnitOwner(userDetails.getId(),application,status,requestId);
        }
        throw new UnAuthenticatedUserException();
    }

    private ResponseEntity<?> updateApplicationStatusForRentalUnitOwner(String userId, Application application, ApplicationStatus status, String requestId) {
        if(false == ApplicationService.getAllowedRentalUnitStatusForOwner().contains(status)) throw new InvalidParamValueException("status",ApplicationService.getAllowedRentalUnitStatusForOwner().toString());
        Map<String,Object> updateMap = new HashMap<>();
        updateMap.put("applicationStatus",status);
        if(ApplicationStatus.view_property == status){
//          todo: take further action like sending notification
        }else if(ApplicationStatus.approved == status){
            verifyApplicationForApprovalOrToSubmitApplication(application);
//          todo: take further action like sending notification
        }else if(ApplicationStatus.rejected == status){
//          todo: take further action like sending notification with some reason
        }
        if(false == updateMap.isEmpty()){
            updateMap.put("lastUpdatedOn",Instant.now().toEpochMilli());
        }
        boolean isUpdated = applicationRepo.update(application.getId(),updateMap);
        return ResponseEntity.ok(ApiResponse.builder()
                        .data("updated application status")
                .build());
    }

    private ResponseEntity<?> updateApplicationStatusForStudent(String userId, Application application, ApplicationStatus status, String requestId) {
        if(false == ApplicationService.getAllowedRentalUnitStatusForStudentToTakeAction().contains(status)) throw new InvalidParamValueException("status",ApplicationService.getAllowedRentalUnitStatusForOwner().toString());
        Map<String,Object> updateMap = new HashMap<>();
        updateMap.put("applicationStatus",status);
        if(ApplicationStatus.pending_document_upload == status){
//          todo: take further action like sending notification
        }else if(ApplicationStatus.review_in_process == status){
            verifyApplicationForApprovalOrToSubmitApplication(application);
//          todo: take further action like sending notification
        }
        if(false == updateMap.isEmpty()){
            updateMap.put("lastUpdatedOn",Instant.now().toEpochMilli());
        }
        boolean isUpdated = applicationRepo.update(application.getId(),updateMap);
        return ResponseEntity.ok(ApiResponse.builder()
                        .data("updated application status")
                .build());
    }

    private void verifyApplicationForApprovalOrToSubmitApplication(Application application) {
        for (String studentId : application.getStudents()) {
            User student = userService.findUserById(studentId);
            for (String docType : ApplicationService.getRequiredFilesForApplicationApproval()) {
                if(false == student.getDocumentPaths().containsKey(docType) || null == student.getDocumentPaths().get(docType) || student.getDocumentPaths().get(docType).trim().isEmpty())
                    throw new ActionNotAllowedException("approve_application","some of the students are missing required documents");
            }
        }
    }
}
