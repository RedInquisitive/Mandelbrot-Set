package red;

public class Config {
	private Config() throws InstantiationException {
		throw new InstantiationException("Cannot create an instance of the configuration class!");
	}
	
	public static final int DEFAULT_SIZE_WIDTH = 1280,
							DEFAULT_SIZE_HEIGHT = 720;
	
	public static final int MAX_ITERATIONS = 20;
	
	public static final double ZOOM_SCALE = .10;
}
