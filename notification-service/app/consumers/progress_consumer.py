from kafka import KafkaConsumer
import json
import redis
import os
import logging

logger = logging.getLogger(__name__)

KAFKA_BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
REDIS_HOST = os.getenv("REDIS_HOST", "localhost")
TOPIC = "listening-progress-events"
GROUP_ID = "notification-service-group"


def start_kafka_consumer():
    """Consume listening progress events and deduplicate via Redis."""
    r = redis.Redis(host=REDIS_HOST, port=6379, decode_responses=True)

    consumer = KafkaConsumer(
        TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP,
        group_id=GROUP_ID,
        value_deserializer=lambda m: json.loads(m.decode("utf-8")),
        auto_offset_reset="earliest",
        enable_auto_commit=True,
    )

    logger.info("Kafka consumer started, listening on topic: %s", TOPIC)

    for message in consumer:
        event = message.value
        user_id = event.get("userId")
        audiobook_id = event.get("audiobookId")
        position = event.get("positionSeconds")

        # Deduplication key with 5-second window
        dedup_key = f"progress:{user_id}:{audiobook_id}:{position // 5}"
        if r.set(dedup_key, "1", nx=True, ex=10):
            # Process event — update push notification streak, etc.
            logger.debug("Processing progress event: user=%s book=%s pos=%s",
                         user_id, audiobook_id, position)
            _update_streak(r, user_id)


def _update_streak(r: redis.Redis, user_id: str):
    """Update daily listening streak for the user."""
    from datetime import date
    streak_key = f"streak:{user_id}:{date.today().isoformat()}"
    r.incr(streak_key)
    r.expire(streak_key, 86400 * 2)  # 2 days TTL
