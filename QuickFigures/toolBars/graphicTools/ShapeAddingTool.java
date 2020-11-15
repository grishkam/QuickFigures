package graphicTools;

import java.awt.Rectangle;

import javax.swing.Icon;

import graphicalObjects_BasicShapes.ShapeGraphic;

public interface ShapeAddingTool {

	String getShapeName();

	ShapeGraphic createShape(Rectangle rectangle);
	public Icon getIcon() ;

}
