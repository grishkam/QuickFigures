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
 * Date Modified: Mar 28, 2021
 * Date Created: Mar 18, 2021
 * Version: 2022.1
 */
package textObjectProperties;

import java.awt.Color;

/**
objects implement this interface if they have a property that modifies a certain color
@see ColorDimmer for more about the 'dimming'
 */
public interface DimsColor {
	/**returns a dimmed version of the color*/
	public Color getDimmedColor(Color c);
}
