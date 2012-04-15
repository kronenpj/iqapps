#!/bin/sh

AND_SERIAL="emulator-5554"
TARGET="android-7"
AND_HOME="/opt/android-sdk-linux_x86/platforms/$TARGET"

BASE=$(/bin/ls -1 /opt | grep android | head)

ANDROID_SERIAL="$AND_SERIAL" ANDROID_HOME="$AND_HOME" ant $*
