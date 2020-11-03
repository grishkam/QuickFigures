package fileread;

import exportMenus.QuickImport;

public abstract class ExcelDataImport extends QuickImport {
	@Override
	protected String getExtension() {
		return "xlsx";
	}

	@Override
	protected String getExtensionName() {
		// TODO Auto-generated method stub
		return "excel files";
	}
}
