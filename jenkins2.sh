#! /bin/bash -e
# Additional wrapper, which adds custom environment options for the run

if [ -z "$DEV_HOST" ] ; then
  echo "WARNING: DEV_HOST is undefined, localhost will be used. Some bits like Docker Cloud may work incorrectly"
fi

extra_java_opts=( \
  '-Djenkins.install.runSetupWizard=false -Djenkins.model.Jenkins.slaveAgentPort=50000' \
  '-Djenkins.model.Jenkins.slaveAgentPortEnforce=true' \
  "-Dio.jenkins.dev.security.createAdmin=${CONF_CREATE_ADMIN}" \
  "-Dio.jenkins.dev.security.allowRunsOnMaster=${CONF_ALLOW_RUNS_ON_MASTER}" \
  "-Dio.jenkins.dev.host=${DEV_HOST}" \
  '-Xdebug' \
  '-Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n' \
  '-Dhudson.model.LoadStatistics.clock=1000' \
)

export JAVA_OPTS="$JAVA_OPTS ${extra_java_opts[@]}"
exec /usr/local/bin/jenkins.sh "$@"