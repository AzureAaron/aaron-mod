package net.azureaaron.mod.init;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.azureaaron.mod.MethodReference;
import net.azureaaron.mod.Processor;

public class InitProcessor {

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
		Processor.forEachClass(inputStream -> Processor.readClass(inputStream, classReader -> new InitReadingClassVisitor(classReader, methodReferences)));
	}

	private static void injectInitCalls(List<MethodReference> methodReferences) {
		Path mainClassFile = Objects.requireNonNull(Processor.findClass("Main.class"), "Main class wasn't found :(").toPath();

		Processor.writeClass(mainClassFile, classWriter -> new InitInjectingClassVisitor(classWriter, methodReferences));
	}
}
