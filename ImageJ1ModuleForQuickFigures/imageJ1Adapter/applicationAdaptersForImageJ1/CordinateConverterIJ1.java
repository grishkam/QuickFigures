/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package applicationAdaptersForImageJ1;



import graphicalObjects.BasicCoordinateConverter;
import ij.ImagePlus;

/**Coordinate converted used if graphics need to be superimposed on an IJ1 image
  not accessible to users but might be of use to programmers*/
public class CordinateConverterIJ1 extends BasicCoordinateConverter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImagePlus imp=null;
	
	
	
	public CordinateConverterIJ1(ImagePlus imp) {
		this.imp=imp;
	}

	@Override
	public double transformX(double ox) {
		return imp.getCanvas().screenXD(ox);
	}

	@Override
	public double transformY(double oy) {
		return imp.getCanvas().screenYD(oy);
	}

	@Override
	public double getMagnification() {
		return imp.getCanvas().getMagnification();
	}

	
	
	
}
