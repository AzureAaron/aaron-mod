 package net.azureaaron.mod.init;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import net.azureaaron.mod.MethodReference;

public class InitInjectingClassVisitor extends ClassVisitor {
	private final List<MethodReference> methodReferences;

	public InitInjectingClassVisitor(ClassVisitor classVisitor, List<MethodReference> methodReferences) {
		super(Opcodes.ASM9, classVisitor);
		this.methodReferences = methodReferences;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		//Only replace the Main class' init method which is private, static, has no args, and returns void
		if ((access & Opcodes.ACC_PRIVATE) != 0 && (access & Opcodes.ACC_STATIC) != 0 && name.equals("init") && descriptor.equals("()V")) {
			MethodNode methodNode = new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions);

			//Add a method call for each init method
			for (MethodReference method : methodReferences) {
				methodNode.visitMethodInsn(Opcodes.INVOKESTATIC, method.className(), method.methodName(), method.descriptor(), method.itf());
			}

			//Return from the method
			methodNode.visitInsn(Opcodes.RETURN);

			//Replace the target method with our new generated one
			methodNode.accept(this.getDelegate());

			return null;
		}

		return super.visitMethod(access, name, descriptor, signature, exceptions);
	}
}
