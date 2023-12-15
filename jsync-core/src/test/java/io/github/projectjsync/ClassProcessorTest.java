package io.github.projectjsync;

import io.github.projectjsync.core.AsyncMethodGenerator;
import io.github.projectjsync.processor.ProcessMojo;
import io.github.projectjsync.processor.infra.MethodInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static io.github.projectjsync.core.SampleDuplicateMethodGenerator.METHOD_NAME_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;

public class ClassProcessorTest {

    private MethodInjector methodInjector;
    private AsyncMethodGenerator asyncMethodGenerator;

    @BeforeEach
    void setup() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.methodInjector = new MethodInjector();
        this.asyncMethodGenerator = new ProcessMojo().getMethodGenerator();
    }

    @Test
    public void classProcessorTest() throws Exception {
        ProcessMojo.DEBUG_MODE = true;
        File input = new File(this.getClass().getResource("/Foo.class").getFile());
        File output = methodInjector.handle(input, asyncMethodGenerator);
        assertThat(input).withFailMessage("The Output class file should differ with the Input file, test should get run on debug mode")
                .isNotEqualTo(output);
        assertThat(output.exists()).withFailMessage("The generated Output class file does not exists")
                .isTrue();
        assertMethodGenerated(output, "Foo");
    }

    private void assertMethodGenerated(File file, String className) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
        MyClassLoader myClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());
        Class<?> cls = myClassLoader.getClass(className, bytes);
        try {
            Method[] methods = cls.getMethods();
            assertThat(Arrays.stream(methods).map(Method::getName)).contains("getMessage" + METHOD_NAME_SUFFIX);
        } catch (NoClassDefFoundError ignore) {
        }
    }

    public static class MyClassLoader extends ClassLoader {
        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> getClass(String name, byte[] code) {
            return defineClass(name, code, 0, code.length);
        }
    }
}
