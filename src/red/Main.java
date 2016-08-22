package red;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

public class Main {
	

	
	public static void main(String[] args) {
        JFrame frame = new JFrame("Mandelbrot Set");
        
        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(Config.DEFAULT_SIZE_WIDTH, Config.DEFAULT_SIZE_HEIGHT));
        frame.setMinimumSize(new Dimension(Config.DEFAULT_SIZE_WIDTH, Config.DEFAULT_SIZE_HEIGHT));
        
        Viewer panel = new Viewer(Config.DEFAULT_SIZE_WIDTH, Config.DEFAULT_SIZE_HEIGHT);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                panel.setSize(frame.getWidth(), frame.getHeight());
            }
        });
        
        while (true) {
        	panel.render();
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
        }
	}
}
