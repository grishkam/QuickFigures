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
package layout.basicFigure;

public class GridLayoutEditEvent implements GridEditEventTypes{

	
	
	
	private int type;
	private double arg1;
	private double arg2;;
	
	public  GridLayoutEditEvent(GridLayout l, int type, double n1, double n2) {
		this.setType(type);
		this.setArg1(n1);
		this.setArg2(n2);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getArg1() {
		return arg1;
	}

	public void setArg1(double n1) {
		this.arg1 = n1;
	}

	public double getArg2() {
		return arg2;
	}

	public void setArg2(double n2) {
		this.arg2 = n2;
	}
	
	
	
}
