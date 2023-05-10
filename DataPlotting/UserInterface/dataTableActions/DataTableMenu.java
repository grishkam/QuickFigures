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
 * Version: 2023.2
 */
package dataTableActions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenuBar;

import basicMenusForApp.MenuBarForApp;
import basicMenusForApp.MenuItemForObj;
import dataTableDialogs.DataTable;
import logging.IssueLog;

/**
 
 * 
 */
public class DataTableMenu implements ActionListener {

	private JMenuBar menuBar;
	private DataTable dataTable;
	
	ArrayList<DataTableAction> actions=new ArrayList<DataTableAction>();
	
	HashMap<String, MenuItemForObj> itemsInstalled=new 	HashMap<String, MenuItemForObj>();
	
	
	public DataTableMenu() {
		actions=createActions();
		
	}

	/**
	 * @return 
	 * 
	 */
	public static ArrayList<DataTableAction> createActions() {
		ArrayList<DataTableAction> actions2=new ArrayList<DataTableAction>();
		actions2.add(new DistributeColumnsToTable());
		actions2.add(new CreateSampleSetupFile());
		actions2.add(new CreateFunctionFromDataTables());
		return actions2;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DataTableAction currentAction = (DataTableAction) itemsInstalled.get(e.getActionCommand());
		IssueLog.log("The action command was "+e.getActionCommand());
		currentAction.processTableAction(dataTable, new DataTableActionContext());
		
	}

	/**
	 * @param bar 
	 * @param dataTable 
	 * 
	 */
	public void installDefaultActionsOnMenuBar(JMenuBar bar, DataTable dataTable) {
		this.menuBar=bar;
		this.dataTable=dataTable;
		for(DataTableAction a: this.getActionList()) {
			MenuBarForApp.addItemToMenuBar(a, bar, this,itemsInstalled);
		}
	}
	
	public ArrayList<DataTableAction> getActionList() {
		return actions;}

}
