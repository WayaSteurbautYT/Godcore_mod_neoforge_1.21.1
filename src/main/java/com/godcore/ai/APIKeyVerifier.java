package com.godcore.ai;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APIKeyVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIKeyVerifier.class);
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    
    public enum Provider {
        GEMINI("Google Gemini", "https://generativelanguage.googleapis.com/v1beta/models"),
        OLLAMA("Ollama (Local)", "http://localhost:11434/api/tags"),
        HUGGINGFACE("Hugging Face", "https://api-inference.huggingface.co/models");
        
        private final String displayName;
        private final String verificationEndpoint;
        
        Provider(String displayName, String verificationEndpoint) {
            this.displayName = displayName;
            this.verificationEndpoint = verificationEndpoint;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getVerificationEndpoint() {
            return verificationEndpoint;
        }
    }
    
    public static CompletableFuture<VerificationResult> verifyApiKey(Provider provider, String apiKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return performVerification(provider, apiKey);
            } catch (Exception e) {
                LOGGER.error("API key verification failed for provider: {}", provider, e);
                return new VerificationResult(false, "Verification failed: " + e.getMessage(), provider);
            }
        }, executor);
    }
    
    private static VerificationResult performVerification(Provider provider, String apiKey) throws IOException {
        OkHttpClient client = new OkHttpClient();
        
        String url = provider.getVerificationEndpoint();
        if (provider == Provider.GEMINI) {
            url += "?key=" + apiKey;
        }
        
        Request.Builder requestBuilder = new Request.Builder()
            .url(url)
            .get();
        
        if (provider == Provider.HUGGINGFACE) {
            requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                LOGGER.info("API key verification successful for provider: {}", provider);
                return new VerificationResult(true, "API key is valid", provider, responseBody);
            } else {
                String errorBody = response.body() != null ? response.body().string() : "";
                LOGGER.warn("API key verification failed for provider: {} - Status: {}", provider, response.code());
                return new VerificationResult(false, "Invalid API key or request failed", provider, errorBody);
            }
        }
    }
    
    public static CompletableFuture<TestConnectionResult> testConnection(Provider provider, String apiKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return performConnectionTest(provider, apiKey);
            } catch (Exception e) {
                LOGGER.error("Connection test failed for provider: {}", provider, e);
                return new TestConnectionResult(false, "Connection test failed: " + e.getMessage(), provider, 0);
            }
        }, executor);
    }
    
    private static TestConnectionResult performConnectionTest(Provider provider, String apiKey) throws IOException {
        long startTime = System.currentTimeMillis();
        
        OkHttpClient client = new OkHttpClient();
        
        String url = provider.getVerificationEndpoint();
        if (provider == Provider.GEMINI) {
            url += "?key=" + apiKey;
        }
        
        Request.Builder requestBuilder = new Request.Builder()
            .url(url)
            .get();
        
        if (provider == Provider.HUGGINGFACE) {
            requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = client.newCall(request).execute()) {
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (response.isSuccessful()) {
                LOGGER.info("Connection test successful for provider: {} - Response time: {}ms", provider, responseTime);
                return new TestConnectionResult(true, "Connection successful", provider, responseTime);
            } else {
                LOGGER.warn("Connection test failed for provider: {} - Status: {}", provider, response.code());
                return new TestConnectionResult(false, "Connection failed", provider, responseTime);
            }
        }
    }
    
    public static class VerificationResult {
        public final boolean success;
        public final String message;
        public final Provider provider;
        public final String details;
        
        public VerificationResult(boolean success, String message, Provider provider) {
            this(success, message, provider, "");
        }
        
        public VerificationResult(boolean success, String message, Provider provider, String details) {
            this.success = success;
            this.message = message;
            this.provider = provider;
            this.details = details;
        }
    }
    
    public static class TestConnectionResult {
        public final boolean success;
        public final String message;
        public final Provider provider;
        public final long responseTimeMs;
        
        public TestConnectionResult(boolean success, String message, Provider provider, long responseTimeMs) {
            this.success = success;
            this.message = message;
            this.provider = provider;
            this.responseTimeMs = responseTimeMs;
        }
    }
}
