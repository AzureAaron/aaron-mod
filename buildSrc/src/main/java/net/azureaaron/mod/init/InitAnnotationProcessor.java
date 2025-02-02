package net.azureaaron.mod.init;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import net.azureaaron.mod.AnnotationProcessor;

public class InitAnnotationProcessor {

	/**
	 * Finds all methods in the compiled .class files of the mod with an {@code @Init} annotation and injects
	 * calls to those initializer methods into the main class
	 */
	public static void apply() {
		long start = System.currentTimeMillis();
		Map<MethodReference, Integer> methodReferences = new HashMap<>();

		//Find all methods with the @Init annotation
		findInitMethods(methodReferences);

		//Sort the methods by their priority and then by class name. Then its flattened to a list.
		List<MethodReference> sortedMethodReferences = methodReferences.entrySet()
				.stream()
				.sorted(Map.Entry.<MethodReference, Integer>comparingByValue().thenComparing(entry -> entry.getKey().className()))
				.map(Map.Entry::getKey)
				.toList();

		//Inject calls to the @Init annotated methods in the Main class
		injectInitCalls(sortedMethodReferences);
		System.out.println("Injecting init methods took: " + (System.currentTimeMillis() - start) + "ms");
	}

	private static void findInitMethods(Map<MethodReference, Integer> methodReferences) {
		AnnotationProcessor.forEachClass(inputStream -> {
			try {
				ClassReader classReader = new ClassReader(inputStream);
				classReader.accept(new InitReadingClassVisitor(classReader, methodReferences), ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static void injectInitCalls(List<MethodReference> methodReferences) {
		Path mainClassFile = Objects.requireNonNull(AnnotationProcessor.findClass("Main.class"), "Main class wasn't found :(").toPath();

		try (InputStream inputStream = Files.newInputStream(mainClassFile)) {
			ClassReader classReader = new ClassReader(inputStream);
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			classReader.accept(new InitInjectingClassVisitor(classWriter, methodReferences), 0);

			try (OutputStream outputStream = Files.newOutputStream(mainClassFile)) {
				outputStream.write(classWriter.toByteArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
