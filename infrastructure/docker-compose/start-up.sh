#!/bin/bash

cd "$(dirname "$0")"
export COMPOSE_IGNORE_ORPHANS=1

echo "Starting Zookeeper"

docker compose -f common.yml -f zookeeper.yml up -d

zookeeperCheckResult=$(echo ruok | nc localhost 2181)

while [[ ! $zookeeperCheckResult == "imok" ]]; do
  >&2 echo "Zookeeper is not running yet!"
  sleep 2
  zookeeperCheckResult=$(echo ruok | nc localhost 2181)
done

echo "Starting Kafka cluster"

docker compose -f common.yml -f kafka_cluster.yml up -d

until docker compose -f common.yml -f kafka_cluster.yml exec -T kafka-broker-1 \
  kafka-topics --bootstrap-server kafka-broker-1:9092 --list >/dev/null 2>&1; do
  >&2 echo "Kafka cluster is not running yet!"
  sleep 2
done

echo "Creating Kafka topics"

docker compose -f common.yml -f init_kafka.yml up -d

until docker compose -f common.yml -f kafka_cluster.yml exec -T kafka-broker-1 \
  kafka-topics --bootstrap-server kafka-broker-1:9092 --list 2>/dev/null | grep -q 'debezium.order.payment_outbox'; do
  >&2 echo "Kafka topics are not created yet!"
  sleep 2
done

servicesCheckResult=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8083/connectors)

while [[ ! $servicesCheckResult == "200" ]]; do
  >&2 echo "Debezium is not running yet!"
  sleep 2
  servicesCheckResult=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8083/connectors)
done

echo "Creating debezium connectors"

curl --location --request POST 'localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "order-payment-connector",
  "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "host.docker.internal",
      "database.port": "5433",
      "database.user": "postgres",
      "database.password": "admin",
      "database.dbname" : "postgres",
      "database.server.name": "PostgreSQL-15",
      "table.include.list": "order.payment_outbox",
      "topic.prefix": "debezium",
      "tombstones.on.delete" : "false",
      "slot.name" : "order_payment_outbox_slot",
      "plugin.name": "pgoutput",
      "auto.create.topics.enable": false,
      "auto.register.schemas": false
      }
 }'

echo "Start-up completed"
