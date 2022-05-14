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
 * Date Created: Mar 26, 2022
 * Date Modified: Mar 26, 2022
 * Version: 2022.1
 */
package dataTableActions;

import java.io.File;

import javax.swing.Icon;

import org.apache.poi.ss.usermodel.Workbook;

import applicationAdapters.DisplayedImage;
import dataTableDialogs.ExcelTableReader;
import dataTableDialogs.TableReader;
import fileread.ReadExcelData;
import graphicalObjects.ZoomableGraphic;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**
 
 * 
 */
public abstract class BasicDataTableAction implements DataTableAction {

	protected boolean askForFile=false;

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		
		boolean tablesFound=false;
		DataTableActionContext context=new DataTableActionContext();
		if(diw!=null)for(ZoomableGraphic item: diw.getImageAsWorksheet().getTopLevelLayer().getObjectsAndSubLayers()) {
			if(item instanceof TableReader) {
				processTableAction((TableReader) item, context);
				tablesFound=true;
			}
		}
		
		
		
		if(tablesFound==false&&this.askForFile) {
			IssueLog.log("Could not find data file will ask user to open");
			File file = FileChoiceUtil.getOpenFile();
			if(file.getAbsolutePath().toLowerCase().endsWith("xlsx")) try {
				Workbook wb = ReadExcelData.fileToWorkBook(file.getAbsolutePath());
				ExcelTableReader tr = new  ExcelTableReader(wb,wb.getSheetAt(0), file.getAbsolutePath());
				processTableAction(tr, context);
			} catch (Throwable t) {
				IssueLog.logT(t);
			}
		} else if(tablesFound==false) {
			processTableAction(null, context);
		}

	}


	@Override
	public String getCommand() {
		return "Action 1"+this.getNameText();
	}



	@Override
	public String getMenuPath() {
		return "Tables";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public Icon getSuperMenuIcon() {
		return null;
	}
	
	

}
