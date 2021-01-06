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
 * Version: 2021.1
 */
package genericTools;

import java.awt.Cursor;

import externalToolBar.DragAndDropHandler;

public class PanTool extends BasicToolBit {
	
	{this.normalCursor=Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);}
	{createIconSet("icons3/HandToolIcon.jpg","icons3/HandToolIconPressed.jpg","icons3/HandToolIcon.jpg");};
	double dsx=0;
	double dsy=0;
	
	
	
	@Override
	public void mousePressed() {
		dsx=0;
		dsy=0;
	}
	
	@Override
	public void mouseDragged() {
		//dsx+=this.getDragCordinateX()-getClickedCordinateX();
	//	dsy+=this.getDragCordinateY()-this.getClickedCordinateY();
		
		//IssueLog.log("ppints "+dsx+"   ,  "+dsy);
		
		this.getImageDisplayWrapperClick().setCursor(normalCursor);
		
		this.getImageDisplayWrapperClick().scrollPane((int) -getXDisplaceMent(), (int) -getYDisplaceMent());
		//getImageDisplayWrapperClick().setScrollCenter(this.getClickedCordinateX()+dsx, 0);
		//getImageDisplayWrapperClick().updateDisplay();
		//super.setClickPointToDragReleasePoint();
		
	}
	
	@Override
	public void mouseEntered() {
		this.getImageDisplayWrapperClick().setCursor(normalCursor);
		
		
	}
	
	public DragAndDropHandler getDragAndDropHandler() {
		return new NormalToolDragHandler(this);
	}

}
