web: java $JAVA_OPTS -Ddw.server.applicationConnectors[0].port=$PORT -Ddw.twilio.accountId=$TWILIO_ACCOUNT_SID -Ddw.twilio.accountToken=$TWILIO_ACCOUNT_TOKEN -Ddw.twilio.numbers[0]=$TWILIO_NUMBER -Ddw.twilio.localDomain=$TWILIO_DOMAIN -Ddw.s3.accessKey=$AWS_ACCESS_KEY -Ddw.s3.accessSecret=$AWS_SECRET_KEY -Ddw.s3.attachmentsBucket=$AWS_ATTACHMENTS_BUCKET -Ddw.cache.url=$REDIS_URL -Ddw.directory.url=$REDIS_URL -Ddw.apn.bundleId=$APN_BUNDLE_ID -Ddw.apn.pushCertificate=$APN_PUSH_CERTIFICATE -Ddw.apn.pushKey=$APN_PUSH_KEY -Ddw.gcm.apiKey=$GCM_API_KEY -Ddw.database.driverClass=org.postgresql.Driver -Ddw.database.user=`echo $DATABASE_URL | awk -F'://' {'print '} | awk -F':' {'print '}` -Ddw.database.password=`echo $DATABASE_URL | awk -F'://' {'print '} | awk -F':' {'print '} | awk -F'@' {'print '}` -Ddw.database.url=jdbc:postgresql://`echo $DATABASE_URL | awk -F'@' {'print '}` -Ddw.messageStore.driverClass=org.postgresql.Driver -Ddw.messageStore.user=`echo $HEROKU_POSTGRESQL_MAROON_URL | awk -F'://' {'print '} | awk -F':' {'print '}` -Ddw.messageStore.password=`echo $HEROKU_POSTGRESQL_MAROON_URL | awk -F'://' {'print '} | awk -F':' {'print '} | awk -F'@' {'print '}` -Ddw.messageStore.url=jdbc:postgresql://`echo $HEROKU_POSTGRESQL_MAROON_URL | awk -F'@' {'print '}` -Ddw.turn.secret=$TURN_SECRET -Ddw.turn.uris[0]=$TURN_URIS_0 -jar target/TextSecureServer-1.65.jar server config/$STAGE.yml
