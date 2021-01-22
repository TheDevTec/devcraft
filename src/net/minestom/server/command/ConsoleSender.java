package net.minestom.server.command;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minestom.server.permission.Permission;

/**
 * Represents the console when sending a command to the server.
 */
public class ConsoleSender implements CommandSender {

    private final Set<Permission> permissions = new CopyOnWriteArraySet<>();

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public Set<Permission> getAllPermissions() {
        return permissions;
    }
}
