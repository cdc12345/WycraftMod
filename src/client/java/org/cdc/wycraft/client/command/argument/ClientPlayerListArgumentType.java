package org.cdc.wycraft.client.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;

public class ClientPlayerListArgumentType implements ArgumentType<String> {

	@Override public String parse(StringReader reader) throws CommandSyntaxException {
		return reader.readString();
	}

	public static String getPlayerName(CommandContext<?> context, String name) {
		return context.getArgument(name, String.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		Objects.requireNonNull(Minecraft.getInstance().getConnection()).getOnlinePlayers().forEach(a -> {
			builder.suggest(Objects.requireNonNull(a.getProfile().name()));
		});
		return builder.buildFuture();
	}

	@Override public Collection<String> getExamples() {
		return Collections.singleton("cdc12345");
	}
}
