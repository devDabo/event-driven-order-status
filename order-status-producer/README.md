# Order Status Producer

## Start

```bash
cd infrastructure/docker-compose
docker compose -f common.yml -f postgres_debezium.yml pull
docker compose -f common.yml -f postgres_debezium.yml up -d
./start-up.sh
```

Then start `order-status-consumer` and `order-status-producer` from their module directories or IDE run configs.

## Stop

```bash
cd infrastructure/docker-compose
./shutdown.sh
docker compose -f common.yml -f postgres_debezium.yml down
```
