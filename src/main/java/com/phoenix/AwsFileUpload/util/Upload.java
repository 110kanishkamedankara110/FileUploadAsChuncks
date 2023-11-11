package com.phoenix.AwsFileUpload.util;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Upload {
    public static String upload(Part p) throws Exception {
        InputStream is = p.getInputStream();
        String message="";
        try {
            uploadFile(p.getSubmittedFileName(),is);
            message = "The file has been uploaded successfully";
        } catch (Exception ex) {
            message = "Error uploading file: " + ex.getMessage();
        }
        return message;
    }

    private static final String BUCKET = "awsfileuploadtest110";

    public static void uploadFile(String fileName, InputStream inputStream)
            throws S3Exception, AwsServiceException, SdkClientException, IOException {

        S3Client client = S3Client.builder().build();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)

                .acl("public-read")
                .contentType("video/mp4")
                .build();
        client.putObject(request,
                RequestBody.fromInputStream(inputStream, inputStream.available()));



        S3Waiter waiter = client.waiter();
        HeadObjectRequest waitRequest = HeadObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .build();

        WaiterResponse<HeadObjectResponse> waitResponse = waiter.waitUntilObjectExists(waitRequest);
        waitResponse.matched().response().ifPresent(response -> {
            System.out.println("File Uploaded to s3 Successfully..........");
        });
    }
}

