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
 * Date Modified: Jan 4, 2021
 * Version: 2023.1
 */
package messages;

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
	
	/***/
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
			GriddedPanel mainPanel = super.getCurrentUsePanel();
			for(String t: text) {
				super.gridPositionY++;
				
				mainPanel.add(new JLabel(t), gc);
				super.gridPositionY++;
				gc.gridy=super.gridPositionY;
			}
			super.getCurrentUsePanel().moveGrid(0, 10);
		
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

}
