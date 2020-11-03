package standardDialog;

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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;


public class FontChooser extends JPanel implements MouseMotionListener, ItemListener, KeyListener,OnGridLayout  {
	
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	transient ArrayList<FontInputListener> listeners=new ArrayList<FontInputListener>();

	Font font=new Font("Arial", Font.BOLD, 12);;
	GridBagLayout gb=new GridBagLayout();
	GridBagConstraints gc=new GridBagConstraints();
	
	{this.setLayout(new FlowLayout());}
	public static String[] fontStyles= {"Plain", "Bold", "Italic", "Bold+Italic"}; 
	
	JLabel label=new JLabel("Font ");
	JComboBox styleChooser=generateStyleCombo();
	JComboBox famChooser=generateFamilyCombo();
	NumericTextField sizeField=new NumericTextField(12); {sizeField.addKeyListener(this);}
	FontSizeSelector fontdisplay=new FontSizeSelector();
	private String key;
	private Font originalValue; {fontdisplay.addMouseMotionListener(this);}
	{
		this.setLayout(gb);
		placeItems(this,0,0);}
	
	public void setUIFontSize(float f) {
		Font UIfont = label.getFont().deriveFont(f);
		famChooser.setFont(UIfont);
		label.setFont(UIfont);
		styleChooser.setFont(UIfont);
		sizeField.setFont(UIfont);
	}
	
	public void placeItems(Container jp, int x0, int y0) {
		 gc=new GridBagConstraints();
		gc.gridx=x0;
		gc.gridy=y0;
		
		//jp.setLayout(gb);
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
		
		//return jp;
	}

	public JPanel createFontPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		 GridBagConstraints gc2 = new GridBagConstraints();
		 gc2.gridx=1; gc2.gridy=1;
		 panel.add(famChooser, gc2);
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
	
	public FontChooser(Font f) {
		setSelectedFont(f);
		this.originalValue=f;
		
	}
	
	public void revert() {
		setSelectedFont(originalValue);
	}
	
	public void setSelectedFont(Font f) {
		styleChooser.setSelectedIndex(f.getStyle());
		famChooser.setSelectedItem(f.getName());
		setFieldAndDisplayFont(f);
	}
	
	private void setFieldAndDisplayFont(Font f) {
		sizeField.setNumber(f.getSize());
		fontdisplay.setFont(f);
	}
	
	
	
	public JComboBox generateStyleCombo() {
		JComboBox output=new JComboBox(fontStyles);
		output.addItemListener(this);
		output.setRenderer(new styleCellRenerer());
		return output;
	}
	public JComboBox<String> generateFamilyCombo() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		 String[] fonts = ge.getAvailableFontFamilyNames();
		JComboBox<String> output=new JComboBox<String>(fonts);
		output.addItemListener(this);
		output.setRenderer(new fontCellRenerer());
		return output;
	}
	
	public Font getSelectedFont() {
		font=new Font(famChooser.getSelectedItem()+"", this.styleChooser.getSelectedIndex(),(int) sizeField.getNumberFromField() );
		return  font;
	}
	
	public JComboBox getFamChoser() {
		return famChooser;
	}
	
	private void updateDisplayFromComboBox() {
		fontdisplay.setFont(getSelectedFont());;
		fontdisplay.repaint();
	}

	public static void main(String[] args) {
		JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		ff.add(new JButton("button"));
		FontChooser sb = new FontChooser(new Font("Arial", Font.BOLD, 12));
		
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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int gridWidth() {
		// TODO Auto-generated method stub
		return 3;
	}
	
	public class fontCellRenerer extends BasicComboBoxRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public  Component	getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
			Font font=new Font(value.toString(), out.getFont().getStyle(), out.getFont().getSize());
			out.setFont(font);
			return out;
				}
	}
	public class styleCellRenerer extends BasicComboBoxRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public  Component	getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
			Font font=new Font(out.getFont().getFamily(), index, out.getFont().getSize());
			out.setFont(font);
			return out;
				}
	}
	public void setKey(String key) {
		this.key=key;
		
	}
	
}
