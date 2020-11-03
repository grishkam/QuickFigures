package externalToolBar;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import applicationAdapters.CanvasMouseEventWrapper;
import imageDisplayApp.ImageAndDisplaySet;
public interface InterfaceExternalTool<ImageType>  {
	public void mousePressed(ImageType imp, CanvasMouseEventWrapper e) ;
	public void mouseClicked(ImageType imp, CanvasMouseEventWrapper e) ;
	public void mouseDragged(ImageType imp, CanvasMouseEventWrapper e) ;
	public void mouseReleased(ImageType imp, CanvasMouseEventWrapper e) ;
	public void mouseExited(ImageType imp, CanvasMouseEventWrapper e) ;
	public void mouseEntered(ImageType imp, CanvasMouseEventWrapper e) ;
	public void mouseMoved(ImageType imp, CanvasMouseEventWrapper e) ;
	
	public boolean keyPressed(ImageType imp, KeyEvent e) ;
	public boolean keyReleased(ImageType imp, KeyEvent e) ;
	public boolean keyTyped(ImageType imp, KeyEvent e) ;
	public InterfaceKeyStrokeReader<ImageType> getCurrentKeyStrokeReader();
	
	
	/**The method that shows an options meny for the tool */
	public void showOptionsDialog();
	
	/**A method that shows the menu for menu tools. */
	public void controlClickDialog(Component c);
	/**A method that performs an action for action tools*/
	public void performLoadAction();
	
	/**Retrives a list of menu items if one wants a simplistic popup menu to appear on 
	  control click*/
	ArrayList<JMenuItem> getPopupMenuItems();
	
	public void setImageAndClickPoint(ImageType imp, int x, int y);
	
	/**lets the code know that this is a tool that just performs an action is not to be set as the current tool*/
	boolean isActionTool();
	/**true if this is a tool that just shows a menu and nothing more. false otherwise.*/
	boolean isMenuOnlyTool();
	
	String getToolName() ;
	String getToolIcon() ;
	
	/**icons for the tool*/
	Icon getToolImageIcon() ;
	Icon getToolPressedImageIcon() ;
	Icon getRollOverIcon() ;
	
//	public void introduceButton(JButton jb);
	public void introduceButton(JButton jb);
	
	public DragAndDropHandler getDraghandler();
	
	public void handleFileListDrop(ImageAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file);
	public String getToolTip();
	
	public boolean userSetSelectedItem(Object o);
	public void onToolChange(boolean b);
	
}
