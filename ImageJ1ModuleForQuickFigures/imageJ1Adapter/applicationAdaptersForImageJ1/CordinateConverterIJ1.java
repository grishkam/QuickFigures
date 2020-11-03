package applicationAdaptersForImageJ1;



import graphicalObjects.BasicCordinateConverter;
import ij.ImagePlus;

/**cordinate converted used if graphics need to be superimposed on an IJ1 image*/
public class CordinateConverterIJ1 extends BasicCordinateConverter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImagePlus imp=null;
	
	
	
	public CordinateConverterIJ1(ImagePlus imp) {
		this.imp=imp;
	}

	@Override
	public double transformX(double ox) {
		return imp.getCanvas().screenXD(ox);
	}

	@Override
	public double transformY(double oy) {
		return imp.getCanvas().screenYD(oy);
	}

	@Override
	public double getMagnification() {
		return imp.getCanvas().getMagnification();
	}

	
	
	
}
