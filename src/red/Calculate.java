package red;

import java.math.BigDecimal;
import java.math.MathContext;
import java.security.InvalidParameterException;

public class Calculate extends Thread {
	private final int thread, totalThreads;
	private int[] pixels;
	private final BigDecimal x, y, width, height;
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
	public Calculate(int[] pixels, int thread, int totalThreads, BigDecimal x, BigDecimal y, BigDecimal width, BigDecimal height, int screenWidth, int screenHeight) {
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
				BigDecimal c_real = new BigDecimal(col - screenWidth/2.0).multiply(width.divide(new BigDecimal(screenWidth),MathContext.DECIMAL32));
				BigDecimal c_imaginary =new BigDecimal(row - screenHeight/2.0).multiply(height.divide(new BigDecimal(screenWidth),MathContext.DECIMAL32));
				BigDecimal x = BigDecimal.ZERO;
				BigDecimal y = BigDecimal.ZERO;
				int iteration = 0;
				while((x.multiply(x)).add(y.multiply(y)).compareTo(new BigDecimal("4.0")) <= 0 && iteration < Config.MAX_ITERATIONS) {
					BigDecimal x_new = (x.multiply(x)).subtract(y.multiply(y)).add(c_real);
					y = ((new BigDecimal("2.0")).multiply(x).multiply(y)).add(c_imaginary);
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
