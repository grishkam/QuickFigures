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
package externalToolBar;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import logging.IssueLog;
import menuUtil.SmartPopupJMenu;

/**The methods in this class organize a toolbar, maintain a list of tools, keeps track of the current tool
 * and a list of open toolbars*/
public class AbstractExternalToolset<ImageType> implements MouseListener, WindowListener {
	
	public static final int DEFAULT_ICONSIZE = 25;

	/**An arraow of tool change listeners*/
	Vector<ToolChangeListener> listeners=new Vector<ToolChangeListener>();
	 public void addToolChangeListener(ToolChangeListener lis) {
		 listeners.add(lis);
	 }
	 
	/**Keeps track of all toolsets*/
	public static Vector<AbstractExternalToolset<?>> openToolsets= new Vector<AbstractExternalToolset<?>>();
	
	HashMap<AbstractButton, InterfaceExternalTool<ImageType>> buttonToolPairs=new HashMap<AbstractButton, InterfaceExternalTool<ImageType>>();
	AbstractButton currentToolButton;
	
	
	protected Vector<InterfaceExternalTool<ImageType>> tools= new Vector<InterfaceExternalTool<ImageType>> ();
	protected Vector<AbstractButton> buttons= new Vector<AbstractButton>();
	public InterfaceExternalTool<ImageType> currentTool;
	
	private int Xinnitial=0;
	private int Yinnitial=0;
	GridBagConstraints toolBarBridConstraints=new GridBagConstraints();

	/**the max number of tools that can appear in one row beyond that are added to the subsequent row*/
	protected int maxGridx=11;
	
	/**The JFrame that is used*/
	private JFrame frame=new JFrame(); {
		frame.setLayout(new GridBagLayout()); frame.addWindowListener(this);openToolsets.add(this);
		
	}
	
	/**the JToolbar objects that is used*/
	protected JToolBar toolbar=new JToolBar();
	{ innitializeToolbar();}
	
	/**Sets uo the layout of the toolbar. adds the JToolbar object to the window*/
	public void innitializeToolbar() {
		GridBagLayout g = new GridBagLayout(); 
		GridBagConstraints c=new GridBagConstraints(); 
		c.gridy=2; 
		toolbar.setLayout(g);
		frame.add(toolbar ,c ); 
		toolbar.setMargin(new Insets(0,0,0,0)); 
		toolbar.setMinimumSize(new Dimension(0,0));  
		
		toolBarBridConstraints.insets=new Insets(0,0,0,0);
		toolBarBridConstraints.ipadx=0; 
		toolBarBridConstraints.ipady=0;
		toolBarBridConstraints.gridheight=1;
		toolBarBridConstraints.gridwidth=1;
		toolBarBridConstraints.gridx=Xinnitial;
		toolBarBridConstraints.gridy=Yinnitial;
		
	}
	
	/**returns teh window that contains this toolbar*/
	public JFrame getframe() {
		return frame;
	}
	
	/**Sets the current tool*/
	public void setCurrentTool(InterfaceExternalTool<ImageType> currentTool) {
		if(currentTool==null) return;
		if (this.currentTool!=null) {
			 this.currentTool.onToolChange(false);//some tools do something when they are deactivated
		 }
		this.currentTool=currentTool;
		this.currentTool.onToolChange(true);//some tools do something when they are activated
		
		for(ToolChangeListener tcl: listeners) try {//some classes do something when a tool switch is made
			if (tcl!=null) tcl.ToolChanged( getCurrentTool() );
		} catch (Throwable t) {}
	}
	
	/**Searches through the open toolbars. 
	 * If a tool containing the given name is found, sets that as the current tool*/
	public static InterfaceExternalTool<?> setCurrentTool(String st) {
		for (AbstractExternalToolset<?> toolset :openToolsets ) {
			InterfaceExternalTool<?> n = toolset.selectToolWithName(st);
			if(n!=null) return n;
			}
		return null;
		}
	
/**Finds a tool with the given name */
public InterfaceExternalTool<ImageType> selectToolWithName(String name) {
	for(AbstractButton button: buttonToolPairs.keySet()) {
		InterfaceExternalTool<ImageType> tool = buttonToolPairs.get(button);
		if(tool.getToolName().equals(name)) {
			selectTool( tool, (JButton)button);
			return tool;
		}
	}
	return null;
}
	
	/**returns the current tool*/
	public InterfaceExternalTool<ImageType>  getCurrentTool() {
		return currentTool;
	}
	
	
	
	/**moves the grid bag constrants for the next tool*/
	 protected void gridToNextLine() {
		toolBarBridConstraints.gridx=Xinnitial;
		toolBarBridConstraints.gridy=toolBarBridConstraints.gridy+1;
	}
	
	 /**displays a popup menu in response to control clicks*/
	public void controlClickDialog(Component c, InterfaceExternalTool<ImageType> clickedTool) {
		SmartPopupJMenu pup = new SmartPopupJMenu();
		for(JMenuItem stackPanel: clickedTool.getPopupMenuItems())	pup.add(stackPanel); 
		pup.show(c, 0, 0+c.getHeight());
	}
	
