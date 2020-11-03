package objectDialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import standardDialog.ChoiceInputEvent;
import standardDialog.ChoiceInputListener;
import standardDialog.ColorComboboxPanel;
import standardDialog.ComboBoxPanel;
import standardDialog.ObjectEditEvent;
import standardDialog.ObjectInputPanel;
import standardDialog.OnGridLayout;
import standardDialog.StringInputEvent;
import standardDialog.StringInputListener;
import standardDialog.StringInputPanel;
import utilityClassesForObjects.TextLineSegment;

public class TextLineSegmentPanel extends  ObjectInputPanel implements StringInputListener, ChoiceInputListener, OnGridLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextLineSegment segment;
	StringInputPanel textPanel;
	ColorComboboxPanel colorPanal;
	ComboBoxPanel scriptType;
	ComboBoxPanel scriptStyle;
	private ComboBoxPanel scriptLine;
	Boolean includeColor=true;
	
	
	public TextLineSegmentPanel(TextLineSegment t) {
		setSegment(t);
		textPanel=new StringInputPanel("Text", t.getText(),15);
		
		if (includeColor)	colorPanal=new ColorComboboxPanel("Text Color", null, t.getUniqueTextColor());
		
		scriptType=new ComboBoxPanel("Text is", new String[] {"normal", "superscript", "subscript"},t.isSubOrSuperScript());
		scriptStyle=new ComboBoxPanel("Text style", new String[] {"normal","Plain", "Bold", "Italic", "Bold+Italic"},t.isSubOrSuperScript());
		scriptLine=new ComboBoxPanel("Line Type", new String[] {"no line","Underline", "Strike Through"},t.getLines());
		
		addListeners();
		this.setLayout(new GridBagLayout());
		this.placeItems(this, 0, 0);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(350,200);
	}
	
	public void addListeners() {
		textPanel.addStringInputListener(this);
		scriptType.addChoiceInputListener(this);
		scriptStyle.addChoiceInputListener(this);
		scriptLine.addChoiceInputListener(this);
		if (includeColor)	colorPanal.addChoiceInputListener(this);
		
	}
	
	public void setSegmentToPanels() {
		 getSegment().setText(textPanel.getTextFromField());
		 if (includeColor)	 getSegment().setTextColor(colorPanal.getSelectedColor());
		 getSegment(). setScript(scriptType.getSelectedIndex());
		 getSegment().setUniqueStyle(scriptStyle.getSelectedIndex());
		 getSegment().setLines(scriptLine.getSelectedIndex());
	}

	@Override
	public void numberChanged(ChoiceInputEvent ne) {
		setSegmentToPanels();
		this.notifyListeners(new ObjectEditEvent(getSegment()));
	}

	@Override
	public void StringInput(StringInputEvent sie) {
		setSegmentToPanels();
		this.notifyListeners(new ObjectEditEvent(getSegment()));
	}

	@Override
	public void placeItems(Container jp, int x0, int y0) {
		textPanel.placeItems(jp, x0, y0);
		colorPanal.placeItems(jp, x0, y0+1);
		scriptType.placeItems(jp, x0, y0+2);
		scriptStyle.placeItems(jp, x0, y0+3);
		scriptLine.placeItems(jp, x0, y0+4);
	}

	@Override
	public int gridHeight() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public int gridWidth() {
		// TODO Auto-generated method stub
		return 2;
	}

	public TextLineSegment getSegment() {
		return segment;
	}

	public void setSegment(TextLineSegment segment) {
		this.segment = segment;
	}
	
	
	
}
