/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
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
