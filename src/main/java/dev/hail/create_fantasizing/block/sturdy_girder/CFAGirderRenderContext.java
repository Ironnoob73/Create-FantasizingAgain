package dev.hail.create_fantasizing.block.sturdy_girder;

/**
 * By Deepseek V4.1 Flash 1017:
 * Thread-local flag used while Flywheel builds a track curve's girder visual.
 * The girder visual is created inside Create's private track visual code, so
 * this lets the girder model selection know which girder type is being drawn.
 */
public final class CFAGirderRenderContext {

    private static final ThreadLocal<Boolean> STURDY = new ThreadLocal<>();

    private CFAGirderRenderContext() {
    }

    public static void set(boolean sturdy) {
        STURDY.set(sturdy);
    }

    public static boolean isSturdy() {
        return Boolean.TRUE.equals(STURDY.get());
    }

    public static void clear() {
        STURDY.remove();
    }
}
