package appContext;

public class ImageDPIHandler {

	public static int getStandardDPI() {
		return 72;
	}
	
	public static double ratioFor300DPI() {
		double i=getStandardDPI();
		return i/300.0;
	}
	
	public ImageDPIHandler() {
		 
	}

}
