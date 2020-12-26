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
import genericMontageLayoutToolKit.GeneralLayoutEditorTool;
import layout.PanelLayout;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;


public class PanelSizeAdjusterTool extends GeneralLayoutEditorTool implements ActionListener, LayoutSpaces {
	int mode=1;
	

	@Override
	public
	void performDragEdit(boolean b) {
		if (mode==1) {
			 getLayoutEditor().augmentPanelHeight(getCurrentLayout(), getYDisplaceMent() , getRowIndexClick());
			 getLayoutEditor().augmentPanelWidth(getCurrentLayout(), getXDisplaceMent() , getColIndexClick());
		}
		
	}
	

	public void onActionPerformed(Object sour, String st) {
		
	}
	
	public ArrayList<JMenuItem> getPopupMenuItems() {
		//return 	new MontageEditCommandMenu( currentlyInFocusWindowImage().createLayout()).getPanelSizeList();
		return null;
	}
	
	@Override
	public String getToolTip() {
			return "Adjust Layout Panel Size";
		}
	
	@Override
	public String getToolName() {
			return "Panel Size AdjustTool";
		}
	
	{this.setIconSet(new  PanelSizeToolIcon(0).generateIconSet());}
	
	class PanelSizeToolIcon extends GeneralLayoutToolIcon {

		/**
		 * @param type
		 */
		public PanelSizeToolIcon(int type) {
			super(type);
			super.paintBoundry=false;
			super.panelColor=new Color[] {GREEN_TONE};
		}
		
		/**
		creates a layout for drawing and icon
		 */
		protected PanelLayout createSimpleIconLayout( int type) {
			BasicLayout layout = new BasicLayout(2, 2, 7, 7, 2,2, true);
			layout.setLabelSpaces(2, 2,2,2);
			layout.move(2,2);
			layout.setIndividualRowHegihts(4, 7);
			if(type!=NORMAL_ICON_TYPE) {
				layout.setIndividualRowHegihts(4, 12);
			}
			return layout;
		}
		
		/**
		 * @param type
		 * @return
		 */
		protected GeneralLayoutToolIcon generateAnother(int type) {
			return new PanelSizeToolIcon(type);
		}
	}

}
