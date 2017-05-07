package example;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/*
Tested with
{
  "firstName":"value1",
  "lastName" : "value2"
}
*/

public class Hello implements RequestHandler<Request, Response> {

  public Response handleRequest(Request request, Context context) {
    String greetingString = String.format("Hello %s %s.", request.firstName, request.lastName);
    String vtl = "";
    //Just try the S3 fetch with hard coded resource
    AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                             .withRegion(Regions.EU_WEST_1)
                                             .build();
    S3Object xFile = s3Client.getObject("images-ingest-mappings", "upi.vm");
    InputStream contents = xFile.getObjectContent();
    try {
      vtl = this.read(contents);
    } catch (IOException e){
      System.out.println("Failed to read in VTL file");
    }
    return new Response(greetingString + ", fetched VTL as: " + vtl);
  }

  private static String read(InputStream input) throws IOException {
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }
  }
}