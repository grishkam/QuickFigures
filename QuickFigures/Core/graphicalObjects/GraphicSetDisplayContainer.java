package graphicalObjects;

import java.awt.Dimension;

import applicationAdapters.ImageWrapper;
import genericMontageKit.SelectionManager;
import graphicalObjects_LayerTypes.GraphicLayer;
import undo.UndoManagerPlus;

public interface GraphicSetDisplayContainer {

	public void updateDisplay();
	
	public GraphicLayer getGraphicLayerSet();
	
	public void onItemLoad(ZoomableGraphic z);
	public SelectionManager getSelectionManagger();
	
	
	public Dimension getCanvasDims();
	
	public UndoManagerPlus getUndoManager();
	public ImageWrapper getAsWrapper();
	public String getTitle();
}
