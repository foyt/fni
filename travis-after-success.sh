#!/bin/bash

if [[ $deploy = true ]]; then
  python travis/m2conf.py;
  mvn deploy --settings ~/.m2/mySettings.xml;
fi;