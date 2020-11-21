package genericMontageLayoutToolKit;

import externalToolBar.GraphicToolIcon;
import genericMontageUIKit.Object_Mover;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;

public class LayoutMover extends Object_Mover {
	{super.bringSelectedToFront=true; 
	super.onlySelectThoseOfClass=PanelLayoutGraphic.class;
	}
	{//createIconSet("icons2/LayoutMoverIcon.jpg","icons2/LayoutMoverIconPress.jpg","icons2/LayoutMoverIcon.jpg");
	set=GraphicToolIcon.createIconSet(prepareIcon());
	}

	/**
	 * @return
	 */
	protected LayoutShowingToolIcon prepareIcon() {
		return new LayoutShowingToolIcon(0, true);
	};
	
	@Override
	public String getToolTip() {
			
			return "Select and Manipulate Layouts";
		}
	

	@Override
	public String getToolName() {
			
			return "Select, move and edit layouts";
		}
	
}
