#!/usr/bin/env bash

set -e

function cleanup {
  echo "Removing db container"
  docker stop plexibase-db
  docker rm -v plexibase-db
}

trap cleanup EXIT

docker run -v $(pwd)/db:/docker-entrypoint-initdb.d -p 5432:5432 --name plexibase-db -e POSTGRES_PASSWORD=mysecretpassword -d postgres

DB_NAME=postgres DB_USER=postgres DB_PASSWORD=mysecretpassword DB_HOST=localhost sbt run