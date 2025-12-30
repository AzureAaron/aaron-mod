package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.Colour.ColourProfiles;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.debug.Debug;
import net.azureaaron.mod.utils.Constants;
import net.azureaaron.mod.utils.UnsafeAccess;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ReflectCommand implements UnsafeAccess {

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register(ReflectCommand::register);
	}

	private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		if (Debug.debugEnabled()) {
			//Maybe turn this into subcommands
			dispatcher.register(literal("reflect")
					.then(argument("opcode", word())
							.suggests((context, builder) -> SharedSuggestionProvider.suggest(OPCODES, builder))
							.then(argument("target class", word())
									.then(argument("target field", word())
											.executes(context -> reflectionExecutor(context.getSource(), getString(context, "opcode"), getString(context, "target class"), getString(context, "target field"), null, null))
											.then(argument("type", word())
													.suggests((context, builder) -> SharedSuggestionProvider.suggest(TYPES, builder))
													.then(argument("new value", string())
													.executes(context -> reflectionExecutor(context.getSource(), getString(context, "opcode"), getString(context, "target class"), getString(context, "target field"), getString(context, "type"), getString(context, "new value")))))))));
		}
	}

	private static final Supplier<MutableComponent> INVALID_OPCODE = () -> Constants.PREFIX.get().append(Component.literal("Invalid Opcode!").withStyle(ChatFormatting.RED));
	private static final Supplier<MutableComponent> INVALID_TYPE = () -> Constants.PREFIX.get().append(Component.literal("Invalid Type!").withStyle(ChatFormatting.RED));
	private static final Supplier<MutableComponent> TYPE_MISMATCH = () -> Constants.PREFIX.get().append(Component.literal("Type Mismatch!").withStyle(ChatFormatting.RED));
	private static final Supplier<MutableComponent> TYPE_MISSING = () -> Constants.PREFIX.get().append(Component.literal("Missing 'type' parameter!").withStyle(ChatFormatting.RED));
	private static final List<String> OPCODES = Arrays.asList("GETFIELD", "PUTFIELD");
	private static final List<String> TYPES = Arrays.asList("byte", "char", "double", "float", "int", "long", "short", "boolean", "string");

	@SuppressWarnings("deprecation")
	private static int reflectionExecutor(FabricClientCommandSource source, String opcode, String targetClass, String target, String type, String newValue) {
		ColourProfiles colourProfile = Constants.PROFILE.get();

		if (!OPCODES.contains(opcode)) {
			source.sendError(INVALID_OPCODE.get());
			return Command.SINGLE_SUCCESS;
		}

		if (type != null && !TYPES.contains(type)) {
			source.sendError(INVALID_TYPE.get());
			return Command.SINGLE_SUCCESS;
		}

		try {

			Class<?> cls = Class.forName(targetClass);
			Object classObject = (Object) cls;

			if (opcode.equals("GETFIELD")) {
				Field field = cls.getDeclaredField(target);
				field.setAccessible(true);
				String fieldValue = field.get(null).toString();

				source.sendFeedback(Component.literal("Field Get » ").withColor(colourProfile.primaryColour.getAsInt())
						.append(Component.literal("[" + field.getType().getName() + "] " + field.getName() + ": ").withColor(colourProfile.secondaryColour.getAsInt())
								.append(Component.literal(fieldValue).withColor(colourProfile.infoColour.getAsInt()))));
			}

			if (opcode.equals("PUTFIELD")) {
				if (type == null) {
					source.sendError(TYPE_MISSING.get());
					return Command.SINGLE_SUCCESS;
				}

				Field field = cls.getDeclaredField(target);
				field.setAccessible(true);
				long offset = (Modifier.isStatic(field.getModifiers())) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);

				if (!field.getType().getName().equals(type.replace("string", "java.lang.String"))) {
					source.sendError(TYPE_MISMATCH.get());
					return Command.SINGLE_SUCCESS;
				}

				switch (type) {
				case "byte":
					UNSAFE.putByte(classObject, offset, Byte.parseByte(newValue));
					break;

				case "char":
					UNSAFE.putChar(classObject, offset, newValue.charAt(0));
					break;

				case "double":
					UNSAFE.putDouble(classObject, offset, Double.parseDouble(newValue));
					break;

				case "float":
					UNSAFE.putFloat(classObject, offset, Float.parseFloat(newValue));
					break;

				case "int":
					UNSAFE.putInt(classObject, offset, Integer.parseInt(newValue));
					break;

				case "long":
					UNSAFE.putLong(classObject, offset, Long.parseLong(newValue));
					break;

				case "short":
					UNSAFE.putShort(classObject, offset, Short.parseShort(newValue));
					break;

				case "boolean":
					UNSAFE.putBoolean(classObject, offset, Boolean.parseBoolean(newValue));
					break;

				case "string":
					UNSAFE.putObject(classObject, offset, newValue);
					break;

				default:
					source.sendError(Component.literal("Field uses an unsupported type!"));
					return Command.SINGLE_SUCCESS;
				}
				String fieldValue = field.get(null).toString();

				source.sendFeedback(Component.literal("Field Set » ").withColor(colourProfile.primaryColour.getAsInt())
						.append(Component.literal("[" + field.getType().getName() + "] " + field.getName() + ": ").withColor(colourProfile.secondaryColour.getAsInt())
								.append(Component.literal(fieldValue).withColor(colourProfile.infoColour.getAsInt()))));
			}

		} catch (ReflectiveOperationException e) {
			switch (e) {
				case ClassNotFoundException ex -> source.sendError(Constants.PREFIX.get().append(Component.literal("The requested class wasn't found!").withStyle(ChatFormatting.RED)));
				case NoSuchFieldException ex -> source.sendError(Constants.PREFIX.get().append(Component.literal("The requested field wasn't found!").withStyle(ChatFormatting.RED)));

				case null, default -> {}
			}

			e.printStackTrace();
		}

		return Command.SINGLE_SUCCESS;
	}
}
