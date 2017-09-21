package net.gnu.explorer;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gnu.common.view.SlidingHorizontalScroll;
import android.support.v7.widget.*;
import android.support.v4.app.*;
import net.gnu.texteditor.*;
import net.gnu.explorer.SlidingTabsFragment.*;
import android.os.*;
import java.util.*;
import android.view.animation.*;
import android.content.Intent;
import android.widget.Button;
import android.graphics.PorterDuff;

public class SlidingTabsFragment extends Fragment implements TabAction, Width {

	private String TAG = "SlidingTabsFragment";
	private FragmentManager childFragmentManager;

	private SlidingHorizontalScroll mSlidingHorizontalScroll;

	private ViewPager mViewPager;
	public PagerAdapter pagerAdapter;

	ArrayList<PagerItem> mTabs = new ArrayList<PagerItem>();

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		Log.d(TAG, "onCreate " + savedInstanceState);
//		super.onCreate(savedInstanceState);
//	}

	private void initTabs() {
		File storage = new File("/storage");
		File[] fs = storage.listFiles();
		//String[] st = sdCardPath.split(":");
		File f;
		for (int i = fs.length - 1; i >= 0; i--) {
			f = fs[i];
			Log.d(TAG, f + ".");
			if (f.canWrite()) {
				mTabs.add(new PagerItem(f.getAbsolutePath(), ".*", true, null));
			}
		}
		mTabs.add(new PagerItem("/sdcard", "", true, null));

		mTabs.add(new PagerItem("/storage/MicroSD", "", true, null));
	}

