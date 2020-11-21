package graphicalObjects;

import java.awt.Dimension;

import applicationAdapters.ImageWrapper;
import genericMontageKit.OverlayObjectManager;
import graphicalObjects_LayerTypes.GraphicLayer;
import undo.UndoManagerPlus;

public interface FigureDisplayContainer {

	public void updateDisplay();
	
	public GraphicLayer getGraphicLayerSet();
	
	public void onItemLoad(ZoomableGraphic z);
	public OverlayObjectManager getSelectionManagger();
	
	
	public Dimension getCanvasDims();
	
	public UndoManagerPlus getUndoManager();
	public ImageWrapper getAsWrapper();
	public String getTitle();
}
