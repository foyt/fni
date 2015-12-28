#!/bin/bash
export run_tests=false
export start_sc_tunnel=false
export deploy=false

if [[ "$TRAVIS_PULL_REQUEST" != "false" ]]; then
  export run_tests=true
fi;

if [[ ("$use_sc" = "true") && ("$run_tests" = "true") ]]; then
  export start_sc_tunnel=true
fi;

if [[ ("$TRAVIS_PULL_REQUEST" = "false") && ("$TRAVIS_BRANCH" = "devel") ]]; then
  export deploy=true
fi;

echo "Test setup: run tests: $run_tests, start sauce tunnel: $start_sc_tunnel, deploy: $deploy"