	/**
	 * Inflates the {@link View} which will be displayed by this
	 * {@link Fragment}, from the app's resources.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView " + savedInstanceState);
		return inflater.inflate(R.layout.fragment_sample, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "onViewCreated " + this + ", " + savedInstanceState);

		final Bundle args = getArguments();

		if (savedInstanceState == null) {
			if (args == null) {
				initTabs();
			} else {
				int no = args.getInt("no");
				for (int i = 0; i < no; i++) {
					ContentFragment cf = ContentFragment.newInstance(SlidingTabsFragment.this, 
						args.getString(ExplorerActivity.EXTRA_DIR_PATH + i),
						args.getString(ExplorerActivity.EXTRA_SUFFIX + i),
						args.getBoolean(ExplorerActivity.EXTRA_MULTI_SELECT + i),
						args.getBundle("frag" + i));
					mTabs.add(new PagerItem(cf));
				}
			}
//		} else {
//			initTabs();
		}
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		if (childFragmentManager == null)
			childFragmentManager = getChildFragmentManager();

		if (savedInstanceState == null) {
			Log.d(TAG, "onViewCreated " + mTabs);
			pagerAdapter = new PagerAdapter(childFragmentManager);
			mViewPager.setAdapter(pagerAdapter);
			Log.d(TAG, "mViewPager " + mViewPager);
			if (args != null) {
				mViewPager.setCurrentItem(args.getInt("pos", 1), true);
			} else {
				mViewPager.setCurrentItem(1);
			}
		} else {
			mTabs.clear();
			//int i = 0;
			final List<Fragment> fragments = childFragmentManager.getFragments();
			final String firstTag = savedInstanceState.getString("fake0");
			final String lastTag = savedInstanceState.getString("fakeEnd");
			String tag;
			PagerItem pagerItem;
			ContentFragment frag;
			final int size = fragments.size();
			for (int i = 0; i < size; i++) {
				tag = savedInstanceState.getString(i + "");
				frag = (ContentFragment) childFragmentManager.findFragmentByTag(tag);
				if (frag != null) {
					pagerItem = new PagerItem(frag);
					pagerItem.dir = savedInstanceState.getString(frag.getTag());
					frag.CURRENT_PATH = pagerItem.dir;
					//Log.d(TAG, "onViewCreated frag " + i + ", " + tag + ", " + frag.getTag() + ", " + pagerItem.dir + ", " + frag);
					mTabs.add(pagerItem);
				}
			}
			if (firstTag != null) {
				mTabs.get(0).fakeFrag = (ContentFragment) childFragmentManager.findFragmentByTag(firstTag);
				mTabs.get(0).fakeFrag.CURRENT_PATH = mTabs.get(0).dir;
				mTabs.get(mTabs.size() - 1).fakeFrag = (ContentFragment) childFragmentManager.findFragmentByTag(lastTag);
				mTabs.get(mTabs.size() - 1).fakeFrag.CURRENT_PATH = mTabs.get(mTabs.size() - 1).dir;
			}
			
			//Log.d(TAG, "mTabs=" + mTabs);
			//Log.d(TAG, "fragments=" + fragments);
			// Get the ViewPager and set it's PagerAdapter so that it can
			// display items
			pagerAdapter = new PagerAdapter(childFragmentManager);
			mViewPager.setAdapter(pagerAdapter);
			mViewPager.setCurrentItem(savedInstanceState.getInt("pos", 0), true);
		}
		mViewPager.setOffscreenPageLimit(16);
		// BEGIN_INCLUDE (setup_slidingtablayout)
		// Give the SlidingTabLayout the ViewPager, this must be done AFTER the
		// ViewPager has had it's PagerAdapter set.
		mSlidingHorizontalScroll = (SlidingHorizontalScroll) view.findViewById(R.id.sliding_tabs);
		mSlidingHorizontalScroll.fra = SlidingTabsFragment.this;
		tabClicks = new TabClicks(12);

		mSlidingHorizontalScroll.setViewPager(mViewPager);
		mSlidingHorizontalScroll.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				//private int prevPage = -1;//previousState, currentState, 

				@Override
				public void onPageScrolled(int pageSelected, float positionOffset,
										   int positionOffsetPixel) {
//					Log.e("onPageScrolled", "pageSelected: " + pageSelected
//						+ ", positionOffset: " + positionOffset
//						+ ", positionOffsetPixel: " + positionOffsetPixel);
				}

				@Override
				public void onPageSelected(int pageSelected) {
					Log.d(TAG, "onPageSelected: " + pageSelected + ", mTabs.size() " + mTabs.size());

					final int size = mTabs.size();
					if (size > 1) {
						if (pageSelected == 0) {
							pageSelected = size;
							mViewPager.setCurrentItem(pageSelected , false);
						} else if (pageSelected == size + 1) {
							mViewPager.setCurrentItem(1, false);
							pageSelected = 1;
						}

						if (pageSelected == 1 || pageSelected == size) {
							final PagerItem pi = mTabs.get(size - pageSelected);
							if (pi.fakeFrag != null) {
								pi.fakeFrag.clone(pi.frag);
//								pi.fakeFrag.setDirectoryButtons();
//								//pi.fakeFrag.listView1.getAdapter().notifyDataSetChanged();
//								//pi.fakeFrag.setRecyclerViewLayoutManager();
//								int index = pi.frag.gridLayoutManager.findFirstVisibleItemPosition();
//								if (pi.fakeFrag.gridLayoutManager == null || pi.fakeFrag.gridLayoutManager.getSpanCount() != pi.fakeFrag.spanCount) {
//									pi.fakeFrag.listView1.removeItemDecoration(pi.fakeFrag.dividerItemDecoration);
//									pi.fakeFrag.gridLayoutManager = new GridLayoutManager(getContext(), pi.fakeFrag.spanCount);
//									pi.fakeFrag.listView1.setLayoutManager(pi.fakeFrag.gridLayoutManager);
//								}
//								
								pi.fakeFrag.status.setBackgroundColor(ExplorerActivity.IN_DATA_SOURCE_2);
//								
//								index = pi.frag.gridLayoutManager.findFirstVisibleItemPosition();
//								final View vi = pi.frag.listView1.getChildAt(0); 
//								final int top = (vi == null) ? 0 : vi.getTop();
//								pi.fakeFrag.gridLayoutManager.scrollToPositionWithOffset(index, top);
								
							}
						}
					}
					final ContentFragment createFragment = pagerAdapter.getItem(pageSelected);
					final ExplorerActivity activity = (ExplorerActivity) getActivity();
					activity.dir = createFragment.CURRENT_PATH;
					activity.curContentFrag = createFragment;
					activity.slideFrag2.getCurrentFragment2().select(false);
					
					createFragment.horizontalDivider6 = activity.slideFrag2.getCurrentFragment2().horizontalDivider6;
					createFragment.rightCommands = activity.slideFrag2.getCurrentFragment2().rightCommands;
					Log.e(TAG, "createFragment.leftCommands: " + createFragment.leftCommands);
					Log.e(TAG, "createFragment.rightCommands: " + createFragment.rightCommands);
//					if (activity.slideFrag2.pagerAdapter != null && activity.slideFrag2.getContentFragment2(Frag.TYPE.EXPLORER.ordinal()) == createFragment) {
//						createFragment.commands = createFragment.rightCommands;
//						createFragment.right = activity.left;
//						createFragment.left = activity.right;
//						createFragment.horizontalDivider = createFragment.horizontalDivider6;
//						createFragment.leftSize = -activity.leftSize;
//						createFragment.deletePastes = activity.deletePastesBtn2;
//					} else {
						createFragment.commands = createFragment.leftCommands;
						createFragment.right = activity.right;
						createFragment.left = activity.left;
						createFragment.horizontalDivider = createFragment.horizontalDivider11;
					createFragment.width.size = activity.leftSize;
					//createFragment.deletePastes = createFragment.deletePastesBtn;//activity.
					//}
					
					if (createFragment.selectedInList1.size() == 0 && activity.COPY_PATH == null && activity.MOVE_PATH == null) {
						if (createFragment.leftCommands.getVisibility() == View.VISIBLE) {
							createFragment.horizontalDivider11.setVisibility(View.GONE);
							createFragment.leftCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
							createFragment.leftCommands.setVisibility(View.GONE);
						}
					} else {
						createFragment.leftCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
						createFragment.leftCommands.setVisibility(View.VISIBLE);
						createFragment.horizontalDivider11.setVisibility(View.VISIBLE);
						createFragment.updateDelPaste();
					}
					
				}

				@Override
				public void onPageScrollStateChanged(int state) {
					//final int currentPage = mViewPager.getCurrentItem();
					//Log.e("onPageScrollStateChanged", "state:" + state + ", currentPage: " + currentPage);
//					final int size = mTabs.size() - 1;
//					if (currentPage == size || currentPage == 0) {
//						previousState = currentState;
//						currentState = state;
//						if (previousState == 1 && currentState == 0) {
//							mViewPager.setCurrentItem(currentPage == 0 ? size : 0, true);
//						}
//					}
				}
			});
		//Log.d(TAG, "mSlidingHorizontalScroll " + mSlidingHorizontalScroll);
		// BEGIN_INCLUDE (tab_colorizer)
		// Set a TabColorizer to customize the indicator and divider colors.
		// Here we just retrieve
		// the tab at the position, and return it's set color
		mSlidingHorizontalScroll.setCustomTabColorizer(new SlidingHorizontalScroll.TabColorizer() {
				@Override
				public int getIndicatorColor(int position) {
					return 0xffff0000;
				}

				@Override
				public int getDividerColor(int position) {
					return 0xff888888;
				}

			});
	}
	
	public boolean circular() {
		return true;
	}
	@Override
	public void addTab(Intent intent, String title) {
	}

	public void addTab(String dir, String suffix, boolean multi, Bundle bundle) {

		final ContentFragment frag = getCurrentFragment();
		if (dir == null) {
			dir = frag.CURRENT_PATH;
			suffix = frag.suffix;
			multi = frag.multiFiles;
			bundle = null;
		}
		
		final PagerItem pagerItem = new PagerItem(dir, suffix, multi, bundle);
		Log.d(TAG, "addTab1 pagerAdapter=" + pagerAdapter + ", dir=" + dir + ", mTabs=" + mTabs);

		final FragmentTransaction ft = childFragmentManager.beginTransaction();
		final ArrayList<PagerItem> mTabs2 = new ArrayList<PagerItem>(mTabs);
		if (mTabs.size() > 1) {
			final int size = mTabs.size();
			final int currentItem = mViewPager.getCurrentItem();

			SlidingTabsFragment.PagerItem get = mTabs.get(0);
			ft.remove(get.fakeFrag);
			get.fakeFrag = null;

			get = mTabs.get(size - 1);
			ft.remove(get.fakeFrag);
			get.fakeFrag = null;

			for (int j = 0; j < size; j++) {
				ft.remove(mTabs.remove(0).frag);
			}
			pagerAdapter.notifyDataSetChanged();
			ft.commitNow();
			
			for (PagerItem pi : mTabs2) {
				mTabs.add(pi.update());
			}
			mTabs.add(currentItem, pagerItem);
			mViewPager.setAdapter(pagerAdapter);
			mViewPager.setCurrentItem(currentItem + 1);
		} else {
			SlidingTabsFragment.PagerItem remove = mTabs.remove(0);
			ft.remove(remove.frag);
			pagerAdapter.notifyDataSetChanged();
			ft.commitNow();
			mTabs.add(remove);
			mTabs.add(pagerItem);
			pagerAdapter.notifyDataSetChanged();
			mViewPager.setCurrentItem(2);
		}
		notifyTitleChange();
		mTabs2.clear();
		Log.d(TAG, "addTab2 " + dir + ", " + mTabs);
	}

	public int size() {
		return mTabs.size();
	}

	void closeTab(ContentFragment m) {
		Log.e(TAG, "closeTab " + m + ", " + mTabs);

		int i = 0;
		final ArrayList<PagerItem> mTabs2 = new ArrayList<PagerItem>(mTabs);
		for (PagerItem pi : mTabs) {
			if (pi.frag == m) {
				Log.e(TAG, "closeTab " + i);
				break;
			}
			i++;
		}

		final FragmentTransaction ft = childFragmentManager.beginTransaction();
		SlidingTabsFragment.PagerItem pi = mTabs.get(0);
		ft.remove(pi.fakeFrag);
		pi.fakeFrag = null;
		pi = mTabs.get(mTabs.size() - 1);
		ft.remove(pi.fakeFrag);
		pi.fakeFrag = null;
		for (int j = mTabs2.size() - 1; j >= i; j--) {
			ft.remove(mTabs.remove(j).frag);
		}
		if (mTabs.size() == 1 && mTabs2.size() == 2) {
			pi = mTabs.remove(0);
			ft.remove(pi.frag);
			pi.fakeFrag = null;
		}
		//mTabs.clear();
		pagerAdapter.notifyDataSetChanged();
		ft.commitNow();

		mTabs2.remove(i);
		if (mTabs.size() == 0 && i > 0) {
			mTabs.add(mTabs2.get(0));
		}
		for (int j = i; j < mTabs2.size(); j++) {
			mTabs.add(mTabs2.get(j));
		}
		pagerAdapter.notifyDataSetChanged();
		mTabs2.clear();
		notifyTitleChange();
		mViewPager.setCurrentItem(i <= mTabs.size() - 1 && mTabs.size() > 1 ? i + 1: mTabs.size() == 1 ? 0 : i);
	}

	public void closeCurTab() {
		final ContentFragment main = getCurrentFragment();
		Log.d(TAG, "closeCurTab " + main);
		closeTab(main);
	}

	public void closeOtherTabs() {
		final ContentFragment cur = getCurrentFragment();
		Log.d(TAG, "closeOtherTabs " + cur);
		final int size = mTabs.size();
		int i = 0;
		final ArrayList<PagerItem> mTabs2 = new ArrayList<PagerItem>(mTabs);
		for (PagerItem pi : mTabs) {
			if (pi.frag == cur) {
				Log.e(TAG, "closeOtherTabs " + i);
				break;
			}
			i++;
		}

		final FragmentTransaction ft = childFragmentManager.beginTransaction();
		SlidingTabsFragment.PagerItem get = mTabs.get(0);
		ft.remove(get.fakeFrag);
		get.fakeFrag = null;
		get = mTabs.get(size - 1);
		ft.remove(get.fakeFrag);
		get.fakeFrag = null;
		for (int j = 0; j < size; j++) {
			ft.remove(mTabs.remove(0).frag);
		}
		pagerAdapter.notifyDataSetChanged();
		ft.commitNow();

		mTabs.add(mTabs2.get(i));
		mTabs2.clear();
		pagerAdapter.notifyDataSetChanged();
		notifyTitleChange();
		mViewPager.setCurrentItem(0);
	}

	void updateLayout(final boolean changeTime) {
		for (PagerItem pi : mTabs) {
			if (pi.frag != null) {
				pi.frag.refreshRecyclerViewLayoutManager();
				if (changeTime && pi.frag.getContext() != null) {
					pi.frag.updateColor(null);
					pi.frag.setDirectoryButtons();
				}
				int no = pi.frag.leftCommands.getChildCount();
				Button b;
				for (int i = 0; i < no; i++) {
					b = (Button) pi.frag.leftCommands.getChildAt(i);
					b.setTextColor(ExplorerActivity.TEXT_COLOR);
					b.getCompoundDrawables()[1].setAlpha(0xff);
					b.getCompoundDrawables()[1].setColorFilter(ExplorerActivity.TEXT_COLOR, PorterDuff.Mode.SRC_IN);
				}
				
			}
		}
	}

//	Bundle saveStates() {
//		final Bundle b = new Bundle();
//		final int size = mTabs.size();
//		b.putInt("no", size);
//		for (int i = 0; i < size; i++) {
//			ContentFragment createFragment = mTabs.get(i).createFragment();
//			b.putString(ExplorerActivity.EXTRA_DIR_PATH + i, createFragment.dir);
//			b.putString(ExplorerActivity.EXTRA_SUFFIX + i, createFragment.suffix);
//			b.putBoolean(ExplorerActivity.EXTRA_MULTI_SELECT + i, createFragment.multiFiles);
//			Bundle bfrag = new Bundle();
//			createFragment.onSaveInstanceState(bfrag);
//			b.putBundle("frag" + i, bfrag);
//		}
//		b.putInt("pos", mViewPager.getCurrentItem());
//		return b;
//	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState " + outState + ", " + childFragmentManager.getFragments());
		try {
			if (mTabs != null && mTabs.size() > 0) {
				int i = 0;
				for (PagerItem pi : mTabs) {
					Log.d(TAG, "onSaveInstanceState pi.frag.getTag() " + pi.frag.getTag() + ", " + pi.frag.CURRENT_PATH + ", " + pi);
					//childFragmentManager.putFragment(outState, "tabb" + i++, pi.frag);
					outState.putString(i++ + "", pi.frag.getTag());
					outState.putString(pi.frag.getTag(), pi.frag.CURRENT_PATH);
				}
				if (mTabs.size() > 1) {
					//Log.d(TAG, "fakeStart 0 tag" + mTabs.get(0).fakeFrag.getTag());
					outState.putString("fake0", mTabs.get(0).fakeFrag.getTag());
					//Log.d(TAG, "fakeEnd tag  " + mTabs.get(mTabs.size()-1).fakeFrag.getTag());
					outState.putString("fakeEnd", mTabs.get(mTabs.size()-1).fakeFrag.getTag());
				}
			}
			outState.putInt("pos", mViewPager.getCurrentItem());
		} catch (Exception e) {
			// Logger.log(e,"puttingtosavedinstance",getActivity());
			e.printStackTrace();
		}
		Log.d(TAG, "onSaveInstanceState2 " + outState + ", " + childFragmentManager);
	}

//	@Override
//	public void onStop() {
//		Log.d(TAG, "onStop " + mViewPager + ", " + mSlidingHorizontalScroll);
////		if (Build.VERSION.SDK_INT > 23) {
////			final FragmentTransaction ft = childFragmentManager.beginTransaction();
////			ft.remove(mTabs.get(0).fakeFrag);
////			mTabs.get(0).fakeFrag = null;
////			ft.remove(mTabs.get(mTabs.size() - 1).fakeFrag);
////			mTabs.get(mTabs.size() - 1).fakeFrag = null;
////			ft.commitAllowingStateLoss();
////		}
//		super.onStop();
//	}

//	@Override
//	public void onPause() {
//		Log.d(TAG, "onPause " + mViewPager + ", " + mSlidingHorizontalScroll);
////		if (Build.VERSION.SDK_INT <= 23) {
////			final FragmentTransaction ft = childFragmentManager.beginTransaction();
////			ft.remove(mTabs.get(0).fakeFrag);
////			mTabs.get(0).fakeFrag = null;
////			ft.remove(mTabs.get(mTabs.size() - 1).fakeFrag);
////			mTabs.get(mTabs.size() - 1).fakeFrag = null;
////			ft.commitAllowingStateLoss();
////		}
//		super.onPause();
//	}

	public void addNewTab(String fPath, String suffix, boolean multi, boolean notify) {
		addTab(fPath, suffix, true, null);
		if (notify) {
			mViewPager.getAdapter().notifyDataSetChanged();
			mSlidingHorizontalScroll.setViewPager(mViewPager);
		}
	}

	public void notifyTitleChange() {
		mSlidingHorizontalScroll.setViewPager(mViewPager);
	}

	public void setCurrentItem(int pos) {
		mViewPager.setCurrentItem(pos, true);
	}

	public int getCount() {
		return mTabs.size();
	}

	public int indexOf(final ContentFragment frag) {
		int i = 0;
		for (PagerItem pi : mTabs) {
			//Log.d(TAG, "indexOf frag " + frag + ", pi.frag " + pi.frag);
			if (frag == pi.frag) {
				return i;
			} else {
				i++;
			}
		}
		return -1;
	}

	public ContentFragment getCurrentFragment() {
		final int currentItem = mViewPager.getCurrentItem();
		Log.d(TAG, "getCurrentFragment." + currentItem + ", " + this);
		return pagerAdapter.getItem(currentItem);
	}

	/**
	 * This class represents a tab to be displayed by {@link ViewPager} and it's
	 * associated {@link SlidingTabLayout}.
	 */
	static class PagerItem implements Parcelable {
		private String dir;
		final String suffix;
		final boolean multi;
		Bundle bundle;
		private ContentFragment frag;
		private ContentFragment fakeFrag;

