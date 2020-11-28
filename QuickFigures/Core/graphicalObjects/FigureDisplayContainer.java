package graphicalObjects;

import java.awt.Dimension;

import applicationAdapters.ImageWrapper;
import genericMontageKit.OverlayObjectManager;
import graphicalObjects_LayerTypes.GraphicLayer;
import undo.UndoManagerPlus;

/**A super interface for classed that display the figures on a canvas.*/
public interface FigureDisplayContainer {

	public void updateDisplay();
	
	public GraphicLayer getGraphicLayerSet();
	
	public void onItemLoad(ZoomableGraphic z);
	public OverlayObjectManager getOverlaySelectionManagger();
	
	
	public Dimension getCanvasDims();
	
	public UndoManagerPlus getUndoManager();
	public ImageWrapper getAsWrapper();
	public String getTitle();
}
