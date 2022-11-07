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
 * Date Modified: April 6, 2021
 * Date Created: April 6, 2021
 * Version: 2022.2
 */
package standardDialog.fonts;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JComboBox;


/**
 this class functions performs to help navigate and autocomplete the font family combo box.
 will eventually modify to work with any combo box. When user types the words "time..."
 orthe start of any other fontname, the combobox switches to "Times New Roman".
 When the user presses enter, the value will become the font that starts with what has been typed.
 Wrote Quickly but works
 */
public class FontSearcher implements KeyListener {

	private JComboBox<String> comboBox;
	private FontChooser chooser;
	private String recentName;

	/**
	 * @param output
	 */
	public FontSearcher(JComboBox<String> output, FontChooser c) {
		this.comboBox=output;
		this.chooser=c;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		String currentText = ""+comboBox.getEditor().getItem();
		
		ArrayList<String> names = chooser.getSimilarNames(currentText);
		
		if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE)return;
		boolean enter = e.getKeyCode()==KeyEvent.VK_ENTER;
		
		
		if(names.size()>=1) {
			
			/**if a user types in 2 letters, sets the comboox to the first font that starts with them*/
			if(currentText.length()>1)
				comboBox.setSelectedItem(names.get(0));
			
			/**if only one font matches, this autocompletes*/
				if(names.size()==1)
					{comboBox.getEditor().setItem(names.get(0));}
				else comboBox.getEditor().setItem(currentText);
				
			recentName=names.get(0);//stores the recent name
			
		}
			
		/**makes sure the combo box popup is visible if the user is typing*/
		if(!enter)
			comboBox.showPopup();
		else comboBox.hidePopup();
		
		/**if user has typed in an invalid font and hit enter, corrects the issue*/
		if(enter) {
			boolean possibleFont = chooser.isFontFamilyPossible(comboBox.getSelectedItem()+"");
			if(!possibleFont)
				comboBox.setSelectedItem(recentName);
			
			possibleFont = chooser.isFontFamilyPossible(currentText+"");
			if(!possibleFont)
				comboBox.getEditor().setItem(comboBox.getSelectedItem());
			
		}
		
	}

}
