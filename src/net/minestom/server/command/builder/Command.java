package net.minestom.server.command.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentDynamicStringArray;
import net.minestom.server.command.builder.arguments.ArgumentDynamicWord;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.SuggestionType;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.utils.validate.Check;

/**
 * Represents a command which has suggestion/auto-completion.
 * <p>
 * The command works using a list of valid syntaxes.
 * For instance we could build the command
 * "/health set Notch 50" into multiple argument types "/health [set/add/remove] [username] [integer]"
 * <p>
 * All the default argument types can be found in {@link ArgumentType}
 * and the syntax be created/registered using {@link #addSyntax(CommandExecutor, Argument[])}.
 * <p>
 * If the command is executed with an incorrect syntax or without any argument, the default {@link CommandExecutor} will be called,
 * you can set it using {@link #setDefaultExecutor(CommandExecutor)}.
 * <p>
 * Before any syntax to be successfully executed the {@link CommandSender} needs to validated
 * the {@link CommandCondition} sets with {@link #setCondition(CommandCondition)} (ignored if null).
 * <p>
 * Some {@link Argument} could also require additional condition (eg: a number which need to be between 2 values),
 * in this case, if the whole syntax is correct but not the argument condition,
 * you can listen to its error code using {@link #setArgumentCallback(ArgumentCallback, Argument)} or {@link Argument#setCallback(ArgumentCallback)}.
 */
public class Command {

    private final String name;
    private final String[] aliases;

    private CommandExecutor defaultExecutor;
    private CommandCondition condition;

    private final List<CommandSyntax> syntaxes;

    /**
     * Creates a {@link Command} with a name and one or multiple aliases.
     *
     * @param name    the name of the command
     * @param aliases the command aliases
     * @see #Command(String)
     */
    public Command(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;

        this.syntaxes = new ArrayList<>();
    }

    /**
     * Creates a {@link Command} with a name and no alias.
     *
     * @param name the name of the command
     * @see #Command(String, String...)
     */
    public Command(String name) {
        this(name, new String[0]);
    }

    /**
     * Gets the {@link CommandCondition}.
     * <p>
     * It is called after the parsing and just before the execution no matter the syntax used and can be used to check permissions or
     * the {@link CommandSender} type.
     * <p>
     * Worth mentioning that the condition is also used to know if the command known from a player (at connection).
     *
     * @return the command condition, null if not any
     */
    public CommandCondition getCondition() {
        return condition;
    }

    /**
     * Sets the {@link CommandCondition}.
     *
     * @param commandCondition the new command condition, null to do not call anything
     * @see #getCondition()
     */
    public void setCondition(CommandCondition commandCondition) {
        this.condition = commandCondition;
    }

    /**
     * Sets an {@link ArgumentCallback}.
     * <p>
     * The argument callback is called when there's an error in the argument.
     *
     * @param callback the callback for the argument
     * @param argument the argument which get the callback
     */
    public void setArgumentCallback(ArgumentCallback callback, Argument<?> argument) {
        argument.setCallback(callback);
    }

