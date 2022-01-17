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
 * Version: 2022.0
 */
package figureFormat;

import infoStorage.FileBasedMetaWrapper;
import infoStorage.HashMapBasedMeta;
import infoStorage.MetaInfoWrapper;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**Class contains methods for creating a subfolder in my documents for saving figures, templates
 * and exported files.
  this in turn has subfolders for saving templates and for saving temporary files*/
public class DirectoryHandler {
	private static DirectoryHandler defaultHandler;
	
	HashMapBasedMeta prefs;//a list of preferences that may be stored in a file
	String pathOfDocFolder=FileChoiceUtil.pathOfDocumentsFolder();//my documents or documents
	String subPathofDocumentsFolder="/Quick Figures";
	String subPathofTemplatesFoloder="/Quick Figures/Templates";//template subfolder
	String prefsFileName="preferences.txt";
	private String subPathofDefaultTemplate=getSubPathofTemplatesFolder()+"/default template";
	
	/**returns the default directory handler*/
	public static DirectoryHandler getDefaultHandler(){
		if (defaultHandler==null)
			{defaultHandler=new DirectoryHandler();
			PrefsShutDownHook.addShutdownHook();
			}
		
		return defaultHandler;
	} 
	
	/**returns true if a file for the default figure template already exists*/
	public boolean defaultTemplateExits() {
		return new File(fullPathofDefaultTemplate() ).exists();
	}
	
	/**returns the path where the default template will be saved*/
	public String fullPathofDefaultTemplate() {
		return  pathOfDocFolder+subPathofDefaultTemplate;
	}
	
	/**Returns the subfolder of the my documents folder that contians 
	 * everything this directory handler will deal with*/
	public String getFigureFolderPath() {
		makeAllNeededDirsIfAbsent();
		return pathOfDocFolder+subPathofDocumentsFolder;
	}
	
	/**Returns the subfolder for saving temporary files*/
	public String getTempFolderPath() {
		makeAllNeededDirsIfAbsent();
		return pathOfDocFolder+subPathofDocumentsFolder+"/tmp";
	}
	
	/**Returns the subfolder for saving temporary files*/
	public String getTempFolderPath(String subfolder) {
		String path = getTempFolderPath();
		path+="/"+subfolder;
		makeDirectoryIfAbsent(path);
		return path;
	}
	

/**returns the path of the preferences file*/
	public File getPrefsFile() {
		makeAllNeededDirsIfAbsent();
		File f=new File(pathOfDocFolder+subPathofDocumentsFolder+"/"+prefsFileName) ;
		
		if (!f.exists())
			try {
			f.createNewFile();
				
			} catch (Exception e) {
				
			}
		return f;
	}
	
	/**creates all the the folders and subfolders that will be used by QuickFigures.
	  */
	void makeAllNeededDirsIfAbsent() {
		makeDirectoryIfAbsent(pathOfDocFolder+subPathofDocumentsFolder);
		makeDirectoryIfAbsent(pathOfDocFolder+getSubPathofTemplatesFolder());
		makeDirectoryIfAbsent(pathOfDocFolder+subPathofDocumentsFolder+"/tmp");
		//makeDirectoryIfAbsent(pathOfDocFolder+subPathofPrefsFoloder+"/"+prefsFolderName) ;
	}

	/**the path of the templates folder*/
	public String getSubPathofTemplatesFolder() {
		return subPathofTemplatesFoloder;
	}
	
	/**if a particular file path includes folder that do not exist, makes the folders*/
	void makeDirectoryIfAbsent(String path) {
		File f=new File(path) ;
		if (!f.exists())
			try {
			f.mkdirs();
				
			} catch (Exception e) {
				IssueLog.logT(e);
			}
	
	}
	

	
	/**takes a resource stored within the jar file and copies it to the given folder*/
	protected void moveLocalResourceToFolder(String local, String folerPath) {
		File fo=new File(folerPath+"/"+local);
		moveLocalResourceToFile(local, fo);
		
	}

	/**
	 * @param local
	 * @param fo
	 */
	protected void moveLocalResourceToFile(String local, File fo) {
		try {
			FileOutputStream out = new FileOutputStream(fo);
			InputStream in = getClass().getClassLoader().getResourceAsStream( local);	;
			byte[] buffer = new byte[1024];
			int len = in.read(buffer);
			while (len != -1) {
			    out.write(buffer, 0, len);
			    len = in.read(buffer);
			}
			out.close();
		} catch (Exception e) {
			IssueLog.logT(e);
			IssueLog.log("Failed to find local resource "+local);
		}
	}
	
	/**Saves String st as a text file to the given file path*/
public void saveStringToPath(String st, String path) throws IOException {
	BufferedWriter out = new BufferedWriter(new FileWriter(path, false));
    out.write(st);
    out.close();
}

/**returns an object for reading the preferences*/
FileBasedMetaWrapper getFileBasedPrefsWrapper() {
	return new FileBasedMetaWrapper(this.getPrefsFile().getAbsolutePath());
}

/**returns the object that stores the preferences hashmap*/
public MetaInfoWrapper getPrefsStorage() {
	if (prefs==null) {
		prefs=new HashMapBasedMeta(getFileBasedPrefsWrapper() );
	}
	
	return prefs;
}
	
/**saves the preferences to a file*/
public void savePrefs() {
	if (prefs!=null)
		getFileBasedPrefsWrapper().setProperty( prefs.toAString());
}
	
	

	public static void setDefaultHandler(DirectoryHandler defaultHandler) {
		DirectoryHandler.defaultHandler = defaultHandler;
	}


	
	
	

}
