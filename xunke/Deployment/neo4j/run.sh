#!/usr/bin/env bash
# By default, this requires you to login with neo4j/neo4j and change the password. You can, for development purposes, disable authentication by passing --env=NEO4J_AUTH=none to docker run.

docker run \
    --publish=7474:7474 --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    neo4j