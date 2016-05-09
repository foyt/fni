#!/bin/bash

if [[ $start_sc_tunnel = true ]]; then
  killall sc
fi;

if [[ ($deploy = true) && ($perform_release = "true") ]]; then
  python travis/m2conf.py;
  mvn deploy --settings ~/.m2/mySettings.xml;
fi;

if [[ ($release = "true") && ($perform_release = "true") ]]; then
  git remote set-url origin git@github.com:foyt/fni.git
  git checkout master
  git reset --hard
  python travis/m2conf.py;
  mvn -B release:prepare release:perform --settings ~/.m2/mySettings.xml
  git checkout -B devel
  git merge master
  git push --set-upstream origin devel
fi;