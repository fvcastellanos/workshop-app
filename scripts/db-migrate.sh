#!/bin/bash

# This script is used to execute database initial migration using Flyway.
mvn -Dflyway.cleanDisabled=false flyway:migrate