package com.university.mcmaster.models.dtos.response;

import com.university.mcmaster.enums.RentalUnitStage;
import com.university.mcmaster.enums.RentalUnitStatus;
import com.university.mcmaster.enums.VerificationStatus;
import com.university.mcmaster.models.entities.*;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class RentalUnitForOwner {
    private String rentalUnitId;
    private Amount rent;
    private Amount deposit;
    private VerificationStatus verificationStatus;
    private Address address;
    private RentalUnitFeatures features;
    private RentalUnitStatus rentalUnitStatus;
    private long createdOn;
    private String posterImageUrl;
    private double avgRating;
    private Contact contact;
    private Map<String,List<HashMap<String,Object>>> images;
    private String title;
    private String description;
    private String leaseTerm;
    private long leaseStartDate;
    private Map<String,Integer> counts;
    private int likes;
    private int reviews;
    private RentalUnitStage stage;
    private boolean live;
    private String organizationName;
    private String sheerIdOrganizationId;
    private int bedsRemaining;
    private VisitingSchedule visitingSchedule;
}