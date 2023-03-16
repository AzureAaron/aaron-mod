package net.azureaaron.mod.commands;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class TestCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("test")
				.executes(context -> printTest(context.getSource()))
				.then(argument("option", word())
						.executes(context -> printTest(context.getSource(), getString(context, "option")))));
	}
			
    private static int printTest(FabricClientCommandSource source) {
    	source.sendFeedback(Texts.bracketedCopyable("test!"));
        return Command.SINGLE_SUCCESS;
    }
    
    private static int printTest(FabricClientCommandSource source, String option) {
    	source.sendFeedback(Text.literal("No tests to be done right now!"));
    	return Command.SINGLE_SUCCESS;
    }
}
