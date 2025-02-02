package net.azureaaron.mod.init;

import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InitReadingClassVisitor extends ClassVisitor {
	private final Map<MethodReference, Integer> methodReferences;
	private final ClassReader classReader;

	public InitReadingClassVisitor(ClassReader classReader, Map<MethodReference, Integer> methodReferences) {
		super(Opcodes.ASM9);
		this.classReader = classReader;
		this.methodReferences = methodReferences;
	}

	@Override
	public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
		return new MethodVisitor(Opcodes.ASM9) {
			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				//Use custom annotation visitor for our Init annotations
				return desc.equals("Lnet/azureaaron/mod/annotations/Init;") ? new InitAnnotationVisitor(methodReferences, getMethodReference()) : super.visitAnnotation(descriptor, visible);
			}

			private MethodReference getMethodReference() {
				String className = classReader.getClassName();
				String methodCall = className + "." + methodName;

				//Init method validity checks
				if ((access & Opcodes.ACC_PUBLIC) == 0) throw new IllegalStateException(methodCall + ": Initializer methods must be public");
				if ((access & Opcodes.ACC_STATIC) == 0) throw new IllegalStateException(methodCall + ": Initializer methods must be static");
				if (!descriptor.equals("()V")) throw new IllegalStateException(methodCall + ": Initializer methods must have no arguments and a void return type");

				//Static interface methods need special handling by the JVM and carry a special itf marker which must be specified in the bytecode
				boolean itf = (classReader.getAccess() & Opcodes.ACC_INTERFACE) != 0;

				return new MethodReference(className, methodName, descriptor, itf);
			}
		};
	}

	private static class InitAnnotationVisitor extends AnnotationVisitor {
		private final Map<MethodReference, Integer> methodReferences;
		private final MethodReference methodReference;

		protected InitAnnotationVisitor(Map<MethodReference, Integer> methodReferences, MethodReference methodReference) {
			super(Opcodes.ASM9);
			this.methodReferences = methodReferences;
			this.methodReference = methodReference;
		}

		//This is the only visit method called if the @Init annotation did not have a priority (or any other properties), so we also need to handle them here
		@Override
		public void visitEnd() {
			methodReferences.putIfAbsent(methodReference, 0);
			super.visitEnd();
		}

		@Override
		public void visit(String name, Object value) {
			if (name.equals("priority")) {
				methodReferences.put(methodReference, (int) value);
			}

			super.visit(name, value);
		}
	}
}
