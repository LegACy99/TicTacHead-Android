package net.ark.tictachead.helpers;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

	public static int generateViewID() {
		for (;;) {
			final int result = s_NextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
			if (s_NextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	//Static
	protected static final AtomicInteger s_NextGeneratedId = new AtomicInteger(1);
}
