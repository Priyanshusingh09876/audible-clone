package com.audible.library.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProgressEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "listening-progress-events";

    public void publishProgressEvent(String userId, String audiobookId, Integer positionSeconds) {
        Map<String, Object> event = Map.of(
                "userId", userId,
                "audiobookId", audiobookId,
                "positionSeconds", positionSeconds,
                "timestamp", System.currentTimeMillis()
        );
        kafkaTemplate.send(TOPIC, userId, event);
        log.debug("Published progress event for user={} audiobook={}", userId, audiobookId);
    }
}
