#! /bin/bash -e
# Additional wrapper, which adds custom environment options for the run

extra_java_opts=( \
  '-Djenkins.install.runSetupWizard=false -Djenkins.model.Jenkins.slaveAgentPort=50000' \
  '-Djenkins.model.Jenkins.slaveAgentPortEnforce=true' \
  "-Dio.jenkins.dev.security.createAdmin=${CONF_CREATE_ADMIN}" \
  "-Dio.jenkins.dev.security.allowRunsOnMaster=${CONF_ALLOW_RUNS_ON_MASTER}" \
  '-Dhudson.model.LoadStatistics.clock=1000' \
)

if [ -z "$DEV_HOST" ] ; then
  echo "WARNING: DEV_HOST is undefined, localhost will be used. Some logic like Docker Cloud may work incorrectly."
else
  extra_java_opts+=( "-Dio.jenkins.dev.host=${DEV_HOST}" )
fi

if [[ "$DEBUG" ]] ; then
  extra_java_opts+=( \
    '-Xdebug' \
    '-Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=y' \
  )
fi

export JAVA_OPTS="$JAVA_OPTS ${extra_java_opts[@]}"
exec /usr/local/bin/jenkins.sh "$@"
