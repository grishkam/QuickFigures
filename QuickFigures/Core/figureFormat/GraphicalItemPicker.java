package figureFormat;

import java.util.ArrayList;

import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import utilityClassesForObjects.ObjectContainer;

/***/
public class GraphicalItemPicker<ItemType extends BasicGraphicalObject> extends ItemPicker<ItemType> {

	/**
	  
	 */
	private static final long serialVersionUID = 1L;
	String optionname="Item";
	
	public GraphicalItemPicker(ItemType model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean isDesirableItem(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public ArrayList<BasicGraphicalObject> getDesiredItemsAsGraphicals(ArrayList<?> input) {
		ArrayList<BasicGraphicalObject> output = new ArrayList<BasicGraphicalObject>();
		for(Object ob:input) {
			if (isDesirableItem(ob)) try {
				output.add((BasicGraphicalObject) ob);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return output;
	}
	
	public ArrayList<BasicGraphicalObject> getDesiredItemsAsGraphicals(ObjectContainer oc ) {
		return getDesiredItemsAsGraphicals(oc.getLocatedObjects());
	}
	
	public ArrayList<BasicGraphicalObject> getDesiredItemsAsGraphicals(GraphicLayer oc ) {
		return getDesiredItemsAsGraphicals(oc.getAllGraphics());
	}

	@Override
	public String getOptionName() {
		// TODO Auto-generated method stub
		return optionname;
	}

	@Override
	public void applyProperties(Object item) {
		// TODO Auto-generated method stub
		
	}
	
	boolean displayGraphicChooser() {
		return true;
	}
	
	public void setToStandardFor(MultichannelDisplayLayer wrap) {
		
	}

	
	
}
