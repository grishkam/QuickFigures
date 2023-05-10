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
 * Date Created: April 24, 2021
 * Date Modified: April 15, 2023
 * Version: 2023.2
 * 
 */
package imageDisplayApp;

import figureFormat.DirectoryHandler;
import infoStorage.BasicMetaDataHandler;
import infoStorage.MetaInfoWrapper;
import layout.RetrievableOption;

/**
 this class stores a list of preferences 
 */
public class UserPreferences {

	/**the current user preferences*/
public static final UserPreferences current=new UserPreferences();
	
	@RetrievableOption(key = "spaceBarScrolling", label="Hold space bar to scroll?")
	public boolean spaceBarScrolling=false;
	

	@RetrievableOption(key = "expimentalFeatures", label="Include Experimental Features?")
	public boolean testNew=false;
	
	@RetrievableOption(key = "oversizeMode", label="'Large inset mode' for images larger than ", category="insets")
	public int useOversizeModeForInsets=5000;
	
	/**stores the preferences as keys*/
	public void store() {
		MetaInfoWrapper storage = DirectoryHandler.getDefaultHandler().getPrefsStorage();
		new BasicMetaDataHandler().saveAnnotatedFields(storage, this,"");
	}
	
	/**loads the stored preferences*/
	public void load() {
		MetaInfoWrapper storage = DirectoryHandler.getDefaultHandler().getPrefsStorage();
		new BasicMetaDataHandler().loadAnnotatedFields(storage, this,"");
	}
	
	
	/**returns true if experimental features are to be included
	 * @return
	 */
	public static boolean experimentalFeaturesIncluded() {
		return UserPreferences.current.testNew;
	}
	
}