	/**Adds a tool*/
	public synchronized void addTool(InterfaceExternalTool<ImageType> tool) {
		
		try{
		
		tools.add(tool);
		JButton jb=new JButton(tool.getToolName());
		tool.introduceButton(jb);
		buttons.add(jb);
		
		
		
		buttonToolPairs.put(jb, tool);
		jb.addMouseListener(this);
		buttonToolPairs.put(jb, tool);
		Icon i=getIconForTool(tool);
		
	
		stripButton(jb);
		
		if (i!=null) {jb.setIcon(i);
		jb.setText(null);
		if (tool.getToolPressedImageIcon()!=null)
		jb.setPressedIcon(tool.getToolPressedImageIcon());}
		if (tool.getToolRollOverImageIcon()!=null) {jb.setRolloverIcon(tool.getToolRollOverImageIcon()); jb.setRolloverEnabled(true);}
		jb.setToolTipText(tool.getToolTip());
		
	
		
		/**adds the button and moves the grid constraints*/
		toolbar.add(jb, toolBarBridConstraints); 
		moveGridConstraints();
		
		} catch (Exception ex) {IssueLog.logT(ex);}
		
		
	}
	
	/**extracts the tool icon*/
	Icon getIconForTool(InterfaceExternalTool<ImageType> tool) {
		Icon i=null;
		i=tool.getToolNormalIcon();
		if (i==null) {
			/**if no icon exists, wills the button with grey*/
			BufferedImage image = new BufferedImage(DEFAULT_ICONSIZE, DEFAULT_ICONSIZE, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();g.setColor(Color.gray); g.fillRect(0, 0, DEFAULT_ICONSIZE, DEFAULT_ICONSIZE);
			i=new ImageIcon(image);
		}
		
		return i;
	}
	
	/**alters the JButton, removing any unnecesary parts*/
	public static void stripButton(JButton jb) {
		jb.setMargin(new Insets(-2, -3, -2, -4));
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setIconTextGap(0); 
	}
	
	
	 /**updates the icon for the current tool's button*/
	public void resetButtonIcon() {
		resetButtonIcon(currentToolButton) ;
	}
	/**updates the icon for the button*/
	public void resetButtonIcon(AbstractButton jb) {
		if (jb==null) return;
		InterfaceExternalTool<ImageType> tool1=buttonToolPairs.get(jb);
		if (tool1==null) return;
			Icon ii=getIconForTool(tool1);
			jb.setIcon(ii);
			jb.setRolloverEnabled(true);
	}
	

	/**changes the grid x and y of the gridbag constraints to the next position in the toolbar*/
	void moveGridConstraints() {
		toolBarBridConstraints.gridx=toolBarBridConstraints.gridx+1;
		if (toolBarBridConstraints.gridx>=getMaxGridx()) {
			gridToNextLine();
		}
		
	}
	
	/**shows the JFrame containing this toolbar*/
	public void showFrame() {frame.pack(); frame.setVisible(true);frame.setResizable(false);}
	
	

	/**double clicking on a tool displays the options dialog*/
	@Override
	public void mouseClicked(MouseEvent arg0) {
		int click=arg0.getClickCount();
		if (click==2&&getCurrentTool()!=null) getCurrentTool() .showOptionsDialog();
	}
	
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
	
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
	
		if (arg0.getSource() instanceof JButton) {
			int click=arg0.getClickCount();
			boolean popup=arg0.isControlDown() || arg0.getButton()==MouseEvent.BUTTON3;
			InterfaceExternalTool<ImageType> clickedTool = buttonToolPairs.get(arg0.getSource());
			if (clickedTool.isMenuOnlyTool()) {
				showToolMenu((JButton)arg0.getSource(), clickedTool );;
				return;
				}
			boolean clickAction=clickedTool.isActionTool();
			
			if (clickAction&&!popup) {clickedTool.performLoadAction(); return;}
			
			/**Any non-action tool will be set as the current tool. The pressed icon for
			 * the button will now become its normal icon. Does not occur if there is a double click*/
			if (click==1 && !clickAction) {
				JButton button = (JButton) arg0.getSource();
				
				selectTool(clickedTool, button);
			}
			
			/**Shows a menu*/
			if (popup&& !clickAction) {
				showToolMenu((JButton)arg0.getSource(), getCurrentTool() );
				return;}
			if (popup&& clickAction) {
				showToolMenu((JButton)arg0.getSource(), clickedTool);
				return;}
			
		}
		
	}

/**Called when the tool button is pressed in a way that chances the tool*/
	private void selectTool(InterfaceExternalTool<ImageType> clickedTool, JButton button) {
		setCurrentTool(clickedTool) ;
		for (AbstractExternalToolset<?> set : openToolsets)set.resetButtonIcon();
		currentToolButton=(AbstractButton) button;
		
		JButton jb=((JButton) button); jb.setIcon(
			jb.getPressedIcon()
			); 
		jb.setRolloverEnabled(false);
	}
	
	/**some tools have popup menus that appear when the tool is clicked*/
	public void showToolMenu(Component c, InterfaceExternalTool<ImageType> clickedTool ) {
		if (clickedTool.getPopupMenuItems()!=null) {controlClickDialog(c, clickedTool);} else
			clickedTool.controlClickDialog(c) ; 
	}
	
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	/**when the window is closed removes the window from the list of toolbars*/
	@Override
	public void windowClosed(WindowEvent arg0) {
		if (arg0.getWindow()==frame) openToolsets.remove(this);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {	}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	/**returns the maximum number of tools that appear in one row of the toolbar.
	  tools beyond that are added to the subsequent row*/
	public int getMaxGridx() {
		return maxGridx;
	}
	/**set the maximum number of tools that appear in one row of the toolbar
	 *  tools beyond that are added to the subsequent row*/
	public void setMaxGridx(int maxGridx) {
		this.maxGridx = maxGridx;
	}

}
