/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package ultilInputOutput;

import java.io.File;

/** Sometimes a file might be saved in a differed path than the stored directory
  this class stores a working directory name where it looks for any files if it 
  cannot locate them in their proper locations. This is only relevant if the user
  chooses load from file (in a certain options dialog), subsequently then saves the figure
  and tries to reopen it despite the folder with both figure and file moving.
  In the current version of QuickFigures, the users are not likely to select this option and
  I am considering removing it
*/
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
	

	/**Attempts to find file in its normal path
	 * if fails, looks in the working directory that is set for this class*/
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
	
	
	/**Tries to find a file with the same name as the given file in the working directory given*/
	File findFileInWorkingDirectory(File file, String workingDirectory) {
		File wd=new File(workingDirectory);
		if (!wd.isDirectory()) {
			wd=wd.getParentFile();
		}
		
		String path=wd.getAbsolutePath();
		if (!path.endsWith("/")) path+="/";
		
		path+=file.getName();
		
		
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
