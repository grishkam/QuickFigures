package genericMontageUIKit;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import externalToolBar.DragAndDropHandler;
import externalToolBar.IconSet;
import imageDisplayApp.ImageWindowAndDisplaySet;

public interface ToolBit {
	
	IconSet getIconSet();

	void mousePressed();

	void mouseDragged();

	void mouseEntered();
	
	void mouseExited();
	
	void mouseReleased();

	void mouseMoved();

	void mouseClicked();


	void setToolCore(ToolCore toolCore);



	void showOptionsDialog();


	ArrayList<JMenuItem> getPopupMenuItems();

	boolean keyPressed(KeyEvent e);

	boolean keyReleased(KeyEvent e);

	boolean keyTyped(KeyEvent e);

String getToolName();

void handleFileListDrop(ImageWindowAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file);

DragAndDropHandler getDragAndDropHandler();

String getToolTip();

boolean isActionTool();

void performLoadAction();

boolean treeSetSelectedItem(Object o);

void onToolChange(boolean b);

String getToolSubMenuName();

	

}
