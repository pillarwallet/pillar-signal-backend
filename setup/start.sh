#!/bin/bash


nohup  java -jar target/TextSecureServer-*.jar server config/production.yml  >> ~/signal.log 2>&1&
