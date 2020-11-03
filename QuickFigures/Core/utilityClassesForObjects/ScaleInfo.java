package utilityClassesForObjects;


import java.awt.geom.Dimension2D;
import java.io.Serializable;

import fieldReaderWritter.RetrievableOption;

public class ScaleInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@RetrievableOption(key="units",  label="units")
	private String units="pixels";
	
	@RetrievableOption(key="pixHeight",  label="The Pixel Height")
	private double pixelHeight=1;
	
	@RetrievableOption(key="pixWidth",  label="The Pixel Width")
	private double pixelWidth=1;
	
	@RetrievableOption(key="pixDepth",  label="The Pixel Depth")
	private double pixelDepth=1;
	
	public ScaleInfo() {}
	
	/**changes units from 'micron' to 'Âµm'*/
	protected void correctMicron() {
		if (units.equals("micron")) units="µm";
	}
	
	public ScaleInfo(String unitname, double pixeldim) {
		setUnits(unitname);
		setPixelWidth(pixeldim);
		setPixelHeight(pixeldim);
	}
	
	public ScaleInfo(String unitname, double pixeldimw, double pixeldimh) {
		setUnits(unitname);
		setPixelWidth(pixeldimw);
		setPixelHeight(pixeldimh);
	}
	
public String toString() {
		return "units = "+this.getUnits() +"  pixelWdith = "+this.getPixelWidth()+"  pixelHeight = "+this.getPixelHeight();
	}
	
	public ScaleInfo copy() {
		ScaleInfo output = new ScaleInfo(getUnits(), getPixelWidth(), getPixelHeight()) ;
		output.setPixelDepth(pixelDepth);
		return output;
	}
	
	
	
	/**scales the pixel dimensions of the image*/
	public void scaleXY(double mag) {
		setPixelHeight(getPixelHeight() / mag);
		setPixelWidth(getPixelWidth() / mag);
	}
	
	/**given the dimensions of the image in pixels this scales them*/
	public double[] convertPixelsToUnits(Dimension2D d) {
		double width=d.getWidth()*this.getPixelWidth();
		double height=d.getHeight()*this.getPixelHeight();
		return new double[] {width, height};
	}
	
	public ScaleInfo getScaledCopyXY(double mag) {
		ScaleInfo outut =copy();
		outut.scaleXY(mag);
		return outut;
	}

	public double getPixelHeight() {
		return pixelHeight;
	}

	public void setPixelHeight(double pixelHeight) {
		this.pixelHeight = pixelHeight;
	}

	public double getPixelWidth() {
		return pixelWidth;
	}

	public void setPixelWidth(double pixelWidth) {
		this.pixelWidth = pixelWidth;
	}

	public double getPixelDepth() {
		return pixelDepth;
	}

	public void setPixelDepth(double pixelDepth) {
		this.pixelDepth = pixelDepth;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

}
