package infoStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintStream;

import figureTemplates.DirectoryHandler;


/**A class that keeps a set of key value pains in a string. Methods innitially written 
  to modify the info of ImagePlus metadata*/
public class FileBasedMetaWrapper extends StringBasedMetaWrapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String path;
	public  FileBasedMetaWrapper(String path) {
		this.path=path;
	}
	
	public  String getProperty() {
		return fileToString(new File(path));
	}
	
	public void setProperty(String newProp) {
		stringToFile(newProp, new File(path));
	}

	/**saves a string object to a file*/
	static void stringToFile(String st, File f) {
		OutputStream fr;
		try {
			fr = new FileOutputStream(f);
		
		PrintStream ps = new PrintStream(fr);
		String[] lines=st.split('\n'+"");
		for(String key:lines) {
			if (key!=null&&!key.equals("null"))ps.println(key);
			}
		ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static String fileToString(File f) {
		try {
			
			BufferedReader vr = new BufferedReader(new FileReader(f));
			String line;
			
				line = vr.readLine();
			String output=line;
			while (line!=null) {
				line=vr.readLine();
				if (line!=null)
				output+='\n'+line;
			}
			vr.close();
		return output;
	}
	catch (Throwable t) {
		return null;
	}
	}
	
	public static void main(String[] args) {
		DirectoryHandler dh = new DirectoryHandler();
		FileBasedMetaWrapper meta = new FileBasedMetaWrapper(dh.getPrefsFile().getAbsolutePath());
		meta.setEntry("name", 6+"");
	}
}
