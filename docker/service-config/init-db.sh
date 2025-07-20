#!/bin/bash

set -e

echo "Initializing database..."
set PGPASSWORD=$POSTGRES_PASSWORD
psql -h localhost -U $POSTGRES_USER -d $POSTGRES_DB -c "CREATE SCHEMA IF NOT EXISTS workshop;"
