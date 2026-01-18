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
 * Date Created: Jan 18, 2026
 * Date Modified: Jan 18, 2026
 * Version: 2026.1
 */
package imageScaling;

/**
 
 * 
 */
public class PixelDensityInstructions {
	private double ppi=300;//the desired ppi
	Interpolation interpolationType=Interpolation.BILINEAR;
	


	
	/***/
	public PixelDensityInstructions(double scale, Interpolation interpolation) {
		this(scale);
		this.interpolationType=interpolation;
		
	}



	/**
	 * @param value
	 */
	public PixelDensityInstructions(double value) {
		ppi=value;
	}
	
	/**
	 * 
	 */
	public PixelDensityInstructions() {
	}



	public double getPPI() {return ppi;}
	public Interpolation getInterpolation() {return interpolationType;}



	/**
	 * @return
	 */
	public Interpolation getInterpolationType() {
		return interpolationType;
	}
}
