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
package utilityClassesForObjects;

/**for any item in which pixels have a width, height and depth corresponsing to a unit*/
public interface ScalededItem {
	/**setters and getters for scale info*/
	public ScaleInfo getScaleInfo();
	public void setScaleInfo(ScaleInfo s);
	
	/**If the item is drawn onto the graphics with a scaling,
	   gets the corrected scale into
	 * */
	public ScaleInfo getDisplayScaleInfo();
	
	/**returns the sixe of the item in units by units.
	  for example, it may be 2micron*2micron*/
	public double[] getDimensionsInUnits();
}
