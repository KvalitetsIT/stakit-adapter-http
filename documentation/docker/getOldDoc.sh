#!/bin/bash

if docker pull kvalitetsit/stakit-adapter-http-documentation:latest; then
    echo "Copy from old documentation image."
    docker cp $(docker create kvalitetsit/stakit-adapter-http-documentation:latest):/usr/share/nginx/html target/old
fi
