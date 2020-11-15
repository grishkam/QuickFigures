package figureFormat;

import infoStorage.FileBasedMetaWrapper;
import infoStorage.HashMapBasedMeta;
import infoStorage.MetaInfoWrapper;
import ultilInputOutput.FileChoiceUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**Class contains methods for creating a subfolder in my documents for saving figures*/
public class DirectoryHandler {
	private static DirectoryHandler defaultHandler;
	
	HashMapBasedMeta prefs;//a list of preferences
	String pathOfDocFolder=FileChoiceUtil.pathOfDocumentsFolder();
	String subPathofDocumentsFolder="/Quick Figures";
	String subPathofTemplatesFoloder="/Quick Figures/Templates";
	String prefsFileName="prefs";
	private String subPathofDefaultTemplate=subPathofTemplatesFoloder+"/default template";;
	
	public static DirectoryHandler getDefaultHandler(){
		if (defaultHandler==null)
		{defaultHandler=new DirectoryHandler();
		PrefsShutDownHook.addShutdownHook();
		}
		
		return defaultHandler;
	} 
	
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
	
	/**creates all the the folders that will be used by QuickFigures*/
	void makeAllNeededDirsIfAbsent() {
		makeDirectoryIfAbsent(pathOfDocFolder+subPathofDocumentsFolder);
		makeDirectoryIfAbsent(pathOfDocFolder+subPathofTemplatesFoloder);
		makeDirectoryIfAbsent(pathOfDocFolder+subPathofDocumentsFolder+"/tmp");
		//makeDirectoryIfAbsent(pathOfDocFolder+subPathofPrefsFoloder+"/"+prefsFolderName) ;
	}
	
	void makeDirectoryIfAbsent(String path) {
		File f=new File(path) ;
		if (!f.exists())
			try {
			f.mkdirs();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	}
	
	void moveLocalResourceToWizDir(String local) {
		moveLocalResourceToFolder(local,getFigureFolderPath() );
	}
	
	void moveLocalResourceToFolder(String local, String folerPath) {
		File fo=new File(folerPath+"/"+local);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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

public MetaInfoWrapper getPrefsStorage() {
	if (prefs==null) {
		prefs=new HashMapBasedMeta(getFileBasedPrefsWrapper() );
	}
	
	return prefs;
}
	
public void savePrefs() {
	if (prefs!=null)
		getFileBasedPrefsWrapper().setProperty( prefs.toAString());
}
	
	public static void main(String[] args) {

	}

	public static void setDefaultHandler(DirectoryHandler defaultHandler) {
		DirectoryHandler.defaultHandler = defaultHandler;
	}
	
	
	

}
