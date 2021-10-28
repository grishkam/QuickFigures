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
 * Version: 2021.2
 */
package standardDialog.fonts;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import standardDialog.InputPanel;
import standardDialog.OnGridLayout;
import standardDialog.numbers.NumericTextField;

/**A dialog component that allows the user to change a Font*/
public class FontChooser extends InputPanel implements MouseMotionListener, ItemListener, KeyListener,OnGridLayout  {
	
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	transient ArrayList<FontInputListener> listeners=new ArrayList<FontInputListener>();

	Font font=new Font("Arial", Font.BOLD, 12);;
	GridBagLayout gb=new GridBagLayout();
	GridBagConstraints gc=new GridBagConstraints();
	
	{this.setLayout(new FlowLayout());}
	
	
	
	/**lists of fonts that will appear in the combo box*/

	public static final int LIMITED_FONT_LIST=0,  SHORT_FONT_LIST=2, LONG_FONT_LIST=3, FULL_FONT_LIST=1;
	public int useFullfontList=0;
	private static Vector<String> limitedfontList;
	private static Vector<String> fullfontList;
	private  Vector<String> fontList;
	
	
	public static String[] fontStyles= {"Plain", "Bold", "Italic", "Bold+Italic"}; 
	
	public static String[] excludeFonts= {"Bookshelf Symbol 7","MS Outlook","EmojiOne Color", "MS Reference Specialty", "OpenSymbol", 
			"Marlett", "Symbol", "MT Extra", "Webdings", "Miriam CLM", "Alef", "Myanmar Text", "Mongolian Baiti", "Javanese Text", "Sylfaen", 
			"Scheherazade", "Algerian", "Agency FB", "Amiri", "Rubik", "Gigi", "Mistral", "Vivaldi", 
			"Bauhaus 93", "Ravie", "Chiller", "Trebuchet MS", "Forte", "Haettenschweiler"}; 
	String[] excludeFontGroups = new String[] {	"Noto","Wingdings","Yu ","MDL2","Quran","Arabic"	,"Kacst"
				
			,"Source ","Sitka ","Segoe"	,"JhengHei","HP Simplified","Gill Sans","Eras ","Dubai","DejaVu "
				
			,"Franklin Gothic","Copperplate Gothic","Bodoni MT","Berlin Sans FB"
				
			,"Leelawadee UI","Malgun Gothic","YaHei UI","Niagara","Nirmala"
				
			,"Tw Cen","MingLiU","David","Frank R","Linux", "Rage", "Goudy"
				
			,"Microsoft ","MS "	,"Miriam"	,"Symbol"," ITC"
				
			,"Dialog","SimSun","Gentium","French","Script","Rockwell","Handwriting"
				
			,"Extra", " FB", " MT", "Lucida", "Extended", " Gothic", "Kufi", "Stencil", "Harlow", "Candara", "Georgia"};
	
	JLabel label=new JLabel("Font ");
	
	JComboBox<?> styleChooser=generateStyleCombo();
	
	JComboBox<?> famChooser2=null;
	NumericTextField sizeField=new NumericTextField(12); {sizeField.addKeyListener(this);}
	FontSizeSelector fontdisplay=new FontSizeSelector();
	private String key;
	private Font originalValue;
	
	
	
	
	 {fontdisplay.addMouseMotionListener(this);}
	{
		this.setLayout(gb);
		}
	
	public void setUIFontSize(float f) {
		Font UIfont = label.getFont().deriveFont(f);
		getFontFamilyComboBox().setFont(UIfont);
		label.setFont(UIfont);
		styleChooser.setFont(UIfont);
		sizeField.setFont(UIfont);
	}

	/**returns the font family combo box
	 * @return
	 */
	public JComboBox<?> getFontFamilyComboBox() {
		if(famChooser2==null) {
			famChooser2=generateFamilyCombo();
		}
		return famChooser2;
	}
	

	
	public void placeItems(Container jp, int x0, int y0) {
		 gc=new GridBagConstraints();
		gc.gridx=x0;
		gc.gridy=y0;
		
		gc.insets=firstInsets;
		gc.anchor=GridBagConstraints.EAST;
		jp.add(label, gc);
		 gc=new GridBagConstraints();
		 gc.gridx=x0;
		gc.insets=middleInsets;
		gc.gridwidth=2;
		gc.gridy=y0;
		gc.gridx++;
		gc.anchor=GridBagConstraints.WEST;
		
		jp.add(createFontPanel(), gc);
		
		gc.gridx=3+x0;
		
		gc.gridy=0+y0;
		gc.gridheight=2;
		jp.add(fontdisplay,gc);
		
		
	}

