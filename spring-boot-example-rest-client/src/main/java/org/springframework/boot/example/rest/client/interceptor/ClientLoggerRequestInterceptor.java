package org.springframework.boot.example.rest.client.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class ClientLoggerRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        logRequest(request, body);
        var response = execution.execute(request, body);
        return logResponse(request, response);
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("Request: {} {}", request.getMethod(), request.getURI());

        if (body != null && body.length > 0) {
            log.info("Request body: {}", new String(body, UTF_8));
        }
    }

    private ClientHttpResponse logResponse(HttpRequest request,
                                           ClientHttpResponse response) throws IOException {
        log.info("Response status: {}", response.getStatusCode());

        byte[] responseBody = response.getBody().readAllBytes();

        if (responseBody.length > 0) {
            log.info("Response body: {}", new String(responseBody, UTF_8));
        }

        return response;
    }
}
