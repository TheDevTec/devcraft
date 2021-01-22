package net.minestom.server.command.builder;

import java.util.Map;

import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.Player;

/**
 * Represents a syntax in {@link Command}
 * which is initialized with {@link Command#addSyntax(CommandExecutor, Argument[])}.
 */
public class CommandSyntax {

    private CommandCondition commandCondition;
    private CommandExecutor executor;

    private final Map<String, Object> defaultValuesMap;
    private final Argument<?>[] args;

    protected CommandSyntax(CommandCondition commandCondition,
                            CommandExecutor commandExecutor,
                            Map<String, Object> defaultValuesMap,
                            Argument<?>... args) {
        this.commandCondition = commandCondition;
        this.executor = commandExecutor;

        this.defaultValuesMap = defaultValuesMap;
        this.args = args;
    }

    protected CommandSyntax(CommandCondition commandCondition,
                            CommandExecutor commandExecutor,
                            Argument<?>... args) {
        this(commandCondition, commandExecutor, null, args);
    }

    /**
     * Gets the condition to use this syntax.
     *
     * @return this command condition, null if none
     */
    public CommandCondition getCommandCondition() {
        return commandCondition;
    }

    /**
     * Changes the command condition of this syntax.
     * <p>
     * Be aware that changing the command condition will not automatically update players auto-completion.
     * You can create a new packet containing the changes with
     * {@link net.minestom.server.command.CommandManager#createDeclareCommandsPacket(Player)}.
     *
     * @param commandCondition the new command condition, null to remove it
     */
    public void setCommandCondition(CommandCondition commandCondition) {
        this.commandCondition = commandCondition;
    }

    /**
     * Gets the {@link CommandExecutor} of this syntax, executed once the syntax is properly written.
     *
     * @return the executor of this syntax
     */
    public CommandExecutor getExecutor() {
        return executor;
    }

    /**
     * Changes the {@link CommandExecutor} of this syntax.
     *
     * @param executor the new executor
     */
    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    protected Map<String, Object> getDefaultValuesMap() {
        return defaultValuesMap;
    }

    /**
     * Gets all the required {@link Argument} for this syntax.
     *
     * @return the required arguments
     */
    public Argument<?>[] getArguments() {
        return args;
    }

}
