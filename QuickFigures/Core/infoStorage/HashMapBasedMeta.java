package infoStorage;

import java.util.HashMap;

public class HashMapBasedMeta extends BasicMetaInfoWrapper  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<String, String> map =new HashMap<String, String>();
	
	
	public HashMapBasedMeta() {}
	public HashMapBasedMeta(String st) {
		if (st==null)st="";
		String[] sts = st.split(""+'\n');
		for(String s: sts) {
			String[] keyval = s.split("=");
			if (keyval.length>1) {
				map.put(keyval[0], keyval[1]);
			}
		}
	}
	
	public String toAString() {
		String output="";
		for(String key: map.keySet()) {
			if (key==null) continue;
			output+='\n'+key+"="+map.get(key);
		}
		return output;
	}
	
	public HashMapBasedMeta(StringBasedMetaWrapper st) {
		this(st.getProperty());
	}

	@Override
	public void setEntry(String entryname, String number) {
		map.put(entryname, number);
		
	}

	@Override
	public void replaceInfoMetaDataEntry(String b, String entryC) {
		map.put(b, entryC);
		
	}

	@Override
	public String getEntryAsString(String b) {
		// TODO Auto-generated method stub
		return map.get(b);
	}

	@Override
	public void removeEntry(String entryname) {
		map.remove(entryname);
		
	}
	
	

}
