package figureFormat;

/**A shutdown hook that saves the preferences of the default directory handler to a file
  if that file is loaded, the working directory used previously would be used again*/
public class PrefsShutDownHook implements Runnable{
	
static boolean addedHook=false;
	
	public static void addShutdownHook() {
		if (addedHook) return;
		Runtime.getRuntime().addShutdownHook(new Thread(new PrefsShutDownHook()));
		addedHook=true;
	}

	@Override
	public void run() {
		DirectoryHandler.getDefaultHandler().savePrefs();
	}
}
