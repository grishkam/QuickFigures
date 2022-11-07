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
 * Date Modified: Jan 7, 2021
 * Version: 2022.2
 */
package dataTableDialogs;

import java.awt.Color;
import java.util.ArrayList;

/**an interface for any sort of target that takes the form of a table*/
public interface TableReader {

	/**returns the value at the row and column given*/
	Object getValueAt(int rowNumber, int checkCol);

	void setValueAt(Object value, int rowNumber, int colNumber);
	
	int getRowCount();
	int getColumnCount();
	
	public void saveTable(boolean b, String outputFileName);

	/**
	 * @param newParam TODO
	 * @return
	 */
	TableReader createNewSheet(String newParam);

	/**
	 * @return
	 */
	String getOriginalSaveAddress();

	/**
	 * @param i
	 * @param j
	 */
	void setWrapTextAt(int i, int j);

	/**
	 * @param color
	 */
	void setCellColor(Color color, int i, int j);

	/**
	 * @param c
	 */
	void setupColorMap(ArrayList<Color> c);

	/**
	 * @param i
	 * @return
	 */
	Object getSheetName(int i);

}
