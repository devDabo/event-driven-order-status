# event-driven-order-status

Small event-driven order/payment app using:

- `order-status-producer` for the HTTP order API and order status updates
- `order-status-consumer` for payment processing
- Postgres, Debezium, Kafka, Schema Registry, and Kafka UI for CDC and messaging

## Start

```bash
cd infrastructure/docker-compose
docker compose -f common.yml -f postgres_debezium.yml pull
docker compose -f common.yml -f postgres_debezium.yml up -d
./start-up.sh
```

Then start `order-status-consumer` and `order-status-producer`

## Stop

```bash
cd infrastructure/docker-compose
./shutdown.sh
docker compose -f common.yml -f postgres_debezium.yml down
```

## Start Frontend
 TanStack Start SSR frontend for creating orders through a simple server rendered form

```bash
cd order-fe
```
### Install
```bash
npm i
```
### Run
```bash
npm run dev
```
