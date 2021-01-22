package net.minestom.server.event.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import net.minestom.server.chat.JsonMessage;
import net.minestom.server.entity.Player;
import net.minestom.server.event.CancellableEvent;
import net.minestom.server.event.PlayerEvent;

/**
 * Called every time a {@link Player} write and send something in the chat.
 * The event can be cancelled to do not send anything, and the format can be changed.
 */
public class PlayerChatEvent extends PlayerEvent implements CancellableEvent {

    private final Collection<Player> recipients;
    private String message;
    private Function<PlayerChatEvent, JsonMessage> chatFormat;

    private boolean cancelled;

    public PlayerChatEvent(Player player, Collection<Player> recipients, String message) {
        super(player);
        this.recipients = new ArrayList<>(recipients);
        this.message = message;
    }

    /**
     * Changes the chat format.
     *
     * @param chatFormat the custom chat format, null to use the default one
     */
    public void setChatFormat(Function<PlayerChatEvent, JsonMessage> chatFormat) {
        this.chatFormat = chatFormat;
    }

    /**
     * Those are the players who will receive the message.
     * <p>
     * It can be modified to add or remove recipient.
     *
     * @return a modifiable list of message targets
     */
    public Collection<Player> getRecipients() {
        return recipients;
    }

    /**
     * Gets the message sent.
     *
     * @return the sender's message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Used to change the message.
     *
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Used to retrieve the chat format for this message.
     * <p>
     * If null, the default format will be used.
     *
     * @return the chat format which will be used, null if this is the default one
     */
    public Function<PlayerChatEvent, JsonMessage> getChatFormatFunction() {
        return chatFormat;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
