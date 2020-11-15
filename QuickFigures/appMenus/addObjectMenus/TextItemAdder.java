package addObjectMenus;

import java.awt.Rectangle;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

class TextItemAdder extends BasicGraphicAdder {
	
	boolean simple=false;
	
	public TextItemAdder(boolean isSimple) {
		simple=isSimple;
	}
	
	
	
	TextGraphic iconText=new TextGraphic(); {
		iconText.setFont(iconText.getFont().deriveFont((float) 42));	
		iconText.setFillBackGround(true);
		iconText.getBackGroundShape().copyAttributesFrom(RectangularGraphic.blankRect(new Rectangle(),iconText.getTextColor()));
		iconText.getBackGroundShape().setStrokeColor(iconText.getTextColor());
		iconText.setText("text");
		iconText.setLocationUpperLeft(0, 0);
		
	}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		TextGraphic out = new TextGraphic();
		if(!simple) out=new ComplexTextGraphic();
		out.setLocationUpperLeft(50, 50);
		addLockedItemToSelectedImage(out);
		gc.add(out);
		out.showOptionsDialog();
		return out;
		
	}

	@Override
	public String getCommand() {
		return "addText";
	}

	@Override
	public String getMenuCommand() {
		if(!simple) return "Add Rich Text";
		return "Add Text";
	}
	
	public Icon getIcon() {
		return TextGraphic.createImageIcon();
	}
	
}
