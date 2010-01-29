#!/bin/sh

AND_SERIAL="emulator-5554"
TARGET="android-1.1"
AND_HOME="/opt/android-sdk-update-manager/platforms/$TARGET"

BASE=$(/bin/ls -1 /opt | grep android | head)

ANDROID_SERIAL="$AND_SERIAL" ANDROID_HOME="$AND_HOME" ant $*