	public JPanel createFontPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		 GridBagConstraints gc2 = new GridBagConstraints();
		 gc2.gridx=1; gc2.gridy=1;
		 panel.add(getFontFamilyComboBox(), gc2);
		 gc2.gridy++; 
		 gc2.anchor=GridBagConstraints.WEST;
		 panel.add(createStyleSizePanel() , gc2);
		 return panel;
	}
	
	JPanel createStyleSizePanel() {
		JPanel panel = new JPanel();
		panel.add(styleChooser);
		panel.add(sizeField);
		return panel;
	}
	
	public FontChooser(Font f, int fullList) {
		useFullfontList=fullList;
		this.originalValue=f;
		setSelectedFont(f);
		placeItems(this,0,0);
		
	}
	
	public void revert() {
		setSelectedFont(originalValue);
	}
	
	/**sets the font*/
	public void setSelectedFont(Font f) {
		
		styleChooser.setSelectedIndex(f.getStyle());
		
		String fontName=f.getName();
	
		
		JComboBox<?> box = getFontFamilyComboBox();
		long time = System.currentTimeMillis();
		box.setSelectedItem(fontName);
		time= System.currentTimeMillis()-time;
		
		setFieldAndDisplayFont(f);
	}
	
	/**sets the font that is shown in the field*/
	private void setFieldAndDisplayFont(Font f) {
		sizeField.setNumber(f.getSize());
		fontdisplay.setFont(f);
	}
	
	
	/**generates the font style combo box*/
	public JComboBox<?> generateStyleCombo() {
		JComboBox<?> output=new JComboBox<String>(fontStyles);
		output.addItemListener(this);
		output.setRenderer(new FontStyleCellRenerer());
		return output;
	}
	
	/**generates the font family combo box. user will see the appearance of each font family as well as the name
	 * @param name */
	public JComboBox<String> generateFamilyCombo() {
		Vector<String> pfont = prepareFontList();
	
		JComboBox<String> output=new JComboBox<String>(pfont);
		output.setEditable(true);
		
		output.addItemListener(this);
		output.setRenderer(new FontFamilyCellRenerer());
		output.getEditor().getEditorComponent().addKeyListener(new FontSearcher(output, this));
		return output;
	}

	/**returns a list of fonts
	 * @return
	 */
	protected Vector<String> prepareFontList() {
		
		if(fontList!=null)
			return fontList;
		
		String[] possibleFonts = getPossibleFonts();
		limitedfontList = new Vector<String>();
		fullfontList = new Vector<String>();
		
		/**fonts that do not appear properly on my system are excluded */
		for(String p: possibleFonts)
			{ 
			fullfontList.add(p);
			boolean useFont=true;
					for(String p2: excludeFonts)
							if(p.equals(p2)) {useFont=false;break;}
					if (useFont)
						for(String p2: excludeFontGroups)
						if(p.contains(p2)) {useFont=false;break;}
					if(useFont)	{
						limitedfontList.add(p);
						//System.out.println(p+",");
						}
					
					}
		
		resetFontList();
		
		return fontList;
	}

	/**
	 * 
	 */
	protected void resetFontList() {
		if(useFullfontList==FULL_FONT_LIST)	
				fontList=fullfontList;	
			else  
				fontList= limitedfontList;
	}
	
	public void setUseFullFontList(int b) {
		this.useFullfontList=b;
		this.resetFontList();
		
	}

	/**returns a list of possible fonts
	 * @return
	 */
	protected String[] getPossibleFonts() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		 String[] possibleFonts = ge.getAvailableFontFamilyNames();
		
		return possibleFonts;
	}
	
	public Font getSelectedFont() {
		font=new Font(getFontFamilyComboBox().getSelectedItem()+"", this.styleChooser.getSelectedIndex(),(int) sizeField.getNumberFromField() );
		return  font;
	}
	

	private void updateDisplayFromComboBox() {
		fontdisplay.setFont(getSelectedFont());;
		fontdisplay.repaint();
	}

	public static void main(String[] args) {
		JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		ff.add(new JButton("button"));
		FontChooser sb = new FontChooser(new Font("Arial", Font.BOLD, 12), FontChooser.LIMITED_FONT_LIST);
		
		ff.add(sb);
		ff.pack();
		
		ff.setVisible(true);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (arg0.getSource()==fontdisplay) {
			sizeField.setNumber(fontdisplay.getFontSize());
			FontInputEvent fi = (new FontInputEvent(this, (Component)arg0.getSource(), this.getSelectedFont()));
			fi.setKey(key);
			notifyListeners(fi);
		}
		
		
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
		 updateDisplayFromComboBox();
		 if (arg0.getSource() ==sizeField ) {
		 		updateDisplayFromComboBox();
		 		notifyListeners(new FontInputEvent(this, (Component)arg0.getSource(), this.getSelectedFont()));
	 	}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		
		 if (arg0.getSource() instanceof JComboBox) {
			 		updateDisplayFromComboBox();
			 		notifyListeners(new FontInputEvent(this, (Component)arg0.getSource(), this.getSelectedFont()));
		 	}
	}
	
	public void notifyListeners(FontInputEvent ni) {
		for(FontInputListener l :listeners) {
			if(l==null) continue;
			l.FontChanged(ni);
		}
	}
	
	public void addFontInputListener(FontInputListener ni) {
		listeners.add(ni);
	}
	public void removeFontInputListener(FontInputListener ni) {
		listeners.remove(ni);
	}
	public ArrayList<FontInputListener> getFontInputListeners() {
		return listeners;
	}

	@Override
	public int gridHeight() {
		return 2;
	}

	@Override
	public int gridWidth() {
		return 3;
	}
	
	/**A renderer that shows each font family by name and appearance*/
	public class FontFamilyCellRenerer extends BasicComboBoxRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public  Component	getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
			Font font=new Font(value.toString(), out.getFont().getStyle(), out.getFont().getSize());
			out.setFont(font);
			return out;
				}
	}
	
	/**a renderer that renders the bold option as a bold font, plain as plain and so on*/
	public class FontStyleCellRenerer extends BasicComboBoxRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public  Component	getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
			Font font=new Font(out.getFont().getFamily(), index, out.getFont().getSize());
			out.setFont(font);
			return out;
				}
	}
	public void setKey(String key) {
		this.key=key;
		
	}
	
	
	/**returns true if that font is an option*/
	public boolean isFontFamilyPossible(String st) {
		for(String font: getPossibleFonts()) {
			if(font.equals(st))
				return true;
		}	
		
		return false;
	}
	
	/**returns true if that font is an option*/
	public ArrayList<String> getSimilarNames(String st) {
		ArrayList<String> output=new ArrayList<String>();
		for(String font: getPossibleFonts()) {
			if(font.toLowerCase().startsWith(st.toLowerCase()))
				output.add(font);
		}	
		
		return output;
	}
	
}
