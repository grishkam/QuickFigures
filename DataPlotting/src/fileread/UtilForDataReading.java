package fileread;

import java.util.ArrayList;

public class UtilForDataReading {

	public static ArrayList<String> getUniqueStrings(ArrayList<String> input) {
			ArrayList<String> output=new ArrayList<String>();
			
			for(String s: input) {
				if (!output.contains(s)&&s!=null&&!s.equals("null")) output.add(s);
			}
			return output;
	}

}
