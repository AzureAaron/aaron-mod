package net.azureaaron.mod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import net.azureaaron.mod.init.InitProcessor;
import net.azureaaron.mod.object.ObjectProcessor;

public class Processor implements Plugin<Project> {
	public static final Logger LOGGER = Logging.getLogger(Processor.class);
	/**
	 * The directory where compiled .class files are output.
	 */
	public static File classesDir;

	@Override
	public void apply(Project project) {
		project.getTasks().withType(JavaCompile.class).named("compileJava").get().doLast(task -> {
			classesDir = ((JavaCompile) task).getDestinationDirectory().get().getAsFile();

			InitProcessor.apply();
			ObjectProcessor.apply();
		});
	}

	public static void forEachClass(@NotNull File directory, BiConsumer<Path, InputStream> consumer) {
		try {
			Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
					if (!path.toString().endsWith(".class")) return FileVisitResult.CONTINUE;
					try (InputStream inputStream = Files.newInputStream(path)) {
						consumer.accept(path, inputStream);
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

	public static void forEachClass(BiConsumer<Path, InputStream> consumer) {
		forEachClass(classesDir, consumer);
	}

	public static void forEachClass(Consumer<InputStream> consumer) {
		forEachClass((_path, inputStream) -> consumer.accept(inputStream));
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

	public static void readClass(InputStream classData, Function<ClassReader, ClassVisitor> visitorFactory) {
		try {
			ClassReader classReader = new ClassReader(classData);
			classReader.accept(visitorFactory.apply(classReader), ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeClass(Path classFilePath, Function<ClassWriter, ClassVisitor> visitorFactory) {
		try (InputStream inputStream = Files.newInputStream(classFilePath)) {
			ClassReader classReader = new ClassReader(inputStream);
			//ASM's frame calculation reads classes reflectively which will cause a TypeNotPresentException
			//since the classes we are transforming aren't on the class path. So we need to manually calculate
			//the stack frames.
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classReader.accept(visitorFactory.apply(classWriter), 0);

			try (OutputStream outputStream = Files.newOutputStream(classFilePath)) {
				outputStream.write(classWriter.toByteArray());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
