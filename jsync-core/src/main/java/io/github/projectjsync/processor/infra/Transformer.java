package io.github.projectjsync.processor.infra;

import io.github.projectjsync.core.AsyncMethod;
import io.github.projectjsync.core.AsyncMethodGenerator;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.ArrayList;
import java.util.List;

class Transformer extends ClassAdapter {

    protected final AsyncMethodGenerator methodGenerator;

    private String internalClassName;
    /**
     * Synthetic methods to be generated.
     */
    private final List<AsyncMethod> asyncMethods = new ArrayList<>();

    Transformer(ClassVisitor cv, AsyncMethodGenerator methodGenerator) {
        super(cv);
        this.methodGenerator = methodGenerator;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.internalClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }

    /**
     * Looking for methods annotated with {@link io.github.projectjsync.annotations.async}.
     */
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String mdesc, final String signature, final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, mdesc, signature, exceptions);
        return new MethodAdapter(mv) {
            @Override
            public AnnotationVisitor visitAnnotation(String adesc, boolean visible) {
                final AnnotationVisitor av = super.visitAnnotation(adesc, visible);
                if (adesc.equals(MethodInjector.ANNOTATION_ASYNC))
                    return new AnnotationNode(adesc) {
                        @Override
                        public void visitEnd() {
                            super.visitEnd();
                            accept(av);
                            AsyncMethod method = new AsyncMethod(access, name, mdesc, signature, exceptions, Type.getReturnType(mdesc));
                            asyncMethods.add(method);
                        }
                    };
                return av;
            }
        };
    }

    /**
     * Inject methods at the end.
     */
    @Override
    public void visitEnd() {
        for (AsyncMethod method : asyncMethods) {
            methodGenerator.inject(this.cv, method, this.internalClassName);
        }
        super.visitEnd();
    }
}
