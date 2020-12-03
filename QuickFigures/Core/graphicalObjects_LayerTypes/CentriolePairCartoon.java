package graphicalObjects_LayerTypes;

import java.awt.Color;

import graphicalObjects_BasicShapes.RectangularGraphic;

public class CentriolePairCartoon extends GraphicGroup {

	/**
	 * 
	 */
	
	public CentriolePairCartoon() {
		generateInnitialCentrioles();
	}
	
	private static final long serialVersionUID = 1L;
	
	RectangularGraphic centriole1;
	RectangularGraphic centriole2;
	
	public void generateInnitialCentrioles() {
		centriole1=new RectangularGraphic(0,0,10,25);
		centriole2=new RectangularGraphic(20,0,25,10);
		getTheLayer().add(centriole1);
		getTheLayer().add(centriole2);
		setPropertiesInitialOfCentriole(centriole1);
		setPropertiesInitialOfCentriole(centriole2);
		centriole2.setAngle(Math.PI/8);
		
	}
	
	private void setPropertiesInitialOfCentriole(RectangularGraphic centriole1) {
		centriole1.setStrokeWidth(1);
		centriole1.makeNearlyDashLess();
		centriole1.setFillColor(Color.black);
		centriole1.setFilled(true);
		centriole1.setStrokeColor(Color.DARK_GRAY);
		centriole1.setAntialize(true);
	}


}
