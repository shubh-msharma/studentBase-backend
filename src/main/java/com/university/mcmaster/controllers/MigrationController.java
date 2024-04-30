package com.university.mcmaster.controllers;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.storage.*;
import com.google.common.collect.ImmutableList;
import com.google.firebase.cloud.FirestoreClient;
import com.university.mcmaster.enums.FilePurpose;
import com.university.mcmaster.models.entities.StudentDocFile;
import com.university.mcmaster.utils.EnvironmentVariables;
import com.university.mcmaster.utils.FirestoreConstants;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/migration")
public class MigrationController {

    @GetMapping("/user-doc")
    public ResponseEntity<?> fixUserDocsMap() {
        try {
            for (QueryDocumentSnapshot document : FirestoreClient.getFirestore().collection(FirestoreConstants.FS_USERS)
                    .get().get().getDocuments()) {
                FirestoreClient.getFirestore().collection(FirestoreConstants.FS_USERS)
                        .document(document.getId())
                        .update(new HashMap<String, Object>() {{
                            for (FilePurpose filePurpose : FilePurpose.validForStudent()) {
                                put("documentPaths." + filePurpose.toString(), StudentDocFile.builder().build());
                            }
                        }});
            }
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.ok(e.getMessage());
        }
        return ResponseEntity.ok("updated docs map for users");
    }

    @GetMapping("/storage-cors")
    public ResponseEntity<?> fixStorageCors() {
        Storage storage = StorageOptions.newBuilder().setProjectId(EnvironmentVariables.PROJECT_ID).build().getService();
        Bucket bucket = storage.get(EnvironmentVariables.BUCKET_NAME);
        List<HttpMethod> methods = new ArrayList<HttpMethod>() {{
            add(HttpMethod.GET);
            add(HttpMethod.PUT);
            add(HttpMethod.POST);
            add(HttpMethod.DELETE);
        }};
        Cors corsConfiguration = Cors.newBuilder()
                .setOrigins(ImmutableList.of(Cors.Origin.of("*")))
                .setMethods(ImmutableList.copyOf(methods))
                .setResponseHeaders(ImmutableList.of("Content-Type"))
                .setMaxAgeSeconds(3600)
                .build();
        bucket.toBuilder().setCors(ImmutableList.of(corsConfiguration)).build().update();
        System.out.println("done");
        return ResponseEntity.ok("updated cors");
    }
}