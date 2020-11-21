package addObjectMenus;

import java.awt.Color;
import java.awt.Rectangle;

import graphicTools.RectGraphicTool;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

/**An adding menu item that adds a rectangular object 
  (or at least one that extends the rectangular graphic superclass)*/
public class RectangleAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	static int count=0; {count++;}
	private static final long serialVersionUID = 1L;
	private RectGraphicTool tool;
	String menuPath = "Shapes";
	
	/**creates an object adder from the rectangular graphic tool*/
	public RectangleAdder(RectGraphicTool t, String subMenuName) {
		this.tool=t;
		if (subMenuName!=null) menuPath +="<"+subMenuName;
		t.getModel().setStrokeColor(Color.blue);
		t.getModel().setRectangle(new Rectangle(0,0,50,50));// the icon will be large due to this
	}
	
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		RectangularGraphic rg = getModelForIcon().copy();
		
		gc.add(rg);
		rg.showOptionsDialog();
		return rg;
	}
	
	

	@Override
	public String getCommand() {
		return "Add rect"+unique+tool.getShapeName()+count;
	}

	@Override
	public String getMenuCommand() {
		return "Add "+tool.getShapeName();
	}

	public RectangularGraphic getModelForIcon() {
		return tool.getModel();
	}
	
	@Override
	public String getMenuPath() {
		return menuPath;
	}


}
