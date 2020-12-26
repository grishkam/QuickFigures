/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package genericMontageLayoutToolKit;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import genericMontageLayoutToolKit.GeneralLayoutEditorTool;
import layout.PanelLayout;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;

/**A layout tool for adjusting the label spaces of a layout. */
public class LabelSpaceAdjusterTool  extends GeneralLayoutEditorTool implements LayoutSpaces{

	private static final int LABEL_SPACES = 0, LAYOUT_LOCATION=1;
	int mode=LABEL_SPACES;
	
	
	public void performDragEdit(boolean shift) {
		BasicLayout layout = getCurrentLayout();
		if (mode==LABEL_SPACES) {
			if (shift) {
				
				 getLayoutEditor().addRightLabelSpace(layout, getMouseDisplacementX());
				 getLayoutEditor().addBottomLabelSpace(layout, getMouseDisplacementY());
				 layout.resetPtsPanels();
				
				
			} else {
				 getLayoutEditor().addLeftLabelSpace(layout, -getMouseDisplacementX());
				 getLayoutEditor().addTopLabelSpace(layout, -getMouseDisplacementY());
				 layout.resetPtsPanels();
			
			}
			}
		
		if (mode==LAYOUT_LOCATION) {
		
				if (shift) {
					 getLayoutEditor().addRightSpecialSpace(layout, getMouseDisplacementX());
					 getLayoutEditor().addBottomSpecialSpace(layout, getMouseDisplacementY());
				} else {
					 getLayoutEditor().addLeftSpecialSpace(layout, getMouseDisplacementX());
					 getLayoutEditor().addTopSpecialSpace(layout, getMouseDisplacementY());
				}
		
			
		
	}
		
		layout.afterEditDone();
	}
	
	
	/**A Dialog for changing the type of tool*/
	@Override
	public void showOptionsDialog() {
					StandardDialog gd = new StandardDialog(getClass().getName().replace("_",
					" "), true);
			
			String[] option2 = new String[] {
					"Label Space Adjuster (Top/Left), (shift Bottom/Right) ",
					"Move Layout (Top/Left, shift for Bottom/Right)" };
			gd.add("Adjust ",new ChoiceInputPanel("Adjust ", option2, mode));
			
			gd.showDialog();
			
			if (gd.wasOKed()) {
				mode = gd.getChoiceIndex("Adjust ");
				
}

	}
	
	{this.setIconSet(new  LabelSpaceIcon(0).generateIconSet());}
	
	public ArrayList<JMenuItem> getPopupMenuItems() {
		ArrayList<JMenuItem> output = new ArrayList<JMenuItem>();
		return output;
			}
	
	@Override
	public String getToolTip() {
		if (mode==LABEL_SPACES)  
				{return "Adjust Label Spaces (try holding shift)";}
		
			return "Adjust Layout Position";
		}
	
	public String getToolName() {return getToolTip();}
	
	
	class LabelSpaceIcon extends GeneralLayoutToolIcon {

		/**
		 * @param type
		 */
		public  LabelSpaceIcon(int type) {
			super(type);
			// TODO Auto-generated constructor stub
		}
		
		/**
		creates a layout for drawing and icon
		 */
		protected PanelLayout createSimpleIconLayout( int type) {
			BasicLayout layout = new BasicLayout(2, 2, 6, 6, 2,2, true);
			layout.setLabelSpaces(2, 2,2,2);
			layout.move(2,3);
			if(type!=NORMAL_ICON_TYPE) {
				layout.setLabelSpaces(3, 2, 6, 2);
			}
			return layout;
		}
		
		/**
		 * @param type
		 * @return
		 */
		protected GeneralLayoutToolIcon generateAnother(int type) {
			return new  LabelSpaceIcon(type);
		}
	}
	
}