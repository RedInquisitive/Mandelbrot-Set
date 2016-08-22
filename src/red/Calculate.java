package red;

import java.security.InvalidParameterException;

public class Calculate extends Thread {
	private final int thread, totalThreads;
	private int[] pixels;
	private final double x, y, width, height;
	private int screenWidth, screenHeight;
	boolean finished = false;
	
	/**
	 * Dispatches a thread to calculate Mandelbrot.
	 * @param pixels The system pixels array.
	 * @param thread The thread number. (Example: thread "0" is the first thread in a set of 4 threads.)
	 * @param totalThreads Total amount of threads running. For 4 threads, the allowable threads
	 * @param x The X position we are rendering the Mandelbrot from
	 * @param y The Y position we are rendering the Mandelbrot from
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	public Calculate(int[] pixels, int thread, int totalThreads, double x, double y, double width, double height, int screenWidth, int screenHeight) {
		this.setPriority(Thread.NORM_PRIORITY + 1);
		this.thread = thread;
		if(thread < 0 || thread >= totalThreads) {
			throw new InvalidParameterException("The running thread must be between 0 and " + totalThreads + ".");
		}
		this.totalThreads = totalThreads;
		this.pixels = pixels;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		System.out.println("Dispatching new thread. Thread number " + thread + " active.");
	}
	
	public void run() {
		exit:
		for(int row = thread; row < screenHeight; row += totalThreads) {
			for(int col = 0; col < screenWidth; col++) {
				double c_real = (col - screenWidth/2.0) * width/screenWidth;
				double c_imaginary = (row - screenHeight/2.0) * width/screenWidth;
				double x = 0, y = 0;
				int iteration = 0;
				while(x * x + y * y <= 4.0 && iteration < Config.MAX_ITERATIONS) {
					double x_new = x * x - y * y + c_real;
					y = 2.0 * x * y + c_imaginary;
					x = x_new;
					iteration++;
					if(Thread.currentThread().isInterrupted()) {
						System.out.println("Stopping old thread. Thread number " + thread + " stopped!");
						break exit;
					}
				}
				if(iteration < Config.MAX_ITERATIONS) {
					int color = (int)((double)0xFF * (((double)iteration ) / (double)Config.MAX_ITERATIONS));
					if(color > 0xFF) color = 0xFF;
					pixels[col + row * screenWidth] = (color << 24) + (color << 16) + (color << 8) + color;
				} else {
					pixels[col + row * screenWidth] = 0;
				}
			}
		}
		System.out.println("Thread number " + thread + " is done.");
		finished = true;
	}
	
	public boolean isRunning() {
		return !finished;
	}
}
