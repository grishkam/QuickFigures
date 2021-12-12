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
 * Date Modified: Jan 6, 2021
 * Version: 2021.2
 */
package standardDialog.strings;

import java.awt.event.ActionListener;

import javax.swing.JButton;

/**A special string panel that simply displays a button with text on it */
public class ButtonPanel extends  StringInputPanel{

	private JButton content=new JButton();
	

	public ButtonPanel(String labeln, JButton contend, ActionListener listentome) {
		super(labeln, contend.getText());
		this.content=contend;
		contend.addActionListener(listentome);
	}
	
	public ButtonPanel(String labeln, String contend, ActionListener l) {
		this(labeln, new JButton(contend), l);
	}
	
	/**sets the text that is displayed as information*/
	@Override
	public void setContentText(String contend) {
		getTextField().setText(contend);
	}

	
	private static final long serialVersionUID = 1L;


	@Override
	protected JButton getTextField() {
		return getTheButton();
	}

	/**return the JButton
	 * @return
	 */
	public JButton getTheButton() {
		if (content==null) {
			content=new JButton();
		}
		return content;
	}


}
