package com.jjoaquin3.literalura.util;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

public class ApiClient
{
    private final HttpClient httpClient;

    public ApiClient()
    {
        this.httpClient = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(16))
                .build();
    }

    public Optional<String> get(String uri)
    {
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(16))
                .header("Content-Type", "application/json")
                .build();

        try
        {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(response.body());
        }
        catch (IOException | InterruptedException e)
        {
            System.err.println("Exception: " + e.getMessage());
            return Optional.empty();
        }
    }
}