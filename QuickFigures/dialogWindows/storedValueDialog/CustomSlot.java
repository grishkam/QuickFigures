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
 * Date Created: Dec 3, 2022
 * Date Modified: Dec 3, 2022
 * Version: 2022.2
 */
package storedValueDialog;

import java.awt.Color;

import layout.RetrievableOption;
import standardDialog.StandardDialog;

/**
 
 * 
 */
public interface CustomSlot {

	static Color bad_input = new Color(230, 190, 190);
	static Color good_input = new Color(190, 230, 190);
	static Color neutral_input = new Color(230, 230, 230);
	
	/**
	 * @param d
	 * @param o
	 * @param so
	 */
	void addInput(StandardDialog d, RetrievableOption o, CustomSlot so);

}
