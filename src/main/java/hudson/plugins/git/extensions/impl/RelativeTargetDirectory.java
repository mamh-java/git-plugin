package hudson.plugins.git.extensions.impl;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Job;
import hudson.model.TaskListener;
import hudson.plugins.git.GitException;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.Messages;
import hudson.plugins.git.extensions.GitSCMExtension;
import hudson.plugins.git.extensions.GitSCMExtensionDescriptor;
import hudson.slaves.WorkspaceList;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Instead of checking out to the top of the workspace, check out somewhere else.
 *
 * @author Marc Guenther
 * @author Andrew Bayer
 * @author Kohsuke Kawaguchi
 */
public class RelativeTargetDirectory extends GitSCMExtension {
    private String relativeTargetDir;
    private boolean relativeToTmp;

    @DataBoundConstructor
    public RelativeTargetDirectory(String relativeTargetDir, boolean relativeToTmp) {
        this.relativeTargetDir = relativeTargetDir;
        this.relativeToTmp = relativeToTmp;
    }

    public RelativeTargetDirectory(String relativeTargetDir) {
        this.relativeTargetDir = relativeTargetDir;
        this.relativeToTmp = false;
    }

    public String getRelativeTargetDir() {
        return relativeTargetDir;
    }

    public boolean isRelativeToTmp() {
        return relativeToTmp;
    }

    @Override
    public FilePath getWorkingDirectory(GitSCM scm, Job<?, ?> context, FilePath workspace, EnvVars environment, TaskListener listener) throws IOException, InterruptedException, GitException {
        FilePath ws = workspace;
        if (relativeToTmp) {
            ws = WorkspaceList.tempDir(workspace);
        }

        if (relativeTargetDir == null || relativeTargetDir.length() == 0 || relativeTargetDir.equals(".")) {
            return ws;
        }
        // JENKINS-10880: workspace can be null
        if (ws == null) {
            return null;
        }
        return ws.child(environment.expand(relativeTargetDir));
    }

    @Extension
    public static class DescriptorImpl extends GitSCMExtensionDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.check_out_to_a_sub_directory();
        }
    }
}
