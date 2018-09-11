Signal Run instruction


download the latest signal jar

curl -u YOUR_ARTIFACTYORY_USERNAME:ARTIFACTORY_PASSWORD -O "https://pillarproject.jfrog.io/pillarproject/signal-backend-generic/signal-backend-{VERSION}.tar.gz"  



EXTRACT IT


mkdir pillar-signal-backend

tar -C pillar-signal-backend -xzf  signal-backend-{VERSION}.tar.gz

RUN

Migrate the databases

java -jar target/TextSecureServer-*.jar accountdb migrate config/production.yml

java -jar target/TextSecureServer-*.jar messagedb migrate config/production.yml

Run

java -jar target/TextSecureServer-*.jar server config/production.yml