		private static final String TAG = "PagerItem";

		protected PagerItem(Parcel in) {
			dir = in.readString();
			suffix = in.readString();
			multi = in.readInt() != 0 ? true : false;
			bundle = in.readBundle();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(dir);
			dest.writeString(suffix);
			dest.writeInt(multi ? 1 : 0);
			dest.writeBundle(bundle);
		}

		public static final Parcelable.Creator<PagerItem> CREATOR = new Parcelable.Creator<PagerItem>() {
			public PagerItem createFromParcel(Parcel in) {
				return new PagerItem(in);
			}

			public PagerItem[] newArray(int size) {
				return new PagerItem[size];
			}
		};

		@Override
		public Object clone() {
			return new PagerItem(dir, suffix, multi, bundle);
		}

		PagerItem(final Fragment frag1) {
			Log.d(TAG, "tag=" + frag1.getTag() + ", " + frag1);
			this.frag = (ContentFragment) frag1;
			dir = frag.CURRENT_PATH;
			suffix = frag.suffix;
			multi = frag.multiFiles;
			//bundle = frag.bundle;
		}

		PagerItem(final String dir, final String suffix, final boolean multi, final Bundle bundle) {
			this.dir = dir;
			this.suffix = suffix;
			this.multi = multi;
			this.bundle = bundle;
		}

