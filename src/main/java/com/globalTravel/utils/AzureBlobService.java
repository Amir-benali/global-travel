package com.globalTravel.utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AzureBlobService {


    private static final String connectionString = "DefaultEndpointsProtocol=https;AccountName=globaltravelst;AccountKey=BZV+gsukTi/C5g8qpM5GeUf1Fy4klSrbJkiASacXlmzhEHv2ZSUe5qSU5bUaFj6+chX+fyjCBkHT+AStlGTwIQ==;EndpointSuffix=core.windows.net";
    private static final String containerName = "globaltravelstorage";

    public static BlobServiceClient getBlobServiceClient() {
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    public static BlobContainerClient getBlobContainerClient() {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        return blobServiceClient.getBlobContainerClient(containerName);
    }
    public static String uploadImage(File file) throws IOException {
            BlobContainerClient containerClient = getBlobContainerClient();
        String blobName = UUID.randomUUID().toString() + "-" + file.getName();  // Unique blob name to prevent overwriting
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        try (InputStream inputStream = new FileInputStream(file)) {
            blobClient.upload(inputStream, file.length(), true);  // Overwrite existing file if any


        }

        return blobClient.getBlobUrl();  // Return the URL of the uploaded blob
    }
}
