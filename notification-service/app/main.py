from fastapi import FastAPI
from app.consumers.progress_consumer import start_kafka_consumer
import asyncio
import threading

app = FastAPI(title="Notification Service", version="1.0.0")


@app.on_event("startup")
async def startup_event():
    """Start Kafka consumer in background thread on startup."""
    thread = threading.Thread(target=start_kafka_consumer, daemon=True)
    thread.start()


@app.get("/health")
def health():
    return {"status": "ok", "service": "notification-service"}


@app.get("/api/v1/notifications/{user_id}")
def get_notifications(user_id: str):
    # Placeholder — in production, fetch from Redis or DB
    return {"userId": user_id, "notifications": []}
