package figureFormat;

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
