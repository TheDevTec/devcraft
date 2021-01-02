package me.devtec.fang.commands;

public class PluginCommand {
    private CommandExecutor executor;
    private TabCompleter completer;
    private String permission;
    private String[] aliases;

    private final String name;
    public PluginCommand(String name){
        this.name=name;
    }

    public CommandExecutor getCommandExecutor(){
        return executor;
    }

    public void setCommandExecutor(CommandExecutor executor){
        this.executor=executor;
    }

    public TabCompleter getTabCompleter(){
        return completer;
    }

    public void setTabCompleter(TabCompleter completer){
        this.completer=completer;
    }

    public String getName(){
        return name;
    }

    public String getPermission(){
        return permission;
    }

    public void setPermission(String permission){
        this.permission=permission;
    }

    public String[] getAliases(){
        return aliases;
    }

    public void setAliases(String... aliases){
        this.aliases=aliases;
    }
}
