java $JAVA_OPTS -Ddw.server.applicationConnectors[0].port=$PORT -Ddw.twilio.accountId=$TWILIO_ACCOUNT_SID -Ddw.twilio.accountToken=$TWILIO_ACCOUNT_TOKEN -Ddw.twilio.numbers[0]=$TWILIO_NUMBER -Ddw.twilio.localDomain=$TWILIO_DOMAIN -Ddw.push.host=$PUSH_HOST -Ddw.push.port=$PUSH_PORT -Ddw.push.username=$PUSH_USERNAME -Ddw.push.password=$PUSH_PASSWORD -Ddw.s3.accessKey=$AWS_ACCESS_KEY -Ddw.s3.accessSecret=$AWS_SECRET_KEY -Ddw.s3.attachmentsBucket=$AWS_ATTACHMENTS_BUCKET -Ddw.cache.url=$REDIS_URL -Ddw.directory.url=$REDIS_URL -Ddw.database.driverClass=org.postgresql.Driver -Ddw.database.user=`echo $DATABASE_URL | awk -F'://' {'print $2'} | awk -F':' {'print $1'}` -Ddw.database.password=`echo $DATABASE_URL | awk -F'://' {'print $2'} | awk -F':' {'print $2'} | awk -F'@' {'print $1'}` -Ddw.database.url=jdbc:postgresql://`echo $DATABASE_URL | awk -F'@' {'print $2'}` -Ddw.messageStore.driverClass=org.postgresql.Driver -Ddw.messageStore.user=`echo $HEROKU_POSTGRESQL_MAROON_URL | awk -F'://' {'print $2'} | awk -F':' {'print $1'}` -Ddw.messageStore.password=`echo $HEROKU_POSTGRESQL_MAROON_URL | awk -F'://' {'print $2'} | awk -F':' {'print $2'} | awk -F'@' {'print $1'}` -Ddw.messageStore.url=jdbc:postgresql://`echo $HEROKU_POSTGRESQL_MAROON_URL | awk -F'@' {'print $2'}` -Ddw.turn.secret=$TURN_SECRET -Ddw.turn.uris[0]=$TURN_URIS_0 -Ddw.websocket.enabled=true -jar target/TextSecureServer-1.12.jar accountdb migrate config/$STAGE.yml
java $JAVA_OPTS -Ddw.server.applicationConnectors[0].port=$PORT -Ddw.twilio.accountId=$TWILIO_ACCOUNT_SID -Ddw.twilio.accountToken=$TWILIO_ACCOUNT_TOKEN -Ddw.twilio.numbers[0]=$TWILIO_NUMBER -Ddw.twilio.localDomain=$TWILIO_DOMAIN -Ddw.push.host=$PUSH_HOST -Ddw.push.port=$PUSH_PORT -Ddw.push.username=$PUSH_USERNAME -Ddw.push.password=$PUSH_PASSWORD -Ddw.s3.accessKey=$AWS_ACCESS_KEY -Ddw.s3.accessSecret=$AWS_SECRET_KEY -Ddw.s3.attachmentsBucket=$AWS_ATTACHMENTS_BUCKET -Ddw.cache.url=$REDIS_URL -Ddw.directory.url=$REDIS_URL -Ddw.database.driverClass=org.postgresql.Driver -Ddw.database.user=`echo $DATABASE_URL | awk -F'://' {'print $2'} | awk -F':' {'print $1'}` -Ddw.database.password=`echo $DATABASE_URL | awk -F'://' {'print $2'} | awk -F':' {'print $2'} | awk -F'@' {'print $1'}` -Ddw.database.url=jdbc:postgresql://`echo $DATABASE_URL | awk -F'@' {'print $2'}` -Ddw.messageStore.driverClass=org.postgresql.Driver -Ddw.messageStore.user=`echo $HEROKU_POSTGRESQL_MAROON_URL | awk -F'://' {'print $2'} | awk -F':' {'print $1'}` -Ddw.messageStore.password=`echo $HEROKU_POSTGRESQL_MAROON_URL | awk -F'://' {'print $2'} | awk -F':' {'print $2'} | awk -F'@' {'print $1'}` -Ddw.messageStore.url=jdbc:postgresql://`echo $HEROKU_POSTGRESQL_MAROON_URL | awk -F'@' {'print $2'}` -Ddw.turn.secret=$TURN_SECRET -Ddw.turn.uris[0]=$TURN_URIS_0 -Ddw.websocket.enabled=true -jar target/TextSecureServer-1.12.jar messagedb migrate config/$STAGE.yml
