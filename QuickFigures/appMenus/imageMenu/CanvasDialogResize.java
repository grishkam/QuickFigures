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
 * Date Modified: Jan 4, 2021
 * Version: 2021.1
 */
package imageMenu;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import javax.swing.JLabel;

import appContext.ImageDPIHandler;
import appContext.RulerUnit;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWorkSheet;
import basicMenusForApp.BasicMenuItemForObj;
import figureFormat.DirectoryHandler;
import imageDisplayApp.CanvasOptions;
import layout.BasicObjectListHandler;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;
import messages.ShowMessage;
import standardDialog.StandardDialog;
import standardDialog.attachmentPosition.AttachmentPositionBox;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputEvent;
import standardDialog.choices.ChoiceInputListener;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.StringInputPanel;
import storedValueDialog.StoredValueDilaog;
import undo.CanvasResizeUndo;

/**simple menu item that displays a dialog to allow the user to input a canvas size*/
public class CanvasDialogResize extends BasicMenuItemForObj {

	public static int NORMAL=0, INCH=1, CENTIMETER=2;
	static String[] values= {"Points", "Inches", "cm"};
	public boolean includePositionBox=true;
	private int type=NORMAL;
	
	public CanvasDialogResize(boolean pBox) {
		includePositionBox=pBox;
	}
	public CanvasDialogResize(int type) {
		this.type=type;
	}


	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if(diw==null) {
			ShowMessage.showOptionalMessage("No worksheet!", true, "You must have an open worksheet to use this option");
			
			return;
		}
		
		CanvasResizeUndo undo = new CanvasResizeUndo(diw);//creates an undo
		ImageWorkSheet iw = diw.getImageAsWorksheet();
		performResize(iw);
		
		diw.updateDisplay();
		diw.updateWindowSize();
		undo.establishFinalState();
		diw.getUndoManager().addEdit(undo);
	}

	public void performResize(ImageWorkSheet iw) {
		new CanvasDialog(iw, includePositionBox);
	}
	/**
	 * @return
	 */
	public double getRatio() {
		double ratio=1;
		if (type==INCH) ratio=ImageDPIHandler.getInchDefinition();
		if (type==CENTIMETER) ratio=ImageDPIHandler.getCMDefinition();
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
		return "Edit<Canvas";
	}
	
/**The dialog for canvas edits*/
public class CanvasDialog extends StandardDialog {
	
	/**
	 * 
	 */
	private static final String BLOCK_AUTO_RESIZE = "block resize";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageWorkSheet iw;
	boolean includePositionBox1=true;
	Rectangle r1;
	private Rectangle2D.Double r2;
	
	
	 private AttachmentPosition snappingBehaviour=AttachmentPosition.defaultInternal();
	 
		JLabel label=new JLabel("Position Of Items");
		 AttachmentPositionBox relativePositionBox=new  AttachmentPositionBox(snappingBehaviour);
		private double width2;
		private double height2;
		private NumberInputPanel wInput;
		private NumberInputPanel hInput;
		
	public CanvasDialog(ImageWorkSheet iw, boolean fancy) {
		this.setTitle("Size of "+iw.getTitle());
				snappingBehaviour.setLocationTypeInternal(RectangleEdges.UPPER_LEFT);
				this.includePositionBox1=fancy;
				setModal(true);
				this.iw=iw;
				Dimension d = iw.getCanvasDims();//.getDimensionsXY();
				r1=new Rectangle(d);
				//String adder="";
				//if(type==INCH) adder=" (inches)";
				//if(type==CENTIMETER) adder=" (cm)";
				this.add("name", new StringInputPanel("Title", iw.getTitle()));
				
				ChoiceInputPanel unitPanel = new ChoiceInputPanel("Units", values, type);
				this.add("unit", unitPanel);
				width2 = d.getWidth();
				wInput = new NumberInputPanel("Width", width2/ getRatio(), 1);
				this.add("width", wInput);
				height2 = d.getHeight();
				hInput = new NumberInputPanel("Height", height2/ getRatio(), 1);
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
					public void valueChanged(ChoiceInputEvent ne) {
						type=(int) ne.getChoiceIndex();
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
					this.add(relativePositionBox, c);
				}
				;
			
				
			BooleanInputPanel auto = new BooleanInputPanel("Block Auto Resize", !iw.allowAutoResize());
			this.add(BLOCK_AUTO_RESIZE, auto);
			
				addSubordinateDialog("Other Canvas Options",  new StoredValueDilaog(CanvasOptions.current)  );
				
				
				this.showDialog();
	}
	
	

	/**updates the ruler units*/
	public void updateRulerUnits() {
		RulerUnit unit = ImageDPIHandler.getRulerUnit();
		if (type==INCH) unit=ImageDPIHandler.getRulerUnit().getInchVersion();
		if (type==CENTIMETER) unit= ImageDPIHandler.getRulerUnit().getCMVersion();
		DirectoryHandler.getDefaultHandler().getPrefsStorage().setEntry(RulerUnit.key, unit.getShortName());
		ImageDPIHandler.setRulerUnit(unit);
		
	}
	
	public void onOK() {
		BasicObjectListHandler boh = new BasicObjectListHandler();
		String title=this.getString("name");
		double ww = width2;
		double hh =height2;
		
		if (includePositionBox1) {
		r2=new Rectangle2D.Double(0,0,(int)ww,(int)hh);
		Double r3 = new Rectangle2D.Double(); r3.setRect(r1);
		snappingBehaviour.doInternalSnapEdgePointToEdgePoint(snappingBehaviour.getSnapLocationTypeInternal(), r3, r2);
			r1.setRect(r3);
		}
		
		iw.setAllowAutoResize(!this.getBoolean(BLOCK_AUTO_RESIZE));
		
		boh.CanvasResizeObjectsIncluded(iw, (int)ww, (int)hh, (int) r1.x, (int) r1.y);
		iw.setTitle(title);
		
		updateRulerUnits();
		
	}
	
}







}
