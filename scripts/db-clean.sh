#!/bin/bash

# This script is used to clean the database using Flyway.
mvn -Dflyway.cleanDisabled=false flyway:clean
