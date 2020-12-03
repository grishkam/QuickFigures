package graphicTools;

import externalToolBar.IconWrappingToolIcon;
import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;

/**A tool for adding text items with multiple lines*/
public class ComplexText_GraphicTool extends Text_GraphicTool{

	{model= new ComplexTextGraphic();;
	set=TreeIconWrappingToolIcon.createIconSet(model);
	}

	public ComplexText_GraphicTool(boolean b) {
		super(b);
		if (editorOnly) {
			set=IconWrappingToolIcon.createIconSet(new textCursorIcon());
		}
	}
	
	public TextGraphic makeNewTextObject() {
		ComplexTextGraphic textob = new ComplexTextGraphic();
		textob.copyAttributesFrom(model);
		textob.setTextColor(getForeGroundColor());
		textob.getParagraph().getLastLine().getLastSegment().setText("Text");
		return textob;
	}
	
	@Override
	public String getToolTip() {
		if (editorOnly) return "Edit Text Objects";
			return "Create Multiline/MultiColor Text Objects";
		}

}
