package org.jenkinsci.plugins.deployment.workflowsteps;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * Created by fbelzunc on 23/11/2015.
 */
public class AwaitDeploymentStep extends AbstractStepImpl implements Serializable {

    @DataBoundConstructor
    public AwaitDeploymentStep() {
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(AwaitDeploymentStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "awaitDeployment";
        }

        @Override
        public String getDisplayName() {
            return "Await for Deployment";
        }
    }
}
