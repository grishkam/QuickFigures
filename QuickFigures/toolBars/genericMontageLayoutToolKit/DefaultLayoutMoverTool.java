/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 5, 2021
 * Version: 2023.2
 */
package genericMontageLayoutToolKit;
import genericMontageLayoutToolKit.GeneralLayoutEditorTool;
import layout.PanelLayout;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import logging.IssueLog;

/**Layout Tool that moves the clicked layout*/
public class DefaultLayoutMoverTool  extends GeneralLayoutEditorTool implements LayoutSpaces{

	
	public void performDragEdit(boolean shift) {
		try {
				 getLayoutEditor().moveLayout(getCurrentLayout(), getMouseDisplacementX(), getMouseDisplacementY());
	
			} catch (Throwable t) {IssueLog.logT(t);}
	}
	

	@Override
	public String getToolTip() {
			return "Adjust Layout Position";
		}
	@Override
	public String getToolName() {
			return "Move Figure Layout";
		}

	{this.setIconSet(new  MoverIcon(0).generateIconSet());}
	class MoverIcon extends GeneralLayoutToolIcon {

		/**
		 * @param type
		 */
		public MoverIcon(int type) {
			super(type);
			// TODO Auto-generated constructor stub
		}
		
		/**
		creates a layout for drawing and icon
		 */
		protected PanelLayout createSimpleIconLayout( int type) {
			BasicLayout layout = new BasicLayout(2, 1, 6, 6, 2,2, true);
			layout.setLabelSpaces(2, 2,2,2);
			layout.move(2,3);
			if(type!=NORMAL_ICON_TYPE) {
				layout.move(2, 8);
			}
			return layout;
		}
		
		/**
		 * @param type
		 * @return
		 */
		protected GeneralLayoutToolIcon generateAnother(int type) {
			return new MoverIcon(type);
		}
	}
	
	

	
}