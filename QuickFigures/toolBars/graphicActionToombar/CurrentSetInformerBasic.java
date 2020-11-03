package graphicActionToombar;

import java.util.ArrayList;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import applicationAdapters.DisplayedImageWrapper;
import graphicalObjects.GraphicSetDisplayContainer;
import imageMenu.CanvasAutoResize;
import undo.CanvasResizeUndo;

public class CurrentSetInformerBasic implements CurrentSetInformer {
	public static String workingDirectory="";
	private static DisplayedImageWrapper  currentActiveDisplayGroup=null;
	private static ArrayList<DisplayedImageWrapper  > allVisible=new ArrayList<DisplayedImageWrapper  >();
	
	private static GraphicSetDisplayContainer activeGraphicDisplay;
	
	
	
	@Override
	public GraphicSetDisplayContainer getCurrentlyActiveOne() {
		// TODO Auto-generated method stub
		if (currentActiveDisplayGroup!=null) return currentActiveDisplayGroup.getImageAsWrapper();
		
		return activeGraphicDisplay;
	}
	
	public void updateDisplayCurrent() {
		if (getCurrentlyActiveOne() !=null) {
			
			getCurrentlyActiveOne().updateDisplay();
			
		}
	}
	public static void updateActiveDisplayGroup() {
		if (currentActiveDisplayGroup!=null)
			currentActiveDisplayGroup.updateDisplay();
	}

	public static DisplayedImageWrapper  getCurrentActiveDisplayGroup() {
		return currentActiveDisplayGroup;
	}
	
	


	public static void setCurrentActiveDisplayGroup(
			DisplayedImageWrapper  currentActiveDisplayGroup) {
		CurrentSetInformerBasic.currentActiveDisplayGroup = currentActiveDisplayGroup;
		activeGraphicDisplay=currentActiveDisplayGroup.getImageAsWrapper();
		
	}



	public void setActiveGraphicDisplay(GraphicSetDisplayContainer activeGraphicDisplay) {
		CurrentSetInformerBasic.activeGraphicDisplay = activeGraphicDisplay;
	}

	@Override
	public DisplayedImageWrapper getCurrentlyActiveDisplay() {
		// TODO Auto-generated method stub
		return currentActiveDisplayGroup;
	}

	@Override
	public ArrayList<DisplayedImageWrapper> getVisibleDisplays() {
		
		return allVisible;
	}
	
	public static void onApperance(DisplayedImageWrapper diw) {
		allVisible.add(diw);
		
	}
	
	public static void onDisapperance(DisplayedImageWrapper diw) {
		allVisible.remove(diw);
		
	}
	
	
	public static void canvasResize() {
		new CanvasAutoResize().performActionDisplayedImageWrapper(CurrentSetInformerBasic.getCurrentActiveDisplayGroup());
	}
	
	public static CanvasResizeUndo canvasResizeUndoable() {
		return new CanvasAutoResize().performUndoableAction(CurrentSetInformerBasic.getCurrentActiveDisplayGroup());
	}

	@Override
	public void addUndo(UndoableEdit e) {
		try {
			getCurrentActiveDisplayGroup().getUndoManager().addEdit(e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public UndoManager getUndoManager() {
		if (getCurrentActiveDisplayGroup()==null) return null;
		return getCurrentActiveDisplayGroup().getUndoManager();
	}

}
