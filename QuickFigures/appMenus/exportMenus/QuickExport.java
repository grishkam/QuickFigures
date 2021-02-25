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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package exportMenus;

import java.awt.Window;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import ultilInputOutput.FileChoiceUtil;

/**An abstract superclass for export menu items*/
public abstract class QuickExport  extends BasicMenuItemForObj {

	protected boolean openImmediately;

	
	public QuickExport(boolean openNow) {
		openImmediately=openNow;
	}
	public QuickExport() {
		this(false);
	}

	@Override
	public String getMenuPath() {
		return "File<Export";
	}
	
	/**Returns a user selected file path including the extension*/
	public File getFileAndaddExtension() {
		File f=showFileChooer(getExtension());
		if (f==null) return null;
		String fpath = f.getAbsolutePath();
		if (!fpath.endsWith("."+getExtension())) {
			fpath+="."+getExtension();
			f=new File(fpath);
		}
		return f;
	}

	protected  abstract String getExtension() ;
	
	
	File showFileChooer(String extensions) {
		FileChoiceUtil.ensureWindowsLook();
		
		JFileChooser jc = new JFileChooser( FileChoiceUtil.getWorkingDirectory());
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				getExtensionName() , getExtension());
		    jc.setFileFilter(filter);
		
		jc.showSaveDialog(null);
		
		if (jc.getSelectedFile()==null) return null;
		 FileChoiceUtil.setWorkingDirectory(jc.getSelectedFile().getParent());
		return jc.getSelectedFile();
	}
	
	
	/**shows the saved file*/
	public Window viewSavedFile(File f) {return null;}
	
	/**
	 * a method to be accessed by programmer. not implemented for all subclasses
	 saves the image in the given path
	 */
	public void saveInPath(DisplayedImage diw, String newpath) throws Exception {}

	protected  abstract String getExtensionName() ;
	
}
