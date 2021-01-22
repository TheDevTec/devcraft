package net.minestom.server.map.framebuffers;

import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_CREATION_API;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_NATIVE_CONTEXT_API;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_API;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetError;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import net.minestom.server.map.Framebuffer;
import net.minestom.server.map.MapColors;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.utils.time.TimeUnit;

public abstract class GLFWCapableBuffer {

    protected final byte[] colors;
    private final ByteBuffer pixels;
    private final long glfwWindow;
    private final int width;
    private final int height;
    private final ByteBuffer colorsBuffer;
    private boolean onlyMapColors;

    protected GLFWCapableBuffer(int width, int height) {
        this(width, height, GLFW_NATIVE_CONTEXT_API, GLFW_OPENGL_API);
    }

    /**
     * Creates the framebuffer and initializes a new context
     */
    protected GLFWCapableBuffer(int width, int height, int apiContext, int clientAPI) {
        this.width = width;
        this.height = height;
        this.colors = new byte[width*height];
        colorsBuffer = BufferUtils.createByteBuffer(width*height);
        this.pixels = BufferUtils.createByteBuffer(width*height*4);
        if(!glfwInit()) {
            throw new RuntimeException("Failed to init GLFW");
        }

        GLFWErrorCallback.createPrint().set();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        glfwWindowHint(GLFW_CONTEXT_CREATION_API, apiContext);
        glfwWindowHint(GLFW_CLIENT_API, clientAPI);

        this.glfwWindow = glfwCreateWindow(width, height, "", 0L, 0L);
        if(glfwWindow == 0L) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                PointerBuffer desc = stack.mallocPointer(1);
                int errcode = glfwGetError(desc);
                throw new RuntimeException("("+errcode+") Failed to create GLFW Window.");
            }
        }
    }

    public GLFWCapableBuffer unbindContextFromThread() {
        glfwMakeContextCurrent(0L);
        return this;
    }

    public void changeRenderingThreadToCurrent() {
        glfwMakeContextCurrent(glfwWindow);
        GL.createCapabilities();
    }

    public int setupRenderLoop(long period, TimeUnit unit, Runnable rendering) {
        return Scheduler.repeating(period, period, new Runnable() {
            private boolean first = true;
        	public void run() {
        		if(first) {
                changeRenderingThreadToCurrent();
                first = false;
        		}
        		render(rendering);
        	}
        });
    }

    public void render(Runnable rendering) {
        rendering.run();
        glfwSwapBuffers(glfwWindow);
        prepareMapColors();
    }

    /**
     * Called in render after glFlush to read the pixel buffer contents and convert it to map colors.
     * Only call if you do not use {@link #render(Runnable)} nor {@link #setupRenderLoop(long, TimeUnit, Runnable)}
     */
    public void prepareMapColors() {
        if(onlyMapColors) {
            colorsBuffer.rewind();
            glReadPixels(0, 0, width, height, GL_RED, GL_UNSIGNED_BYTE, colorsBuffer);
            colorsBuffer.get(colors);
        } else {
            glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int i = Framebuffer.index(x, y, width)*4;
                    int red = pixels.get(i) & 0xFF;
                    int green = pixels.get(i+1) & 0xFF;
                    int blue = pixels.get(i+2) & 0xFF;
                    int alpha = pixels.get(i+3) & 0xFF;
                    int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    colors[Framebuffer.index(x, y, width)] = MapColors.closestColor(argb).getIndex();
                }
            }
        }
    }

    public void cleanup() {
        glfwTerminate();
    }

    public long getGLFWWindow() {
        return glfwWindow;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    /**
     * Tells this buffer that the **RED** channel contains the index of the map color to use.
     *
     * This allows for optimizations and fast rendering (because there is no need for a conversion)
     */
    public void useMapColors() {
        onlyMapColors = true;
    }

    /**
     * Opposite to {@link #useMapColors()}
     */
    public void useRGB() {
        onlyMapColors = false;
    }
}
