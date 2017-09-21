package net.gnu.explorer;

import java.io.*;
import dalvik.system.*;
import java.util.*;
import net.gnu.util.*;
import android.app.*;
import com.google.classysharkandroid.dex.*;

public class FillClassesNamesThread extends Thread {
	
	private final byte[] bytes;
	private final ExplorerActivity activity;
	//private final ArrayList<String> classesList = new ArrayList<>();
	private final String sb1, sb2, sb3;
	private final File f;
	
	public FillClassesNamesThread(ExplorerActivity activity, byte[] bytes, File f, String sb1, String sb2, String sb3) {
		this.bytes = bytes;
		this.activity = activity;
		this.sb1 = sb1;
		this.sb2 = sb2;
		this.sb3 = sb3;
		this.f = f;
	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		final int methodCount = countMethod();
		activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					StringBuilder sb = new StringBuilder(sb1);
					sb.append("\nMethod count " + methodCount);
					sb.append(sb2).append(sb3);
					final String name = ExplorerApplication.PRIVATE_PATH + "/" + f.getName() + ".html";
					try {
						FileUtil.writeFileAsCharset(new File(name), sb.toString(), "utf-8");
					} catch (IOException e) {
						e.printStackTrace();
					}
					activity.slideFrag2.pagerAdapter.getItem(Frag.TYPE.WEB.ordinal()).load(name);
					activity.slideFrag2.mViewPager.setCurrentItem(Frag.TYPE.WEB.ordinal(), true);
				}
			});
	}

	int countMethod() {
		int methodCount = 0;
		try {
			
			final File incomeFile = File.createTempFile("classes" + Thread.currentThread().getId(), ".dex", activity.getCacheDir());

			FileUtil.bArr2File(bytes, incomeFile.getAbsolutePath());
			
			final File optimizedFile = File.createTempFile("opt" + Thread.currentThread().getId(), ".dex", activity.getCacheDir());

			final DexFile dx = DexFile.loadDex(incomeFile.getPath(),
										 optimizedFile.getPath(), 0);
			final Enumeration<String> classNames = dx.entries();
			final DexClassLoader loader = DexLoaderBuilder.fromBytes(activity, bytes);
			
			Class<?> loadClass;
			while (classNames.hasMoreElements()) {
				try {
					loadClass = loader.loadClass(classNames.nextElement());
//					Reflector reflector = new Reflector(loadClass);
//					reflector.generateClassData();
//					String result = reflector.toString();
					methodCount += loadClass.getConstructors().length;
					methodCount += loadClass.getMethods().length;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			incomeFile.delete();
			optimizedFile.delete();
		} catch (Exception e) {
			// ODEX, need to see how to handle
			e.printStackTrace();
		}
		return methodCount;
	}
}
