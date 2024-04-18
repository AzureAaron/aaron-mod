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
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.util.Constants;
import net.azureaaron.mod.util.UnsafeAccess;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ReflectCommand implements UnsafeAccess {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		//Maybe turn this into subcommands
		dispatcher.register(literal("reflect")
				.then(argument("opcode", word())
						.suggests((context, builder) -> CommandSource.suggestMatching(OPCODES, builder))
						.then(argument("target class", word())
								.then(argument("target field", word())
										.executes(context -> reflectionExecutor(context.getSource(), getString(context, "opcode"), getString(context, "target class"), getString(context, "target field"), null, null))
										.then(argument("type", word())
												.suggests((context, builder) -> CommandSource.suggestMatching(TYPES, builder))
												.then(argument("new value", string())
												.executes(context -> reflectionExecutor(context.getSource(), getString(context, "opcode"), getString(context, "target class"), getString(context, "target field"), getString(context, "type"), getString(context, "new value")))))))));
	}
	
	private static final Supplier<MutableText> INVALID_OPCODE = () -> Constants.PREFIX.get().append(Text.literal("Invalid Opcode!").formatted(Formatting.RED));
	private static final Supplier<MutableText> INVALID_TYPE = () -> Constants.PREFIX.get().append(Text.literal("Invalid Type!").formatted(Formatting.RED));
	private static final Supplier<MutableText> TYPE_MISMATCH = () -> Constants.PREFIX.get().append(Text.literal("Type Mismatch!").formatted(Formatting.RED));
	private static final Supplier<MutableText> TYPE_MISSING = () -> Constants.PREFIX.get().append(Text.literal("Missing 'type' parameter!").formatted(Formatting.RED));
	private static final List<String> OPCODES = Arrays.asList("GETFIELD", "PUTFIELD");
	private static final List<String> TYPES = Arrays.asList("byte", "char", "double", "float", "int", "long", "short", "boolean", "string");
	
    @SuppressWarnings("deprecation")
	private static int reflectionExecutor(FabricClientCommandSource source, String opcode, String targetClass, String target, String type, String newValue) {
    	ColourProfiles colourProfile = AaronModConfigManager.get().colourProfile;
    	
    	if(!OPCODES.contains(opcode)) {
    		source.sendError(INVALID_OPCODE.get());
    		return Command.SINGLE_SUCCESS;
    	}
    	
    	if(type != null && !TYPES.contains(type)) {
    		source.sendError(INVALID_TYPE.get());
    		return Command.SINGLE_SUCCESS;
    	}
    	
    	try {
    		
        	Class<?> cls = Class.forName(targetClass);
        	Object classObject = (Object) cls;
        	
        	if(opcode.equals("GETFIELD")) {
        		Field field = cls.getDeclaredField(target);
        		field.setAccessible(true);
        		String fieldValue = field.get(null).toString();
        		
        		source.sendFeedback(Text.literal("Field Get » ").withColor(colourProfile.primaryColour.getAsInt())
        				.append(Text.literal("[" + field.getType().getName() + "] " + field.getName() + ": ").withColor(colourProfile.secondaryColour.getAsInt())
        						.append(Text.literal(fieldValue).withColor(colourProfile.infoColour.getAsInt()))));
        	}
        	
        	if(opcode.equals("PUTFIELD")) {
        		if(type == null) {
        			source.sendError(TYPE_MISSING.get());
        			return Command.SINGLE_SUCCESS;
        		}
        		
        		Field field = cls.getDeclaredField(target);
        		field.setAccessible(true);
        		long offset = (Modifier.isStatic(field.getModifiers())) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);
        		
        		if(!field.getType().getName().equals(type.replace("string", "java.lang.String"))) {
            		source.sendError(TYPE_MISMATCH.get());
            		return Command.SINGLE_SUCCESS;
        		}
        		        		
        		switch(type) {
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
        			source.sendError(Text.literal("Field uses an unsupported type!"));
        			return Command.SINGLE_SUCCESS;
        		}
        		String fieldValue = field.get(null).toString();
        		        		
        		source.sendFeedback(Text.literal("Field Set » ").withColor(colourProfile.primaryColour.getAsInt())
        				.append(Text.literal("[" + field.getType().getName() + "] " + field.getName() + ": ").withColor(colourProfile.secondaryColour.getAsInt())
        						.append(Text.literal(fieldValue).withColor(colourProfile.infoColour.getAsInt()))));
        	}
        	
    	} catch (ReflectiveOperationException e) {
    		switch (e) {
    			case ClassNotFoundException ex -> source.sendError(Constants.PREFIX.get().append(Text.literal("The requested class wasn't found!").formatted(Formatting.RED)));
    			case NoSuchFieldException ex -> source.sendError(Constants.PREFIX.get().append(Text.literal("The requested field wasn't found!").formatted(Formatting.RED)));

    			case null, default -> {}
    		}

    		e.printStackTrace();
    	}
    	
		return Command.SINGLE_SUCCESS;
    }
}
