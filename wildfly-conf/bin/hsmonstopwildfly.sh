#!/usr/bin/env bash

DIRNAME=`dirname "$0"`

. "$DIRNAME/hsmoncommon.sh"

JBOSS_PIDFILE=$TMP_DIR/hsmon.jboss.pid

echo "pidfile=$JBOSS_PIDFILE"

if [ -e $JBOSS_PIDFILE ]; then
  printf "Shutting down wildfly:  "
  kill -15 `cat $JBOSS_PIDFILE`
  rm $JBOSS_PIDFILE
else
  echo "wildfly doesn't appear to be running."
fi
