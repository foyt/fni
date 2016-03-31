#!/bin/bash

if [[ $start_sc_tunnel = true ]]; then
  killall sc
fi;

for f in itests/target/*.png; do curl --form "fileupload=@$f" http://uploads.im/api?upload; done

cat itests/target/cargo/configurations/wildfly8x/log/server.log