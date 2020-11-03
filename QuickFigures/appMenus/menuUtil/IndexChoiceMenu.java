package menuUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import logging.IssueLog;

public class IndexChoiceMenu<Type> extends JMenu implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int startind=1;
	int endind=2;
	String name="";
	Type object;
	HashMap<JMenuItem, Integer> map=new HashMap<JMenuItem, Integer>();
	
	public IndexChoiceMenu(Type o, String name, int st, int en) {
		super(name);
		this.object=o;
		this.startind=st;
				this.endind=en;
				try {
					generateJMenuItems();
				} catch (Throwable e) {
					IssueLog.log(e);
				}
	}
	
	public void generateJMenuItems() {
		int s=startind;
		while (s<=endind) {
			JMenuItem jm = new JMenuItem(""+s);
			jm.addActionListener(this);
			map.put(jm, s);
			this.add(jm);
			s++;
		}
	}
	
	
	public void performAction(Type t, int i) {
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() instanceof JMenuItem) {
			JMenuItem s=(JMenuItem) arg0.getSource() ;
			int i=map.get(s);
			performAction(object, i);	
		}
		
	}
	
	

}
