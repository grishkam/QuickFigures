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
 * Date Modified: Jan 5, 2021
 * Version: 2022.2
 */
package infoStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintStream;

import figureFormat.DirectoryHandler;


/**A class that keeps a set of key value pairs in a string. 
 * that string is read directly from a file*/
public class FileBasedMetaWrapper extends StringBasedMetaWrapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String path;
	public  FileBasedMetaWrapper(String path) {
		this.path=path;
	}
	
	public  String getProperty() {
		return fileToString(new File(path));
	}
	
	public void setProperty(String newProp) {
		stringToFile(newProp, new File(path));
	}

	/**saves a string object to a file*/
	public static void stringToFile(String st, File f) {
		OutputStream fr;
		try {
			fr = new FileOutputStream(f);
		
		PrintStream ps = new PrintStream(fr);
		String[] lines=st.split('\n'+"");
		for(String key:lines) {
			if (key!=null&&!key.equals("null"))ps.println(key);
			}
		ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static String fileToString(File f) {
		try {
			
			BufferedReader vr = new BufferedReader(new FileReader(f));
			String line;
			
				line = vr.readLine();
			String output=line;
			while (line!=null) {
				line=vr.readLine();
				if (line!=null)
				output+='\n'+line;
			}
			vr.close();
		return output;
	}
	catch (Throwable t) {
		return null;
	}
	}
	
	public static void main(String[] args) {
		DirectoryHandler dh = new DirectoryHandler();
		FileBasedMetaWrapper meta = new FileBasedMetaWrapper(dh.getPrefsFile().getAbsolutePath());
		meta.setEntry("name", 6+"");
	}
}
