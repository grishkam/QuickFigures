package basicAppAdapters;

import java.awt.Color;
import appContext.GeneralAppContext;

public class ToolAdapterG implements GeneralAppContext {

	static Color foregroundCol=Color.black;
	static Color backgroundCol=Color.black;
	
	
/**
	@Override
	public int getClickedXScreen(DisplayedImageWrapper imp, MouseEvent e) {
		// TODO Auto-generated method stub
		return e.getX();
	}

	@Override
	public int getClickedYScreen(DisplayedImageWrapper imp, MouseEvent e) {
		// TODO Auto-generated method stub
		return e.getY();
	}
*/


/**

	@Override
	public AbstractMontageLayout<DisplayedImageWrapper> createLayout(DisplayedImageWrapper imp) {
		// TODO Auto-generated method stub
		return null;
	}*/

	
/**
	@Override
	public AbstractMontageLayout<DisplayedImageWrapper> getModelLayout() {
		// TODO Auto-generated method stub
		return null;
	}*/







/**
 * 
 *
	@Override
	public DisplayedImageWrapper currentlyInFocusWindowImage() {
		// TODO Auto-generated method stub
		return CurrentSetInformerBasic .getCurrentActiveDisplayGroup();
	}

	

	@Override
	public void setCursor(DisplayedImageWrapper imp, Cursor c) {
		imp.setCursor(c);
		
	}

	@Override
	public boolean shfitDown() {
		// TODO Auto-generated method stub
		return KeyDownTracker.isKeyDown(KeyEvent.VK_SHIFT);
	}
	*/
	
/**
	@Override
	public int clickCount(MouseEvent e) {
		// TODO Auto-generated method stub
		return e.getClickCount();
	}

	@Override
	public boolean altKeyDown() {
		// TODO Auto-generated method stub
		return KeyDownTracker.isKeyDown(KeyEvent.VK_ALT);
	}*/

/**
	@Override
	public boolean hasMontageMetaData(DisplayedImageWrapper imp) {
		// TODO Auto-generated method stub
		return false;
	}*/

	@Override
	public Color getForeGroundColor() {
		// TODO Auto-generated method stub
		return foregroundCol;
	}

	@Override
	public Color getBackGroundColor() {
		// TODO Auto-generated method stub
		return backgroundCol;
	}


/**
	@Override
	public ImageWrapper createImageWrapper(DisplayedImageWrapper imp) {
		// TODO Auto-generated method stub
		return imp.getImageAsWrapper();
	}

	@Override
	public BasicMontageLayout createLayout(MetaInfoWrapper imp) {
		// TODO Auto-generated method stub
		return null;
	}*/

	/**
	@Override
	public AbstractMontageUpdater<DisplayedImageWrapper> getUpdater(
			ImageWrapper imp) {
		// TODO Auto-generated method stub
		return null;
	}



	


	@Override
	public CanvasMouseEventWrapper createCanvasMouseEventWrapper(DisplayedImageWrapper imp, CanvasMouseEventWrapper me) {
		// TODO Auto-generated method stub
		if (me==null) return new GMouseEvent(imp, null);
		return me;
		
	}

	*/
}
