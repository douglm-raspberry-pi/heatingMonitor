#! /bin/bash

# Script to start jboss with properties defined

DIRNAME=$(dirname "$0")

. "$DIRNAME"/hsmoncommon.sh

export JBOSS_PIDFILE=$TMP_DIR/hsmon.jboss.pid
export LAUNCH_JBOSS_IN_BACKGROUND=true

if [ "x$JBOSS_PIDFILE" = "x" ]; then
  export JBOSS_PIDFILE=/var/tmp/hsmon.jboss.pid
fi

echo "pidfile=$JBOSS_PIDFILE"

PRG="$0"

usage() {
  echo "  $PRG [-heap size] [-stack size] [-newsize size] [-permgen size]"
  echo "       [-debug] "
  echo "       [-oomdump directory-path] [-logs directory-path]"
  echo ""
  echo " Where:"
  echo ""
  echo " -heap sets the heap size and should be n for bytes"
  echo "                                        nK for kilo-bytes (e.g. 2560000K)"
  echo "                                        nM for mega-bytes (e.g. 2560M)"
  echo "                                        nG for giga-bytes (e.g. 1G)"
  echo "  Default: $heap"
  echo ""
  echo " -permsize sets the permgen size and has the same form as -heap"
  echo " -oomdump enables an oom dump into the given directory"
  echo "  The value should probably not be less than 256M"
  echo "  Default: $permsize"
  echo ""
  echo " -debug sets the logging level to DEBUG"
  echo ""
}

# ===================== Defaults =================================

# Stack can't be smaller until some recursive constructs are
# removed from the xsl - specifically the year day recurrence stuff in
# event creation
stack="600k"
heap="600M"
newsize="200M"
permsize="256M"
testmode=""
profiler=""
oomdump=""
logdir=""

#debugGc=false

exprfilters=INFO

# Figure out where java is for version checks
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
	JAVA="$JAVA_HOME/bin/java"
    else
	JAVA="java"
    fi
fi

# Check our java version
version=$($JAVA -version 2>&1 | sed -E -n 's/.* version "([^.-]*).*/\1/p')
if [[ "$version" -lt "11" ]]; then
  echo "Java 21 or greater is required"
  exit 1
fi

# =================== End defaults ===============================

LOG_THRESHOLD="-Dorg.hsmon.log.level=INFO"

while [ "$1" != "" ]
do
  # Process the next arg
  case $1       # Look at $1
  in
    -usage | -help | -? | ?)
      usage
      exit
      ;;
    -heap)         # Heap size bytes or nK, nM, nG
      shift
      heap="$1"
      shift
      ;;
    -stack)
      shift
      stack="$1"
      shift
      ;;
    -newsize)
      shift
      newsize="$1"
      shift
      ;;
    -permsize)
      shift
      permsize="$1"
      shift
      ;;
    -oomdump)
      shift
      oomdump="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$1/heap-$(date +'%Y-%m-%d_%H:%m:%S').hprof"
      shift
      ;;
    -logs)
      shift
      logdir="-Djboss.server.log.dir=$1"
      shift
      ;;
    -profile)
      shift
      profiler="-agentlib:yjpagent"
      ;;
    -debug)
      shift
      LOG_THRESHOLD="-Dorg.hsmon.log.level=DEBUG"
      ;;
    -debugexprfilters)
      shift
      exprfilters=DEBUG
      shift
      ;;
    *)
      usage
      exit 1
      ;;
  esac
done

# If this is empty only localhost will be available.
# With this address anybody can access the consoles if they are not locked down.
JBOSS_BIND="-b 0.0.0.0 -bmanagement 0.0.0.0"

HSMON_DATA_DIR=$JBOSS_DATA_DIR/hsmon
HSMON_DATA_DIR_DEF=-Dorg.hsmon.data.dir=$HSMON_DATA_DIR/

# Define the system properties used to locate the module specific data

# Configurations property file

HSMON_CONF_DIR="$JBOSS_SERVER_DIR/configuration/hsmon"
HSMON_CONF_DIR_DEF="-Dorg.bedework.config.dir=$HSMON_CONF_DIR/"

# Opensearch home

JAVA_OPTS="$JAVA_OPTS -Xms$heap -Xmx$heap -Xss$stack"

# Put all the temp stuff inside the jboss temp
JAVA_OPTS="$JAVA_OPTS -Djava.io.tmpdir=$JBOSS_SERVER_DIR/tmp"
JAVA_OPTS="$JAVA_OPTS $oomdump"
JAVA_OPTS="$JAVA_OPTS $logdir"
JAVA_OPTS="$JAVA_OPTS $profiler"

#HAWT_OPTS="-Dhawtio.authenticationEnabled=true -Dhawtio.realm=other -Dhawtio.role=hawtioadmin"

export GC_LOG=true

export JAVA_OPTS="$JAVA_OPTS -XX:MetaspaceSize=$permsize -XX:MaxMetaspaceSize=$permsize"

RUN_CMD="$JBOSS_HOME/bin/standalone.sh"
RUN_CMD="$RUN_CMD $JBOSS_BIND"
#RUN_CMD="$RUN_CMD $HAWT_OPTS"
RUN_CMD="$RUN_CMD $testmode"
RUN_CMD="$RUN_CMD $LOG_THRESHOLD $LOG_LEVELS"
RUN_CMD="$RUN_CMD $HSMON_CONF_DIR_DEF $HSMON_CONF_FILE_DEF $HSMON_DATA_DIR_DEF"

echo $RUN_CMD

$RUN_CMD
