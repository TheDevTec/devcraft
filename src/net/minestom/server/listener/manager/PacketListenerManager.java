package net.minestom.server.listener.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.listener.AbilitiesListener;
import net.minestom.server.listener.AdvancementTabListener;
import net.minestom.server.listener.AnimationListener;
import net.minestom.server.listener.BlockPlacementListener;
import net.minestom.server.listener.ChatMessageListener;
import net.minestom.server.listener.CreativeInventoryActionListener;
import net.minestom.server.listener.EntityActionListener;
import net.minestom.server.listener.KeepAliveListener;
import net.minestom.server.listener.PlayerDiggingListener;
import net.minestom.server.listener.PlayerHeldListener;
import net.minestom.server.listener.PlayerPositionListener;
import net.minestom.server.listener.PlayerVehicleListener;
import net.minestom.server.listener.PluginMessageListener;
import net.minestom.server.listener.RecipeListener;
import net.minestom.server.listener.ResourcePackListener;
import net.minestom.server.listener.SettingsListener;
import net.minestom.server.listener.SpectateListener;
import net.minestom.server.listener.StatusListener;
import net.minestom.server.listener.TabCompleteListener;
import net.minestom.server.listener.TeleportListener;
import net.minestom.server.listener.UseEntityListener;
import net.minestom.server.listener.UseItemListener;
import net.minestom.server.listener.WindowListener;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.packet.client.ClientPlayPacket;
import net.minestom.server.network.packet.client.play.ClientAdvancementTabPacket;
import net.minestom.server.network.packet.client.play.ClientAnimationPacket;
import net.minestom.server.network.packet.client.play.ClientChatMessagePacket;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;
import net.minestom.server.network.packet.client.play.ClientCloseWindow;
import net.minestom.server.network.packet.client.play.ClientCraftRecipeRequest;
import net.minestom.server.network.packet.client.play.ClientCreativeInventoryActionPacket;
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket;
import net.minestom.server.network.packet.client.play.ClientHeldItemChangePacket;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.network.packet.client.play.ClientKeepAlivePacket;
import net.minestom.server.network.packet.client.play.ClientPlayerAbilitiesPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerBlockPlacementPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPluginMessagePacket;
import net.minestom.server.network.packet.client.play.ClientResourcePackStatusPacket;
import net.minestom.server.network.packet.client.play.ClientSettingsPacket;
import net.minestom.server.network.packet.client.play.ClientSpectatePacket;
import net.minestom.server.network.packet.client.play.ClientStatusPacket;
import net.minestom.server.network.packet.client.play.ClientSteerBoatPacket;
import net.minestom.server.network.packet.client.play.ClientSteerVehiclePacket;
import net.minestom.server.network.packet.client.play.ClientTabCompletePacket;
import net.minestom.server.network.packet.client.play.ClientTeleportConfirmPacket;
import net.minestom.server.network.packet.client.play.ClientUseItemPacket;
import net.minestom.server.network.packet.client.play.ClientVehicleMovePacket;
import net.minestom.server.network.packet.client.play.ClientWindowConfirmationPacket;
import net.minestom.server.network.packet.server.ServerPacket;

public final class PacketListenerManager {

    private static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();

    @SuppressWarnings("rawtypes")
	private final Map<Class<? extends ClientPlayPacket>, PacketListenerConsumer> listeners = new ConcurrentHashMap<>();

