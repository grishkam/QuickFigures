package ultilInputOutput;


import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import appContext.CurrentAppContext;
import figureTemplates.DirectoryHandler;
import logging.IssueLog;

public class FileChoiceUtil {
	private static String workingDirectory=DirectoryHandler.getDefaultHandler().getFigureFolderPath();
	
	public static boolean overrideExistingOrNot(File f) {
		
			if (f.exists()) {
				int i=JOptionPane.showConfirmDialog(null, 	
						"Are you sure?. This will eliminate the existing file\n"
					    + new File("").getName()+"\n",
					    "Override?",
					    JOptionPane.YES_NO_OPTION
					    );
				if (i==0) {
					IssueLog.log("will override");
					return true;
					
				}
			}
			return false;
		
	}
	
	
	public static boolean yesOrNo(String s) {
			int i=JOptionPane.showConfirmDialog(null, 	
					s,
				    "",
				    JOptionPane.YES_NO_OPTION
				    );
			if (i==0) {
				return true;
				
			}
		
		return false;
	
}
	
	public static boolean OkOrNo(String s) {
		JOptionPane.showMessageDialog(null, s);
		
	
	
	return false;

}
	
	public static void ensureWidowsLook() {
		if (IssueLog.isWindows()) {
			 try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				IssueLog.log(e);
			} 
		}
	}

	/**Allows the user to select many input files*/
	public static File[]  getFiles() {
		ensureWidowsLook();
		
		JFileChooser jd= new JFileChooser(CurrentAppContext.getDefaultDirectory()); jd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES ); jd.setDialogTitle("Choose input file"); jd.setMultiSelectionEnabled(true) ; jd.showOpenDialog(null); 
		  File[] files=jd.getSelectedFiles();
		  return files;
	}
	
	public static ArrayList<File> getFileArray()  {
		ArrayList<File> files=new ArrayList<File>();
		 
			 File[] files2 = FileChoiceUtil.getFiles();
			 for(File f: files2) {files.add(f);}
		 
		 
		 return files;
	}
	
	private static File showIJDDFileDialog(FileDialog fd, String dir, String name) {
		ensureWidowsLook();
		if (dir==null) {
			dir=name;
			
		}
		if (dir==null) {
			dir="";
			
		}
		
		
		File startingDir=new File(dir);
		
		if (startingDir.isDirectory()) {
		fd.setDirectory(dir);}
		else {
			fd.setDirectory(startingDir.getParent());
				if (name==null)
				fd.setFile(startingDir.getName());
				else fd.setFile(name);
		}  
	       fd.setVisible(true);
	       String st=fd.getFile();
	       if (st==null) return null;
	       
	       return new File(fd.getDirectory()+File.separator+fd.getFile());
	}
	
	public static File  getOpenFile() {
		String dd = CurrentAppContext.getDefaultDirectory();
		return getOpenFile(dd);
		
		 /**
		JFileChooser jd= new JFileChooser(OpenDialog.getDefaultDirectory()); jd.setFileSelectionMode(JFileChooser.FILES_ONLY ); jd.setDialogTitle("Choose input file");  jd.showOpenDialog(null); 
		  File files=jd.getSelectedFile();
		  return files;*/
	}
	
	public static File getOpenFile(String dd) {
		ensureWidowsLook();
		 FileDialog fd = new  FileDialog(new JFrame(), "Open ", FileDialog.LOAD);
	       
		
		 if (!(new File(dd).isDirectory())) {
			 dd=(new File(dd)).getParent();
		 }
	       return showIJDDFileDialog(fd, CurrentAppContext.getDefaultDirectory(), null);
	      
	}
	

	public static File  getSaveFile() {
		return getSaveFile(CurrentAppContext.getDefaultDirectory(), null); 
	}
	
	public static File  getSaveFileInternalWD() {
		return getSaveFile(getWorkingDirectory(), null); 
	}
	
	public static File  getSaveFile(String path, String name) {
		 FileDialog fd = new  FileDialog(new JFrame(), "Open ", FileDialog.SAVE);
	       return showIJDDFileDialog(fd,path , name );
		/**
		JFileChooser jd= new JFileChooser(path); 
		jd.setFileSelectionMode(JFileChooser.FILES_ONLY );
		jd.setDialogTitle("Choose input file"); 
		jd.showSaveDialog(null); 
		 File files=jd.getSelectedFile();
		  return files;*/
	      
	}
	
	/**Uses a JFileChooser to get a file from the user*/
	public static File getUserOpenFile() {
		FileChoiceUtil.ensureWidowsLook();
		JFileChooser jc = new JFileChooser( FileChoiceUtil.getWorkingDirectory());
		jc.showOpenDialog(null);
		File f=jc.getSelectedFile();
		return f;
	}

	public static String getWorkingDirectory() {
		if (workingDirectory==null) {
			workingDirectory=DirectoryHandler.getDefaultHandler().getFigureFolderPath();
		}
		if (workingDirectory==null) {
			workingDirectory="";
		}
		
		return workingDirectory;
	}

	public static void setWorkingDirectory(String workingDirectory) {
		FileChoiceUtil.workingDirectory = workingDirectory;
		DirectoryHandler.getDefaultHandler().getPrefsStorage().setEntry("WD", FileChoiceUtil.workingDirectory);
	}
	
	public static void main(String[] args) {
		
		/** Sets the Swing look and feel to the system look and feel (Windows only). */
	    
	       
	        try {
	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	          
	        } catch(Throwable t) {}
	       
	        
	       
	    
		//getFile();
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
    	 // IssueLog.log("done " +filename);
	
}
	
	public static String pathOfDocumentsFolder() {
		String docfolder=new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
		//IssueLog.log("home is "+ new JFileChooser().getFileSystemView().getHomeDirectory());
		if (IssueLog.isWindows() && docfolder.contains("Documents")) return docfolder;
		return new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+"/Documents";
	}
	
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
}
