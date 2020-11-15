package applicationAdaptersForImageJ1;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import undo.UndoManagerPlus;
import utilityClassesForObjects.Selectable;

import java.awt.Cursor;
import java.awt.Window;

import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import channelMerging.MultiChannelDisplayWrapper;
import channelMerging.MultiChannelWrapper;
import graphicalObjects.CordinateConverter;

/**See interface.  Very few of the methods from the interfaces 
 need be implemented for this class to perform its function so most are not implemented.
 	*/
public class ImagePlusDisplayWrap implements DisplayedImage,MultiChannelDisplayWrapper {

	ImagePlus imp;

	public ImagePlusDisplayWrap(ImagePlus imp) {
		this.imp=imp;
	}
	
	@Override
	public void updateDisplay() {
		if (imp!=null) imp.updateAndDraw();

	}

	@Override
	public ImageWrapper getImageAsWrapper() {
		if (imp==null) return null;
		return new ImagePlusWrapper(imp);
	}

	@Override
	public CordinateConverter<?> getConverter() {
		return new CordinateConverterIJ1(imp);
	}

	@Override
	public Window getWindow() {
		// TODO Auto-generated method stub
		if (imp==null) return null;
		return imp.getWindow();
	}

	/**not implemented*/
	@Override
	public void updateWindowSize() {
	

	}

	@Override
	public void setCursor(Cursor c) {
		ImageCanvas.setCursor(c, 0);

		
	}

	@Override
	public MultiChannelWrapper getMultiChannelWrapper() {
		if (imp==null) return null;
		return new ImagePlusWrapper(imp);
	}

	@Override
	public int getCurrentChannel() {
		// TODO Auto-generated method stub
		return imp.getChannel();
	}

	/**not implemented*/
	@Override
	public void zoomOutToFitScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCurrentFrame() {
		// TODO Auto-generated method stub
		return imp.getFrame();
	}

	@Override
	public int getCurrentSlice() {
		// TODO Auto-generated method stub
		return imp.getSlice();
	}

	/**not implemented*/
	@Override
	public UndoManagerPlus getUndoManager() {
		// TODO Auto-generated method stub
		return new UndoManagerPlus();
	}

	/**not implemented*/
	@Override
	public void zoom(String st) {
		
		
	}

	@Override
	public void setCurrentFrame(int currentFrame) {
		imp.setT(currentFrame);
		
	}
	/**not implemented*/
	@Override
	public void setEndFrame(int frame) {
		
		
	}

	@Override
	public int getEndFrame() {
		// TODO Auto-generated method stub
		return imp.getNFrames();
	}
	/**not implemented*/
	@Override
	public void scrollPane(double dx, double dy) {
		// TODO Auto-generated method stub
		
	}
	/**not implemented*/
	@Override
	public void setScrollCenter(double dx, double dy) {
		
	}

	@Override
	public void closeWindowButKeepObjects() {
		imp.getWindow().setVisible(false);
		
	}

	@Override
	public Selectable getSelectedItem() {
		// TODO Auto-generated method stub
		return null;
	}

	/**not implemented*/
	@Override
	public void setSelectedItem(Selectable s) {
		// TODO Auto-generated method stub
		
	}

	
	/**not implemented*/
	@Override
	public double getZoomLevel() {
		// TODO Auto-generated method stub
		return 1;
	}
	/**not implemented*/
	@Override
	public void setZoomLevel(double z) {
		// TODO Auto-generated method stub
		
	}

}
