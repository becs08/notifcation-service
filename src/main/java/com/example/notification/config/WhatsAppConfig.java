package com.example.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration pour le RestTemplate utilisé par ConnectivityChecker pour WhatsApp
 */
@Configuration
public class WhatsAppConfig {

    @Value("${connection.timeout:5000}")
    private int connectionTimeout;

    @Value("${socket.timeout:10000}")
    private int socketTimeout;

    @Bean(name = "whatsappRestTemplate")
    public RestTemplate whatsappRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(socketTimeout);

        return new RestTemplate(factory);
    }
}
