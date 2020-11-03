package basicAppAdapters;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.undo.AbstractUndoableEdit;

import applicationAdapters.CanvasMouseEventWrapper;
import applicationAdapters.DisplayedImageWrapper;
import applicationAdapters.ImageWrapper;
import basicMenusForApp.SelectedSetLayerSelector;
import imageDisplayApp.KeyDownTracker;
import selectedItemMenus.LayerSelector;

public class GMouseEvent implements CanvasMouseEventWrapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient DisplayedImageWrapper imp;
	transient MouseEvent e;
	
	public GMouseEvent(DisplayedImageWrapper imp, MouseEvent e) {
		this.imp=imp;
		this.e=e;
	}
	
	public DisplayedImageWrapper getImageDispay() {return imp;}
	public ImageWrapper getImage() {return imp.getImageAsWrapper();}
	
	/**returns the cordinate of the clickpoin on the canvas*/
	@Override
	public int getClickedXImage() {
		//imp.getTheWindow()
		return this.convertClickedXImage(e.getX());
		//return 0;
	}

	@Override
	public int getClickedYImage() {
		// TODO Auto-generated method stub
		return this.convertClickedYImage( e.getY());
	}
	
	public Point getCordinatePoint() {
		return new Point(getClickedXImage(), getClickedYImage());
	}
	
	/**When given the clickpoint relative to a the screen or compoent geometry,
	  this returns the cordinate in the image*/
	@Override
	public int convertClickedXImage( int x) {
		return (int) imp.getConverter().unTransformX(x);
		// TODO Auto-generated method stub
	
	}

	@Override
	public int convertClickedYImage( int y) {
		// TODO Auto-generated method stub
		return (int) imp.getConverter().unTransformY(y);
	}
	
	@Override
	public int getClickedXScreen() {
		// TODO Auto-generated method stub
		return e.getX();
	}

	@Override
	public int getClickedYScreen() {
		// TODO Auto-generated method stub
		return e.getY();
	}

	@Override
	public DisplayedImageWrapper getAsDisplay() {
		// TODO Auto-generated method stub
		return imp;
	}
	
	@Override
	public int getClickedChannel() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getClickedFrame() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getClickedSlice() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int mouseButton() {
		return e.getButton();
	}
	
	
	public int clickCount() {
		// TODO Auto-generated method stub
		return e.getClickCount();
	}

	@Override
	public boolean altKeyDown() {
		if (e.isAltDown()) return true;
		return KeyDownTracker.isKeyDown(KeyEvent.VK_ALT);
	}
	
	public boolean shfitDown() {
		return e.isShiftDown();
		//if (e.isShiftDown()) return true;
		//return KeyDownTracker.isKeyDown(KeyEvent.VK_SHIFT);
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
		if (e.isPopupTrigger()) return true;
		if(e.getButton()==3) return true;
		return false;
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
	
	public boolean isControlDown() {
		// TODO Auto-generated method stub
		return e.isControlDown();
	}

	@Override
	public LayerSelector getSelectionSystem() {
		return new SelectedSetLayerSelector(imp.getImageAsWrapper());
		
	}

	@Override
	public void addUndo(AbstractUndoableEdit... e) {
		
		if(e!=null &&imp!=null &&imp.getUndoManager()!=null) {
			imp.getUndoManager().addEdits(e);
		}
	}
}
