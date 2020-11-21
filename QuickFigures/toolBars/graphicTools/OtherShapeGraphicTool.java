package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import genericMontageUIKit.ToolBit;
import graphicalObjects_BasicShapes.RectangularGraphic;

public class OtherShapeGraphicTool extends RectGraphicTool implements ToolBit {


public OtherShapeGraphicTool(RectangularGraphic model) {
	this.model=model;
	{model.setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(model);}}
}

	public RectangularGraphic createShape(Rectangle r) {
		RectangularGraphic ouput = getModel().copy();
		ouput.setRectangle(r);
		return ouput;
	}
	

}
