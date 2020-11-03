package uiForAnimations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import applicationAdapters.DisplayedImageWrapper;
import selectedItemMenus.LayerSelector;

public class TimeLineMenu extends JMenu implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<TimeLineOperator> items=new ArrayList<TimeLineOperator>();
	private LayerSelector ls;
	private DisplayedImageWrapper diw;
	HashMap<String, TimeLineOperator> map=new HashMap<String, TimeLineOperator>();
	private TimeLineDialog dialog;
	
	
	
	public TimeLineMenu(DisplayedImageWrapper diw, LayerSelector ls, TimeLineDialog dialog) {
		
		super("Keyframes");
		
		this.ls=ls;
		this.diw=diw;
		this.dialog=dialog;
		setup();
	
	}
	
	void setup() {
			this.setText("Key Frames"); this.setName("Key Frames");
		addOperation(new KeyFrameAssign(false));
		addOperation(new KeyFrameAssign(true));
		addOperation(new KeyFrameAssignRemove(false));
		addOperation(new KeyFrameVanish(false));
		addOperation(new KeyFrameProgress(1));
		addOperation(new KeyFrameProgress(0));
		addOperation(new KeyFrameMove());
		addOperation(new KeyFrameAssignRemove(true));
		addOperation(new KeyFrameOptionsDialog(0));
		addOperation(new KeyFrameOptionsDialog(1));
		addOperation(new KeyFrameOptionsDialog(2));
	}
	
	boolean addOperation(TimeLineOperator mso) {
		
		
		
		JMenuItem menuitem = new JMenuItem(mso.getMenuCommand());
		menuitem.setActionCommand(mso.getMenuCommand());
		map.put(mso.getMenuCommand(), mso);
		menuitem.addActionListener(this);
		menuitem.setIcon(mso.getIcon());
		this.add(menuitem);
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		TimeLineOperator mso = map.get(arg0.getActionCommand());
		mso.setSelector(ls);
		mso.setSelection(ls.getSelecteditems());
		mso.setDisplay(diw);
		mso.setUI(dialog);
		mso.run();;
		diw.updateDisplay();
		dialog.repaint();
	}

}
