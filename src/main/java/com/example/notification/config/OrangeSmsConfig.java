package com.example.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OrangeSmsConfig {
    @Value("${orange.sms.api.url}")
    private String apiUrl;

    @Value("${orange.sms.api.clientId}")
    private String clientId;

    @Value("${orange.sms.api.clientSecret}")
    private String clientSecret;

    @Value("${orange.sms.sender.address}")
    private String senderAddress;

    @Value("${orange.sms.sender.name}")
    private String senderName;

    @Value("${connection.timeout:5000}")
    private int connectionTimeout;

    @Value("${socket.timeout:10000}")
    private int socketTimeout;

    // Getters explicites pour remplacer l'annotation @Data de Lombok
    public String getApiUrl() {
        return apiUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    // Setters explicites
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    @Bean(name = "orangeSmsRestTemplate")
    public RestTemplate orangeSmsRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(socketTimeout);

        return new RestTemplate(factory);
    }
}
