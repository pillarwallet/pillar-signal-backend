#!/bin/bash
set -euo pipefail

applicationName=$1
targetEnvironment=$2
publishedArtifact=$3

MESSAGE="{
    \"content\": \"$applicationName succesfully deployed to $targetEnvironment.\", 
    \"embeds\": [ 
        { 
            \"title\": \"$CIRCLE_PROJECT_REPONAME\",
            \"url\": \"https://circleci.com/workflow-run/$CIRCLE_WORKFLOW_WORKSPACE_ID\",
            \"description\": \"$publishedArtifact\",
            \"color\": 3394662
        }
    ]
}"

curl -d "$MESSAGE" -H "Content-Type: application/json" "$DISCORD_WEBHOOK_URL"