#!/bin/bash
set -euo pipefail

applicationName=$1
targetEnvironment=$2
publishedArtifact=$3

payload=$(
cat <<EOM
{
    "attachments": [
        {
            "fallback": "$applicationName succesfully deployed to $targetEnvironment.",
            "color": "#33CC66",
            "pretext": "$applicationName succesfully deployed to $targetEnvironment.",
            "title": "$CIRCLE_PROJECT_REPONAME",
            "title_link": "https://circleci.com/workflow-run/$CIRCLE_WORKFLOW_WORKSPACE_ID",
            "text": "$publishedArtifact",
            "ts": $(date '+%s')
        }
    ]
}
EOM
)

curl -X POST --data-urlencode payload="$payload" "$SLACK_WEBHOOK_URL"
