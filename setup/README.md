Signal Run instruction


download the latest signal jar

curl -u YOUR_ARTIFACTYORY_USERNAME:ARTIFACTORY_PASSWORD -O "https://pillarproject.jfrog.io/pillarproject/signal-backend-generic/signal-backend-{VERSION}.tar.gz"  



EXTRACT IT


mkdir pillar-signal-backend

tar -C pillar-signal-backend -xzf  signal-backend-{VERSION}.tar.gz

RUN

./start.sh


STOP

./stop.sh
