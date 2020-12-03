package applicationAdaptersForImageJ1;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.undo.AbstractUndoableEdit;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import selectedItemMenus.LayerSelector;

/**implementation of CanvasMouseEventWrapper  interface
  that converts clicks on the imageJ cancas to events.
    not accessible to users but might be of use to programmers */
public class IJ1MEWrapper implements CanvasMouseEvent {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImagePlus imp;
	MouseEvent e;
	private int slice;
	private int frame;
	private int chan;
	
	public IJ1MEWrapper(ImagePlus imp, MouseEvent me) {
		this.imp=imp;
		this.e=me;
		this.chan=imp.getChannel();
		this.frame=imp.getFrame();
		this.slice=imp.getSlice();
	}
	
	@Override
	public int getCoordinateX() {
		ImageCanvas ic=imp.getCanvas();
		return ic.offScreenX((int)(e.getX()));
	}

	@Override
	public int getCoordinateY() {
		ImageCanvas ic=imp.getCanvas();
		return ic.offScreenY((int)(e.getY()));
	}

	@Override
	public int getClickedXScreen() {
		return  e.getX();
	}

	@Override
	public int getClickedYScreen() {
		return e.getY();

}
	
	public int convertClickedXImage( int x) {
		ImageCanvas ic=imp.getCanvas();
		return ic.offScreenX((int)(x));
	}

	@Override
	public int convertClickedYImage( int y) {
		ImageCanvas ic=imp.getCanvas();
		return ic.offScreenY((int)(y));
	}
	
	@Override
	public DisplayedImage getAsDisplay() {
		// TODO Auto-generated method stub
		return new ImagePlusDisplayWrap(imp);
	}
	
	@Override
	public int getClickedChannel() {
		return chan;
	}

	@Override
	public int getClickedFrame() {
		return frame;
	}

	@Override
	public int getClickedSlice() {
		return slice;
	}
	@Override
	public int mouseButton() {
		// TODO Auto-generated method stub
		return e.getButton();
	}
	
	public boolean shfitDown() {
		return IJ.shiftKeyDown();
	}
	@Override
	public int clickCount( ) {
		return e.getClickCount();
	}

	@Override
	public boolean altKeyDown() {
		return IJ.altKeyDown();
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return e.getComponent();
	}

	@Override
	public Object getSource() {
		// TODO Auto-generated method stub
		return e.getSource();
	}

	@Override
	public boolean isPopupTrigger() {
		// TODO Auto-generated method stub
		return e.isPopupTrigger();
	}

	@Override
	public boolean isMetaDown() {
		// TODO Auto-generated method stub
		return e.isMetaDown();
	}

	@Override
	public MouseEvent getAwtEvent() {
		// TODO Auto-generated method stub
		return e;
	}

	@Override
	public boolean isControlDown() {
		// TODO Auto-generated method stub
		return e.isControlDown();
	}
	public Point getCoordinatePoint() {
		return new Point(getCoordinateX(), getCoordinateY());
	}

	@Override
	public LayerSelector getSelectionSystem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addUndo(AbstractUndoableEdit... e) {
		// TODO Auto-generated method stub
		
	}
	
}
