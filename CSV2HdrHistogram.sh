#!/bin/bash

SCRIPT_DIR=$(cd `dirname $BASH_SOURCE` && pwd)

java -cp ${SCRIPT_DIR}/target/CSV2HdrHistogram.jar CSV2HdrHistogram $@
