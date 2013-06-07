package net.ark.tictachead.helpers;

import java.util.List;

public class Utilities {
	protected Utilities() {
		//Do nothing
	}
	
	public static long[] createArray(List<Long> list) {
		//Initialize
		long[] Array = new long[]{};
		if (list != null && !list.isEmpty()) {
			//Create
			Array = new long[list.size()];
			for (int i = 0; i < list.size(); i++) Array[i] = list.get(i).longValue();
		}
		
		//Return
		return Array;
	}
}
