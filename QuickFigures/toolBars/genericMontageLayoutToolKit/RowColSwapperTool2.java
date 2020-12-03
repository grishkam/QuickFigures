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
import externalToolBar.IconSet;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import gridLayout.LayoutSpaces;


public class RowColSwapperTool2 extends GeneralLayoutEditorTool implements LayoutSpaces{
	{resetClickPointOnDrag=false;}
	//ArrayList<PanelSwapListener<ImagePlus>> psListeners=new ArrayList<PanelSwapListener<ImagePlus>>();
	
	public RowColSwapperTool2(int mode) {
		this.mode=mode;
	}
	
	public int mode=1;

	 public int markerType() {
	    	return mode;
	    }
	
	public void performReleaseEdit(boolean b) {

		if (mode==0) {  
			getEditor().swapMontagePanels(getCurrentLayout(), getPanelIndexClick(), getPanelIndexDrag());
		
		}
		
		if (mode==1) {
			 getEditor().swapColumn(getCurrentLayout(), getColIndexClick(), getColIndexDrag());
		
		}
		
		if (mode==2) {
			 getEditor().swapRow(getCurrentLayout(), getRowIndexClick(), getRowIndexDrag());
			
			
		}
		
		if (layoutGraphic.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane parentLayer = (FigureOrganizingLayerPane) layoutGraphic.getParentLayer();
			 parentLayer.updateChannelOrder(mode);
			
		}
		
		super.getImageWrapperClick().updateDisplay();
		
	}
	
	{createIconSet("icons/ColumnSwapperIcon.jpg",
				"icons/ColumnSwapperPressIcon.jpg",
				"icons/ColumnSwapperRollIcon.jpg",
				"icons/RowSwapperIcon.jpg",
				"icons/RowSwapperPressIcon.jpg",
				 "icons/RowSwapperRollIcon.jpg", 
				 "icons/PanelSwapperIcon.jpg",
				"icons/PanelSwapperIconPressed.jpg",
				"icons/PanelSwapperIconRollOver.jpg"
				);
	}
	
	IconSet set1=new IconSet("icons/ColumnSwapperIcon.jpg",
			"icons/ColumnSwapperPressIcon.jpg",
			"icons/ColumnSwapperRollIcon.jpg");
	IconSet set2=new IconSet("icons/RowSwapperIcon.jpg",
			"icons/RowSwapperPressIcon.jpg",
			 "icons/RowSwapperRollIcon.jpg");
	IconSet set3=new IconSet( "icons/PanelSwapperIcon.jpg",
			"icons/PanelSwapperIconPressed.jpg",
			"icons/PanelSwapperIconRollOver.jpg");
	
	@Override
	public
	IconSet getIconSet() {
		if (mode==0) return set3;
		if (mode==1) return set1;
		return set2;
	}
	
	 protected String getTextBase() {
		 if (mode==LayoutSpaces.ROWS) return "Row";
			if (mode==LayoutSpaces.COLS) return  "Column";
			
			 return "Panel";
	    
	}
	

	@Override
	public String getToolTip() {
			
			return "Swap "+getTextBase();
		}
	@Override
	public String getToolName() {
			
			return getToolTip();
		}

}
