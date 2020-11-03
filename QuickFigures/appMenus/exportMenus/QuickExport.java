package exportMenus;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import basicMenusForApp.BasicMenuItemForObj;
import ultilInputOutput.FileChoiceUtil;

public abstract class QuickExport  extends BasicMenuItemForObj {

	
	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
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
		FileChoiceUtil.ensureWidowsLook();
		
		JFileChooser jc = new JFileChooser( FileChoiceUtil.getWorkingDirectory());
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				getExtensionName() , getExtension());
		    jc.setFileFilter(filter);
		
		jc.showSaveDialog(null);
		
		if (jc.getSelectedFile()==null) return null;
		 FileChoiceUtil.setWorkingDirectory(jc.getSelectedFile().getParent());
		return jc.getSelectedFile();
	}

	protected  abstract String getExtensionName() ;
}
