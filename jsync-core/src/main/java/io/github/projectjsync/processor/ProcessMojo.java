package io.github.projectjsync.processor;

import io.github.projectjsync.annotations.async;
import io.github.projectjsync.core.AsyncMethodGenerator;
import io.github.projectjsync.processor.infra.MethodInjector;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;

public class ProcessMojo extends AbstractMojo {
    // TODO: For real test and replacing the generated class file set the following flag to "false"
    public static boolean DEBUG_MODE = false;
    public static final String DEBUG_FILE_POSTFIX = "_2";

    /**
     * The method generator class should get assigned inside the host project
     */
    private String targetAsyncMethodGenerator = "io.github.projectjsync.core.SampleDuplicateMethodGenerator";

    /**
     * The directory containing generated classes.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File index = new File(classesDirectory, "META-INF/annotations/" + async.class.getName());
        if (!index.exists()) {
            getLog().debug("Skipping because there's no " + index);
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(index), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                File classFile = new File(classesDirectory, line.replace('.', '/') + ".class");
                if (DEBUG_MODE && classFile.getName().endsWith(DEBUG_FILE_POSTFIX + ".class")) {
                    getLog().debug("Debug file - Skipping " + line);
                    return;
                }
                getLog().debug("Processing " + line);
                AsyncMethodGenerator methodGenerator = getMethodGenerator();
                new MethodInjector().handle(classFile, methodGenerator);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to process async/await syntactic sugar", e);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new MojoExecutionException("Failed to get target Async processor: " + e.getMessage(), e);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException _) {
            }
        }
    }

    public AsyncMethodGenerator getMethodGenerator() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class cls = Class.forName(targetAsyncMethodGenerator);
        AsyncMethodGenerator obj = (AsyncMethodGenerator) cls.newInstance();
        return obj;
    }
}
