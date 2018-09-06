#!/bin/bash

kill -9 $(ps aux | grep "TextSecureServer" | grep -v "grep" |tr -s " "| cut -d " " -f 2)
