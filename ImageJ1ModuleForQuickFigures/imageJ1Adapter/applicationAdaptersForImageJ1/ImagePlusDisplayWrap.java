package applicationAdaptersForImageJ1;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import undo.UndoManagerPlus;
import utilityClassesForObjects.Selectable;

import java.awt.Cursor;
import java.awt.Window;

import applicationAdapters.DisplayedImageWrapper;
import applicationAdapters.ImageWrapper;
import channelMerging.MultiChannelDisplayWrapper;
import channelMerging.MultiChannelWrapper;
import graphicalObjects.CordinateConverter;

/**See interface*/
public class ImagePlusDisplayWrap implements DisplayedImageWrapper,MultiChannelDisplayWrapper {

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

	@Override
	public UndoManagerPlus getUndoManager() {
		// TODO Auto-generated method stub
		return new UndoManagerPlus();
	}

	@Override
	public void zoom(String st) {
		
		
	}

	@Override
	public void setCurrentFrame(int currentFrame) {
		imp.setT(currentFrame);
		
	}

	@Override
	public void setEndFrame(int frame) {
		
		
	}

	@Override
	public int getEndFrame() {
		// TODO Auto-generated method stub
		return imp.getNFrames();
	}

	@Override
	public void scrollPane(double dx, double dy) {
		// TODO Auto-generated method stub
		
	}

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

	@Override
	public void setSelectedItem(Selectable s) {
		// TODO Auto-generated method stub
		
	}

}
