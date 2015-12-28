#!/bin/bash

if [[ $start_sc_tunnel = true ]]; then
  killall sc
fi;

cat itests/target/cargo/configurations/wildfly8x/log/server.log