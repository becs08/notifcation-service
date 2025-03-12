package com.example.notification.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilitaire pour vérifier la connectivité d'un utilisateur à WhatsApp.
 *
 * Dans un environnement de production, cette classe pourrait:
 * 1. Vérifier le statut WhatsApp via l'API WhatsApp Business (si disponible)
 * 2. Maintenir un cache des statuts récents pour limiter les appels API
 * 3. Implémenter une stratégie de fallback si l'API de vérification n'est pas disponible
 */
@Component
public class ConnectivityChecker {

    private static final Logger log = LoggerFactory.getLogger(ConnectivityChecker.class);

    private final RestTemplate whatsappRestTemplate;

    // Cache simple pour éviter de faire trop d'appels API
    // En production, utiliser un cache distribué comme Redis
    private final Map<String, Boolean> connectivityCache = new ConcurrentHashMap<>();

    public ConnectivityChecker(@Qualifier("whatsappRestTemplate") RestTemplate whatsappRestTemplate) {
        this.whatsappRestTemplate = whatsappRestTemplate;
    }

    /**
     * Vérifie si un numéro de téléphone a accès à WhatsApp et est connecté à internet.
     *
     * IMPORTANT : Cette méthode est simplifiée pour l'exemple.
     * L'API WhatsApp Business ne fournit pas directement un moyen de vérifier si un utilisateur est "en ligne".
     * En production, vous pourriez:
     * - Vérifier si le numéro est enregistré sur WhatsApp (existe dans l'annuaire)
     * - Maintenir un système de pings périodiques pour les utilisateurs de votre application
     * - Utiliser les webhooks de WhatsApp pour suivre les changements d'état
     *
     * @param phoneNumber Le numéro à vérifier
     * @return true si l'utilisateur est connecté à WhatsApp, false sinon
     */
    public boolean isConnectedToWhatsApp(String phoneNumber) {
        // Vérifier le cache d'abord
        if (connectivityCache.containsKey(phoneNumber)) {
            return connectivityCache.get(phoneNumber);
        }

        try {
            // Avec Twilio, nous n'avons pas de moyen direct de vérifier si un numéro
            // est enregistré sur WhatsApp avant d'envoyer un message

            // Pour les besoins de l'exemple, supposons que tous les numéros ont WhatsApp
            // En production, vous pourriez implémenter une heuristique basée sur:
            // - L'historique des messages réussis
            // - Un ping périodique pour vérifier la délivrabilité
            // - La localisation du numéro (certains pays ont une adoption plus élevée de WhatsApp)

            boolean isConnected = true; // Supposons que tous les numéros sont sur WhatsApp

            // Mettre en cache le résultat (avec une durée de vie courte)
            connectivityCache.put(phoneNumber, isConnected);

            // Programmer l'expiration du cache après 5 minutes
            new Thread(() -> {
                try {
                    Thread.sleep(5 * 60 * 1000);
                    connectivityCache.remove(phoneNumber);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

            return isConnected;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de la connectivité WhatsApp pour {}: {}",
                    phoneNumber, e.getMessage());
            return false;
        }
    }
}
