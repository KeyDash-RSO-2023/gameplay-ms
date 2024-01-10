package si.fri.rso.samples.imagecatalog.services.services;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GeneratorClient {

    private final String baseUrl;

    public GeneratorClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getTextToType(String language, int length, boolean punctuation) {
        System.out.println(language + " " + length + " " + punctuation);
        HttpClient client = HttpClient.newHttpClient();

        // URL encode the language parameter to handle special characters
        String encodedLanguage = URLEncoder.encode(language, StandardCharsets.UTF_8);

        // Construct the query parameters
        String queryParams = String.format("language=%s&length=%d&punctuation=%b",
                encodedLanguage, length, punctuation);

        // Build the final URI
        URI uri = URI.create(String.format("%s/generate?%s", this.baseUrl, queryParams));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            HttpResponse<String> response = responseFuture.get(); // This blocks until the request is complete
            String responseBody = response.body();
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getString("textToType"); // Extract the text from the JSON object
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt(); // set the interrupt flag
            throw new RuntimeException("Failed to send request or parse response", e);
        }
    }
}
