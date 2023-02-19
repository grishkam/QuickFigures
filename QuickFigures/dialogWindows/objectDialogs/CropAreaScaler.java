/**
 * Author: Greg Mazo
 * Date Created: Nov 17, 2021
 * Date Modified: Nov 17, 2021
 * Version: 2023.1
 */
package objectDialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import graphicalObjects_Shapes.RectangularGraphic;
import locatedObject.RectangleEdges;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.numbers.NumberInputPanel;

/**
 A dialog for scaling the crop area that already exists within a crop dialog
 */
public class CropAreaScaler extends GraphicItemOptionsDialog {

	private CroppingDialog parentDialog;
	private RectangularGraphic innitialCroparea;
	
	public static final String scaleFactorKey="factor", propagateKey="Change Image Scale To Fit in same area";
	
	/**suggested scales*/
	double[] scales=new double[] {0.1, 0.25, 0.5, 0.75, 1, 2, 2.5, 3, 4, 5, 10};
	private double currentScale=1;
	private boolean changeScaleOfImage=false;
	private Component triggerButton;
	private double innitialScaleRecord=1;

	/**
	 * @param croppingDialog
	 */
	public CropAreaScaler(CroppingDialog croppingDialog, Component locationAnchor) {
		this.setTitle("Scale Crop Area to the desired scale factor");
		this.parentDialog=croppingDialog;
		this.innitialCroparea=parentDialog.cropAreaRectangle.copy();
		this.innitialScaleRecord=croppingDialog.scaleFactorForCropArea;
		super.setUpdateAfterEachItemChange(true);
		this.addOptionsToDialog();
		this.setWindowCentered(true);
		this.triggerButton=locationAnchor;
		
	}
	
	/**returns a scaled version of the rectangle */
	public RectangularGraphic createScaledRect(RectangularGraphic oroginal, double scale) {
		RectangularGraphic output = oroginal.copy();
		output.setLocationType(RectangleEdges.CENTER);
		Point2D location = output.getLocation();
		output.setWidth(output.getObjectWidth()*scale);
		output.setHeight(output.getObjectHeight()*scale);
		output.setLocation(location);
		
		return output;
	}
	
	
	protected void addOptionsToDialog() {
		NumberInputPanel inputPanel = new NumberInputPanel("Scale Factor", this.currentScale, true, true, 0, 100);
		inputPanel.setSliderConstants(determinePossibleScaleFactors());
		inputPanel.setDecimalPlaces(3);
		this.add(scaleFactorKey, inputPanel);
		this.add( propagateKey, new BooleanInputPanel("Change image scale to fit panels in same area?", this.changeScaleOfImage));
	}
	
	@Override
	protected void setItemsToDiaog() {
		currentScale=this.getNumber(scaleFactorKey);
		changeScaleOfImage=this.getBoolean(propagateKey);
		
		RectangularGraphic newCropArea = createScaledRect(innitialCroparea, currentScale);
		if (parentDialog.isCropRectangleValid(newCropArea)) {
			parentDialog.setRectangleTo(newCropArea);
			parentDialog.scaleFactorForCropArea=this.innitialScaleRecord*this.currentScale;//records what scaling has been done to the crop area
			parentDialog.changeScale=changeScaleOfImage;
			parentDialog.repaint();
		}
		
	}
	
	/**returns a list of scale factors*/
	public ArrayList<Double> determinePossibleScaleFactors() {
		ArrayList<Double> output = new ArrayList<Double> ();
		for(double d: scales) {
			RectangularGraphic newCropArea = createScaledRect(innitialCroparea, d);
			if (parentDialog.isCropRectangleValid(newCropArea)) {
				output.add(d);
			}
		}
		
		return output;
		
		
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param e
	 */
	public void showAsPopup(ActionEvent e) {
		//SmartPopupJMenu popup = new SmartPopupJMenu();
		JPopupMenu popup = createPopupMenuVersion();
		
		
		popup.pack();
		Object source = e.getSource();
		if(triggerButton==null&&source instanceof Component)
			{triggerButton=(Component) source;}
			popup.show(triggerButton, 0, 0);
		
	}



}
