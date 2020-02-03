#!/usr/bin/env groovy
import com.continuousx.jenkins.pipeline.config.PipelineConfig
import com.continuousx.jenkins.pipeline.config.PipelineMetadata
import com.continuousx.jenkins.pipeline.config.PipelineType
import org.jenkinsci.plugins.workflow.libs.Library

@Library(['jenkins-cx-shared-library']) _

PipelineConfig pipelineConfig = new PipelineConfig(
        metadata: new PipelineMetadata(
                name: 'jenkins-cx-shared-library',
                type: PipelineType.PIPELINE_JENKINS_SHARED_LIB
        )
)

PipelineGlobal( pipelineConfig )