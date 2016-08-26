package red;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Viewer extends Canvas {
	
	private BufferedImage image;
	private ArrayList<Calculate> threads = new ArrayList<>();
	private int[] pixels;
	private int maxThreads = Runtime.getRuntime().availableProcessors();
	
	private int lastWidth, lastHeight, currentWidth, currentHeight = 0;
	
	private double zoomLevel = 0;
	private double x1, y1, x2, y2;
	
	/**
	 * Creates a Canvas to draw the fractal on.
	 * @param width Width of this component
	 * @param height Height of this component
	 */
	public Viewer(int width, int height) {
		setSize(width, height);
		lastWidth = width;
		lastHeight = height;
	}
	
	/**
	 * Renders the fractal.
	 */
	public void render() {
		for(int i = threads.size(); i > 0 ; i--) {
			threads.get(0).halt();
			threads.remove(0);
		}
		image = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		for(int i = 0; i < maxThreads; i++) {
			Calculate calculator = new Calculate(pixels, i, maxThreads, x1, y1, x2, y2, currentWidth, currentHeight);
			calculator.start();
			threads.add(calculator);
		}
	}
	
	/**
	 * Updates the screen
	 */
	public void draw() {
		if(lastWidth != currentWidth || lastHeight != currentHeight) {
			lastWidth = currentWidth;
			lastHeight = currentHeight;
			render();
		}
		
		final BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2); // Double buffering!
			return;
		}
		final Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Sets the size of this component.
	 */
	public void setSize(int width, int height) {
		this.currentWidth = width;
		this.currentHeight = height;
		reset();
	}

	/**
	 * Resets the zoom of the fractal.
	 */
	public void reset() {
		this.x1 = -2.0;
		this.y1 = (double)currentHeight/(double)currentWidth * -2.0;
		this.x2 = 2.0;
		this.y2 = (double)currentHeight/(double)currentWidth * 2.0;
	}

	/**
	 * Zooms to a region of the fractal
	 * @param wheelRotation
	 * @param cursorX
	 * @param cursorY
	 */
	public void zoom(int wheelRotation, int cursorX, int cursorY) {
		double percentX = (double)cursorX / (double)currentWidth;
		double percentY = (double)cursorY / (double)currentHeight;
		
		if(wheelRotation < 0) {
			System.out.println("Zoom in!");
			
			x1 += (x2 - x1) * Config.ZOOM_SCALE / 2 * percentX;
			x2 -= (x2 - x1) * Config.ZOOM_SCALE / 2 * (1.0-percentX);
			y1 += (y2 - y1) * Config.ZOOM_SCALE / 2 * (percentY);
			y2 -= (y2 - y1) * Config.ZOOM_SCALE / 2 * (1.0-percentY);

			zoomLevel++;
		}
		if(wheelRotation > 0) {
			System.out.println("Zoom out!");
			
			x1 -= (x2 - x1) * Config.ZOOM_SCALE / 2 * percentX;
			x2 += (x2 - x1) * Config.ZOOM_SCALE / 2 * (1.0-percentX);
			y1 -= (y2 - y1) * Config.ZOOM_SCALE / 2 * (percentY);
			y2 += (y2 - y1) * Config.ZOOM_SCALE / 2 * (1.0-percentY);
			
			zoomLevel--;
		}
		
		Config.MAX_ITERATIONS += 25;

		render();
	}
}