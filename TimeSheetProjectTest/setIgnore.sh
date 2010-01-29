#!/bin/sh

DIR="/home/kronenpj/src/android/TimeSheetProjectTest/src/com/googlecode/iqapps/IQTimeSheet/Test"

if [ "x$1" != "x" ]; then
  # Enable all tests
  echo "Enabling all tests"
  sed -i -e 's%^	@Ignore%	// @Ignore%' ${DIR}/*java 
else
  # Disable all tests
  echo "Disabling all tests"
  sed -i -e 's%^	// @Ignore%	@Ignore%' ${DIR}/*java
fi
