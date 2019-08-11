#!/bin/bash

cd "$(dirname "$0")"
cd client
java -jar demo.jar $1 $2 $3

