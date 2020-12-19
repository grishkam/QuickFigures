/**
 * Author: Greg Mazo
 * Date Modified: Dec 11, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package messages;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JLabel;

import standardDialog.GriddedPanel;
import standardDialog.OnGridLayout;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputPanel;

/**
 A class related to showing messages to the user. The user will have a 'dont show again' options for some messages
 */
public class ShowMessage {
	
	/**A hashmap of message settings objects, helps store information*/
	static HashMap<String, MessageSettings> settings=new HashMap<String, MessageSettings>();
	
	public static Boolean showMessages(String... texts) {
		StandardDialog d = new StandardDialog();
		d.setModal(true);
		d.setLayout(new GridBagLayout());
		int y=1;
		for(String s: texts) {
			GridBagConstraints c = new GridBagConstraints();
			c.anchor=GridBagConstraints.NORTHWEST;
			c.gridy=y;
			y++;
			d.add(new JLabel(s), c);
			}
			
		d.removeOptionsTab();
		d.setHideCancel(true);
		d.setWindowCentered(true);
		d.showDialog();
		
		if(d.wasOKed())
			return true;
		if(d.wasCanceled())
			return false;
		
		return null;
	}
	
	public static void showMessages(Iterable<String> string) {
		StandardDialog d = new StandardDialog();
		d.setModal(true);
		d.setLayout(new GridBagLayout());
		int y=1;
		for(String s: string) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridy=y; y++;
			d.add(new JLabel(s), c);
			}
		d.removeOptionsTab();
		d.setHideCancel(true);
		d.setWindowCentered(true);
		d.showDialog();
	}
	
	
	/**shows a message with a 'dont show again checkbox'*/
	public static boolean showOptionalMessage(String title, boolean oneTime, Set<String> st) {
		String[] ar = new String[st.size()];
		 st.toArray(ar);
		return showOptionalMessage(title, oneTime,ar);
	}
	
	/**shows a message with a 'dont show again checkbox'*/
	public static boolean showOptionalMessage(String title, boolean oneTime, String... st) {
		MessageSettings set = settings.get(title);
		if(set==null) {
			set=new MessageSettings();
			settings.put(title, set);
		}
		
		if(set.dontShowAnymore) return true;
		
		if (oneTime) set.dontShowAnymore=true;
		
		MessageDialog m = new MessageDialog(set);
		m.setTitle(title);
		
		m.addMessage(st);
		m.showDialog();
		if (m.wasOKed()) return true;
		return false;
		
	}
	
	static class MessageDialog extends StandardDialog {

		/**
		 * 
		 */
		private static final String DO_NOT_SHOW_AGAIN = "wontshowAgain";
		private MessageSettings messageSettings;

		/**
		 * @param object
		 */
		public MessageDialog(MessageSettings object) {
			messageSettings=object;
			this.setModal(true);
			this.setWindowCentered(true);
			
			if (messageSettings==null)
				return;
				}
		
		public void addMessage(String... text) {
			GridBagConstraints gc = new GridBagConstraints();
			
			
			gc.insets=OnGridLayout.lastInsets;
			gc.gridx=super.gx;
			gc.gridy=super.gridPositionY;
			gc.gridwidth=3;
			gc.gridheight=1;
			gc.anchor = GridBagConstraints.WEST;
			GriddedPanel mainPanel = super.getMainPanel();
			for(String t: text) {
				super.gridPositionY++;
				
				mainPanel.add(new JLabel(t), gc);
				super.gridPositionY++;
				gc.gridy=super.gridPositionY;
			}
			super.getMainPanel().moveGrid(0, 10);
		
			this.add(DO_NOT_SHOW_AGAIN, new BooleanInputPanel("Dont show this again", messageSettings.dontShowAnymore));
			
			
		}
		
		
		
		/**what action to take when the ok button is pressed*/
		protected void onOK() {
			messageSettings.dontShowAnymore=this.getBoolean(DO_NOT_SHOW_AGAIN);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}
	
	

}
