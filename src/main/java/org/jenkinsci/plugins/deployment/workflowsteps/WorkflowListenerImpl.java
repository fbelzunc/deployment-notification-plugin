package org.jenkinsci.plugins.deployment.workflowsteps;

import hudson.Extension;
import hudson.model.AbstractProject;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.deployment.DeploymentFacet;
import org.jenkinsci.plugins.deployment.DeploymentFacetListener;
import org.jenkinsci.plugins.deployment.DeploymentTrigger;
import org.jenkinsci.plugins.deployment.HostRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fbelzunc on 25/11/2015.
 */
@Extension
public class WorkflowListenerImpl extends DeploymentFacetListener {
    public final ExecutorService POOL = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    @Override
    public void onChange(final DeploymentFacet facet, HostRecord newRecord) {
        LOGGER.log(Level.FINE, "Deployment triggered");
        POOL.submit(new Runnable() {
            public void run() {
                for (AbstractProject<?, ?> p : Jenkins.getInstance().getAllItems(AbstractProject.class)) {
                    DeploymentTrigger t = p.getTrigger(DeploymentTrigger.class);
                    t.checkAndFire(facet);
                }
            }
        });
            /*POOL.submit(new Runnable() {
                public void run() {
                    for (AbstractProject<?,?> p : Jenkins.getInstance().getAllItems(AbstractProject.class)) {
                        DeploymentTrigger t = p.getTrigger(DeploymentTrigger.class);
                        if (t!=null) {
                            t.checkAndFire(facet);

                        }
                    }
                }
            });*/
    }
    private static final Logger LOGGER = Logger.getLogger(DeploymentTrigger.class.getName());
}
