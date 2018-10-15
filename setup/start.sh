#!/bin/bash


nohup  java -jar target/TextSecureServer-*.jar server config/$PIPELINE_PLATFORM.yml  >> ~/signal.log 2>&1&
