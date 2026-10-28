package dev.hail.create_fantasizing.block.sturdy_girder;

/**
 * By Deepseek V4.1 Flash 1017:
 * Carries the girder item used for the track connection that is currently being
 * created. Create only knows a boolean "has girder" flag, so this lets the
 * {@code BezierConnection} constructor remember which girder item was held in
 * the player's off hand.
 */
public final class CFAGirderTypeTracker {

    private static final ThreadLocal<Boolean> CURRENT = new ThreadLocal<>();

    private CFAGirderTypeTracker() {
    }

    public static void set(boolean sturdy) {
        CURRENT.set(sturdy);
    }

    public static boolean isSturdy() {
        return Boolean.TRUE.equals(CURRENT.get());
    }

    public static void clear() {
        CURRENT.remove();
    }
}
