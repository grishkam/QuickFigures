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
 * Date Modified: May 13, 2023
 * Version: 2023.2
 */
package messages;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import standardDialog.GriddedPanel;
import standardDialog.OnGridLayout;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputPanel;
import storedValueDialog.StoredValueDilaog;

/**
 A class created to showing messages to the user. The user will have a 'dont show again' options for some messages
 */
public class ShowMessage {
	
	/**A hashmap of message settings objects, helps store information*/
	static HashMap<String, MessageSettings> settings=new HashMap<String, MessageSettings>();
	
	public static StandardDialog showNonModel(String... st) {
		StandardDialog d = new StandardDialog();
		d.setModal(false);
		d.setWindowCentered(true);
		d.setTitle(st[0]);
		d.setLayout(new GridBagLayout());
		int y=1;
		for(int i=1; i<st.length; i++) {
			GridBagConstraints c = new GridBagConstraints();
			c.anchor=GridBagConstraints.NORTHWEST;
			c.gridy=y;
			y++;
			d.add(new JLabel(st[i]), c);
		} 
		d.removeOptionsTab();
		d.setHideCancel(true);
		d.setWindowCentered(true);
		d.showDialog();
		
		return d;
	}
	
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
	public static boolean showOptionalMessage(String title, boolean oneTime, ArrayList<String> st) {
		String[] ar = new String[st.size()];
		 st.toArray(ar);
		return showOptionalMessage(title, oneTime,ar);
	}
	
	/**Shows a message with a 'dont show again checkbox' */
	public static boolean showOptionalMessage(String title) {
		return showOptionalMessage(title, false, title);
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
		
		MessageDialog m = new MessageDialog(set, false);
		m.setTitle(title);
		
		m.addMessage(st);
		m.showDialog();
		set.lastOk=m.wasOKed();
		if (m.wasOKed()) return true;
		return false;
		
	}
	
	
	
	/**shows a message with a 'dont show again checkbox'*/
	public static boolean showOptionalMessageWithOptionsWORK(String title, StoredValueDilaog optionHolder, boolean mandatory, boolean onetime, String... st) {
		MessageSettings set = settings.get(title);
		if(set==null) {
			set=new MessageSettings();
			settings.put(title, set);
		}
		
		if(set.dontShowAnymore) 
			return set.lastOk;
		if(set.dontShowForAWhile>0) {
			set.dontShowForAWhile--;
			return set.lastOk;
		}
		
		
		MessageDialog m = new MessageDialog(set, mandatory);
		m.setTitle(title);
		

		//m.getOptionDisplayTabs().addTab("options",);
		//m.add( optionHolder.removeOptionsTab().getComponent(0), m.gc);
		if(optionHolder!=null)
			m.addSubordinateDialogsAsTabs(optionHolder.getTitle(), optionHolder);
		
		m.addMessage(st);
		
	
		m.showDialog();
		set.lastOk=m.wasOKed();
		
		if (m.wasOKed()) 
			return true;
		
		
		return false;
		
	}
	
	/**shows a message with a 'dont show again checkbox'*/
	public static boolean showOptionalYesOrNo(String title, boolean onetime, String... st) {
		return showOptionalYesOrNo(title, null, false, onetime, st);
	}

	/**shows a message with a 'dont show again checkbox'*/
	public static boolean showOptionalYesOrNo(String title, StoredValueDilaog optionHolder, boolean mandatory, boolean onetime, String... st) {
		MessageSettings set = settings.get(title);
		if(set==null) {
			set=new MessageSettings();
			settings.put(title, set);
		}
		
		if(set.dontShowAnymore) 
			return set.lastOk;
		if(set.dontShowForAWhile>0) {
			set.dontShowForAWhile--;
			return set.lastOk;
		}
		
		if(onetime) {
			set.dontShowAnymore=true;
		}
		MessageDialog m = new MessageDialog(set, mandatory);
		m.makeYesNoCancel();
		
		m.setTitle(title);
		

		if(optionHolder!=null)
			m.addSubordinateDialogsAsTabs(optionHolder.getTitle(), optionHolder);
		
		m.addMessage(st);
		
	
		m.showDialog();
		set.lastOk=m.wasOKed();
		
		if (m.wasOKed()) 
			return true;
		
		
		return false;
		
	}
	
	static class MessageDialog extends StandardDialog {

		/**
		 * 
		 */
		private static final String DO_NOT_SHOW_AGAIN = "wontshowAgain";
		private MessageSettings messageSettings;
		
		GridBagConstraints gc = new GridBagConstraints();
		private boolean mandatory;

		/**
		 * @param object
		 */
		public MessageDialog(MessageSettings object, boolean mandatory) {
			messageSettings=object;
			this.setModal(true);
			this.setWindowCentered(true);
			this.mandatory=mandatory;
			if (messageSettings==null)
				return;
				}
		
		public void addItem(Component c) {
			super.gridPositionY+=20;
			gc.gridy=super.gridPositionY;
			mainPanel.add(c, gc);
		}
		
		public void addMessage(String... text) {
			
			
			
			gc.insets=OnGridLayout.lastInsets;
			gc.gridx=super.gx;
			gc.gridy=super.gridPositionY;
			gc.gridwidth=3;
			gc.gridheight=1;
			gc.anchor = GridBagConstraints.WEST;
			GriddedPanel mainPanel = super.getCurrentUsePanel();
			for(String t: text) {
				super.gridPositionY++;
				
				mainPanel.add(new JLabel(t), gc);
				super.gridPositionY++;
				gc.gridy=super.gridPositionY;
			}
			super.getCurrentUsePanel().moveGrid(0, 10);
		
			if(!mandatory)
				this.add(DO_NOT_SHOW_AGAIN, new BooleanInputPanel("Dont show this again", messageSettings.dontShowAnymore));
			
			
		}
		
		
		
		
		
		/**what action to take when the ok button is pressed*/
		protected void onOK() {
			
			if(!mandatory)
				messageSettings.dontShowAnymore=this.getBoolean(DO_NOT_SHOW_AGAIN);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}
	
	/**Displays a modal dialog that presents the user with a question. returns the answer */
	public static boolean yesOrNo(String s) {
			int i=JOptionPane.showConfirmDialog(null, 	
					s,
				    "",
				    JOptionPane.YES_NO_OPTION
				    );
			if (i==JOptionPane.YES_OPTION) {
				return true;
				
			}
		
		return false;
	
}

	/**
	 If a user has chosen the 'dont show again option' for a message this undoes that effect, the message will be shown
	 */
	public static void resetOptionalMessage(String st) {
		settings.remove(st);
		
	}

}
