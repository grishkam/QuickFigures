/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package journalCriteria;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComboBox;

import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;

public class FormatOptionsDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<?> list;
	private JournalCriteria jc=new JournalCriteria();
	private JComboBox<?> output;
	private String[] fontOptions;

	public FormatOptionsDialog(ArrayList<?extends Object> list, JournalCriteria jc) {
		this.list=list;
		this.jc=jc;
		NumberInputPanel win = new NumberInputPanel("Minimum Stroke Width", jc.minimumStroke);
		add("minStroke", win);
		NumberInputPanel win2 = new NumberInputPanel("Minimum Font Size", jc.minimumFont);
		add("minFont", win2);
		add("Family", generateFamilyCombo());
		this.setModal(true);
		this.setWindowCentered(true);
	}
	
	public static void main(String[] args) {
		JournalCriteria jc1 = new JournalCriteria();
		new FormatOptionsDialog(new ArrayList<Object>(), jc1).showDialog();;
		
	}
	
	public void onOK() {
		jc.minimumStroke=(float) this.getNumber("minStroke");
		jc.minimumFont=(float) this.getNumber("minFont");
		jc.prefferedFontFamily=fontOptions[getChoiceIndex("Family")];
		
		if (list!=null &&list.size()>0) {
			for(Object o: list) {
				jc.ApplyCriteria(o);
			}
		}
		{
			
			
		};
	}
	
	public ChoiceInputPanel generateFamilyCombo() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		 String[] fonts = ge.getAvailableFontFamilyNames();
		 String[] fonts2 = new String[fonts.length+1];
		 this.fontOptions=fonts2;
		 fonts2[0]=""; for(int i=0; i<fonts.length; i++) {fonts2[i+1]=fonts[i];}
		 output=new JComboBox<String>(fonts2);
		 output.setSelectedIndex(Arrays.binarySearch(fonts2, jc.prefferedFontFamily));
	     ChoiceInputPanel cbp = new ChoiceInputPanel("Ideal Font", output);
		return cbp;
	}

}
