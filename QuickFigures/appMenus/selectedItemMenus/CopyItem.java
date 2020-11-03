package selectedItemMenus;

import java.util.ArrayList;

import applicationAdapters.DisplayedImageWrapper;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import imageMenu.CombineImages;

public class CopyItem extends BasicMultiSelectionOperator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean move=false;
	
	public static ArrayList<ZoomableGraphic> thearray=null;
	static String text="";
	
	
	@Override
	public String getMenuCommand() {
		
		return "Copy";
	}
	
	
	
	@Override
	public void setSelection(ArrayList<ZoomableGraphic> array) {
	thearray=array;
	/**
	if (array.size()>1) {
		boolean complete = isCompleteLayer(array.get(0).getParentLayer(),array);
		if (complete) {
			ArrayList<ZoomableGraphic> ar2 = new ArrayList<ZoomableGraphic>();
			ar2.add(array.get(0).getParentLayer());
			thearray=ar2;
		}
	}*/
		
	}

	@Override
	public void run() {
		setSelection(this.getSelector().getSelecteditems());
		for(ZoomableGraphic i:getSelector().getSelecteditems() ) {
			if(i instanceof TextGraphic) {
				TextGraphic t=(TextGraphic) i;
				text=t.getText();
			}
			if(i instanceof ComplexTextGraphic) {
				ComplexTextGraphic t=(ComplexTextGraphic) i;
				if(t.isEditMode()) text=t.copySelectedRegion();
			}
		}

		if (move) {
			DisplayedImageWrapper destination = CombineImages.getChoice("Select where");
			GraphicLayer l = this.getSelector().getSelectedLayer();
		}
	}
	
	static boolean isCompleteLayer(GraphicLayer l, ArrayList<ZoomableGraphic> thearray) {
		ArrayList<ZoomableGraphic> all = l.getAllGraphics();
		if (all.size()==thearray.size()) {
			for(ZoomableGraphic a: all) {
				if (a.getParentLayer()!=l) return false;
			}
			for(ZoomableGraphic a: thearray) {
				if (a.getParentLayer()!=l) return false;
			}
		}
		
		return true;
	}
	
public String getMenuPath() {
		
		return "Item";
	}

public GraphicLayerPane duplicateLayer(GraphicLayer l) {
	GraphicLayerPane out = new GraphicLayerPane(l.getName());
	
	return out;
}

}
	//

