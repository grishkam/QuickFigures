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
 * Date Modified: Jan 6, 2021
 * Version: 2021.2
 */
package includedToolbars;


import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;

import basicMenusForApp.OpeningFileDropHandler;
import genericMontageLayoutToolKit.BorderAdjusterTool;
import genericMontageLayoutToolKit.LayoutMover;
import genericMontageLayoutToolKit.LayoutScalerTool;
import genericMontageLayoutToolKit.RowColNumberTool;
import genericMontageLayoutToolKit.DefaultLayoutMoverTool;
import genericMontageLayoutToolKit.LabelSpaceAdjusterTool;
import genericMontageLayoutToolKit.PanelSizeAdjusterTool;
import genericMontageLayoutToolKit.SelectPanelsTool;
import genericMontageLayoutToolKit.PanelGrabberTool;
import genericMontageLayoutToolKit.RowAndColumnSwapperTool;
import genericMontageLayoutToolKit.RowLabelIntroducerTool;
import genericTools.GeneralTool;
import genericTools.ToolBit;
import layout.basicFigure.LayoutSpaces;

/**A toolbar containing tools that allow the user to edit the layout.
  This toolbar is useful but not critical to the user*/
public class LayoutToolSet extends QuickFiguresToolBar {
	
	public static LayoutToolSet currentToolset;

	public void start() {
		
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
		return new ToolBit[] {new RowColNumberTool(),new PanelSizeAdjusterTool()
		,new LayoutScalerTool(),
				new PanelGrabberTool(1),
				new PanelGrabberTool(2),
				new PanelGrabberTool(0),
				new SelectPanelsTool()};
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
		
		output.add(new RowAndColumnSwapperTool(1));
		output.add(new RowAndColumnSwapperTool(2));
		output.add(new RowAndColumnSwapperTool(0));
		
		output.add(new DefaultLayoutMoverTool());
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
