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
package multiChannelFigureUI;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.junit.Test;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import appContextforIJ1.ImageDisplayTester;
import externalToolBar.InterfaceExternalTool;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelListElement;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import genericTools.GeneralTool;
import genericTools.ToolBit;
import genericTools.ToolTester;
import imageDisplayApp.ImageWindowAndDisplaySet;
import includedToolbars.ObjectToolset1;
import logging.IssueLog;
import testing.FigureTest;

/**
 runs an automated text to determine if the functions of the inset tool work
 */
public class InsetToolTest extends ToolTester{

	private FigureTest figureTest;

	@Test
	public void test() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		ImageDisplayTester.startToolbars(true);
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		figureTest=new FigureTest();
		
		FigureOrganizingLayerPane f = figureTest.createFirstExample();
		
		ImageWindowAndDisplaySet image =(ImageWindowAndDisplaySet) figureTest.gg;
		InsetTool currentTool=null;
		
		
		InterfaceExternalTool<?> mover = ObjectToolset1.setCurrentTool(InsetTool.INSET_TOOL_NAME);
		assert(mover instanceof GeneralTool) ;
		if (mover instanceof GeneralTool) {
			GeneralTool t=(GeneralTool) mover;
			ToolBit bit = t.getToolbit();
			assert(bit instanceof InsetTool) ;
			currentTool=(InsetTool) bit;
			assert(currentTool!=null);
			
		}
		
		PanelListElement panel = f.getAllPanelLists().getMergePanel();
		Point2D p1 = panel.getImageDisplayObject().getLocationUpperLeft();
		//Rectangle panelBounds = panel.getImageDisplayObject().getBounds();
		
		/**performs a mouse motion, in the first test example these locations are visually confirmed to have recognizable features*/
		Point2D.Double location1 = new Point2D.Double(p1.getX()+40, p1.getY()+44);
		Point2D.Double location2 = new Point2D.Double(p1.getX()+60, p1.getY()+58);
		this.getSimulation(image).simulate(location1,location2);
		
		Point2D.Double location3 = new Point2D.Double(p1.getX()+20, p1.getY()+24);
		Point2D.Double location4 = new Point2D.Double(p1.getX()+38, p1.getY()+38);
		this.getSimulation(image).simulate(location3,location4);
		
		PanelGraphicInsetDefiner inset2 = currentTool.getMostRecentDrawnInset();
		if (inset2!=null) {
			inset2.setAngle(Math.PI/3);
			inset2.updateImagePanels();
		}
		
		
		currentTool.createInsetOnImagePanel(currentTool.getImageClicked(),panel.getImageDisplayObject(), new Rectangle(2,2, 15, 16));
		image.closeWindowButKeepObjects();
	}

}
