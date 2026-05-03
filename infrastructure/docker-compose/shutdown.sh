#!/bin/bash

cd "$(dirname "$0")"

echo "Delete debezium connectors"

curl --location --request DELETE 'localhost:8083/connectors/order-payment-connector'

echo "Shutdown zookeeper"

docker compose -f common.yml -f zookeeper.yml down

sleep 5

echo "Shutdown kafka cluster"

docker compose -f common.yml -f kafka_cluster.yml down

sleep 5

echo "Shutdown init kafka"

docker compose -f common.yml -f init_kafka.yml down

sleep 5

echo "Deleting Kafka and Zookeeper volumes"

yes | rm -r ./volumes/kafka/*
yes | rm -r ./volumes/zookeeper/*

echo "Shutdown services"
