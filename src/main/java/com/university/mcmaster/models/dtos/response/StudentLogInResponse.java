package com.university.mcmaster.models.dtos.response;

import com.university.mcmaster.enums.UserRole;
import com.university.mcmaster.enums.VerificationStatus;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentLogInResponse {
    private String email;
    private String phoneNumber;
    private VerificationStatus verificationStatus;
    private long verifiedOn;
    private UserRole userRole;
    private String name;
    private List<Map<String,String>> documents;
}
