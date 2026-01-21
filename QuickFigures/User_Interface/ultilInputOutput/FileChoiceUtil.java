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
 * Date Modified: Mar 26, 2022
 * Version: 2023.2
 */
package ultilInputOutput;


import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import appContext.CurrentAppContext;
import figureFormat.DirectoryHandler;
import logging.IssueLog;

/**this class contains methods related to file dialogs, opening, and saving files*/
public class FileChoiceUtil {
	private static String workingDirectory=DirectoryHandler.getDefaultHandler().getFigureFolderPath();
	private static HashMap<String, String> recentPaths=new HashMap<String, String>();
	

	
	/**Displays a modal dialog that presents the user with a question. returns the answer */
	public static boolean yesOrNo(String s) {
			int i=JOptionPane.showConfirmDialog(null, 	
					s,
				    "",
				    JOptionPane.YES_NO_OPTION
				    );
			if (i==JOptionPane.YES_OPTION) {
				return true;
				
			}
		
		return false;
	
}
	
	
	/**Displays a modal dialog that presents the user with a message and the option to click ok*/
	public static boolean OkOrNo(String s) {
		JOptionPane.showMessageDialog(null, s);
	return false;
}
	
	/**sets the look and feel for file choosers*/
	public static void ensureWindowsLook() {
		if (IssueLog.isWindows()) {
			 try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				IssueLog.logT(e);
			} 
		}
	}

	/**Allows the user to select many input files*/
	public static File[]  getFiles() {
		ensureWindowsLook();
		
		JFileChooser jd= new JFileChooser(CurrentAppContext.getDefaultDirectory()); jd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES ); jd.setDialogTitle("Choose input files"); jd.setMultiSelectionEnabled(true) ; jd.showOpenDialog(null); 
		  File[] files=jd.getSelectedFiles();
		  return files;
	}
	
	/**Allows the user to select many input files*/
	public static File  getFolder(String message) {
		ensureWindowsLook();
		
		JFileChooser jd= new JFileChooser(CurrentAppContext.getDefaultDirectory()); jd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY ); jd.setDialogTitle("Choose input folder");  
		jd.setName(message);
		jd.showOpenDialog(null); 
		 
		  return jd.getSelectedFile();
	}
	
	/**displays a dialog to the user for choosing files and returns the list of files as an array*/
	public static ArrayList<File> getFileArray()  {
		ArrayList<File> files=new ArrayList<File>();
			 File[] files2 = FileChoiceUtil.getFiles();
			 for(File f: files2) {files.add(f);}
		 return files;
	}
	
	/**opens a file dialog that resembles the imagej file dialog*/
	private static File showIJDDFileDialog(FileDialog fd, String dir, String name) {
		String st = determineStartingDirectoryAndSetupDialog(fd, dir, name, false);
	       if (st==null) return null;
	       
	       return new File(fd.getDirectory()+File.separator+fd.getFile());
	}
	
	/**sets up the appearance and the starting directory for a file open dialog
	 * @param fd
	 * @param dir
	 * @param name
	 * @return
	 */
	public static String determineStartingDirectoryAndSetupDialog(FileDialog fd, String dir, String name, boolean multipleDialog) {
		ensureWindowsLook();
		if (dir==null) {
			dir=name;
			
		}
		if (dir==null) {
			dir="";
			
		}
		
		
		File startingDir=new File(dir);
		
		if (startingDir.isDirectory()) {
			fd.setDirectory(dir);
		}
		else {
			fd.setDirectory(startingDir.getParent());
				
				
				
		}  
		
		if (name==null&&startingDir!=null)
			fd.setFile(startingDir.getName());
		else 
			if(name!=null)
				fd.setFile(name);
		 fd.setMultipleMode(multipleDialog);
		
	       fd.setVisible(true);
	       String st=fd.getFile();
	      
		return st;
	}
	
	/**opens a file dialog that resembles the imagej file dialog*/
	private static ArrayList<File> showMultipleFileDialog(FileDialog fd, String dir, String name) {
		if(fd==null) {
			 fd=new  FileDialog(new JFrame(), "Open ", FileDialog.LOAD);
		}
		fd.setMultipleMode(true);
		String st = determineStartingDirectoryAndSetupDialog(fd, dir, name, true);
	       if (st==null) return null;
	       ArrayList<File> output = new ArrayList<File>();
	      File[] files = fd.getFiles();
	      for(File f: files) {
	    	  if(f==null)
	    		  continue;
	    	  output.add(f);
	      }
	      return output;
	}
	
	
	/**shows a file dialog for the user to open a file in the default directory*/
	public static File  getOpenFile() {
		String dd = CurrentAppContext.getDefaultDirectory();
		return getOpenFile(dd, true);
	}
	
	/**shows a file dialog for the user to open a file with a specific default directory*/
	public static File  getOpenFile(String type) {
		String dd = recentPaths.get(type);
		if(dd==null)
			dd = CurrentAppContext.getDefaultDirectory();
		File openFile = getOpenFile(dd, true);
		if(openFile!=null)
			recentPaths.put(type, openFile.getAbsolutePath());
		return openFile;
	}
	
	/**shows a file dialog for the user to open a file in the given directory*/
	public static File getOpenFile(String dd, boolean modal) {
		ensureWindowsLook();
		 FileDialog fd = new  FileDialog(new JFrame(), "Open ", FileDialog.LOAD);
		 fd.setModal(modal);
		 if (!(new File(dd).isDirectory())) {
			 dd=(new File(dd)).getParent();
		 }
		 if(dd==null) {
			 dd=CurrentAppContext.getDefaultDirectory();
		 }
		
	       return showIJDDFileDialog(fd, dd, null);
	      
	}
	

	/**shows a file dialog for the user to save a file in the default directory*/
	public static File  getSaveFile() {
		return getSaveFile(CurrentAppContext.getDefaultDirectory(), null); 
	}
	/**shows a file dialog for the user to open a file in the default directory for this class*/
	public static File  getSaveFileInternalWD() {
		return getSaveFile(getWorkingDirectory(), null); 
	}
	/**shows a file dialog for the user to save a file in the directory 'path' with name 'name'*/
	public static File  getSaveFile(String path, String name) {
		 FileDialog fd = new  FileDialog(new JFrame(), "Open ", FileDialog.SAVE);
	       return showIJDDFileDialog(fd,path , name );	      
	}
	
	/**shows a file dialog for the user to save a file in the default directory*/
	public static File  getSaveFile(String name) {
		return getSaveFile(CurrentAppContext.getDefaultDirectory(), name); 
	}
	
	/**Uses a JFileChooser to get a file from the user*/
	public static File getUserOpenFile() {
		FileChoiceUtil.ensureWindowsLook();
		JFileChooser jc = new JFileChooser( FileChoiceUtil.getWorkingDirectory());
		jc.showOpenDialog(null);
		File f=jc.getSelectedFile();
		return f;
	}

	/**Returns the default directory for this class*/
	public static String getWorkingDirectory() {
		if (workingDirectory==null) {
			workingDirectory=DirectoryHandler.getDefaultHandler().getFigureFolderPath();
		}
		if (workingDirectory==null) {
			workingDirectory="";
		}
		
		return workingDirectory;
	}
	/**Sets the default directory for this class*/
	public static void setWorkingDirectory(String workingDirectory) {
		FileChoiceUtil.workingDirectory = workingDirectory;
		DirectoryHandler.getDefaultHandler().getPrefsStorage().setEntry("WD", FileChoiceUtil.workingDirectory);
	}
	
	
	/**When given an output file name, finds a particular local resource and appends it to the file*/
	public static void addLocalStringToFile(String filename, String localFile, ClassLoader cl) throws Exception {
		writeLocalStringToFile(filename, localFile, cl, true);
	}
	
	/**When given an output file name, finds a particular local resource and appends it to the file*/
	public static void writeLocalStringToFile(String filename, String localFile, ClassLoader cl, boolean append) throws Exception {
	 	InputStream stream = cl.getResourceAsStream(localFile);
    	
	 	if (stream==null){IssueLog.log( "did not find file"); return;}
	 	
    	Scanner scan2 =  new Scanner(stream);
    
	     FileWriter fw = new FileWriter(filename,append); //the true will append the new data
	    IssueLog.log("will add to " +filename);
	 	
    	while(scan2.hasNextLine()) {
    		String line = scan2.nextLine();
    		fw.write('\n'+line);//appends the string to the file
    		
	}
    	fw.close();
    	scan2.close();
	
}
	
	/**returns the path of the documents folder for the user*/
	public static String pathOfDocumentsFolder() {
		String docfolder=new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
		if (IssueLog.isWindows() && docfolder.contains("Documents")) return docfolder;
		return new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+"/Documents";
	}
	
	/**returns the path of the documents folder for the user*/
	public static String pathOfDocumentsFolderWithHome() {
		return "~"+"/Documents";
	}
	
	/**Reads the input stream and returns the content as a string*/
	public static String readStringFrom(InputStream is) {
		BufferedReader br = new BufferedReader( new InputStreamReader(is));
		String sr="";
		String output="";
		boolean firstLine=true;
		while (sr!=null) {
			try {
				sr=br.readLine();
				if (sr!=null) 
					{
					if (firstLine) {firstLine=false; output+=sr;}
					else output+='\n'+sr;
					}
			} catch (IOException e) {
				break;
			}
		}
		return output;
	}
	
	/**Asked the user if the file should be overwritten
	 * returns yes without asking if the file does not exist*/
	public static int overrideQuestion(File f) {
		if (f.exists()) {
			int i=JOptionPane.showConfirmDialog(null, 	
					"Are you sure?. This will eliminate the existing file\n"
				    + f.getName()+"\n",
				    "Override?",
				    JOptionPane.YES_NO_OPTION
				    );
			if (i==JOptionPane.YES_OPTION) {
				f.delete();
				
			}
			return i;
		}
		return JOptionPane.YES_OPTION;
	}
	
	/**Asks permission to override the file*/
	public static boolean overridePermission(File f) {
		return  overrideQuestion( f)==	JOptionPane.YES_OPTION;
	}
	
	
	public static void main(String[] args) {
		
		
		
		//ArrayList<File> s = showMultipleFileDialog();
		//for(File s1: s) {
			//System.out.println(s1);
		//}
	}
	/**
	 * @return
	 */
	public static ArrayList<File> showMultipleFileDialog() {
		return showMultipleFileDialog(null, workingDirectory, "");
	}
}
