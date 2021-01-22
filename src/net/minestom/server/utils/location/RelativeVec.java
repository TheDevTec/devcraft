package net.minestom.server.utils.location;

import net.minestom.server.entity.Entity;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.Vector;

/**
 * Represents a relative {@link Vector}.
 *
 * @see RelativeLocation
 */
public class RelativeVec extends RelativeLocation<Vector> {

    public RelativeVec(Vector location, boolean relativeX, boolean relativeY, boolean relativeZ) {
        super(location, relativeX, relativeY, relativeZ);
    }

    @Override
    public Vector fromRelativePosition(Entity entity) {
        if (!relativeX && !relativeY && !relativeZ) {
            return location.clone();
        }
        final Position entityPosition = entity.getPosition();

        final float x = location.getX() + (relativeX ? entityPosition.getX() : 0);
        final float y = location.getY() + (relativeY ? entityPosition.getY() : 0);
        final float z = location.getZ() + (relativeZ ? entityPosition.getZ() : 0);

        return new Vector(x, y, z);
    }
}
