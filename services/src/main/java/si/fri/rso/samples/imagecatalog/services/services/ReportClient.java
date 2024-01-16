package si.fri.rso.samples.imagecatalog.services.services;

import org.json.JSONObject;
import si.fri.rso.samples.imagecatalog.lib.TypingSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ReportClient {
    private final String baseUrl;

    public ReportClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String saveTypingSession(TypingSession ts, int userId) {

        System.out.println("saving typing session");
        HttpClient client = HttpClient.newHttpClient();

        // Create a JSON object to represent the typing session data
        JSONObject json = new JSONObject();
        json.put("typingSessionId", ts.getTypingSessionId());
        json.put("userId", userId);
        json.put("language", ts.getLanguage());
        json.put("length", ts.getLength());
        json.put("punctuation", ts.isPunctuation());
        json.put("startTime", ts.getStartTime().toString());
        json.put("endTime", ts.getEndTime().toString());
        json.put("wpm", ts.getWpm());
        json.put("accuracy", ts.getAccuracy());
        json.put("status", ts.getStatus());

        // Convert the JSON object to a string
        String requestBody = json.toString();

        // Build the final URI
        URI uri = URI.create(this.baseUrl + "/v1/reports");
//        URI uri = URI.create("http://localhost:8085/v1/reports");

        // Build the HttpRequest with the POST method and the JSON body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            // This blocks until the request is complete
            HttpResponse<String> response = responseFuture.get();
            // Check the response status code and handle it appropriately
            if (response.statusCode() == 200) {
                // Handle successful response
                return response.body();
            } else {
                // Handle error response
                throw new RuntimeException("Failed to save typing session, status code: " + response.statusCode());
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt(); // set the interrupt flag
            throw new RuntimeException("Failed to send request or parse response", e);
        }
    }
}
