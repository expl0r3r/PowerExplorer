package net.gnu.explorer;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.util.*;

public class TabClicks {

	private static final String TAG = "TabFunctions";

	private final int maxTabs;
	
	public TabClicks(int maxTabs) {
		this.maxTabs = maxTabs;
	}

	public void click(final Context ctx, final TabAction fra, View v) {
		Log.d(TAG, "click " + ctx + ", " + fra + ", " + v);
		if (fra == null || ctx == null || v == null) {
			return;
		}
		final PopupMenu popup = new PopupMenu(ctx, v);

		popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
		final int count = fra.size();
		if (count == 1) {
			popup.getMenu().getItem(1).setEnabled(false);
			popup.getMenu().getItem(2).setEnabled(false);
		}
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					Log.d(TAG, "clicked " + item);
					int itemId = item.getItemId();
					if (R.id.close == itemId) {
						fra.closeCurTab();
					} else if (R.id.closeOthers == itemId) {
						fra.closeOtherTabs();
					} else if (R.id.newTab == itemId) {
						if (count < maxTabs) {
							fra.addTab(null, null, false, null);//, "utf-8",false, '\n');
						} else {
							Toast.makeText(ctx, "Maximum " + count + " tabs only", Toast.LENGTH_SHORT).show();
						}
					}
					return true;
				}
			});
		popup.show();
	}
}
