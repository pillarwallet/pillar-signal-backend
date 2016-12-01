# Resources

* Heroku Redis
* 2x Heroku Postgres

# Config Variables

Values set by Heroku

* DATABASE_URL
* HEROKU_POSTGRESQL_MAROON_URL
* REDIS_URL

NOTE: When I set this up, heroku created the config variable `HEROKU_POSTGRESQL_MAROON_URL` for the 2nd database.
This is reflected in the `Procfile`. If this turns out to be something else the `Procfile` will need to be updated.

All the following are needed with dummy data to get it running (or real data if you have it). I used `none` for
all values expcet `*_PORT` which expects a number.

* AWS_ACCESS_KEY
* AWS_ATTACHMENTS_BUCKET
* AWS_SECRET_KEY
* PUSH_HOST
* PUSH_PASSWORD
* PUSH_PORT
* PUSH_USERNAME
* TURN_SECRET
* TURN_URIS_0

Additional options that need real data (for now):

 * TWILIO_ACCOUNT_SID
 * TWILIO_ACCOUNT_TOKEN
 * TWILIO_DOMAIN (set to the heroku app's domain)
 * TWILIO_NUMBER

 # Post deploy

```
heroku run bash
sh heroku-init.sh
```

Then

```
heroku ps:scale web:1
```
