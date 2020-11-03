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

import applicationAdapters.CanvasMouseEventWrapper;
import imageDisplayApp.ImageAndDisplaySet;
import logging.IssueLog;

/**A default tool that can be used as both space filler in an external toolset or */
public class DummyTool<ImageType> implements InterfaceExternalTool<ImageType>, InterfaceKeyStrokeReader<ImageType>{

	

	private JButton toolButton;

	{
	try{this.getClass().getResource("Blank.jpg");} catch(Throwable t){
		IssueLog.log("problem coult not find Blank.jpg");
	}
}
	@Override
	public void mousePressed(ImageType imp, CanvasMouseEventWrapper e) {
	}

	@Override
	public void mouseClicked(ImageType imp, CanvasMouseEventWrapper e) {}

	@Override
	public void mouseDragged(ImageType imp, CanvasMouseEventWrapper e) {}

	@Override
	public void mouseReleased(ImageType imp, CanvasMouseEventWrapper e) {}

	@Override
	public void mouseExited(ImageType imp, CanvasMouseEventWrapper e) {}

	@Override
	public void mouseEntered(ImageType imp, CanvasMouseEventWrapper e) {}

	@Override
	public void mouseMoved(ImageType imp, CanvasMouseEventWrapper e) {}

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
	public String getToolIcon() {
		return "Blank Tool";
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
	public Icon getToolImageIcon() {
		// TODO Auto-generated method stub
		try{this.getClass().getResource("Blank.jpg");} catch(Throwable t){
			IssueLog.log("problem coult not find Blank.jpg");
		}
		return null;
	}

	@Override
	public Icon getToolPressedImageIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getRollOverIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterfaceKeyStrokeReader<ImageType> getCurrentKeyStrokeReader() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void introduceButton(JButton jb) {
		this.toolButton=jb;
		
	}


	public static class colorIcon implements Icon {

		@Override
		public int getIconHeight() {
			// TODO Auto-generated method stub
			return 25;
		}

		@Override
		public int getIconWidth() {
			// TODO Auto-generated method stub
			return 25;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(this.getColor());
			g.draw3DRect(x+3, y+3, 19, 19, true);
		}

		Color theColor=Color.black;
		private Color getColor() {
			// TODO Auto-generated method stub
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
	public void handleFileListDrop(ImageAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DragAndDropHandler getDraghandler() {
		return new BasicDragHandler();
	}

	@Override
	public String getToolTip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean userSetSelectedItem(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onToolChange(boolean b) {
		// TODO Auto-generated method stub
		
	}

	



}
