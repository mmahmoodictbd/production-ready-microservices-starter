#!/bin/sh
#
# Install script for production-ready-microservices-starter
# Home Page: https://github.com/mmahmoodictbd/production-ready-microservices-starter
#
# Usage:
#  mkdir production-ready-microservices-starter && cd $_
#  bash <(curl -sSL https://raw.githubusercontent.com/mmahmoodictbd/production-ready-microservices-starter/master/init.sh)"
#  docker-compose up -d
#

check_cmd() {
    command -v "$1" >/dev/null 2>&1
}

echo "Welcome to production-ready-microservices-starter, please wait..."
echo ""

if check_cmd docker; then
    echo "docker is installed"
else
    echo "docker is not installed, please try again after it's installed"
    exit 1
fi

if check_cmd docker-compose; then
    echo "docker-compose is installed"
else
    curl -sSL https://raw.githubusercontent.com/docker/compose/master/script/run/run.sh > /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose || exit 1
fi

curl -sSLO https://raw.githubusercontent.com/mmahmoodictbd/production-ready-microservices-starter/master/deployment/dev-keys/public_key.der

curl -sSLO https://raw.githubusercontent.com/mmahmoodictbd/production-ready-microservices-starter/master/docker-compose-demo.yaml

echo "You're all set! "
echo "Run 'docker-compose -f docker-compose-demo.yaml up -d' then go to http://localhost:33000 on your browser."