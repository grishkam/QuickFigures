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

	protected RectangularGraphic createNewRect(Rectangle r) {
		RectangularGraphic ouput = model.copy();
		ouput.setRectangle(r);
		return ouput;
	}
	

}
