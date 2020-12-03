package journalCriteria;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComboBox;

import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;

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
	
	public ComboBoxPanel generateFamilyCombo() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		 String[] fonts = ge.getAvailableFontFamilyNames();
		 String[] fonts2 = new String[fonts.length+1];
		 this.fontOptions=fonts2;
		 fonts2[0]=""; for(int i=0; i<fonts.length; i++) {fonts2[i+1]=fonts[i];}
		 output=new JComboBox<String>(fonts2);
		 output.setSelectedIndex(Arrays.binarySearch(fonts2, jc.prefferedFontFamily));
	     ComboBoxPanel cbp = new ComboBoxPanel("Ideal Font", output);
		return cbp;
	}

}
