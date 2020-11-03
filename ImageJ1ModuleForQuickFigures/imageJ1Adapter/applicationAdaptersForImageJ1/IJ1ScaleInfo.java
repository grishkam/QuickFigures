package applicationAdaptersForImageJ1;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.measure.Calibration;
import utilityClassesForObjects.ScaleInfo;

public class IJ1ScaleInfo extends ScaleInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IJ1ScaleInfo(ImagePlus imp) {
		if (imp==null) return;
		FileInfo fi=imp.getFileInfo();//.getFileInfo();
		if (fi==null) return;
		setToIJCalibration(imp.getCalibration());
		correctMicron();
	/**	setUnits(fi.unit);
		setPixelWidth(fi.pixelWidth);
		setPixelHeight(fi.pixelHeight);*/
		
	}
	
	public void setToIJCalibration(Calibration cal) {
		if (cal==null) return;
		setUnits(cal.getUnit());
		this.setPixelWidth(cal.pixelWidth);
		this.setPixelHeight(cal.pixelHeight);
		this.setPixelDepth(cal.pixelDepth);
	}
	
	

}
