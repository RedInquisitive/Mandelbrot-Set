package red;

public class Config {
	private Config() throws InstantiationException {
		throw new InstantiationException("Cannot create an instance of the configuration class!");
	}
	
	public static final int DEFAULT_SIZE_WIDTH = 320,
							DEFAULT_SIZE_HEIGHT = 240;
	
	public static final int MAX_ITERATIONS = 50;
}