		/**
		 * @return A new {@link Fragment} to be displayed by a {@link ViewPager}
		 */
		ContentFragment createFragment(SlidingTabsFragment s) {
			Log.d(TAG, "createFragment() " + dir + ", " + frag);
			if (frag == null) {
				frag = ContentFragment.newInstance(s, dir, suffix, multi, bundle);
			}
//			if (fakeFrag != null) {
//				fakeFrag.clone(frag);
//			}
			return frag;
		}

		ContentFragment createFakeFragment(SlidingTabsFragment s) {
			Log.d(TAG, "createFakeFragment() " + fakeFrag + ", " + dir + ", " + frag);
			if (frag == null && fakeFrag == null) {
				fakeFrag = ContentFragment.newInstance(s, dir, suffix, multi, bundle);
				fakeFrag.fake = true;
			} else if (fakeFrag == null) {
				fakeFrag = ContentFragment.newOriFakeInstance(frag);
			} else if (fakeFrag != null && frag != null) {
				fakeFrag.clone(frag);
				//fakeFrag.refreshDirectory();
			}
			return fakeFrag;
		}

		/**
		 * @return the title which represents this tab. In this sample this is
		 *         used directly by
		 *         {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
		 */
		String getDir() {
			Log.d(TAG, "getDir() " + dir + ", " + frag);
			if (dir == null) {
				return "";
			} else if (frag == null || frag.CURRENT_PATH == null || frag.CURRENT_PATH.length() == 0) {
				if ("/".equals(dir)) {
					return "/";
				}
				return new File(dir).getName();
			} else {
				if ("/".equals(frag.CURRENT_PATH)) {
					return "/";
				}
				return new File(frag.CURRENT_PATH).getName();
			}
		}

