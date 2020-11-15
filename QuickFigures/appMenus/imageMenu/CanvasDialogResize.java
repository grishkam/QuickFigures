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
import standardDialog.NumberInputPanel;
import standardDialog.SnapBox;
import standardDialog.StandardDialog;
import standardDialog.StringInputPanel;
import undo.CanvasResizeUndo;
import utilityClassesForObjects.SnappingPosition;

public class CanvasDialogResize implements MenuItemForObj {

	static int NORMAL=0;
	public static int Inch=1;
	static int Centimmeter=2;
	public boolean fancy=true;
	private int type=0;
	
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
		double ratio=1;
		if (type==Inch) ratio=72;
		if (type==Centimmeter) ratio=72/2.54;
		new canvasDialog(iw, fancy, ratio);
	}
	
	
	@Override
	public String getCommand() {
		if (type==Inch) return "Canvas Resize Dialog (Inch)";
		if (type==Centimmeter) return "Canvas Resize Dialog (cm)";
		return "Canvas Resize Dialog";
	}

	@Override
	public String getNameText() {
		if (type==Inch) return "Resize Canvas (in)";
		if (type==Centimmeter) return "Resize Canvas (cm)";
		return "Resize Canvas";
	}

	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
		return "Image<Canvas";
	}
	

public class canvasDialog extends StandardDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageWrapper iw;
	boolean fancy=true;
	Rectangle r1;
	private Rectangle2D.Double r2;
	double ratio=1;
	
	 private SnappingPosition snappingBehaviour=SnappingPosition.defaultInternal();
		JLabel label=new JLabel("Position Of Items");
		 SnapBox Box=new  SnapBox(snappingBehaviour);
		
	
	public canvasDialog(ImageWrapper iw, boolean fancy, double ratio) {
		this.ratio=ratio;
		this.fancy=fancy;
		setModal(true);
		this.iw=iw;
		Dimension d = iw.getCanvasDims();//.getDimensionsXY();
		r1=new Rectangle(d);
		String adder="";
		if(type==Inch) adder=" (inches)";
		if(type==Centimmeter) adder=" (cm)";
		this.add("name", new StringInputPanel("Title", iw.getTitle()));
		this.add("width", new NumberInputPanel("Width"+adder, d.getWidth()/ratio));
		this.add("height", new NumberInputPanel("Height"+adder, d.getHeight()/ratio));
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
		double ww = this.getNumber("width")*ratio;
		double hh = this.getNumber("height")*ratio;
		
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


}
