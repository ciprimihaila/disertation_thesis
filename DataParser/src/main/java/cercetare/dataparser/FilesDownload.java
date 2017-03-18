/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.dataparser;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author ciprian
 */
public class FilesDownload {

    public static void main(String[] args) {

        AWSCredentials credentials = new BasicAWSCredentials("AKIAIROLE7UI6UBAGTUQ",
                "nnDyECojEfOZuytf73WaghWcQL+9oFzSqWUnXNhR");
        AmazonS3 s3Client = new AmazonS3Client(credentials);

        /**
         * files_to_download ----- Lista cu fisierele ce se vor concatena in
         * fisierul final
         *
         */
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName("florinpublic");
        ArrayList<String> files_to_download = new ArrayList<String>();
        /**
         * Selectare fisiere.
         */

        String[] prefixes = new String[]{"upload.23.05.2016/2 March/01/",
            "upload.23.05.2016/2 March/02/",
            "upload.23.05.2016/3 March/01/",
            "upload.23.05.2016/3 March/02/"};

        for (String prefix : prefixes) {
            listObjectsRequest = listObjectsRequest.withPrefix(prefix);

            ObjectListing objectListing;

            /**
             * Incarcarea numelor fisierelor in lista
             *
             */
            do {

                objectListing = s3Client.listObjects(listObjectsRequest);

                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    //if(objectSummary.getKey().contains("2015/11/08"))
                    //System.out.println(objectSummary.getKey() + "   " + objectSummary.getSize());
                    files_to_download.add(objectSummary.getKey());
                }
                listObjectsRequest.setMarker(objectListing.getNextMarker());

            } while (objectListing.isTruncated());

            AmazonS3 object = new AmazonS3Client(credentials);
            S3Object s3object;
            String path = "C:\\Users\\Data Sience\\Desktop\\date_martie\\";
            /**
             * Downloadarea fisierelor si concatenarea lor
             *
             */

            for (int i = 0; i < files_to_download.size(); i++) {
                if (files_to_download.get(i).length() < 10) {
                    File theDir = new File(path + files_to_download.get(i));

                    if (!theDir.exists()) {
                        try {
                            theDir.mkdir();
                        } catch (SecurityException se) {
                            //handle it
                        }
                    }
                } else {
                    try {
                        s3object = object.getObject(new GetObjectRequest("florinpublic",
                                files_to_download.get(i)));
                        InputStream fileStream = new BufferedInputStream(s3object.getObjectContent());
                        GZIPInputStream gzipInputStream;

                        gzipInputStream = new GZIPInputStream(fileStream);

                        InputStreamReader decoder = new InputStreamReader(gzipInputStream, "UTF-8");
                        BufferedReader buffered = new BufferedReader(decoder);
                        String line;
                        String filename = files_to_download.get(i)
                                .substring(0, files_to_download.get(i).length() - 3);
                        BufferedWriter writer = new BufferedWriter(new FileWriter(path + filename));
                        while ((line = buffered.readLine()) != null) {

                            //if(line.toLowerCase().contains("generation-nt.com")) {
                            writer.append(line);
                            writer.newLine();
                            //}
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(FilesDownload.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            files_to_download.clear();
        }
    }

}
