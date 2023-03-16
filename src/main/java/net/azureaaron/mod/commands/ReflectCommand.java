package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.azureaaron.mod.Colour.colourProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.azureaaron.mod.util.UnsafeAccess;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
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
	
	private static final Text INVALID_OPCODE = Text.literal("Invalid Opcode!").styled(style -> style.withColor(Formatting.RED));
	private static final Text INVALID_TYPE = Text.literal("Invalid Type!").styled(style -> style.withColor(Formatting.RED));
	private static final Text TYPE_MISMATCH = Text.literal("Type Mismatch!").styled(style -> style.withColor(Formatting.RED));
	private static final Text TYPE_MISSING = Text.literal("Missing 'type' parameter!").styled(style -> style.withColor(Formatting.RED));
	private static final List<String> OPCODES = Arrays.asList("GETFIELD", "PUTFIELD");
	private static final List<String> TYPES = Arrays.asList("byte", "char", "double", "float", "int", "long", "short", "boolean", "string");
	
    private static int reflectionExecutor(FabricClientCommandSource source, String opcode, String targetClass, String target, String type, String newValue) {
    	if(!OPCODES.contains(opcode)) {
    		source.sendError(INVALID_OPCODE);
    		return Command.SINGLE_SUCCESS;
    	}
    	
    	if(type != null && !TYPES.contains(type)) {
    		source.sendError(INVALID_TYPE);
    		return Command.SINGLE_SUCCESS;
    	}
    	
    	try {
    		
        	Class<?> cls = Class.forName(targetClass);
        	Object classObject = (Object) cls;
        	
        	if(opcode.equals("GETFIELD")) {
        		Field field = cls.getDeclaredField(target);
        		field.setAccessible(true);
        		String fieldValue = field.get(null).toString();
        		
        		source.sendFeedback(Text.literal("Field Get » ").styled(style -> style.withColor(colourProfile.primaryColour))
        				.append(Text.literal("[" + field.getType().getName() + "] " + field.getName() + ": ").styled(style -> style.withColor(colourProfile.secondaryColour))
        						.append(Text.literal(fieldValue).styled(style -> style.withColor(colourProfile.infoColour)))));
        	}
        	
        	if(opcode.equals("PUTFIELD")) {
        		if(type == null) {
        			source.sendError(TYPE_MISSING);
        			return Command.SINGLE_SUCCESS;
        		}
        		
        		Field field = cls.getDeclaredField(target);
        		field.setAccessible(true);
        		long offset = (Modifier.isStatic(field.getModifiers())) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);
        		
        		if(!field.getType().getName().equals(type.replace("string", "java.lang.String"))) {
            		source.sendError(TYPE_MISMATCH);
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
        		        		
        		source.sendFeedback(Text.literal("Field Set » ").styled(style -> style.withColor(colourProfile.primaryColour))
        				.append(Text.literal("[" + field.getType().getName() + "] " + field.getName() + ": ").styled(style -> style.withColor(colourProfile.secondaryColour))
        						.append(Text.literal(fieldValue).styled(style -> style.withColor(colourProfile.infoColour)))));
        	}
        	
    	} catch(ReflectiveOperationException e) {
    		if(e instanceof ClassNotFoundException) source.sendError(Text.literal("The requested class wasn't found!").styled(style -> style.withColor(Formatting.RED)));
    		if(e instanceof NoSuchFieldException) source.sendError(Text.literal("The requested field wasn't found!").styled(style -> style.withColor(Formatting.RED)));
    		e.printStackTrace();
    	}
    	
		return Command.SINGLE_SUCCESS;
    }
}
