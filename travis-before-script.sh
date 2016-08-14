#!/bin/bash

echo "Running before script with test setup: run tests: $run_tests, start sauce tunnel: $start_sc_tunnel, deploy: $deploy"

if [[ $start_sc_tunnel = true ]]; then
  curl -sS https://saucelabs.com/downloads/sc-4.4.0-rc2-linux.tar.gz|tar -xzC /tmp/
  /tmp/sc-4.4.0-rc2-linux/bin/sc -r 10 -u $SAUCE_USERNAME -k $SAUCE_ACCESS_KEY -i $TRAVIS_JOB_NUMBER --vm-version dev-varnish -B *.facebook.com --daemonize --readyfile /tmp/sc-ready &
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

if [[ $run_tests = "true" ]]; then

  if [[ $it_browser = "phantomjs" ]]; then
    if [ ! -f itests/.phantomjs/bin/phantomjs ]; then
      rm -fR itests/.phantomjs
      curl -sSL "https://dl.dropboxusercontent.com/s/wz7o1jqclt8f4sy/phantomjs-2.1.1-linux-x86_64.tar.bz2"|tar -xvjC itests/
      mv itests/phantomjs-2.1.1-linux-x86_64 itests/.phantomjs
    fi;
  fi;
fi;