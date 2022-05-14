/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 5, 2021
 * Version: 2022.1
 */
package selectedItemMenus;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import graphicalObjects.FigureDisplayWorksheet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import locatedObject.LocatedObject2D;
import locatedObject.PointsToFile;
import standardDialog.StandardDialog;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.InfoDisplayPanel;
import undo.AbstractUndoableEdit2;
import undo.UndoManagerPlus;

/**abstract implementation of the MultiSelectionOperator.
  Contains methods that are useful for multiple subclasses*/
public abstract class BasicMultiSelectionOperator implements MultiSelectionOperator, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected transient ArrayList<ZoomableGraphic> array;//a list of selected items
	public transient LayerSelectionSystem selector;//the layer selector object that determines which items are selecred

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


	/**returns all the selected items at an array*/
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
	
	public void setSelector(LayerSelectionSystem graphicTreeUI){
		this.selector=graphicTreeUI;
	}
	
	public LayerSelectionSystem getSelector() {
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
		
			FigureDisplayWorksheet graphicDisplayContainer = getSelector().getWorksheet();
			return graphicDisplayContainer.getUndoManager();
		
	}
	
	/**returns true if this operator can act on the objects selected by the given selector */
	public boolean canUseObjects(LayerSelectionSystem graphicTreeUI) {return true;}
	
	/**returns true if this operator can work with the selector */
	public boolean isValidForLayerSelector(LayerSelectionSystem graphicTreeUI) {return true;}
	
	/**returns the font that will be used for the menu item*/
	public Font getMenuItemFont() {return null;}
	
	/**returns a component that the user can employ to input specific values*/
	public Component getInputPanel() {return null;}
	
	
	/**The undo manager must be transient as one does not want a very long array of undo's to be serialized*/
	private transient UndoManagerPlus undoManager;
	
	/**returns the unto manager*/
	public UndoManagerPlus getCurrentUndoManager() {
		if (selector!=null&&selector.getWorksheet()!=null)
		return selector.getWorksheet().getUndoManager();
		return undoManager;
	}
	
	/**
	 Adds the undo if an undo manager is setup
	 */
	public void addUndo(AbstractUndoableEdit2 edits) {
		if (getCurrentUndoManager()!=null) getCurrentUndoManager().addEdit(edits);
	}
	
	/** popup menus will sometimes show input panels below the screen,
	 * adding a few extra panels below the main one is a workable fix
	 * @param strokeWidthInput
	 * @return
	 */
	public static Component getPaddedPanel(NumberInputPanel strokeWidthInput) {
		return StandardDialog.combinePanels(strokeWidthInput,  new InfoDisplayPanel("  ", ""), new InfoDisplayPanel("  ", ""));
	}
	
	/**if the menu item is rendered in a special way returns the renderer*/
	public JMenuItem getMenuItemRenderer() {return null;}

}
