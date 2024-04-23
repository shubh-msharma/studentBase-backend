package com.university.mcmaster.enums;

import java.util.Arrays;

public enum ApplicationStatus {
    visit_requested,
    view_property,
    pending_document_upload,
    review_in_process,
    approved,
    rejected;

    public static boolean isValidTransition(ApplicationStatus oldStatus,ApplicationStatus newStatus) {
        if(visit_requested == oldStatus) {
            return Arrays.asList(view_property).contains(newStatus);
        }
        if(view_property == oldStatus) {
            return Arrays.asList(pending_document_upload).contains(newStatus);
        }
        if(pending_document_upload == oldStatus) {
            return Arrays.asList(review_in_process).contains(newStatus);
        }
        if(review_in_process == oldStatus) {
            return Arrays.asList(approved,rejected).contains(newStatus);
        }
        return false;
    }

}