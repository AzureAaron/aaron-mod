package net.azureaaron.mod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.function.Consumer;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.azureaaron.mod.init.InitAnnotationProcessor;

public class AnnotationProcessor implements Plugin<Project> {
	public static final Logger LOGGER = Logging.getLogger(AnnotationProcessor.class);
	/**
	 * The directory where compiled .class files are output.
	 */
	public static File classesDir;

	@Override
	public void apply(Project project) {
		project.getTasks().withType(JavaCompile.class).named("compileJava").get().doLast(task -> {
			classesDir = ((JavaCompile) task).getDestinationDirectory().get().getAsFile();

			InitAnnotationProcessor.apply();
		});
	}

	public static void forEachClass(@NotNull File directory, Consumer<InputStream> consumer) {
		try {
			Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
					if (!path.toString().endsWith(".class")) return FileVisitResult.CONTINUE;
					try (InputStream inputStream = Files.newInputStream(path)) {
						consumer.accept(inputStream);
					} catch (IOException e) {
						LOGGER.error("Failed to run consumer on class {}", path, e);
					}

					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			LOGGER.error("Failed to walk classes", e);
		}
	}

	public static void forEachClass(Consumer<InputStream> consumer) {
		forEachClass(classesDir, consumer);
	}

	@Nullable
	public static File findClass(File directory, String className) {
		if (!className.endsWith(".class")) className += ".class";

		if (!directory.isDirectory()) throw new IllegalArgumentException("Not a directory");

		for (File file : Objects.requireNonNull(directory.listFiles())) {
			if (file.isDirectory()) {
				File foundFile = findClass(file, className);

				if (foundFile != null) return foundFile;
			} else if (file.getName().equals(className)) {
				return file;
			}
		}
		return null;
	}

	@Nullable
	public static File findClass(String className) {
		return findClass(classesDir, className);
	}
}
