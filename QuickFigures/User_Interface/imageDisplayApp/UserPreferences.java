/**
 * Author: Greg Mazo
 * Date Created: April 24, 2021
 * Date Modified: April 24, 2021
 * Version: 2021.2
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
	
}
