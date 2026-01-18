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
 * Date Created: April 18, 2021
 * Date Modified: April 18, 2021
 * Version: 2023.2
 */
package imageScaling;

/**
 Class that carries information about the scale and interpolation
 */
public class ScaleInformation {
	private double scale=1;//the scale factor applied
	Interpolation interpolationType=Interpolation.BILINEAR;
	


	
	/***/
	public ScaleInformation(double scale, Interpolation interpolation) {
		this(scale);
		this.interpolationType=interpolation;
		
	}

	/**
	
	 */
	public ScaleInformation() {
		
	}
	
	
	/***/
	public ScaleInformation(double scale) {
		this.scale=scale;
		if(scale<=0)
			this.scale=1;
		
	}

	public double getScale() {
		return scale;
	}

	public Interpolation getInterpolationType() {
		return interpolationType;
	}
	
	public String toString() {
		return "scale="+scale+"  Interpolation="+interpolationType.name().toLowerCase();
	}

	/**gets a version of this with different scale
	 * @param i the new scale
	 * @return
	 */
	public ScaleInformation getAtDifferentScale(double i) {
		return new ScaleInformation(i, interpolationType);
	}
	
	/**gets a version of this with different scale
	 * @param i
	 * @return
	 */
	public ScaleInformation getAtDifferentIterpolation(Interpolation i) {
		return new ScaleInformation(scale, i);
	}
	
	/**returns a version of this that has been multiplied*/
	public ScaleInformation multiplyBy(double factor) {return new ScaleInformation(scale*factor, interpolationType);}

	/**returns a version of this that has been multiplied*/
	public ScaleInformation multiplyBy(double factor, Interpolation interpolationType) {return new ScaleInformation(scale*factor, interpolationType);}


}
