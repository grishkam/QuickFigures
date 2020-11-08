package ultilInputOutput;

import java.io.File;

import logging.IssueLog;

/**As I worked on this, I sometimes changed the working directories 
  and the locations of files. Meanwhile the file paths that were saved
  or included in code would not change. Using simply string replacement
  this helps */
public class FileFinder {
	private static String workingDir="/";
	String extension=null;
	static String name="Greg";
	
	String[][] replacements=new String[][] {
			new String[] {
					"/"+name+"s Data/"+name+" Microscopy Files/"+name+" HP Doccuments/",
					"/"+name+"s Data/"+name+" Microscopy Files/Microscope Images/"
					
			},
			new String[] {
					"/"+name+"s Data/"+name+" Microscopy Files/"+name+" HP Doccuments/",
					"/"+name+"s Data/"+name+" Microscopy Files/Miocroscope Images/"
					
			}
			
	};
	
	
	public static void main(String[] args) {
		String example="/"+name+"s Data/"+name+" Microscopy Files/"+name+" HP Doccuments/C14 Domain Mapping/Oct 13 2013 RG clone 11 c14cn1 double rescue/ha red, percentrin green centriole cohession/delta n c14 plasmid/Image- 0056_taken_10_13_2013.zvi";
	
		File f=new File(example);
		
	
	}
	
	

	
	
	public File findFile(File file) {
		if (file.exists()) return file;
		
		File file2 = findFileInWorkingDirectory(file, getWorkingDir());
		if (file2!=null) return file2;
		
		for(String[] r: replacements) {
			String newpath=file.getAbsolutePath().replace(r[0],r[1]);
			file2 =new File(newpath);
			if (file2!=null) return file2;
			
		}
		
		return null;
	}
	
	
	/**Tries to find a file in the working dir*/
	File findFileInWorkingDirectory(File file, String workingDirectory) {
		File wd=new File(workingDirectory);
		if (!wd.isDirectory()) {
			wd=wd.getParentFile();
		}
		
		String path=wd.getAbsolutePath();
		if (!path.endsWith("/")) path+="/";
		
		path+=file.getName();
		
		//IssueLog.log("looking for file");
		//IssueLog.log(path);
		
		File newfilepath = new File(path);
		if (newfilepath.exists()) return newfilepath;
		
		return null;
	}


	private static String getWorkingDir() {
		return workingDir;
	}


	 private static void setWorkingDir(String workingDir) {
		FileFinder.workingDir = workingDir;
	}

	public static void setWorkingDir(File f) {
		if (f.isDirectory()) setWorkingDir(f.getAbsolutePath());
		else {
			setWorkingDir(f.getParent());
		}
		
	}
	
	
	/**Returns the stored file path.
	 if such a path does not exist,
	 it may return an alternate file path; 
	 */
	public String findExistingFilePath(String path) {
		if (path==null){
			return null;
		}
		
		File file=new File(path);
		if (file.exists()) return path;
		
		file=findFile(file);
		if (file.exists()) return file.getAbsolutePath();
		
		return path;
	}
	
	

}
