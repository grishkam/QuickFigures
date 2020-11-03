package imageMenu;

import java.awt.Rectangle;
import java.util.ArrayList;

import applicationAdapters.DisplayedImageWrapper;
import applicationAdapters.ImageWrapper;
import basicMenusForApp.BasicMenuItemForObj;
import genericMontageKit.BasicOverlayHandler;
import undo.CanvasResizeUndo;
import utilityClassesForObjects.LocatedObject2D;

public class CanvasAutoTrim extends BasicMenuItemForObj{

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
		CanvasResizeUndo undo = new CanvasResizeUndo(diw);//creates an undo
		
		trimCanvas(diw);
		undo.establishFinalState();
		if(!undo.sizeSame())
			diw.getUndoManager().addEdit(undo);
	}

	public void trimCanvas(DisplayedImageWrapper diw) {
		ImageWrapper iw = diw.getImageAsWrapper();
		BasicOverlayHandler boh = new BasicOverlayHandler();
		
		ArrayList<LocatedObject2D> list = iw.getLocatedObjects();
		Rectangle bound=null;
		for(LocatedObject2D l:list) {
			if (l==null) continue;
			if (bound==null) bound=l.getBounds(); 
				else  {
					Rectangle b2 = l.getBounds();
					bound=bound.createUnion(b2).getBounds();
				}
		}
		
		boh.CanvasResizeObjectsIncluded(iw, bound.width, bound.height, -bound.x, -bound.y);
		
		
		diw.updateDisplay();
		diw.updateWindowSize();
	}

	@Override
	public String getCommand() {
		return "Canvas Trim";
	}

	@Override
	public String getNameText() {
		
		return "Canvas Size To Fit All Objects";
	}

	@Override
	public String getMenuPath() {
		return "Image<Canvas";
	}
	




}