    public PacketListenerManager() {
        setListener(ClientKeepAlivePacket.class, KeepAliveListener::listener);
        setListener(ClientChatMessagePacket.class, ChatMessageListener::listener);
        setListener(ClientClickWindowPacket.class, WindowListener::clickWindowListener);
        setListener(ClientCloseWindow.class, WindowListener::closeWindowListener);
        setListener(ClientWindowConfirmationPacket.class, WindowListener::windowConfirmationListener);
        setListener(ClientEntityActionPacket.class, EntityActionListener::listener);
        setListener(ClientHeldItemChangePacket.class, PlayerHeldListener::heldListener);
        setListener(ClientPlayerBlockPlacementPacket.class, BlockPlacementListener::listener);
        setListener(ClientSteerVehiclePacket.class, PlayerVehicleListener::steerVehicleListener);
        setListener(ClientVehicleMovePacket.class, PlayerVehicleListener::vehicleMoveListener);
        setListener(ClientSteerBoatPacket.class, PlayerVehicleListener::boatSteerListener);
        setListener(ClientPlayerPacket.class, PlayerPositionListener::playerPacketListener);
        setListener(ClientPlayerRotationPacket.class, PlayerPositionListener::playerLookListener);
        setListener(ClientPlayerPositionPacket.class, PlayerPositionListener::playerPositionListener);
        setListener(ClientPlayerPositionAndRotationPacket.class, PlayerPositionListener::playerPositionAndLookListener);
        setListener(ClientPlayerDiggingPacket.class, PlayerDiggingListener::playerDiggingListener);
        setListener(ClientAnimationPacket.class, AnimationListener::animationListener);
        setListener(ClientInteractEntityPacket.class, UseEntityListener::useEntityListener);
        setListener(ClientUseItemPacket.class, UseItemListener::useItemListener);
        setListener(ClientStatusPacket.class, StatusListener::listener);
        setListener(ClientSettingsPacket.class, SettingsListener::listener);
        setListener(ClientCreativeInventoryActionPacket.class, CreativeInventoryActionListener::listener);
        setListener(ClientCraftRecipeRequest.class, RecipeListener::listener);
        setListener(ClientTabCompletePacket.class, TabCompleteListener::listener);
        setListener(ClientPluginMessagePacket.class, PluginMessageListener::listener);
        setListener(ClientPlayerAbilitiesPacket.class, AbilitiesListener::listener);
        setListener(ClientTeleportConfirmPacket.class, TeleportListener::listener);
        setListener(ClientResourcePackStatusPacket.class, ResourcePackListener::listener);
        setListener(ClientAdvancementTabPacket.class, AdvancementTabListener::listener);
        setListener(ClientSpectatePacket.class, SpectateListener::listener);
    }

    /**
     * Processes a packet by getting its {@link PacketListenerConsumer} and calling all the packet listeners.
     *
     * @param packet the received packet
     * @param player the player who sent the packet
     * @param <T>    the packet type
     */
    public <T extends ClientPlayPacket> void processClientPacket(T packet, Player player) {

        final Class<?> clazz = packet.getClass();

        @SuppressWarnings("unchecked")
		PacketListenerConsumer<T> packetListenerConsumer = listeners.get(clazz);

        final PacketController packetController = new PacketController();
        for (ClientPacketConsumer clientPacketConsumer : CONNECTION_MANAGER.getReceivePacketConsumers()) {
            clientPacketConsumer.accept(player, packetController, packet);
        }

        if (packetController.isCancel())
            return;

        // Finally execute the listener
        if (packetListenerConsumer != null) {
            packetListenerConsumer.accept(packet, player);
        }
    }

    /**
     * Executes the consumers from {@link ConnectionManager#onPacketSend(ServerPacketConsumer)}.
     *
     * @param packet  the packet to process
     * @param players the players which should receive the packet
     * @return true if the packet is not cancelled, false otherwise
     */
    public boolean processServerPacket(ServerPacket packet, Collection<Player> players) {
        final List<ServerPacketConsumer> consumers = CONNECTION_MANAGER.getSendPacketConsumers();
        if (consumers.isEmpty()) {
            return true;
        }

        final PacketController packetController = new PacketController();
        for (ServerPacketConsumer serverPacketConsumer : consumers) {
            serverPacketConsumer.accept(players, packetController, packet);
        }

        return !packetController.isCancel();
    }

    /**
     * Sets the listener of a packet.
     * <p>
     * WARNING: this will overwrite the default minestom listener, this is not reversible.
     *
     * @param packetClass the class of the packet
     * @param consumer    the new packet's listener
     * @param <T>         the type of the packet
     */
    public <T extends ClientPlayPacket> void setListener(Class<T> packetClass, PacketListenerConsumer<T> consumer) {
        this.listeners.put(packetClass, consumer);
    }

}
