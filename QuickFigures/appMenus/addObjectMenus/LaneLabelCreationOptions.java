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
 * Date Modified: Nov 27, 2021
 * Version: 2022.2
 */
package addObjectMenus;

import java.io.File;

import layout.RetrievableOption;
import textObjectProperties.TextPattern;

/**A set of properties that determine how figure labels are automatically generated*/
public class LaneLabelCreationOptions {
	
	/**The current label creation options*/
	public static LaneLabelCreationOptions current=new LaneLabelCreationOptions() ;
	
	public static final String numberCode="%number%", letterCode="%letter%";
	public static String defaultLabelText="Lane "+numberCode;
	
	@RetrievableOption(key = "nLanes", label="How many lanes?")
	public double nLanes=8;
	
	@RetrievableOption(key = "label prefix and suffix", label="Label text here", nExpected=12)
	public String[] textOfLabel=new String[] {defaultLabelText};
	
	
	@RetrievableOption(key = "mark conditions", label="How many lines of +/- marks", nExpected=4)
	public double nPlusMarks=0;
	
	@RetrievableOption(key = "Mark Text", label="Marks will be")
	public String[] markText=new String[] {"-+", "--++"};

	/**The pattern for numbers*/
	TextPattern pattern1=new TextPattern(); {pattern1.setSuffix("");pattern1.setPrefix("");}
	
	
}
