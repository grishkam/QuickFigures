package exportMenus;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ultilInputOutput.FileChoiceUtil;

public abstract class QuickImport  extends QuickExport {

	
	@Override
	public String getMenuPath() {
		return "File<Import";
	}
	
	File showFileChooer(String extensions) {
		FileChoiceUtil.ensureWindowsLook();
		
		JFileChooser jc = new JFileChooser( FileChoiceUtil.getWorkingDirectory());
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				getExtensionName() , getExtension());
		    jc.setFileFilter(filter);
		
		jc.showOpenDialog(null);
		
		if (jc.getSelectedFile()==null) return null;
		 FileChoiceUtil.setWorkingDirectory(jc.getSelectedFile().getParent());
		return jc.getSelectedFile();
	}

}
