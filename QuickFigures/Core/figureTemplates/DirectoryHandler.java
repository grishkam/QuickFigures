package figureTemplates;

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


public class DirectoryHandler {
	private static DirectoryHandler defaultHandler;
	
	HashMapBasedMeta prefs;
	String pathOfDocFolder=FileChoiceUtil.pathOfDocumentsFolder();
	String subPathofPrefsFoloder="/Quick Figures";
	String subPathofTemplatesFoloder="/Quick Figures/Templates";
	String prefsFolderName="prefs";
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
	
	public String fullPathofDefaultTemplate() {
		//IssueLog.log("Full path of default template");
		//IssueLog.log(pathOfDocFolder+subPathofDefaultTemplate);
		return  pathOfDocFolder+subPathofDefaultTemplate;
	}
	
	/**Returns the subfolder of the my documents folder that contians 
	 * everything this directory handler will deal with*/
	public String getFigureFolderPath() {
		makeAllNeededDirsIfAbsent();
		return pathOfDocFolder+subPathofPrefsFoloder;
	}
	
	/**Returns the subfolder for saving temporary files*/
	public String getTempFolderPath() {
		makeAllNeededDirsIfAbsent();
		return pathOfDocFolder+subPathofPrefsFoloder+"/tmp";
	}
	

	
	
	public File getPrefsFile() {
		makeAllNeededDirsIfAbsent();
		File f=new File(pathOfDocFolder+subPathofPrefsFoloder+"/"+prefsFolderName) ;
		
		if (!f.exists())
			try {
			f.createNewFile();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return f;
	}
	
	void makeAllNeededDirsIfAbsent() {
		makeDirectoryIfAbsent(pathOfDocFolder+subPathofPrefsFoloder);
		makeDirectoryIfAbsent(pathOfDocFolder+subPathofTemplatesFoloder);
		makeDirectoryIfAbsent(pathOfDocFolder+subPathofPrefsFoloder+"/tmp");
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
	
public void saveStringToPath(String st, String path) throws IOException {
	BufferedWriter out = new BufferedWriter(new FileWriter(path, false));
    out.write(st);
    out.close();
}

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
	/**	IssueLog.log(System.getProperty("user.dir"));
		IssueLog.log(new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+"/Documents");
		System.getProperty("os.name");
	
		dh.getPrefsFile();*/
		
	//dh.moveLocalResourceToWizDir("Model Montage1.tif");
	}

	public static void setDefaultHandler(DirectoryHandler defaultHandler) {
		DirectoryHandler.defaultHandler = defaultHandler;
	}
	
	/**
	
	public void savePrefs() {
		try {
			OutputStream fr = new FileOutputStream(getPrefsFile());
			PrintStream ps = new PrintStream(fr);
			HashMap<String, Object> pf = getPrefs();
			for(String key:getPrefs().keySet()) {
				ps.println(key+"="+pf.get(key));
				}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public HashMap<String, Object> getPrefs() {
		
		if (prefs==null) try {
			prefs= new HashMap<String,Object>();
			BufferedReader vr = new BufferedReader(new FileReader(getPrefsFile()));
			String line;
			
				line = vr.readLine();
			
			while (line!=null) {
				String[] duo = line.split("=");
				if (duo.length>0) {
					prefs.put(duo[0], duo[1]);
				}
				line=vr.readLine();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return prefs;
	}
	*/
	

}