		public PagerItem update() {
			dir = frag.CURRENT_PATH;
			return this;
		}

		@Override
		public String toString() {
			return dir + ", frag=" + frag + ", fakeFrag=" + fakeFrag;
		}

		/**
		 * @return the color to be used for indicator on the
		 *         {@link SlidingTabLayout}
		 */
		String getSuffix() {
			return suffix;
		}

		/**
		 * @return the color to be used for right divider on the
		 *         {@link SlidingTabLayout}
		 */
		boolean getMulti() {
			return multi;
		}
	}

	/**
	 * The {@link FragmentPagerAdapter} used to display pages in this sample.
	 * The individual pages are instances of {@link ContentFragment} which just
	 * display three lines of text. Each page is created by the relevant
	 * {@link SamplePagerItem} for the requested position.
	 * <p>
	 * The important section of this class is the {@link #getPageTitle(int)}
	 * method which controls what is displayed in the {@link SlidingTabLayout}.
	 */
	public class PagerAdapter extends FragmentPagerAdapter {

		PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * Return the {@link android.support.v4.app.Fragment} to be displayed at
		 * {@code position}.
		 * <p>
		 * Here we return the value returned from
		 * {@link SamplePagerItem#createFragment()}.
		 */
		@Override
		public ContentFragment getItem(final int position) {
			Log.d(TAG, "getItem " + position);
			//return mTabs.get(position).createFragment();
			if (mTabs.size() > 1) {
				if (position == 0) {
					return mTabs.get(mTabs.size() - 1).createFakeFragment(SlidingTabsFragment.this);
				} else if (position == mTabs.size() + 1) {
					return mTabs.get(0).createFakeFragment(SlidingTabsFragment.this);
				} else {
					return mTabs.get(position - 1).createFragment(SlidingTabsFragment.this);
				}
			} else {
				return mTabs.get(position).createFragment(SlidingTabsFragment.this);
			}
		}

		@Override
		public int getCount() {
			final int size = mTabs.size();
			if (size > 1) {
				return size + 2;
			} else {
				return size;
			}
		}

		// BEGIN_INCLUDE (pageradapter_getpagetitle)
		/**
		 * Return the title of the item at {@code position}. This is important
		 * as what this method returns is what is displayed in the
		 * {@link SlidingTabLayout}.
		 * <p>
		 * Here we return the value returned from
		 * {@link SamplePagerItem#getTitle()}.
		 */
		@Override
		public CharSequence getPageTitle(final int position) {
			//return mTabs.get(position).getDir();
			if (mTabs.size() > 1) {
				if (position == 0) {
					return "";//mTabs.get(mTabs.size() - 1).getTitle();
				} else if (position == mTabs.size() + 1) {
					return "";//mTabs.get(0).getTitle();
				} else {
					return mTabs.get(position - 1).getDir();
				}
			} else {
				return mTabs.get(position).getDir();
			}
		}
		// END_INCLUDE (pageradapter_getpagetitle)

		@Override
		public int getItemPosition(final Object object) {
			for (PagerItem pi : mTabs) {
				if (pi.frag == object) {
					//Log.d(TAG, "getItemPosition POSITION_UNCHANGED" + ", " + object);
					return POSITION_UNCHANGED;
				}
			}
			//Log.d(TAG, "getItemPosition POSITION_NONE" + ", " + object);
			return POSITION_NONE;
		}
	}

}
