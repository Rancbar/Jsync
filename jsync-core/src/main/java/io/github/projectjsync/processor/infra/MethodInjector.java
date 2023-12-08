package io.github.projectjsync.processor.infra;

import io.github.projectjsync.annotations.async;
import io.github.projectjsync.annotations.await;
import io.github.projectjsync.core.AsyncMethodGenerator;
import io.github.projectjsync.processor.ProcessMojo;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.io.*;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class MethodInjector {
    public static final String ANNOTATION_ASYNC = Type.getDescriptor(async.class);
    public static final String ANNOTATION_AWAIT = Type.getDescriptor(await.class);


    public void handle(File classFile, AsyncMethodGenerator methodGenerator) throws IOException {
        FileInputStream in = new FileInputStream(classFile);
        byte[] image;
        try {
            ClassReader classReader = new ClassReader(new BufferedInputStream(in));
            ClassWriter classWriter = new ClassWriter(classReader, COMPUTE_MAXS);
            classReader.accept(new Transformer(new ClassAdapter(classWriter), methodGenerator), 0);
            image = classWriter.toByteArray();
        } catch (IOException e) {
            throw new IOException("Failed to process " + classFile, e);
        } catch (RuntimeException e) {
            throw new IOException("Failed to process " + classFile, e);
        } finally {
            in.close();
        }

        if (ProcessMojo.DEBUG_MODE) {
            String fileName = getNameWithoutExtension(classFile.getAbsolutePath()) + ProcessMojo.DEBUG_FILE_POSTFIX + ".class";
            classFile = new File(classFile.toURI().resolve(fileName));
        }

        FileOutputStream out = new FileOutputStream(classFile);
        out.write(image);
        out.close();
    }

    public static String getNameWithoutExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

}
