#!/bin/bash

if [[ $start_sc_tunnel = true ]]; then
  echo "Stopping Sauce Connect"
  killall sc
fi;

cat itests/target/cargo/configurations/wildfly10x/log/server.log

if [[ $it_browser = "phantomjs" ]]; then
  for f in itests/target/*.png; do curl --form "fileupload=@$f" http://uploads.im/api?upload; done
fi;