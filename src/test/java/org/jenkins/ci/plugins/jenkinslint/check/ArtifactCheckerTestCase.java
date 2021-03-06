package org.jenkins.ci.plugins.jenkinslint.check;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.FreeStyleProject;
import hudson.tasks.ArtifactArchiver;
import hudson.tasks.JavadocArchiver;
import org.jenkins.ci.plugins.jenkinslint.AbstractTestCase;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * ArtifactChecker Test Case.
 *
 * @author Victor Martinez
 */
public class ArtifactCheckerTestCase extends AbstractTestCase {
    private ArtifactChecker checker = new ArtifactChecker(true);

    @Test public void testEmptyJob() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        assertFalse(checker.executeCheck(project));
    }
    @Test public void testPublisherJob() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getPublishersList().add(new JavadocArchiver("",false));
        assertFalse(checker.executeCheck(project));
    }
    @Test public void testEmptyArtifactPublisher() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getPublishersList().add(new ArtifactArchiver("","",false));
        assertTrue(checker.executeCheck(project));
    }
    @Test public void testArtifactPublisher() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getPublishersList().add(new ArtifactArchiver("something","",false));
        assertFalse(checker.executeCheck(project));
    }
    @Issue("JENKINS-42310")
    @Test public void testMavenModuleJob() throws Exception {
        MavenModuleSet project = j.createMavenProject();
        assertFalse(checker.executeCheck(project));
    }
    @Issue("JENKINS-42310")
    @Test public void testMavenArtifact() throws Exception {
        MavenModuleSet project = j.createMavenProject();
        project.getPublishersList().add(new JavadocArchiver("",false));
        assertFalse(checker.executeCheck(project));
        project.getPublishersList().add(new ArtifactArchiver("","",false));
        assertTrue(checker.executeCheck(project));
        project.delete();
        project = j.createMavenProject();
        project.getPublishersList().add(new ArtifactArchiver("something","",false));
        assertFalse(checker.executeCheck(project));
    }
    @Issue("JENKINS-42310")
    @Test public void testMatrixProject() throws Exception {
        MatrixProject project = j.createMatrixProject();
        assertFalse(checker.executeCheck(project));
    }
    @Issue("JENKINS-42310")
    @Test public void testMatrixProjectArtifact() throws Exception {
        MatrixProject project = j.createMatrixProject();
        project.getPublishersList().add(new JavadocArchiver("",false));
        assertFalse(checker.executeCheck(project));
        project.getPublishersList().add(new ArtifactArchiver("","",false));
        assertTrue(checker.executeCheck(project));
        project.delete();
        project = j.createMatrixProject();
        project.getPublishersList().add(new ArtifactArchiver("something","",false));
        assertFalse(checker.executeCheck(project));
    }
    @Test public void testControlComment() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        assertFalse(checker.isIgnored(project.getDescription()));
        project.setDescription("#lint:ignore:" + checker.getClass().getSimpleName());
        assertTrue(checker.isIgnored(project.getDescription()));
    }
    @Test public void testWorkflowJob() throws Exception {
        assertFalse(checker.executeCheck(createWorkflow(null, true)));
    }
}
