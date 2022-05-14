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
 * Date Modified: May 14, 2022
 * Version: 2022.1
 */
package standardDialog.strings;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;

import standardDialog.strings.InfoDisplayPanel.MouseLinkListener;

/**A special string panel that simply displays JLabel with information int than 
  a text field for user input*/
public class InfoDisplayPanel extends  StringInputPanel{

	


	private JLabel content=new JLabel();
	
	
	/**creates a panel with the given information*/
	public InfoDisplayPanel(String labeln, String theTextContent) {
		super(labeln, theTextContent);
		setContentText(theTextContent);
	}
	
	
	public InfoDisplayPanel(String labeln, Rectangle contend) {
		super(labeln, contend.toString());
		setToDimension(contend);
	}
	
	
	/**sets the text that is displayed as information*/
	public void setContentText(String contend) {
		getTextField().setText(contend);
		if(contend.contains("https")) {
			getTextField().addMouseListener(new MouseLinkListener());
			getTextField().setForeground(Color.blue);
		}
	}

	
	private static final long serialVersionUID = 1L;


	/**returns a JLabel text instead of a user editable text field*/
	protected JLabel getTextField() {
		if (content==null) {
			content=new JLabel();
		}
		return content;
	}

	/**
	 
	 * 
	 */
public class MouseLinkListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		try {
			Desktop.getDesktop().browse(new URI(content.getText()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
}
