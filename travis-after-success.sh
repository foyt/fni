#!/bin/bash

if [[ $start_sc_tunnel = true ]]; then
  killall sc
fi;

if [[ $deploy = true ]]; then
  python travis/m2conf.py;
  mvn deploy --settings ~/.m2/mySettings.xml;
fi;

if [[ $release = true ]]; then
  python travis/m2conf.py;
  mvn -B release:prepare release:perform -Darguments="-Dgpg.passphrase=$PGP_PASSPHRASE"
fi;