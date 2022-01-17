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
 * Version: 2022.0
 */
package externalToolBar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import applicationAdapters.CanvasMouseEvent;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;

/**A default tool that can be used as both space filler in a toolbar or as a superclass*/
public class DummyTool<ImageType> implements InterfaceExternalTool<ImageType>, InterfaceKeyStrokeReader<ImageType>{

	

	private JButton toolButton;

	{
	try{this.getClass().getResource("Blank.jpg");} catch(Throwable t){
		IssueLog.log("problem coult not find Blank.jpg");
	}
}
	@Override
	public void mousePressed(ImageType imp, CanvasMouseEvent e) {
	}

	@Override
	public void mouseClicked(ImageType imp, CanvasMouseEvent e) {}

	@Override
	public void mouseDragged(ImageType imp, CanvasMouseEvent e) {}

	@Override
	public void mouseReleased(ImageType imp, CanvasMouseEvent e) {}

	@Override
	public void mouseExited(ImageType imp, CanvasMouseEvent e) {}

	@Override
	public void mouseEntered(ImageType imp, CanvasMouseEvent e) {}

	@Override
	public void mouseMoved(ImageType imp, CanvasMouseEvent e) {}

	@Override
	public void showOptionsDialog() {}

	@Override
	public void controlClickDialog(Component c) {}
	@Override
	public void performLoadAction() {}

	@Override
	public ArrayList<JMenuItem> getPopupMenuItems() {
		return null;
	}

	
	

	@Override
	public void setImageAndClickPoint(ImageType imp, int x, int y) {}

	@Override
	public String getToolName() {
		return "Tool";
	}


	@Override
	public boolean isActionTool() {
		return false;
	}

	@Override
	public boolean isMenuOnlyTool() {
		return false;
	}

	@Override
	public Icon getToolNormalIcon() {
		try{this.getClass().getResource("Blank.jpg");} catch(Throwable t){
			IssueLog.log("problem could not find Blank.jpg");
		}
		return null;
	}

	@Override
	public Icon getToolPressedImageIcon() {
		return null;
	}

	@Override
	public Icon getToolRollOverImageIcon() {
		return null;
	}

	@Override
	public InterfaceKeyStrokeReader<ImageType> getCurrentKeyStrokeReader() {
		return this;
	}

	@Override
	public void introduceButton(JButton jb) {
		this.toolButton=jb;
		
	}


	protected static class ColorIcon implements Icon {

		@Override
		public int getIconHeight() {
			return AbstractExternalToolset.DEFAULT_ICONSIZE;
		}

		@Override
		public int getIconWidth() {
			return AbstractExternalToolset.DEFAULT_ICONSIZE;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(this.getColor());
			int location3d = 3;
			int size3d = 19;
			g.draw3DRect(x+location3d, y+location3d, size3d, size3d, true);
		}

		Color theColor=Color.black;
		private Color getColor() {
			return theColor;
		}}
	
	static void main(String[] args) {
		
	
	}

	@Override
	public boolean keyPressed(ImageType imp, KeyEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyReleased(ImageType imp, KeyEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(ImageType imp, KeyEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public JButton getToolButton() {
		return toolButton;
	}

	@Override
	public void handleFileListDrop(ImageWindowAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file) {

	}

	@Override
	public DragAndDropHandler getDraghandler() {
		return new BasicDragHandler();
	}

	@Override
	public String getToolTip() {
		return null;
	}

	@Override
	public boolean userSetSelectedItem(Object o) {
		return false;
	}

	@Override
	public void onToolChange(boolean b) {
		
	}

	



}
