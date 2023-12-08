package io.github.projectjsync.core;

import org.objectweb.asm.ClassVisitor;

public interface AsyncMethodGenerator {
    void inject(ClassVisitor cv, AsyncMethod method, String internalClassName);
}
