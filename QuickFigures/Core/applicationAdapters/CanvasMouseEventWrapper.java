package applicationAdapters;

import java.awt.Component;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.undo.AbstractUndoableEdit;

import selectedItemMenus.LayerSelector;

public interface CanvasMouseEventWrapper extends Serializable {

	int getClickedXImage();

	int getClickedYImage();
	Point getCordinatePoint() ;
	int convertClickedXImage(int x);

	int convertClickedYImage(int y);

	int getClickedXScreen();

	int getClickedYScreen();

	DisplayedImageWrapper getAsDisplay();
	LayerSelector getSelectionSystem();

	int getClickedSlice();

	int getClickedFrame();

	int getClickedChannel();

	int mouseButton();
	
	public boolean shfitDown();
	public int clickCount();
	public boolean altKeyDown();

	Component getComponent();

	Object getSource();

	boolean isPopupTrigger();

	boolean isMetaDown();
	
	java.awt.event.MouseEvent getAwtEvent();

	boolean isControlDown();
	
	public void addUndo(AbstractUndoableEdit... e);

}
