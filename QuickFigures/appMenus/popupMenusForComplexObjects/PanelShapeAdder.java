package popupMenusForComplexObjects;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JMenuItem;

import graphicActionToolbar.CurrentFigureSet;
import graphicTools.ShapeAddingTool;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import undo.UndoAddItem;
import utilityClassesForObjects.LocatedObject2D;

/**class represents a menu item that adds one shape that will be placed above a panel. 
   The added shape will always be */
public class PanelShapeAdder extends JMenuItem implements Serializable, ActionListener {

	Color shapeStrokeColor = Color.white;
	private ShapeAddingTool shape;//determines what kind of shape is added
	private Rectangle boundry;
	private GraphicLayer layer;

	public PanelShapeAdder(ShapeAddingTool arrowGraphicTool, LocatedObject2D boundry, GraphicLayer layer) {
		super(arrowGraphicTool.getShapeName());
		this.setIcon(arrowGraphicTool.getIcon());
		shape=arrowGraphicTool;
		this.boundry=boundry.getBounds();
		this.layer=layer;
		this.addActionListener(this);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		/**size will be 1/3rd of the bounds of the defining object*/
		Rectangle r = boundry.getBounds();
		int w = r.width/3;
		int h = r.height/3;
		int x = r.x+w;
		int y = r.y+h;
		ShapeGraphic addedShape = shape.createShape(new Rectangle(x,y,w,h));
		
		/**Since most image panels are black, the shape stroke color is set to white*/
		addedShape.setStrokeColor(shapeStrokeColor);
		layer.add(addedShape);
		new CurrentFigureSet().addUndo(new UndoAddItem(layer, addedShape));
		CurrentFigureSet.updateActiveDisplayGroup();
		
	}
	
	
}