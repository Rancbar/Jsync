package io.github.projectjsync.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

@Deprecated
public class SampleDuplicateMethodGenerator implements AsyncMethodGenerator {

    public static final String METHOD_NAME_SUFFIX = "_2";

    @Override
    public void inject(ClassVisitor cv, AsyncMethod method, String internalClassName) {
        String methodName = method.name + METHOD_NAME_SUFFIX;

        Type[] paramTypes = Type.getArgumentTypes(method.desc);

        Type returnType = Type.getObjectType("reactor/core/publisher/Mono");



        MethodVisitor mv = cv.visitMethod(method.access /*| ACC_BRIDGE*/ /*| ACC_SYNTHETIC*/, methodName,
                Type.getMethodDescriptor(returnType, paramTypes), null/*TODO: check is this really correct?*/, method.exceptions);
        mv.visitCode();
        int sz = 0;
        boolean isStatic = (method.access & ACC_STATIC) != 0;
        if (!isStatic) {
            mv.visitVarInsn(ALOAD, 0);
            sz++;
        }
        for (Type p : paramTypes) {
            mv.visitVarInsn(p.getOpcode(ILOAD), sz);
            sz += p.getSize();
        }
        mv.visitMethodInsn(isStatic ? INVOKESTATIC : INVOKEVIRTUAL, internalClassName, method.name, method.desc);
        mv.visitInsn(method.returnType.getOpcode(IRETURN));
        mv.visitMaxs(sz, 0);
        mv.visitEnd();
    }
}
