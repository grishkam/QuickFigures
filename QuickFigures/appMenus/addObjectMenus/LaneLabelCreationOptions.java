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
 * Date Modified: Jan 4, 2021
 * Version: 2021.2
 */
package addObjectMenus;

import layout.RetrievableOption;

/**A set of properties that determine how figure labels are automatically generated*/
public class LaneLabelCreationOptions {
	
	/**The current label creation options*/
	public static LaneLabelCreationOptions current=new LaneLabelCreationOptions() ;
	
	@RetrievableOption(key = "prefix", label="Prefix")
	public String prefix="Lane ";
	@RetrievableOption(key = "suffix", label="Suffix")
	public String suffix="";
	
	@RetrievableOption(key = "nLanes", label="How many lanes?")
	public double nLanes=7;
	
	

}
