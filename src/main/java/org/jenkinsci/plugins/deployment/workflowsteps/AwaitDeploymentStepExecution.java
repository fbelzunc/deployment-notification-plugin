package org.jenkinsci.plugins.deployment.workflowsteps;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.FilePath;
import hudson.Util;
import hudson.model.Action;
import hudson.model.ModelObject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.User;
import hudson.util.HttpResponses;
import jenkins.model.Jenkins;
import jenkins.util.Timer;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by fbelzunc on 23/11/2015.
 */
//@Extension
public class AwaitDeploymentStepExecution extends AbstractStepExecutionImpl {

    @StepContextParameter
    private transient FlowNode node;
    @StepContextParameter
    /*package*/ transient Run run;
    private long end;
    private transient volatile ScheduledFuture<?> task;
    @StepContextParameter
    private transient Run build;
    Set<String> filesArchive = new HashSet<String>();

    /**
     * Optional ID that uniquely identifies this input from all others.
     */
    private String id;


    @DataBoundSetter
    public void setId(String id) {
        this.id = capitalize(Util.fixEmpty(id));
    }

    public String getId() {
        return id;
    }

    private String capitalize(String id) {
        if (id==null)
            return null;
        if (id.length()==0)
            throw new IllegalArgumentException();
        // a-z as the first char is reserved for InputAction
        char ch = id.charAt(0);
        if ('a'<=ch && ch<='z')
            id = ((char)(ch-'a'+'A')) + id.substring(1);
        return id;
    }

    @Override
    public boolean start() throws Exception {
        node.addAction(new AwaitDeploymentAction("Await for deployment"));
        getAwaitDeploymentAction().remove(this);
        List<Run.Artifact> artifacts = build.getArtifacts();
        for (Run.Artifact artifact : artifacts) {
            filesArchive.add(artifact.getFileName());
        }


        //TimeUnit.SECONDS.sleep(30);
        //getContext().onSuccess(null);
        return false;

    }

    @Override public void stop(Throwable cause) throws Exception {
        if (task != null) {
            task.cancel(false);
        }
        getContext().onFailure(cause);
    }

    public void proceed(boolean proceed) {
        if (proceed) {
            getContext().onSuccess(null);
        }
    }

    @Override public void onResume() {
        super.onResume();
    }

    /**
     * Gets the {@link AwaitDeploymentAction} that this step should be attached to.
     */
    private AwaitDeploymentAction getAwaitDeploymentAction() {
        AwaitDeploymentAction a = run.getAction(AwaitDeploymentAction.class);
        if (a==null)
            run.addAction(a=new AwaitDeploymentAction("Await"));
        return a;
    }

    /**
     * The singleton instance registered in the Jenkins extension list.
     *
     * @return the instance.
     */
    public static AwaitDeploymentStepExecution getInstance() {
        ExtensionList<AwaitDeploymentStepExecution> list = Jenkins.getInstance().getExtensionList(AwaitDeploymentStepExecution.class);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            throw new IllegalStateException("Extensions are not loaded yet.");
        }
    }

}
