#!/bin/bash

if [[ $start_sc_tunnel = true ]]; then
  echo "Stopping Sauce Connect"
  killall sc
  wait $(pgrep ps) 
fi;

for f in itests/target/*.png; do curl --form "fileupload=@$f" http://uploads.im/api?upload; done

cat itests/target/cargo/configurations/wildfly8x/log/server.log