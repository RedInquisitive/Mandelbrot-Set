package red;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.math.BigDecimal;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Viewer extends Canvas {
	
	private BufferedImage image;
	private int[] pixels;
	private ArrayList<Calculate> threads = new ArrayList<>();
	private int maxThreads = Runtime.getRuntime().availableProcessors();
	
	private int lastWidth, lastHeight, currentWidth, currentHeight = 0;
	
	public Viewer(int width, int height) {
		setSize(width, height);
	}
	
	public void setSize(int width, int height) {
		currentWidth = width;
		currentHeight = height;
	}
	
	public void render() {
		if(image != null) {
			if(threads.size() != maxThreads) {
				for(int i = 0; i < maxThreads; i++) {
					threads.add(new Calculate(pixels, i, maxThreads, new BigDecimal("-2.0"), new BigDecimal("-2.0"), new BigDecimal("4.0"), new BigDecimal("4.0"), currentWidth, currentHeight));
					threads.get(i).start();
				}
			}
		}
		if(lastWidth != currentWidth || lastHeight != currentHeight) {
			lastWidth = currentWidth;
			lastHeight = currentHeight;
			image = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_RGB);
			pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
			System.out.println("Screen size changed!");
			if(threads.size() == maxThreads) {
				for(int i = 0; i < maxThreads; i++) {
					threads.get(0).interrupt();
					threads.remove(0);
				}
				for(int i = 0; i < maxThreads; i++) {
					threads.add(new Calculate(pixels, i, maxThreads, new BigDecimal("-2.0"), new BigDecimal("-2.0"), new BigDecimal("4.0"), new BigDecimal("4.0"), currentWidth, currentHeight));
					threads.get(i).start();
				}
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
}