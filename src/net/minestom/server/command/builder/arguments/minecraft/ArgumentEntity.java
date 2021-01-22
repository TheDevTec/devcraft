package net.minestom.server.command.builder.arguments.minecraft;

import java.util.Arrays;
import java.util.List;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.registry.Registries;
import net.minestom.server.utils.entity.EntityFinder;
import net.minestom.server.utils.math.IntRange;

// TODO

/**
 * Represents the target selector argument.
 * https://minecraft.gamepedia.com/Commands#Target_selectors
 */
public class ArgumentEntity extends Argument<EntityFinder> {

    public static final int INVALID_SYNTAX = -2;
    public static final int ONLY_SINGLE_ENTITY_ERROR = -3;
    public static final int ONLY_PLAYERS_ERROR = -4;
    public static final int INVALID_ARGUMENT_NAME = -5;
    public static final int INVALID_ARGUMENT_VALUE = -6;
    private static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();
    private static final List<String> selectorVariables = Arrays.asList("@p", "@r", "@a", "@e", "@s");
    private static final List<String> playersOnlySelector = Arrays.asList("@p", "@r", "@a", "@s");
    private static final List<String> singleOnlySelector = Arrays.asList("@p", "@r", "@s");
    // List with all the valid arguments
    private static final List<String> validArguments = Arrays.asList(
            "x", "y", "z",
            "distance", "dx", "dy", "dz",
            "scores", "tag", "team", "limit", "sort", "level", "gamemode", "name",
            "x_rotation", "y_rotation", "type", "nbt", "advancements", "predicate");

    // List with all the easily parsable arguments which only require reading until a specific character (comma)
    private static final List<String> simpleArguments = Arrays.asList(
            "x", "y", "z",
            "distance", "dx", "dy", "dz",
            "scores", "tag", "team", "limit", "sort", "level", "gamemode",
            "x_rotation", "y_rotation", "type", "advancements", "predicate");
    private boolean onlySingleEntity;
    private boolean onlyPlayers;

    public ArgumentEntity(String id) {
        super(id, true);
    }

    public ArgumentEntity singleEntity(boolean singleEntity) {
        this.onlySingleEntity = singleEntity;
        return this;
    }

    public ArgumentEntity onlyPlayers(boolean onlyPlayers) {
        this.onlyPlayers = onlyPlayers;
        return this;
    }

    @Override
    public EntityFinder parse(String input) throws ArgumentSyntaxException {
        return staticParse(input, onlySingleEntity, onlyPlayers);
    }

    public static EntityFinder staticParse(String input,
                                           boolean onlySingleEntity, boolean onlyPlayers) throws ArgumentSyntaxException {
        // Check for raw player name
        if (input.length() <= 16) {
            if (CONNECTION_MANAGER.getPlayer(input) != null) {
                return new EntityFinder()
                        .setTargetSelector(EntityFinder.TargetSelector.ALL_PLAYERS)
                        .setName(input, EntityFinder.ToggleableType.INCLUDE);
            }
        }

        // The minimum size is always 0 (for the selector variable, ex: @p)
        if (input.length() < 2)
            throw new ArgumentSyntaxException("Length needs to be > 1", input, INVALID_SYNTAX);

        // The target selector variable always start by '@'
        if (!input.startsWith("@"))
            throw new ArgumentSyntaxException("Target selector needs to start with @", input, INVALID_SYNTAX);

        final String selectorVariable = input.substring(0, 2);

        // Check if the selector variable used exists
        if (!selectorVariables.contains(selectorVariable))
            throw new ArgumentSyntaxException("Invalid selector variable", input, INVALID_SYNTAX);

        // Check if it should only select single entity and if the selector variable valid the condition
        if (onlySingleEntity && !singleOnlySelector.contains(selectorVariable))
            throw new ArgumentSyntaxException("Argument requires only a single entity", input, ONLY_SINGLE_ENTITY_ERROR);

        // Check if it should only select players and if the selector variable valid the condition
        if (onlyPlayers && !playersOnlySelector.contains(selectorVariable))
            throw new ArgumentSyntaxException("Argument requires only players", input, ONLY_PLAYERS_ERROR);

        // Create the EntityFinder which will be used for the rest of the parsing
        final EntityFinder entityFinder = new EntityFinder()
                .setTargetSelector(toTargetSelector(selectorVariable));

        // The selector is a single selector variable which verify all the conditions
        if (input.length() == 2)
            return entityFinder;

        // START PARSING THE STRUCTURE
        final String structure = input.substring(2);
        return parseStructure(input, entityFinder, structure);
    }

