package selectedItemMenus;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import menuUtil.SmartPopupJMenu;
import undo.UndoManagerPlus;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.PointsToFile;

public abstract class BasicMultiSelectionOperator implements MultiSelectionOperator, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected transient ArrayList<ZoomableGraphic> array;
	//protected GraphicSetDisplayContainer cont;
	protected transient LayerSelector selector;

	@Override
	public void setSelection(ArrayList<ZoomableGraphic> array) {
		this.array=array;

	}
	
	/**returns a list in which the layers among the selected items
	  have been replaced by their contents*/
	public ArrayList<ZoomableGraphic> getAllArray() {
		ArrayList<ZoomableGraphic> array2=new ArrayList<ZoomableGraphic>();
		for(ZoomableGraphic a:array) {
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
	
	public UndoManagerPlus getUndoManager() {
		return getSelector().getGraphicDisplayContainer().getUndoManager();
	}
	
	
	public boolean canUseObjects(LayerSelector graphicTreeUI) {return true;}
	
	public boolean isValidForLayerSelector(LayerSelector graphicTreeUI) {return true;}
	
	public Font getMenuItemFont() {return null;}
	public Component getInputPanel() {return null;}

}
