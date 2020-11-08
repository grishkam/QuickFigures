import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import applicationAdaptersForImageJ1.IJ1ToolSetContainer2;
import applicationAdaptersForImageJ1.ImagePlusDisplayWrap;
import figureTemplates.DirectoryHandler;
import graphicActionToombar.CurrentSetInformerBasic;
import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import imageDisplayApp.KeyDownTracker;
import includedToolbars.ActionToolset1;
import includedToolbars.ActionToolset2;
import includedToolbars.LayoutToolSet;
import includedToolbars.ObjectToolset1;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**This class is used by imageJ to make the toolbars appear*/
public class Toolset_Runner implements PlugIn {
	static boolean firstRun=true;

	@Override
	public void run(String arg0) {
		if (firstRun) {
			onFirstRun();
			firstRun=false;
		}
		
		
		
		if (arg0.equals("file list")) {
			///new FileListGui().run("gui");
		}
		if (arg0.equals("Position Action Tools")) {
			new ActionToolset1().run("");
			new ActionToolset2().run("");
		}
		/**if (arg0.equals("Graphic")) {
			new ObjectToolset().run("");
		}*/
		
		if (arg0.equals("LayoutToolSet")) {
			LayoutToolSet tsr = new LayoutToolSet();
			IJ1ToolSetContainer2 tsc1 = new IJ1ToolSetContainer2(tsr);
			tsr.addToolChangeListener(tsc1);
			tsr.run("");
			
		}
		
		if (arg0.equals("Specify...")) {
			IJ.doCommand("Specify...");
		}
		
		if (arg0.equals("FigureWizApp")||arg0.equals("Object Tools")) {
			
			ObjectToolset1 tsr = new ObjectToolset1();
			IJ1ToolSetContainer2 tsc1 = new IJ1ToolSetContainer2(tsr);
			
			tsr.addToolChangeListener(tsc1);
			tsr.run("");
			new ActionToolset1().run("");
		}

	}
	
	
	
	private void onFirstRun() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		ImagePlus.addImageListener(new  windowAndSet());
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		try {
			if (!DirectoryHandler.getDefaultHandler().defaultTemplateExits())
					checkForAutoRun() ;
			} catch (Throwable t) {
			IssueLog.log(t);
		}
		
	}
	
	
	static String macrosStartupFilePath() {return IJ.getDirectory("macros")+"StartupMacros.txt";}
	static String macrosStartupFilePathIJM() {return IJ.getDirectory("macros")+"RunAtStartup.ijm";}
	
	public static boolean fileContainsString(File f, String contained, String nonContained) throws FileNotFoundException {
		if (!f.exists()) return false;
		
		Scanner scanner = new Scanner(f);
		 boolean hasString=false;
		    while (scanner.hasNextLine()) {
		        String line = scanner.nextLine();
		      
		        if(line.contains(contained) && (nonContained==null||!line.contains(nonContained))) { 
		            hasString=true;
		        }
		    	}
		    scanner.close();
		    
		    return hasString;
	}
	
	static boolean hasAutoRun() throws Exception {
		return hasAutoRunInTXTfile()||hasAutoRunInIJMfile();
	}
	
	static boolean hasAutoRunInTXTfile() throws Exception {
		String macrosPath = IJ.getDirectory("macros");
		
		if (macrosPath!=null) {
			
			File f=new File(macrosStartupFilePath());
			if (!f.exists()) return false;
			boolean hasAutoRun=fileContainsString(f, "AutoRun", "//");
			
			    return hasAutoRun;
	}
		else return false;
		
	}
	
	static boolean hasAutoRunInIJMfile() throws Exception {
		String macrosPath = IJ.getDirectory("macros");
		
		if (macrosPath!=null) {
			
			File f=new File(macrosStartupFilePathIJM());
			
			
			if (!f.exists()) return false;
			boolean hasAutoRun=fileContainsString(f, "Layout Tools", null);
			
			    return hasAutoRun;
	}else
		return false;
	}
		
		
	public static void checkForAutoRun() throws Exception {
		
			    
			    if (!hasAutoRun() ) {
			 
			    	boolean start =   FileChoiceUtil.yesOrNo("Open QuickFigures toolbars whenever ImageJ starts?"); 
			    	IssueLog.log("Will write to startup macros file");
			    	if (start) try {
			    		String st="";
			    		if (FileChoiceUtil.yesOrNo("Do you want to automatically color channels and add slice labels after opening ZVI files?" )) {
			    			st="Z";
			    		}
			    		
			    		File mFile = new File( macrosStartupFilePathIJM());
			    		if (!mFile.exists()) { 
			    			FileChoiceUtil.writeLocalStringToFile(macrosStartupFilePathIJM(), st+"AutoRunMacro2.txt", Toolset_Runner.class.getClassLoader(), false);
			    		}
			    			else
			    		if (mFile.exists()) {
			    			FileChoiceUtil.addLocalStringToFile(macrosStartupFilePathIJM(), st+"AutoRunMacro2.txt", Toolset_Runner.class.getClassLoader());
			    		}
			    		else FileChoiceUtil.addLocalStringToFile(macrosStartupFilePath(), st+"AutoRunMacro.txt", Toolset_Runner.class.getClassLoader());
			    	
			    	
			    	} catch (Exception e) {IssueLog.log(e);}
			    }
		
	}
		
	
	class windowAndSet implements WindowListener, ImageListener {
		
		KeyListener kd=new KeyDownTracker();//not as important in recent versions. might not need the image listener added at all

		@Override
		public void windowActivated(WindowEvent arg0) {
			if (arg0.getWindow() instanceof ImageWindow) {
				ImageWindow iw=(ImageWindow) arg0.getWindow();
				CurrentSetInformerBasic.setCurrentActiveDisplayGroup(new ImagePlusDisplayWrap(iw.getImagePlus()));
		
				iw.getCanvas().addKeyListener(kd);
				
			}
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			if (arg0.getWindow() instanceof ImageWindow) {
				ImageWindow iw=(ImageWindow) arg0.getWindow();
		
				iw.getCanvas().removeKeyListener(kd);
			}
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void imageClosed(ImagePlus arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void imageOpened(ImagePlus arg0) {
			arg0.getWindow().addWindowListener(this);
			
		}

		@Override
		public void imageUpdated(ImagePlus arg0) {
			// TODO Auto-generated method stub
			
		}
}

	
	
	
}
