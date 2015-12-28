#!/bin/bash

if [[ $run_tests = true ]]; then
  mvn $goals \
    -P$it_profile \ 
    -Dexec.args="-Duser.timezone=UTC" \ 
    -Dmaven.javadoc.skip=true \
    -Dsource.skip=true \
    -Dit.properties.file=src/test/resources/it-travis.properties \
    -Dit.package=$package \
    -Dit.browser="$it_browser" \
    -Dit.browser.version="$it_browser_version" \
    -Dit.platform="$it_platform"
fi;