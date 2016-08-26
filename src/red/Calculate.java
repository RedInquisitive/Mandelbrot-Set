package red;

import java.security.InvalidParameterException;

public class Calculate extends Thread {
	private final int thread, totalThreads;
	private int[] pixels;
	private final double x1, y1, x2, y2;
	private int screenWidth, screenHeight, currentIterations;
	private boolean finished = false;
	private boolean shouldQuit = false;
	
	
	/**
	 * Creates a new thread to calculate the fractal.
	 * @param pixels The array of pixels to render the fractal on.
	 * @param thread The thread number (for example, on a 4 core processor, possible thread numbers are 0, 1, 2, and 3)
	 * @param totalThreads The total amount of threads dispatched.
	 * @param x1 x position of the first coordinate of the render region of the fractal
	 * @param y1 y position of the first coordinate of the render region of the fractal
	 * @param x2 x position of the second coordinate of the render region of the fractal
	 * @param y2 y position of the second coordinate of the render region of the fractal
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen.
	 */
	public Calculate(int[] pixels, int thread, int totalThreads, double x1, double y1, double x2, double y2, int screenWidth, int screenHeight, int currentIterations) {
		this.setPriority(Thread.NORM_PRIORITY + 1);
		this.thread = thread;
		if(thread < 0 || thread >= totalThreads) {
			throw new InvalidParameterException("The running thread must be between 0 and " + (totalThreads - 1) + ".");
		}
		this.totalThreads = totalThreads;
		this.pixels = pixels;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.currentIterations = currentIterations;
		System.out.println("Dispatching new thread. Thread number " + thread + " active.");
	}
	
	public void run() {
		exit:
		for(int row = thread; row < screenHeight; row += totalThreads) {
			for(int col = 0; col < screenWidth; col++) {
				double c_real = ((double)col / (double)screenWidth) * (x2 - x1) + x1;
				double c_imaginary = ((double)row / (double)screenHeight) * (y2 - y1) + y1;
				double x = 0, y = 0;
				int iteration = 0;
				while(x * x + y * y <= 4.0 && iteration < currentIterations) {
					double x_new = x * x - y * y + c_real;
					y = 2.0 * x * y + c_imaginary;
					x = x_new;
					iteration++;
					if(shouldQuit) {
						break exit;
					}
				}
				if(iteration < currentIterations) {
					int color = (int)((double)0xFF * (((double)iteration ) / (double)currentIterations));
					if(color > 0xFF) color = 0xFF;
					pixels[col + row * screenWidth] = (color << 24) + (color << 16) + (color << 8) + color;
				} else {
					pixels[col + row * screenWidth] = 0;
				}
			}
		}
		if(shouldQuit) {
			System.out.println("Thread number " + thread + " quit prematurely!");
		} else {
			System.out.println("Thread number " + thread + " is done.");
		}
		finished = true;
	}
	
	/**
	 * Blocking method used to shutdown this thread.
	 * @return true when the thread finally quits.
	 */
	public boolean halt() {
		this.shouldQuit = true;
		while(!finished) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.exit(0);
			}
		}
		return true;
	}
}
