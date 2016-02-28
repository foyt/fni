#!/bin/bash

if [[ $start_sc_tunnel = true ]]; then
  killall sc
fi;

if [[ $deploy = true ]]; then
  python travis/m2conf.py;
  mvn deploy --settings ~/.m2/mySettings.xml;
fi;

if [[ ($release = "true") && ($perform_release = "true") ]]; then
  commitmessage=`git log --pretty=format:"%s" -1`;

  if [[ $commitmessage == *"[RELEASE]"* ]]; then
    git checkout master
    git reset --hard
    python travis/m2conf.py;
    mvn -B release:prepare release:perform --settings ~/.m2/mySettings.xml
    git checkout devel 
    git merge master
    git push
  fi;
  
fi;