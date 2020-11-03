package externalToolBar;


import java.awt.Component;
import java.awt.Dimension;
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


public class AbstractExternalToolset<ImageType> implements MouseListener, WindowListener {
	
	 Vector<ToolChangeListener<ImageType>> listeners=new Vector<ToolChangeListener<ImageType>>();
	 public void addToolChangeListener(ToolChangeListener<ImageType> lis) {
		 listeners.add(lis);
	 }
	 
	/**Keeps track of all toolsets in the app*/
	public static Vector<AbstractExternalToolset<?>> openToolsets= new Vector<AbstractExternalToolset<?>>();
	
	HashMap<AbstractButton, InterfaceExternalTool<ImageType>> buttonToolPairs=new HashMap<AbstractButton, InterfaceExternalTool<ImageType>>();
	AbstractButton currentToolButton;
	
	
	protected Vector<InterfaceExternalTool<ImageType>> tools= new Vector<InterfaceExternalTool<ImageType>> ();
	protected Vector<AbstractButton> buttons= new Vector<AbstractButton>();
	public InterfaceExternalTool<ImageType> currentTool;
	
	int Xinnitial=0;
	private int Yinnitial=0;
	GridBagConstraints toolBarBridConstraints=new GridBagConstraints();

	protected int maxGridx=11;
	
	private JFrame frame=new JFrame(); {
		frame.setLayout(new GridBagLayout()); frame.addWindowListener(this);openToolsets.add(this);
		
	}
	
	protected JToolBar toolbar=new JToolBar();

	public boolean drawMenuIndicator=true;

	 { innitializeToolbar();}
	
	
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
	
	
	public JFrame getframe() {
		return frame;
	}
	
	
	
	public void setCurrentTool(InterfaceExternalTool<ImageType> currentTool) {
		if(currentTool==null) return;
		if (this.currentTool!=null) {
			 this.currentTool.onToolChange(false);
		 }
		this.currentTool=currentTool;
		this.currentTool.onToolChange(true);
		for(ToolChangeListener<ImageType> tcl: listeners) {
			if (tcl!=null) tcl.ToolChanged( getCurrentTool() );
		}
	}
	
	public static InterfaceExternalTool<?> setCurrentTool(String st) {
		for (AbstractExternalToolset<?> toolset :openToolsets ) {
			InterfaceExternalTool<?> n = toolset.selectToolWithName(st);
			if(n!=null) return n;
			}
		return null;
		}
	
/**Finds a tool with the given name
 * @return */
public InterfaceExternalTool<ImageType> selectToolWithName(String st) {
	for(AbstractButton button: buttonToolPairs.keySet()) {
		InterfaceExternalTool<ImageType> tool = buttonToolPairs.get(button);
		if(tool.getToolName().equals(st)) {
			selectTool( tool, (JButton)button);
			return tool;
		}
	}
	return null;
}
	
	public InterfaceExternalTool<ImageType>  getCurrentTool() {
		return currentTool;
	}
	
	
	
	
	 protected void gridToNextLine() {
		toolBarBridConstraints.gridx=Xinnitial;
		toolBarBridConstraints.gridy=toolBarBridConstraints.gridy+1;
	}
	
	public void controlClickDialog(Component c, InterfaceExternalTool<ImageType> clickedTool) {
		SmartPopupJMenu pup = new SmartPopupJMenu();
		for(JMenuItem stackPanel: clickedTool.getPopupMenuItems())	pup.add(stackPanel); 
		pup.show(c, 0, 0+c.getHeight());
	}
	
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
		
		//if (i==null) IssueLog.log("cannot find image icon for tool");
		//try{i=new FileMover().localFileAsImage("icons/ExperimantalIcon.jpg");} catch (Throwable t) {}
		stripButton(jb);
		
		if (i!=null) {jb.setIcon(i);
		jb.setText(null);
		if (tool.getToolPressedImageIcon()!=null)
		jb.setPressedIcon(tool.getToolPressedImageIcon());}
		if (tool.getRollOverIcon()!=null) {jb.setRolloverIcon(tool.getRollOverIcon()); jb.setRolloverEnabled(true);}
		jb.setToolTipText(tool.getToolTip());
		
	
		
		/**adds the button and moves the grid constraints*/
		toolbar.add(jb, toolBarBridConstraints); 
		moveGridConstraints();
		
		} catch (Exception ex) {IssueLog.log(ex);}
		
		
	}
	Icon getIconForTool(InterfaceExternalTool<ImageType> tool) {
		Icon i=tool.getToolImageIcon();
		if (i==null) {
			i=new ImageIcon(new BufferedImage(25, 25, BufferedImage.TYPE_INT_RGB));
		
		}
		
		/**If the item has a menu, draws a little triangle
		if (tool.getPopupMenuItems()!=null&&tool.getPopupMenuItems().size()>0 &&drawMenuIndicator) {
			i=new CompoundIcon(drawMenuIndicator, i);
		}*/
		
		return i;
	}
	
	
	
	public static void stripButton(JButton jb) {
		jb.setMargin(new Insets(-2, -3, -2, -4));
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setIconTextGap(0); 
	}
	
	public void resetButtonIcons() {
		resetOwnButtonIcons() ;
	}
	
	public void resetOwnButtonIcons() {
		for (AbstractButton jb: buttons) {
			resetButtonIcon(jb);
			//jb.setRolloverIcon(new ImageIcon(tool1.getRollOverIcon()));
		}
	}
	
	public void resetButtonIcon() {
		resetButtonIcon(currentToolButton) ;
	}
	public void resetButtonIcon(AbstractButton jb) {
		if (jb==null) return;
		InterfaceExternalTool<ImageType> tool1=buttonToolPairs.get(jb);
		if (tool1==null) return;
			Icon ii=getIconForTool(tool1);
			jb.setIcon(ii);
			jb.setRolloverEnabled(true);
	}
	
	
	//public int maxGridx=1;
	
	void moveGridConstraints() {
		toolBarBridConstraints.gridx=toolBarBridConstraints.gridx+1;
		if (toolBarBridConstraints.gridx>=getMaxGridx()) {
			gridToNextLine();
		}
		
	}
	
	public void showFrame() {frame.pack(); frame.setVisible(true);frame.setResizable(false);
	}
	
	private void addDragAndDrop() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		int click=arg0.getClickCount();
		if (click==2&&getCurrentTool()!=null) getCurrentTool() .showOptionsDialog();
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
           
	
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
		//resetButtonIcons();
		JButton jb=((JButton) button); jb.setIcon(
			jb.getPressedIcon()
			); 
		jb.setRolloverEnabled(false);
	}
	
	public void showToolMenu(Component c, InterfaceExternalTool<ImageType> clickedTool ) {
		if (clickedTool.getPopupMenuItems()!=null) {controlClickDialog(c, clickedTool);} else
			clickedTool.controlClickDialog(c) ; 
	}
	
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getWindow()==frame) openToolsets.remove(this);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {	}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	public int getMaxGridx() {
		return maxGridx;
	}

	public void setMaxGridx(int maxGridx) {
		this.maxGridx = maxGridx;
	}

}
