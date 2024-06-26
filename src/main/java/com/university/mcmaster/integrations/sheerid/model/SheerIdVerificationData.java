package com.university.mcmaster.integrations.sheerid.model;

import com.university.mcmaster.models.FirebaseCommonProps;
import com.university.mcmaster.utils.FirestoreConstants;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SheerIdVerificationData implements FirebaseCommonProps {
    public static String collection = FirestoreConstants.VERIFICATION_DATA;
    private String id;
    private String email;
    private String status;
    private SheerIdVerificationResponseDto verificationResponse;
    private SheerIdVerificationRequestDto verificationRequest;
    private long createdOn;
    private String country;

    @Override
    public String getCollection() {
        return collection;
    }

    @Override
    public String getDbPath() {
        return "";
    }
}
