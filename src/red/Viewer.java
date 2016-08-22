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
	private double x, y, yRatio, width, height;
	
	public Viewer(int width, int height) {
		setSize(width, height, true);
	}
	
	public void setSize(int width, int height, boolean reset) {
		this.currentWidth = width;
		this.currentHeight = height;
		if(reset) {
			this.x = -2.0;
			this.y = (double)height/(double)width * -2.0;
			this.width = 4.0;
			this.height = (double)height/(double)width * 4.0;
			this.yRatio = this.height / 2.0;
		}
		if(getParent() != null) {
			render(true);
		}
	}
	
	public void render(boolean redraw) {
		if(redraw || lastWidth != currentWidth || lastHeight != currentHeight) {
			for(int i = 0; i < threads.size(); i++) {
				threads.get(0).interrupt();
				while(threads.get(0).isRunning()) {
					System.out.println("Borked");
				}
				threads.remove(0);
				i--;
			}
			lastWidth = currentWidth;
			lastHeight = currentHeight;
			image = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_RGB);
			pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
			for(int i = 0; i < maxThreads; i++) {
				Calculate calculator = new Calculate(pixels, i, maxThreads, x, y, width, height, currentWidth, currentHeight);
				calculator.start();
				threads.add(calculator);
			}
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

	public void zoom(int wheelRotation, int x, int y) {
		double percentX = (double)x / (double)lastWidth;
		double percentY = (double)y / (double)lastHeight;
		if(wheelRotation < 0) {
			System.out.println("Zoom in!");
			this.x = (this.x - 2.0) * ((1.0 - percentX) * (1.0 - Config.PERCENT_OF_SCREEN_TO_KEEP_WHEN_ZOOMING_IN) + Config.PERCENT_OF_SCREEN_TO_KEEP_WHEN_ZOOMING_IN) + 2.0;
			this.width = this.width * Config.PERCENT_OF_SCREEN_TO_KEEP_WHEN_ZOOMING_IN;
			this.y = (this.y - yRatio) * ((1.0 - percentY) * (1.0 - Config.PERCENT_OF_SCREEN_TO_KEEP_WHEN_ZOOMING_IN) + Config.PERCENT_OF_SCREEN_TO_KEEP_WHEN_ZOOMING_IN) + yRatio;
			this.height = this.height * Config.PERCENT_OF_SCREEN_TO_KEEP_WHEN_ZOOMING_IN;
			zoomLevel++;
		}
		if(wheelRotation > 0 && zoomLevel > 0) {
			zoomLevel--;
			if(zoomLevel == 0) {
				
			}
		}
		render(true);
	}
}