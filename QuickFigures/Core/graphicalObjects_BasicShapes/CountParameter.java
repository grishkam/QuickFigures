package graphicalObjects_BasicShapes;

import java.io.Serializable;

import graphicalObjects.ZoomableGraphic;
import undo.SimpleTraits;
import utilityClassesForObjects.RectangleEdgePosisions;


/**keeps information about a length or ratio, used by the angle handle and certain shapes*/
public class CountParameter implements Serializable, RectangleEdgePosisions, SimpleTraits<CountParameter> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int value=0;
	private int maxValue=Integer.MAX_VALUE;
	private int minValue=0;
	private String[] names=null;
	public String parameterName="";


	public CountParameter(ZoomableGraphic ovalGraphic) {
		
	}
	
	public CountParameter(ZoomableGraphic ovalGraphic, int length, int zero, int max) {
		this(ovalGraphic);
		this.value=length;
		this.setMaxValue(max);
		this.setMinValue(zero);
	}
	
	public CountParameter(RectangularGraphic r, int i) {
		this(r);
		this.setValue(i);
	}

	public CountParameter copy(RectangularGraphic ovalGraphic) {
		CountParameter out = new CountParameter(ovalGraphic);
		this.giveTraitsTo(out);
		return out;
	}

	
	public int getValue() {
		return value;
	}
	public void setValue(int a) {
		this.value = a;
	}

	@Override
	public SimpleTraits<CountParameter> copy() {
		return copy(null);
	}

	@Override
	public void giveTraitsTo(CountParameter t) {
		t.value=value;
		t.setMaxValue(maxValue);
		t.setMinValue(minValue);
	}

	@Override
	public CountParameter self() {
		return this;
	}

	public String getValueAsString() {
		if(getNames()!=null &&getNames().length>value) return getNames()[value];
		return ""+value;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}
	
	
}


