package net.minestom.server.utils.async;

import java.util.concurrent.CompletableFuture;

public final class AsyncUtils {

    public static void runAsync(Runnable runnable) {
        CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
