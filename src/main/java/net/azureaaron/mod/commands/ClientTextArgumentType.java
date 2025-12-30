package net.azureaaron.mod.commands;

import java.util.Arrays;
import java.util.Collection;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.parsing.packrat.commands.CommandArgumentParser;

public class ClientTextArgumentType implements ArgumentType<Component> {
	private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"",
			"\"{\"text\":\"hello world\"}", "[\"\"]");
	public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(
			text -> Component.translatable("argument.component.invalid", text));
	private static final CommandArgumentParser<Tag> PARSER = SnbtGrammar.createParser(NbtOps.INSTANCE);

	private ClientTextArgumentType() {}//TextArgumentType

	public static Component getTextArgument(CommandContext<FabricClientCommandSource> context, String name) {
		return context.getArgument(name, Component.class);
	}

	public static ClientTextArgumentType text() {
		return new ClientTextArgumentType();
	}

	@Override
	public Component parse(StringReader stringReader) throws CommandSyntaxException {
		//Convert &z to §z
		String str = stringReader.getRemaining().replaceAll("&z", "§z").replaceAll("&Z", "§Z");
		StringReader reader = new StringReader(str);
		try {
			Component text = PARSER.withCodec(NbtOps.INSTANCE, PARSER, ComponentSerialization.CODEC, INVALID_COMPONENT_EXCEPTION).parseForCommands(reader);
			if (text == null) {
				throw INVALID_COMPONENT_EXCEPTION.createWithContext(reader, "empty");
			}
			// Since everything was successful we'll pretend to have read everything
			stringReader.setCursor(stringReader.getString().length());

			return text;
		} catch (Exception exception) {
			String string = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
			throw INVALID_COMPONENT_EXCEPTION.createWithContext(reader, string);
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
