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
package includedToolbars;


import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.OpeningFileDropHandler;
import externalToolBar.InterfaceExternalTool;
import externalToolBar.ToolBarManager;
import genericMontageLayoutToolKit.BorderAdjusterTool;
import genericMontageLayoutToolKit.LayoutMover;
import genericMontageLayoutToolKit.LayoutScalerTool;
import genericMontageLayoutToolKit.MontageLayoutRowColNumberTool;
import genericMontageLayoutToolKit.MontageMoverTool;
import genericMontageLayoutToolKit.LabelSpaceAdjusterTool;
import genericMontageLayoutToolKit.PanelSizeAdjusterTool;
import genericMontageLayoutToolKit.Panel_Selector2;
import genericMontageLayoutToolKit.PannelGrabberTool;
import genericMontageLayoutToolKit.RowColSwapperTool2;
import genericMontageLayoutToolKit.RowLabelIntroducerTool;
import genericMontageUIKit.GeneralTool;
import genericMontageUIKit.ToolBit;
import gridLayout.LayoutSpaces;

/**A toolbar containing tools that allow the user to edit the layout.
  This toolbar is useful but not indispensible to the user*/
public class LayoutToolSet extends QuickFiguresToolBar {
	
	public static LayoutToolSet currentToolset;

	public void start() {
		
	}
	
	public void setCurrentTool(InterfaceExternalTool<DisplayedImage> currentTool) {
		super.setCurrentTool(currentTool);
		ToolBarManager.setCurrentTool(currentTool);
	}
	

	
	public void graphicTools() {
	
		
		for(ToolBit b: getMinimumLayoutToolBits()) {
			
			addToolBit(b);
		}
		
		
		
	for(ToolBit b: getStandardLayoutToolBits()) {
			
			addToolBit(b);
		}
	

	
	addTool(
				new GeneralTool(getOptionalToolBits()));
	
	
	
for(ToolBit b: getLayoutLabelBits3()) {
			
			addToolBit(b);
		}
		
	
		setCurrentTool(this.tools.get(1));
		
	}
	
	/**returns the tool bits for the montage layout editor tools*/
	public static ArrayList<ToolBit> getOptionalToolBits() {
		ArrayList<ToolBit> output=new ArrayList<ToolBit>();
		ToolBit[] a=getBeyondStandardToolBits();
		for(ToolBit i: a) {output.add(i);};
		return output;
	}
	public static ToolBit[] getBeyondStandardToolBits() {
		return new ToolBit[] {new MontageLayoutRowColNumberTool(),new PanelSizeAdjusterTool()
		,new LayoutScalerTool(),
				new PannelGrabberTool(1),
				new PannelGrabberTool(2),
				new PannelGrabberTool(0),
				new Panel_Selector2()};
	} 
	
	/**returns the tool bits for the layout editor tools*/
	public static ArrayList<ToolBit> getMinimumLayoutToolBits() {
		ArrayList<ToolBit> output=new ArrayList<ToolBit>();
		output.add(new LayoutMover());
		output.add(new BorderAdjusterTool());
		return output;
	} 
	
	/**returns more tool bits for the layout editor tools*/
	public static ArrayList<ToolBit> getStandardLayoutToolBits() {
		ArrayList<ToolBit> output=new ArrayList<ToolBit>();
		
		output.add(new RowColSwapperTool2(1));
		output.add(new RowColSwapperTool2(2));
		output.add(new RowColSwapperTool2(0));
		
		output.add(new MontageMoverTool());
		output.add(new LabelSpaceAdjusterTool());
		
		
		return output;
	} 
	
	
	/**returns the least often used tool bits for the layout editor tools*/
	public static ArrayList<ToolBit> getLayoutLabelBits3() {
		ArrayList<ToolBit> output=new ArrayList<ToolBit>();
		output.add(new  RowLabelIntroducerTool(LayoutSpaces.ROW_OF_PANELS));
		output.add(new  RowLabelIntroducerTool(LayoutSpaces.COLUMN_OF_PANELS));
		output.add(new  RowLabelIntroducerTool(LayoutSpaces.PANELS));
	
		
		
		return output;
	} 
	
public void run(String s) {
		
		
		if (currentToolset!=null&&currentToolset!=this) currentToolset.getframe().setVisible(false);
		super.maxGridx=16;
		graphicTools();
	

		
		showFrame();
		
		currentToolset=this;
		this.getframe().setLocation(new Point(5, 40));
	
	}
	
	public void showFrame() {
		super.showFrame();
		getframe().setTitle("Layout Tools");
		addToolKeyListeners();
		new DropTarget(getframe(), new OpeningFileDropHandler());
	}
}
