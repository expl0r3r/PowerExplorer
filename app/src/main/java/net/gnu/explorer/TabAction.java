package net.gnu.explorer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.content.Intent;

public interface TabAction {
	TabClicks tabClicks;
	public boolean circular();
	public void closeCurTab();
	public void closeOtherTabs();
	public void addTab(String dir, String suffix, boolean multi, Bundle bundle);
	public void addTab(final Intent intent, String title);
	public int size();
}

interface Width {
	int size;
}
