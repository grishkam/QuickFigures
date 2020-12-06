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
package selectedItemMenus;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.FigureDisplayContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import undo.AbstractUndoableEdit2;
import undo.UndoManagerPlus;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.PointsToFile;

/**abstract implementation of the MultiSelectionOperator.
  Contains methods that are useful for multiple classes*/
public abstract class BasicMultiSelectionOperator implements MultiSelectionOperator, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected transient ArrayList<ZoomableGraphic> array;//a list of selected items
	public transient LayerSelector selector;//the layer selector object that determines which items are selecred

	/**Sets the list of all the items*/
	@Override
	public void setSelection(ArrayList<ZoomableGraphic> array) {
		this.array=array;

	}
	
	/**returns a list in which the layers among the selected items
	  have been replaced by their contents*/
	public ArrayList<ZoomableGraphic> getAllArray() {
		ArrayList<ZoomableGraphic> array2=new ArrayList<ZoomableGraphic>();
		if (array!=null)for(ZoomableGraphic a:array) {
			if (a instanceof GraphicLayer) {
				array2.addAll(((GraphicLayer)a).getAllGraphics());
			} else array2.add(a);
		}
		return array2;
	}


	protected ArrayList<LocatedObject2D> getAllObjects() {
		ArrayList<ZoomableGraphic> arr = getAllArray();
		ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D>();
		for(ZoomableGraphic i:arr) {
			if (i instanceof LocatedObject2D) output.add((LocatedObject2D) i);
			
		}
		return output;
		
	}
	
	
	public Icon getIcon() {
		return null;
	}
	
	public void setSelector(LayerSelector graphicTreeUI){
		this.selector=graphicTreeUI;
	}
	
	public LayerSelector getSelector() {
		return selector;
	}

	public String getMenuPath() {
		return null;
	}
	
	/**returns a list of the files in the selection.
	  replaces layers with their contents*/
	public ArrayList<File> getPointedFiles() {
		ArrayList<File> files = new  ArrayList<File>();
		ArrayList<ZoomableGraphic> arr = getAllArray();
		for (ZoomableGraphic z: arr) {
			if (z instanceof PointsToFile) {
				PointsToFile filer=(PointsToFile) z;
				if (filer.getFile()!=null) files.add(filer.getFile());
			}
		}
		
		return files;
	}
	
	/**returns the undo manager for the object*/
	public UndoManagerPlus getUndoManager() {
		FigureDisplayContainer graphicDisplayContainer = getSelector().getGraphicDisplayContainer();
		return graphicDisplayContainer.getUndoManager();
	}
	
	
	public boolean canUseObjects(LayerSelector graphicTreeUI) {return true;}
	public boolean isValidForLayerSelector(LayerSelector graphicTreeUI) {return true;}
	
	/**returns the font that will be used for the menu item*/
	public Font getMenuItemFont() {return null;}
	public Component getInputPanel() {return null;}
	
	
	/**The undo manager must be transient as one does not want a very long array of undo's to be serialized*/
	private transient UndoManagerPlus undoManager;
	
	/**returns the unto manager*/
	public UndoManagerPlus getCurrentUndoManager() {
		if (selector!=null&&selector.getGraphicDisplayContainer()!=null)
		return selector.getGraphicDisplayContainer().getUndoManager();
		return undoManager;
	}
	
	/**
	 Adds the undo
	 */
	public void addUndo(AbstractUndoableEdit2 edits) {
		if (getCurrentUndoManager()!=null) getCurrentUndoManager().addEdit(edits);
	}

}
