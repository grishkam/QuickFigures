package graphicalObjects_BasicShapes;

import java.io.Serializable;

import undo.SimpleTraits;
import utilityClassesForObjects.RectangleEdgePosisions;


/**keeps information about a length or ratio, used by the angle handle and certain shapes*/
public class RectangleEdgeParameter implements Serializable, RectangleEdgePosisions, SimpleTraits<RectangleEdgeParameter> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double length=0;
	private double ratioToMax=0.75;
	
	public int zeroLocation=UPPER_LEFT;
	public int maxLengthLocation=TOP;

	public RectangleEdgeParameter(RectangularGraphic ovalGraphic) {
		
	}
	
	public RectangleEdgeParameter(RectangularGraphic ovalGraphic, double length, int zero, int max) {
		this(ovalGraphic);
		this.length=length;
		this.maxLengthLocation=max;
		this.zeroLocation=zero;
	}
	
	public RectangleEdgeParameter copy(RectangularGraphic ovalGraphic) {
		RectangleEdgeParameter out = new RectangleEdgeParameter(ovalGraphic, length, zeroLocation, maxLengthLocation);
		this.giveTraitsTo(out);
		return out;
	}

	public double getRatioToMaxLength() {
		return ratioToMax;
	}
	public void setRatioToMaxLength(double a) {
		ratioToMax= a;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double angle) {
		this.length = angle;
	}

	@Override
	public SimpleTraits<RectangleEdgeParameter> copy() {
		return copy(null);
	}

	@Override
	public void giveTraitsTo(RectangleEdgeParameter t) {
		t.length=length;
		t.ratioToMax=ratioToMax;
		
	}

	@Override
	public RectangleEdgeParameter self() {
		return this;
	}
	
	
}


