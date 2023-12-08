package io.github.projectjsync.core;

import org.objectweb.asm.Type;

public class AsyncMethod {
    public final int access;
    public final String name;
    public final String desc;
    public final String originalSignature;
    public final String[] exceptions;

    public final Type returnType;

    public AsyncMethod(int access, String name, String desc, String originalSignature, String[] exceptions, Type returnType) {
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.originalSignature = originalSignature;
        this.exceptions = exceptions;
        this.returnType = returnType;
    }
}
