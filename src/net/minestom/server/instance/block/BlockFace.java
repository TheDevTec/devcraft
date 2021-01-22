package net.minestom.server.instance.block;

import net.minestom.server.utils.Direction;

public enum BlockFace {
    BOTTOM(Direction.DOWN),
    TOP(Direction.UP),
    NORTH(Direction.NORTH),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST),
    EAST(Direction.EAST);

    private final Direction direction;

    BlockFace(Direction direction) {
        this.direction = direction;
    }

    public Direction toDirection() {
        return direction;
    }

    public BlockFace getOppositeFace() {
        switch(this) {
        case BOTTOM:
            return TOP;
        case TOP:
            return BOTTOM;
        case NORTH:
            return SOUTH;
        case SOUTH:
            return NORTH;
        case WEST:
            return EAST;
        case EAST:
            return WEST;
        }
        return null;
    }
}