    private static EntityFinder parseStructure(String input,
                                               EntityFinder entityFinder,
                                               String structure) throws ArgumentSyntaxException {
        // The structure isn't opened or closed properly
        if (!structure.startsWith("[") || !structure.endsWith("]"))
            throw new ArgumentSyntaxException("Target selector needs to start and end with brackets", input, INVALID_SYNTAX);

        // Remove brackets
        final String structureData = structure.substring(1, structure.length() - 1);
        //System.out.println("structure data: " + structureData);

        String currentArgument = "";
        for (int i = 0; i < structureData.length(); i++) {
            final char c = structureData.charAt(i);
            if (c == '=') {

                // Replace all unnecessary spaces
                currentArgument = currentArgument.trim();

                if (!validArguments.contains(currentArgument))
                    throw new ArgumentSyntaxException("Argument name '" + currentArgument + "' does not exist", input, INVALID_ARGUMENT_NAME);

                i = parseArgument(entityFinder, currentArgument, input, structureData, i);
                currentArgument = ""; // Reset current argument
            } else {
                currentArgument += c;
            }
        }

        return entityFinder;
    }

    private static int parseArgument(EntityFinder entityFinder,
                                     String argumentName,
                                     String input,
                                     String structureData, int beginIndex) throws ArgumentSyntaxException {
        final char comma = ',';
        final boolean isSimple = simpleArguments.contains(argumentName);

        int finalIndex = beginIndex + 1;
        StringBuilder valueBuilder = new StringBuilder();
        for (; finalIndex < structureData.length(); finalIndex++) {
            final char c = structureData.charAt(finalIndex);

            // Command is parsed
            if (isSimple && c == comma)
                break;

            valueBuilder.append(c);
        }

        final String value = valueBuilder.toString().trim();

        //System.out.println("value: " + value);
        switch (argumentName) {
            case "type": {
                final boolean include = !value.startsWith("!");
                final String entityName = include ? value : value.substring(1);
                final EntityType entityType = Registries.getEntityType(entityName);
                if (entityType == null)
                    throw new ArgumentSyntaxException("Invalid entity name", input, INVALID_ARGUMENT_VALUE);
                entityFinder.setEntity(entityType, include ? EntityFinder.ToggleableType.INCLUDE : EntityFinder.ToggleableType.EXCLUDE);
                break;
            }
            case "gamemode": {
                final boolean include = !value.startsWith("!");
                final String gameModeName = include ? value : value.substring(1);
                try {
                    final GameMode gameMode = GameMode.valueOf(gameModeName);
                    entityFinder.setGameMode(gameMode, include ? EntityFinder.ToggleableType.INCLUDE : EntityFinder.ToggleableType.EXCLUDE);
                } catch (IllegalArgumentException e) {
                    throw new ArgumentSyntaxException("Invalid entity game mode", input, INVALID_ARGUMENT_VALUE);
                }
                break;
            }
            case "limit":
                try {
                    final int limit = Integer.parseInt(value);
                    entityFinder.setLimit(limit);
                } catch (NumberFormatException e) {
                    throw new ArgumentSyntaxException("Invalid limit number", input, INVALID_ARGUMENT_VALUE);
                }
                break;
            case "sort":
                try {
                    EntityFinder.EntitySort entitySort = EntityFinder.EntitySort.valueOf(value.toUpperCase());
                    entityFinder.setEntitySort(entitySort);
                } catch (IllegalArgumentException e) {
                    throw new ArgumentSyntaxException("Invalid entity sort", input, INVALID_ARGUMENT_VALUE);
                }
                break;
            case "level":
                try {
                    final IntRange level = ArgumentIntRange.staticParse(value);
                    entityFinder.setLevel(level);
                } catch (ArgumentSyntaxException e) {
                    throw new ArgumentSyntaxException("Invalid level number", input, INVALID_ARGUMENT_VALUE);
                }
                break;
            case "distance":
                try {
                    final IntRange distance = ArgumentIntRange.staticParse(value);
                    entityFinder.setDistance(distance);
                } catch (ArgumentSyntaxException e) {
                    throw new ArgumentSyntaxException("Invalid level number", input, INVALID_ARGUMENT_VALUE);
                }
                break;
        }

        return finalIndex;
    }

    public boolean isOnlySingleEntity() {
        return onlySingleEntity;
    }

    public boolean isOnlyPlayers() {
        return onlyPlayers;
    }

    private static EntityFinder.TargetSelector toTargetSelector(String selectorVariable) {
        if (selectorVariable.equals("@p"))
            return EntityFinder.TargetSelector.NEAREST_PLAYER;
        if (selectorVariable.equals("@r"))
            return EntityFinder.TargetSelector.RANDOM_PLAYER;
        if (selectorVariable.equals("@a"))
            return EntityFinder.TargetSelector.ALL_PLAYERS;
        if (selectorVariable.equals("@e"))
            return EntityFinder.TargetSelector.ALL_ENTITIES;
        if (selectorVariable.equals("@s"))
            return EntityFinder.TargetSelector.SELF;
        throw new IllegalStateException("Weird selector variable: " + selectorVariable);
    }
}