    /**
     * Adds a new syntax in the command.
     * <p>
     * A syntax is simply a list of arguments and an executor called when successfully parsed.
     *
     * @param commandCondition the condition to use the syntax
     * @param executor         the executor to call when the syntax is successfully received
     * @param args             all the arguments of the syntax, the length needs to be higher than 0
     * @return the created {@link CommandSyntax syntaxes},
     * there can be multiple of them when optional arguments are used
     */
    public Collection<CommandSyntax> addSyntax(CommandCondition commandCondition,
                                               CommandExecutor executor,
                                               Argument<?>... args) {
        Check.argCondition(args.length == 0,
                "The syntax argument cannot be empty, consider using Command#setDefaultExecutor");

        // Check optional argument(s)
        boolean hasOptional = false;
        {
            for (Argument<?> argument : args) {
                if (argument.isOptional()) {
                    hasOptional = true;
                }
                if (hasOptional && !argument.isOptional()) {
                    hasOptional = false;
                    break;
                }
            }
        }

        if (!hasOptional) {
            final CommandSyntax syntax = new CommandSyntax(commandCondition, executor, args);
            this.syntaxes.add(syntax);
            return Collections.singleton(syntax);
        } else {
            List<CommandSyntax> optionalSyntaxes = new ArrayList<>();

            // the 'args' array starts by all the required arguments, followed by the optional ones
            List<Argument<?>> requiredArguments = new ArrayList<>();
            Map<String, Object> defaultValuesMap = new HashMap<>();
            boolean optionalBranch = false;
            int i = 0;
            for (Argument<?> argument : args) {
                final boolean isLast = ++i == args.length;
                if (argument.isOptional()) {
                    // Set default value
                    defaultValuesMap.put(argument.getId(), argument.getDefaultValue());

                    if (!optionalBranch && !requiredArguments.isEmpty()) {
                        // First optional argument, create a syntax with current cached arguments
                        final CommandSyntax syntax = new CommandSyntax(commandCondition, executor, defaultValuesMap,
                                requiredArguments.toArray(new Argument[0]));
                        optionalSyntaxes.add(syntax);
                        optionalBranch = true;
                    } else {
                        // New optional argument, save syntax with current cached arguments and save default value
                        final CommandSyntax syntax = new CommandSyntax(commandCondition, executor, defaultValuesMap,
                                requiredArguments.toArray(new Argument[0]));
                        optionalSyntaxes.add(syntax);
                    }
                }
                requiredArguments.add(argument);
                if (isLast) {
                    // Create the last syntax
                    final CommandSyntax syntax = new CommandSyntax(commandCondition, executor, defaultValuesMap,
                            requiredArguments.toArray(new Argument[0]));
                    optionalSyntaxes.add(syntax);
                }
            }

            this.syntaxes.addAll(optionalSyntaxes);
            return optionalSyntaxes;
        }
    }

    /**
     * Adds a new syntax without condition.
     *
     * @see #addSyntax(CommandCondition, CommandExecutor, Argument[])
     */
    public Collection<CommandSyntax> addSyntax(CommandExecutor executor, Argument<?>... args) {
        return addSyntax(null, executor, args);
    }

    /**
     * Gets the main command's name.
     *
     * @return the main command's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the command's aliases.
     *
     * @return the command aliases, can be null or empty
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Gets the default {@link CommandExecutor} which is called when there is no argument
     * or if no corresponding syntax has been found.
     *
     * @return the default executor, null if not any
     * @see #setDefaultExecutor(CommandExecutor)
     */
    public CommandExecutor getDefaultExecutor() {
        return defaultExecutor;
    }

    /**
     * Sets the default {@link CommandExecutor}.
     *
     * @param executor the new default executor, null to remove it
     * @see #getDefaultExecutor()
     */
    public void setDefaultExecutor(CommandExecutor executor) {
        this.defaultExecutor = executor;
    }

    /**
     * Gets all the syntaxes of this command.
     *
     * @return a collection containing all this command syntaxes
     * @see #addSyntax(CommandCondition, CommandExecutor, Argument[])
     */
    public Collection<CommandSyntax> getSyntaxes() {
        return syntaxes;
    }

    /**
     * Allows for tab auto completion, this is called everytime the player press a key in the chat
     * when in a dynamic argument ({@link ArgumentDynamicWord} (when {@link SuggestionType#ASK_SERVER} is used)
     * and {@link ArgumentDynamicStringArray}).
     *
     * @param sender the command sender
     * @param text   the whole player's text
     * @return the array containing all the suggestion for the current arg (split SPACE), can be null
     */
    public String[] onDynamicWrite(CommandSender sender, String text) {
        return null;
    }

    /**
     * Called when a {@link CommandSender} executes this command before any syntax callback.
     * <p>
     * WARNING: the {@link CommandCondition} is not executed, and all the {@link CommandSyntax} are not checked,
     * this is called every time a {@link CommandSender} send a command which start by {@link #getName()} or {@link #getAliases()}.
     * <p>
     * Can be used if you wish to still suggest the player syntaxes but want to parse things mostly by yourself.
     *
     * @param sender    the {@link CommandSender}
     * @param arguments the UNCHECKED arguments of the command, some can be null even when unexpected
     * @param command   the raw UNCHECKED received command
     */
    public void globalListener(CommandSender sender, Arguments arguments, String command) {
    }

}
