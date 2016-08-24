package red;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;

public class Main {
	
	//test comment
	
	public static void main(String[] args) {
		
        JFrame frame = new JFrame("Mandelbrot Set");
        Viewer panel = new Viewer(Config.DEFAULT_SIZE_WIDTH, Config.DEFAULT_SIZE_HEIGHT);
        
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.setSize(new Dimension(Config.DEFAULT_SIZE_WIDTH, Config.DEFAULT_SIZE_HEIGHT));
        frame.setMinimumSize(new Dimension(Config.DEFAULT_SIZE_WIDTH, Config.DEFAULT_SIZE_HEIGHT));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                panel.setSize(frame.getWidth(), frame.getHeight());
            }
        });
        
        frame.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getWheelRotation() != 0) {
					panel.zoom(e.getWheelRotation(), e.getX(), e.getY());
				}
			}
        });
        
        panel.setSize(frame.getWidth(), frame.getHeight());
        panel.render();
        while (true) {
        	panel.draw();
        	try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
        }
	}
}
