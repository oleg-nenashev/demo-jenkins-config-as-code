PLUGIN_MANAGER_TOOL_VERSION=1.1.0
JENKINS_VERSION=2.240
MY_PIPELINE_LIBRARY_DIR=$(CURDIR)/../pipeline-library
CURRENT_HOST=192.168.0.66

build:
	docker build -t onenashev/demo-jenkins-config-as-code .

run:
	docker run --rm --name ci-jenkins-io-dev -v maven-repo:/root/.m2 -v ${MY_PIPELINE_LIBRARY_DIR}:/var/jenkins_home/pipeline-library -v ${MY_OTHER_PIPELINE_LIBS_DIRS}:/var/jenkins_home/pipeline-libs -e DEV_HOST=${CURRENT_HOST} -p 8080:8080 -p 50000:50000  onenashev/demo-jenkins-config-as-code

debug:
	docker run --rm --name ci-jenkins-io-dev -e DEBUG=true -p 5005:5005 -v maven-repo:/root/.m2 -v ${MY_PIPELINE_LIBRARY_DIR}:/var/jenkins_home/pipeline-library -v ${MY_OTHER_PIPELINE_LIBS_DIRS}:/var/jenkins_home/pipeline-libs -e DEV_HOST=${CURRENT_HOST} -p 8080:8080 -p 50000:50000  onenashev/demo-jenkins-config-as-code

clean:
	rm -rf tmp

tmp/jenkins.war:
	wget https://repo.jenkins-ci.org/releases/org/jenkins-ci/main/jenkins-war/${JENKINS_VERSION}/jenkins-war-${JENKINS_VERSION}.war -O tmp/jenkins.war
	touch tmp/jenkins.war

tmp/jenkins-plugin-manager-${PLUGIN_MANAGER_TOOL_VERSION}.jar:
	mkdir -p tmp
	wget https://repo.jenkins-ci.org/releases/io/jenkins/plugin-management/plugin-management-cli/${PLUGIN_MANAGER_TOOL_VERSION}/plugin-management-cli-${PLUGIN_MANAGER_TOOL_VERSION}.jar -O tmp/jenkins-plugin-manager-${PLUGIN_MANAGER_TOOL_VERSION}.jar
	touch tmp/jenkins-plugin-manager-${PLUGIN_MANAGER_TOOL_VERSION}.jar

show-updates: tmp/jenkins-plugin-manager-${PLUGIN_MANAGER_TOOL_VERSION}.jar tmp/jenkins.war
	mkdir -p tmp/plugins
	java -jar tmp/jenkins-plugin-manager-${PLUGIN_MANAGER_TOOL_VERSION}.jar --list -f plugins.txt --no-download -d tmp/plugins -w tmp/jenkins.war --skip-failed-plugins --verbose --available-updates
