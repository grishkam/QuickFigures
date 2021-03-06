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
 * Date Modified: April 24, 2021
 * Version: 2021.1
 * 
 */
package imageMenu;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import imageDisplayApp.UserPreferences;
import storedValueDialog.StoredValueDilaog;

/**This class shows the preferences dialog for quickfigures*/
public class UserPreferenceDialog extends BasicMenuItemForObj {




public UserPreferenceDialog() {
	
}




public void performActionDisplayedImageWrapper(DisplayedImage diw) {
	
		StoredValueDilaog storedValueDilaog = new StoredValueDilaog("Preferences", UserPreferences.current);
		storedValueDilaog.setModal(true);
		storedValueDilaog.showDialog();
		UserPreferences.current.store();
		
		
		
		return;


}

/***/
public String getCommand() {return "Preferences1";}
public String getNameText() {return "Preferences";}
public String getMenuPath() {return "Edit";}






}