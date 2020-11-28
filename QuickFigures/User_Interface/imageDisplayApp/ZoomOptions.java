package imageDisplayApp;

import gridLayout.RetrievableOption;

public class ZoomOptions {
	
	public static ZoomOptions current=new ZoomOptions();
	
	@RetrievableOption(key = "resiepostzoom", label="Resize Window After Zooming")
	boolean resizeWindowsAfterZoom=true;

}
