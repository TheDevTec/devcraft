package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.CancellableEvent;
import net.minestom.server.event.PlayerEvent;
import net.minestom.server.instance.block.CustomBlock;
import net.minestom.server.utils.BlockPosition;

public class PlayerBlockBreakEvent extends PlayerEvent implements CancellableEvent {

    private final BlockPosition blockPosition;

    private final short blockStateId;
    private final CustomBlock customBlock;

    private short resultBlockStateId;
    private short resultCustomBlockId;

    private boolean cancelled;

    public PlayerBlockBreakEvent(Player player, BlockPosition blockPosition,
                                 short blockStateId, CustomBlock customBlock,
                                 short resultBlockStateId, short resultCustomBlockId) {
        super(player);

        this.blockPosition = blockPosition;

        this.blockStateId = blockStateId;
        this.customBlock = customBlock;

        this.resultBlockStateId = resultBlockStateId;
        this.resultCustomBlockId = resultCustomBlockId;
    }

    /**
     * Gets the block position.
     *
     * @return the block position
     */
    public BlockPosition getBlockPosition() {
        return blockPosition;
    }

    /**
     * Gets the broken block state id.
     *
     * @return the block id
     */
    public short getBlockStateId() {
        return blockStateId;
    }

    /**
     * Gets the broken custom block.
     *
     * @return the custom block,
     * null if not any
     */
    public CustomBlock getCustomBlock() {
        return customBlock;
    }

    /**
     * Gets the visual block id result, which will be placed after the event.
     *
     * @return the block id that will be set at {@link #getBlockPosition()}
     * set to 0 to remove
     */
    public short getResultBlockStateId() {
        return resultBlockStateId;
    }

    /**
     * Changes the visual block id result.
     *
     * @param resultBlockStateId the result block id
     */
    public void setResultBlockId(short resultBlockStateId) {
        this.resultBlockStateId = resultBlockStateId;
    }

    /**
     * Gets the custom block id result, which will be placed after the event.
     * <p>
     * Warning: the visual block will not be changed, be sure to call {@link #setResultBlockId(short)}
     * if you want the visual to be the same as {@link CustomBlock#getDefaultBlockStateId()}.
     *
     * @return the custom block id that will be set at {@link #getBlockPosition()}
     * set to 0 to remove
     */
    public short getResultCustomBlockId() {
        return resultCustomBlockId;
    }

    /**
     * Changes the custom block id result, which will be placed after the event;
     *
     * @param resultCustomBlockId the custom block id result
     */
    public void setResultCustomBlockId(short resultCustomBlockId) {
        this.resultCustomBlockId = resultCustomBlockId;
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
