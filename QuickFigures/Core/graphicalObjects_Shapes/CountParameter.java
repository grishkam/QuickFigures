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
 * Date Modified: Jan 5, 2021
 * Version: 2023.2
 */
package graphicalObjects_Shapes;

import java.io.Serializable;

import graphicalObjects.ZoomableGraphic;
import locatedObject.RectangleEdgePositions;
import undo.SimpleTraits;


/**stores a specific int value and information about that value. used by certain shapes
 * @see CountHandle 
 * */
public class CountParameter extends NumberParameter implements Serializable, RectangleEdgePositions, SimpleTraits<CountParameter> {
	
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

	/**creates a copy with the given parent*/
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
	
	/**sets the value to the name i*/
	public void setValue(String a) {
		if (a==null) return;
		if(this.getNames()!=null) {
			for(int i=0; i<getNames().length; i++) {
				if (a.equals(getNames()[i]))
						this.setValue(i);
			}
		}
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

	/**if there are specific names associated with each int value, returns them*/
	public String[] getNames() {
		return names;
	}

	/**set names for each int value*/
	public void setNames(String[] names) {
		this.names = names;
	}

	@Override
	public void setNumber(double n) {
		value=(int) n;
		
	}

	@Override
	public double getNumber() {
		return value;
	}
	
	
}


