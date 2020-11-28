package imageMenu;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import javax.swing.Icon;
import javax.swing.JLabel;

import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import basicMenusForApp.MenuItemForObj;
import genericMontageKit.BasicObjectListHandler;
import standardDialog.ChoiceInputEvent;
import standardDialog.ChoiceInputListener;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputEvent;
import standardDialog.NumberInputListener;
import standardDialog.NumberInputPanel;
import standardDialog.SnapBox;
import standardDialog.StandardDialog;
import standardDialog.StringInputPanel;
import undo.CanvasResizeUndo;
import utilityClassesForObjects.AttachmentPosition;
import utilityClassesForObjects.RectangleEdges;

/**simple menu item that displays a dialog to allow the user to input a canvas size*/
public class CanvasDialogResize implements MenuItemForObj {

	static int NORMAL=0;
	public static int INCH=1;
	static int CENTIMETER=2;
	static String[] values= {"Points", "Inches", "cm"};
	public boolean fancy=true;
	private int type=NORMAL;
	
	public CanvasDialogResize() {}
	public CanvasDialogResize(int type) {
		this.type=type;
	}


	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		CanvasResizeUndo undo = new CanvasResizeUndo(diw);//creates an undo
		ImageWrapper iw = diw.getImageAsWrapper();
		performResize(iw);
		
		diw.updateDisplay();
		diw.updateWindowSize();
		undo.establishFinalState();
		diw.getUndoManager().addEdit(undo);
	}

	public void performResize(ImageWrapper iw) {
		new CanvasDialog(iw, fancy);
	}
	/**
	 * @return
	 */
	public double getRatio() {
		double ratio=1;
		if (type==INCH) ratio=72;
		if (type==CENTIMETER) ratio=72/2.54;
		return ratio;
	}
	
	
	@Override
	public String getCommand() {
		if (type==INCH) return "Canvas Resize Dialog (Inch)";
		if (type==CENTIMETER) return "Canvas Resize Dialog (cm)";
		return "Canvas Resize Dialog";
	}

	@Override
	public String getNameText() {
		if (type==INCH) return "Resize Canvas (in)";
		if (type==CENTIMETER) return "Resize Canvas (cm)";
		return "Resize Canvas";
	}

	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
		return "Image<Canvas";
	}
	

public class CanvasDialog extends StandardDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageWrapper iw;
	boolean fancy=true;
	Rectangle r1;
	private Rectangle2D.Double r2;
	
	
	 private AttachmentPosition snappingBehaviour=AttachmentPosition.defaultInternal();
	 
		JLabel label=new JLabel("Position Of Items");
		 SnapBox Box=new  SnapBox(snappingBehaviour);
		private double width2;
		private double height2;
		private NumberInputPanel wInput;
		private NumberInputPanel hInput;
		
	public CanvasDialog(ImageWrapper iw, boolean fancy) {
		snappingBehaviour.setLocationTypeInternal(RectangleEdges.UPPER_LEFT);
		this.fancy=fancy;
		setModal(true);
		this.iw=iw;
		Dimension d = iw.getCanvasDims();//.getDimensionsXY();
		r1=new Rectangle(d);
		String adder="";
		if(type==INCH) adder=" (inches)";
		if(type==CENTIMETER) adder=" (cm)";
		this.add("name", new StringInputPanel("Title", iw.getTitle()));
		
		ComboBoxPanel unitPanel = new ComboBoxPanel("Units", values, type);
		this.add("unit", unitPanel);
		width2 = d.getWidth();
		wInput = new NumberInputPanel("Width"+adder, width2/ getRatio(), 1);
		this.add("width", wInput);
		height2 = d.getHeight();
		hInput = new NumberInputPanel("Height"+adder, height2/ getRatio(), 1);
		this.add("height", hInput);
		hInput.addNumberInputListener(new NumberInputListener() {
			public void numberChanged(NumberInputEvent ne) {
				height2=ne.getNumber()*getRatio();
			}});
		wInput.addNumberInputListener(new NumberInputListener() {
			public void numberChanged(NumberInputEvent ne) {
				width2 =ne.getNumber()*getRatio();
			}});
	
		unitPanel.addChoiceInputListener(new ChoiceInputListener() {

			@Override
			public void numberChanged(ChoiceInputEvent ne) {
				type=(int) ne.getNumber();
				double w2 = width2/getRatio();
				double h2 = height2/getRatio();
				wInput.setNumber(w2);
				hInput.setNumber(h2);
				
			}
			
		});
		
		
	this.setWindowCentered(true);
		if (fancy) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridwidth=2;
			c.gridy=super.gymax;
			super.gymax++;
			this.add(Box, c);
		}
		this.showDialog();
	}
	
	

	
	public void onOK() {
		BasicObjectListHandler boh = new BasicObjectListHandler();
		String title=this.getString("name");
		double ww = width2;
		double hh =height2;
		
		if (fancy) {
		r2=new Rectangle2D.Double(0,0,(int)ww,(int)hh);
		Double r3 = new Rectangle2D.Double(); r3.setRect(r1);
		snappingBehaviour.doInternalSnapEdgePointToEdgePoint(snappingBehaviour.getSnapLocationTypeInternal(), r3, r2);
			r1.setRect(r3);
		}
		
		boh.CanvasResizeObjectsIncluded(iw, (int)ww, (int)hh, (int) r1.x, (int) r1.y);
		iw.setTitle(title);
		
	}
	
}


@Override
public Icon getIcon() {
	// TODO Auto-generated method stub
	return null;
}

class PartnerNumbers implements NumberInputListener {

	private NumberInputPanel first;
	private NumberInputPanel second;
	private double ratio;

	public PartnerNumbers(NumberInputPanel p1, NumberInputPanel p2, double ratio) {
		p1.addNumberInputListener(this);
		p2.addNumberInputListener(this);
		this.first=p1;
		this.second=p2;
		this.ratio=ratio;
	}
	
	@Override
	public void numberChanged(NumberInputEvent ne) {
		
		if(ne.getSourcePanel()==first) second.setNumber(ne.getNumber()/ratio);
		if(ne.getSourcePanel()==second) first.setNumber(ne.getNumber()*ratio);
	}
	
	
}


}
