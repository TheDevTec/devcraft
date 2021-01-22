package net.minestom.server.utils.clone;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Convenient interface to deep-copy single object or collections.
 * <p>
 * Most of the methods require object to implement the {@link PublicCloneable} interface.
 */
public final class CloneUtils {

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends PublicCloneable> T optionalClone(T object) {
        return object != null ? (T) object.clone() : null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends PublicCloneable> CopyOnWriteArrayList cloneCopyOnWriteArrayList(List<T> list) {
        CopyOnWriteArrayList<T> result = new CopyOnWriteArrayList<>();
        for (T element : list) {
            result.add((T) element.clone());
        }
        return result;
    }

}
