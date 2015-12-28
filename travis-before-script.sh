#!/bin/bash

echo "Running before script with test setup: run tests: $run_tests, start sauce tunnel: $start_sc_tunnel, deploy: $deploy"

if [[ $start_sc_tunnel = true ]]; then
  curl -sS https://saucelabs.com/downloads/sc-4.3.13-linux.tar.gz|tar -xzC /tmp/
  /tmp/sc-4.3.13-linux/bin/sc -u $SAUCE_USERNAME -k $SAUCE_ACCESS_KEY -i $TRAVIS_JOB_NUMBER --vm-version dev-varnish -B *.facebook.com --daemonize --readyfile /tmp/sc-ready --wait-tunnel-shutdown
  t=0;
  while [ ! -f /tmp/sc-ready ]; do 
    sleep 1; 
    t=$((t+1)); 
    if [ $t -gt 180 ]; then 
      killall sc;
      echo "Unable to get Sauce connection within 3 minutes";
      exit 1;
    fi;
  done;
else
  echo "Not starting sauce tunnel"
fi;