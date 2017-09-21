package net.gnu.explorer;

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
import android.content.res.*;
import android.support.v7.widget.*;
import net.gnu.texteditor.*;
import android.support.v4.app.*;
import android.os.*;
import java.util.*;
import android.view.animation.*;
import android.widget.Button;
import android.graphics.PorterDuff;

/**
 * A basic sample which shows how to use
 * {@link com.example.android.common.view.SlidingTabLayout} to display a custom
 * {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class SlidingTabsFragment2 extends Fragment implements Width {

	private static String TAG = "SlidingTabsFragment2";
	private FragmentManager fragmentManager;

	/**
	 * A custom {@link ViewPager} title strip which looks much like Tabs present
	 * in Android v4.0 and above, but is designed to give continuous feedback to
	 * the user when scrolling.
	 */
	private SlidingHorizontalScroll mSlidingHorizontalScroll;

	/**
	 * A {@link ViewPager} which will be used in conjunction with the
	 * {@link SlidingTabLayout} above.
	 */
	ViewPager mViewPager;
	PagerAdapter pagerAdapter;

	/**
	 * List of {@link SamplePagerItem} which represent this sample's tabs.
	 */
	private ArrayList<PagerItem2> mTabs = new ArrayList<PagerItem2>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate " + savedInstanceState);
		super.onCreate(savedInstanceState);
		// BEGIN_INCLUDE (populate_tabs)
		/**
		 * Populate our tab list with tabs. Each item contains a title,
		 * indicator color and divider color, which are used by
		 * {@link SlidingTabLayout}.
		 */

	}

	void initTabs() {
		mTabs.add(new PagerItem2("sdcard", Frag.TYPE.EXPLORER.ordinal(), "/sdcard", null));

		mTabs.add(new PagerItem2("Selection", Frag.TYPE.SELECTION.ordinal(), null, null));

		mTabs.add(new PagerItem2("Text", Frag.TYPE.TEXT.ordinal(), null, null));

		mTabs.add(new PagerItem2("Web", Frag.TYPE.WEB.ordinal(), null, null));

		mTabs.add(new PagerItem2("Pdf", Frag.TYPE.PDF.ordinal(), null, null));

		mTabs.add(new PagerItem2("Photo", Frag.TYPE.PHOTO.ordinal(), null, null));

		mTabs.add(new PagerItem2("Media", Frag.TYPE.MEDIA.ordinal(), null, null));

		mTabs.add(new PagerItem2("Apps", Frag.TYPE.APP.ordinal(), null, null));

		mTabs.add(new PagerItem2("Traffic", Frag.TYPE.TRAFFIC_STATS.ordinal(), null, null));
		
		mTabs.add(new PagerItem2("Process", Frag.TYPE.PROCESS.ordinal(), null, null));

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

	// BEGIN_INCLUDE (fragment_onviewcreated)
	/**
	 * This is called after the
	 * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
	 * Here we can pick out the {@link View}s we need to configure from the
	 * content view.
	 * 
	 * We set the {@link ViewPager}'s adapter to be an instance of
	 * {@link SampleFragmentPagerAdapter}. The {@link SlidingTabLayout} is then
	 * given the {@link ViewPager} so that it can populate itself.
	 * 
	 * @param view
	 *            View created in
	 *            {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
	 */
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
					final Frag cf = Frag.newInstance(this, 
						args.getString("title" + i),
						args.getInt("type" + i),
						args.getString("path" + i),
						args.getBundle("frag" + i));
					mTabs.add(new PagerItem2(cf));
				}
			}
		} else {
			initTabs();
		}
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		if (fragmentManager == null)
			fragmentManager = getChildFragmentManager();

		if (savedInstanceState == null) {
			Log.d(TAG, "onViewCreated " + mTabs);
			pagerAdapter = new PagerAdapter(fragmentManager);
			mViewPager.setAdapter(pagerAdapter);
			Log.d(TAG, "mViewPager " + mViewPager);
			//int currentItem = 1;
			if (args != null) {
				mViewPager.setCurrentItem(args.getInt("pos", 1), true);
//				Frag fr = pagerAdapter.getItem(currentItem);
//				fr.load(args.getString("path" + (currentItem - 1)));
			} else {
				mViewPager.setCurrentItem(1);
			}
		} else {
			mTabs.clear();
			int i = 0;
			List<Fragment> fragments = fragmentManager.getFragments();
			final int total = fragments.size();
			ProcessFragment proFrag = null;
			for (Fragment frag : fragments) {
				if (frag != null) {
					if (i++ == 1) {
						proFrag = (ProcessFragment) frag;
					} else if (i == total) {
						mTabs.get(0).fakeFrag = (ContentFragment) frag;
						mTabs.get(0).fakeFrag.CURRENT_PATH = mTabs.get(0).frag.CURRENT_PATH;
					} else {
						SlidingTabsFragment2.PagerItem2 pagerItem = new PagerItem2(frag);
						pagerItem.title = savedInstanceState.getString(frag.getTag());
						pagerItem.path = savedInstanceState.getString(frag.getTag() + "path");
						((Frag) frag).title = pagerItem.title;
						((Frag) frag).CURRENT_PATH = pagerItem.path;
						mTabs.add(pagerItem);
					}
				}
				Log.d(TAG, "frag " + frag);
			}
			mTabs.get(mTabs.size() - 1).fakeFrag = proFrag;
			// Get the ViewPager and set it's PagerAdapter so that it can display items
			pagerAdapter = new PagerAdapter(fragmentManager);
			mViewPager.setAdapter(pagerAdapter);
			int pos1 = savedInstanceState.getInt("pos", 0);
			mViewPager.setCurrentItem(pos1, true);
		}
		mViewPager.setOffscreenPageLimit(10);
		// BEGIN_INCLUDE (setup_slidingtablayout)
		// Give the SlidingTabLayout the ViewPager, this must be done AFTER the
		// ViewPager has had it's PagerAdapter set.
		mSlidingHorizontalScroll = (SlidingHorizontalScroll) view.findViewById(R.id.sliding_tabs);
		//mSlidingHorizontalScroll.fra = SlidingTabsFragment2.this;
		mSlidingHorizontalScroll.setViewPager(mViewPager);
		mSlidingHorizontalScroll.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				private int previousState, currentState;

				@Override
				public void onPageScrolled(int pageSelected, float positionOffset,
										   int positionOffsetPixel) {
//					Log.e("onPageScrolled", "pageSelected" + pageSelected
//						  + ",positionOffset:" + positionOffset
//						  + ",positionOffsetPixel:" + positionOffsetPixel);
				}

				@Override
				public void onPageSelected(int pageSelected) {
					Log.e("onPageSelected", "pageSelected:" + pageSelected);
					if (pageSelected == 0) {
						pageSelected = mTabs.size();
						mViewPager.setCurrentItem(pageSelected , false);
					} else if (pageSelected == mTabs.size() + 1) {
						pageSelected = 1;
						mViewPager.setCurrentItem(1, false); //notic
					}

					if (pageSelected == mTabs.size()) {
						final PagerItem2 pi2 = mTabs.get(0);
						final ContentFragment fakeFrag = (ContentFragment) pi2.fakeFrag;
						final ContentFragment frag = (ContentFragment) pi2.frag;
						if (fakeFrag != null) {
							fakeFrag.clone(frag);
							//pi.fakeFrag.setDirectoryButtons();
							//pi.fakeFrag.listView1.getAdapter().notifyDataSetChanged();
							//pi.fakeFrag.setRecyclerViewLayoutManager();
//							if (fakeFrag.gridLayoutManager == null || fakeFrag.gridLayoutManager.getSpanCount() != fakeFrag.spanCount) {
//								fakeFrag.listView1.removeItemDecoration(fakeFrag.dividerItemDecoration);
//								fakeFrag.gridLayoutManager = new GridLayoutManager(getContext(), fakeFrag.spanCount);
//								fakeFrag.listView1.setLayoutManager(fakeFrag.gridLayoutManager);
//							}
							fakeFrag.status.setBackgroundColor(ExplorerActivity.IN_DATA_SOURCE_2);
							
//							int index = frag.gridLayoutManager.findFirstVisibleItemPosition();
//							final View vi = frag.listView1.getChildAt(0); 
//							final int top = (vi == null) ? 0 : vi.getTop();
//							fakeFrag.gridLayoutManager.scrollToPositionWithOffset(index, top);

						}
					} else if (pageSelected == 1) {
						final PagerItem2 pi2 = mTabs.get(mTabs.size() - 1);
						final ProcessFragment fakeFrag = (ProcessFragment) pi2.fakeFrag;
						final ProcessFragment frag = (ProcessFragment) pi2.frag;
						final int index = frag.listView.getFirstVisiblePosition();
						final View vi = frag.listView.getChildAt(0);
						final int top = (vi == null) ? 0 : vi.getTop();
						fakeFrag.listView.setSelectionFromTop(index, top);
						fakeFrag.status.setBackgroundColor(ExplorerActivity.IN_DATA_SOURCE_2);
					}

					final Frag createFragment = pagerAdapter.getItem(pageSelected);
					createFragment.select(true);
					
					final ExplorerActivity activity = (ExplorerActivity) getActivity();
					
					createFragment.horizontalDivider11 = activity.slideFrag.getCurrentFragment().horizontalDivider11;
					createFragment.leftCommands = activity.slideFrag.getCurrentFragment().leftCommands;
					Log.e(TAG, "createFragment.leftCommands: " + createFragment.leftCommands);
					Log.e(TAG, "createFragment.rightCommands: " + createFragment.rightCommands);
					//if (activity.slideFrag2.pagerAdapter != null && activity.slideFrag2.getContentFragment2(Frag.TYPE.EXPLORER.ordinal()) == createFragment) {
						createFragment.commands = createFragment.rightCommands;
						createFragment.right = activity.left;
						createFragment.left = activity.right;
						createFragment.horizontalDivider = createFragment.horizontalDivider6;
					createFragment.width.size = -activity.leftSize;
					//createFragment.deletePastes = createFragment.deletePastesBtn2;//activity.
//					} else {
//						createFragment.commands = createFragment.leftCommands;
//						createFragment.right = activity.right;
//						createFragment.left = activity.left;
//						createFragment.horizontalDivider = createFragment.horizontalDivider11;
//						createFragment.leftSize = activity.leftSize;
//						createFragment.deletePastes = activity.deletePastesBtn;
//					}
					if (pageSelected == Frag.TYPE.SELECTION.ordinal()) {
						activity.curContentFrag2 = (ContentFragment2) createFragment;
						if (activity.curContentFrag2.selectedInList1.size() == 0 && activity.COPY_PATH == null && activity.MOVE_PATH == null) {
							if (createFragment.rightCommands.getVisibility() == View.VISIBLE) {
								createFragment.horizontalDivider6.setVisibility(View.GONE);
								createFragment.rightCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
								createFragment.rightCommands.setVisibility(View.GONE);
							}
						} else if (createFragment.rightCommands.getVisibility() == View.GONE) {
							createFragment.rightCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
							createFragment.rightCommands.setVisibility(View.VISIBLE);
							createFragment.horizontalDivider6.setVisibility(View.VISIBLE);
						}
					} else if (pageSelected == Frag.TYPE.EXPLORER.ordinal()) {
						activity.curExploreFrag = (ContentFragment) createFragment;
						if (activity.curExploreFrag.selectedInList1.size() == 0 && activity.COPY_PATH == null && activity.MOVE_PATH == null) {
							if (createFragment.rightCommands.getVisibility() == View.VISIBLE) {
								createFragment.horizontalDivider6.setVisibility(View.GONE);
								createFragment.rightCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
								createFragment.rightCommands.setVisibility(View.GONE);
							}
						} else if (createFragment.rightCommands.getVisibility() == View.GONE) {
							createFragment.rightCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
							createFragment.rightCommands.setVisibility(View.VISIBLE);
							createFragment.horizontalDivider6.setVisibility(View.VISIBLE);
							activity.curExploreFrag.updateDelPaste();
						}
					} else if (pageSelected == Frag.TYPE.APP.ordinal()) {
						if (((AppsFragment)createFragment).myChecked.size() == 0) {
							if (createFragment.rightCommands.getVisibility() == View.VISIBLE) {
								createFragment.horizontalDivider6.setVisibility(View.GONE);
								createFragment.rightCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
								createFragment.rightCommands.setVisibility(View.GONE);
							}
						} else if (createFragment.rightCommands.getVisibility() == View.GONE) {
							createFragment.rightCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
							createFragment.rightCommands.setVisibility(View.VISIBLE);
							createFragment.horizontalDivider6.setVisibility(View.VISIBLE);
						}
					} else if (pageSelected == Frag.TYPE.PROCESS.ordinal()) {
						if (((ProcessFragment)createFragment).myChecked.size() == 0) {
							if (createFragment.rightCommands.getVisibility() == View.VISIBLE) {
								createFragment.horizontalDivider6.setVisibility(View.GONE);
								createFragment.rightCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
								createFragment.rightCommands.setVisibility(View.GONE);
							}
						} else if (createFragment.rightCommands.getVisibility() == View.GONE) {
							createFragment.rightCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
							createFragment.rightCommands.setVisibility(View.VISIBLE);
							createFragment.horizontalDivider6.setVisibility(View.VISIBLE);
						}
					} else if (createFragment.rightCommands.getVisibility() == View.GONE) {
						createFragment.rightCommands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
						createFragment.rightCommands.setVisibility(View.VISIBLE);
						createFragment.horizontalDivider6.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onPageScrollStateChanged(int state) {
					//Comment this code if doesn't need circular pageviewer
					//Log.e("onPageScrollStateChanged", "state:" + state);
//					final int currentPage = mViewPager.getCurrentItem();
//					final int size = mTabs.size() - 1;
//					if (currentPage == size || currentPage == 0) {
//						previousState = currentState;
//						currentState = state;
//						if (previousState == 1 && currentState == 0) {
//							mViewPager.setCurrentItem(currentPage == 0 ? size : 0, false);
//						}
//					}
				}
			});
		Log.d(TAG, "mSlidingHorizontalScroll " + mSlidingHorizontalScroll);
		// BEGIN_INCLUDE (tab_colorizer)
		// Set a TabColorizer to customize the indicator and divider colors.
		// Here we just retrieve
		// the tab at the position, and return it's set color
		mSlidingHorizontalScroll
			.setCustomTabColorizer(new SlidingHorizontalScroll.TabColorizer() {
				@Override
				public int getIndicatorColor(int position) {
					return 0xffff0000;
				}

				@Override
				public int getDividerColor(int position) {
					return 0xff888888;
				}

			});
		// END_INCLUDE (tab_colorizer)
		// END_INCLUDE (setup_slidingtablayout)

	}
	// END_INCLUDE (fragment_onviewcreated)

//	@Override
//	public void onAttach(android.content.Context context) {
//		Log.d(TAG, "onAttach " + context);
//		super.onAttach(context);
//	}

	void updateLayout(boolean changeTime) {
		if (changeTime) {
			for (PagerItem2 pi2 : mTabs) {
				if (pi2.frag.getContext() != null) {
					pi2.frag.updateColor(null);
				}
				int no = pi2.frag.rightCommands.getChildCount();
				Button b;
				for (int i = 0; i < no; i++) {
					b = (Button) pi2.frag.rightCommands.getChildAt(i);
					b.setTextColor(ExplorerActivity.TEXT_COLOR);
					b.getCompoundDrawables()[1].setAlpha(0xff);
					b.getCompoundDrawables()[1].setColorFilter(ExplorerActivity.TEXT_COLOR, PorterDuff.Mode.SRC_IN);
				}
			}
//		} else {
//			((ExploreFragment)mTabs.get(Frag.TYPE.EXPLORER.ordinal() - 1).frag).refreshRecyclerViewLayoutManager(false);
//			((ContentFragment2)mTabs.get(Frag.TYPE.SELECTION.ordinal() - 1).frag).refreshRecyclerViewLayoutManager(false);
		}

	}

//	Bundle saveStates() {
//		Bundle b = new Bundle();
//		final int size = mTabs.size();
//		b.putInt("no", size);
//		for (int i = 0; i < size; i++) {
//			Frag frag = mTabs.get(i).createFragment2();
//			b.putString("title" + i, frag.title);
//			b.putInt("type" + i, frag.type);
//			b.putString("path" + i, frag.path);
//			Bundle bfrag = new Bundle();
//			frag.onSaveInstanceState(bfrag);
//			b.putBundle("frag" + i, bfrag);
//		}
//		b.putInt("pos", mViewPager.getCurrentItem());
//		return b;
//	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState " + outState + ", " + fragmentManager);
		super.onSaveInstanceState(outState);
		try {
			if (mTabs != null && mTabs.size() > 0) {
				int i = 0;
				for (PagerItem2 pi : mTabs) {
					Log.d(TAG, "onSaveInstanceState pi " + pi);
					fragmentManager.putFragment(outState, "tabb" + i++, pi.frag);
					outState.putString(pi.frag.getTag(), pi.frag.title);
					outState.putString(pi.frag.getTag() + "path", pi.frag.CURRENT_PATH);
				}
				outState.putInt("pos", mViewPager.getCurrentItem());
			}
		} catch (Exception e) {
			// Logger.log(e,"puttingtosavedinstance",getActivity());
			e.printStackTrace();
		}
		Log.d(TAG, "onSaveInstanceState2 " + outState + ", " + fragmentManager);
	}

//	@Override
//	public void onStop() {
//		Log.d(TAG, "onStop " + mViewPager + ", " + mSlidingHorizontalScroll);
////		if (Build.VERSION.SDK_INT > 23) {
////			final FragmentTransaction ft = fragmentManager.beginTransaction();
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
//		Log.d(TAG, "onPause");
////		if (Build.VERSION.SDK_INT <= 23) {
////			final FragmentTransaction ft = fragmentManager.beginTransaction();
////			ft.remove(mTabs.get(0).fakeFrag);
////			mTabs.get(0).fakeFrag = null;
////			ft.remove(mTabs.get(mTabs.size() - 1).fakeFrag);
////			mTabs.get(mTabs.size() - 1).fakeFrag = null;
////			ft.commitAllowingStateLoss();
////		}
//		super.onPause();
//	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated " + savedInstanceState);
		super.onActivityCreated(savedInstanceState);
	}

//	@Override
//	public void onAttachFragment(Fragment childFragment) {
//		Log.d(TAG, "onAttachFragment " + childFragment);
//		super.onAttachFragment(childFragment);
//	}

//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		Log.d(TAG, "onConfigurationChanged " + newConfig);
//		super.onConfigurationChanged(newConfig);
//	}

//	@Override
//	public void onViewStateRestored(Bundle savedInstanceState) {
//		Log.d(TAG, "onViewStateRestored " + savedInstanceState);
//		super.onViewStateRestored(savedInstanceState);
//	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onDestroyView() {
		Log.d(TAG, "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
    public void onResume() {
        super.onResume();
		Log.d(TAG, "onResume " + mViewPager + ", act=" + getActivity());
    }

//	void updateColor() {
//		ExploreFragment exFrag = (ExploreFragment)mTabs.get(0).frag;
//		if (exFrag != null) {
//			exFrag.setDirectoryButtons();
//			exFrag.setAdapter();
//		}
//		mSlidingHorizontalScroll.setViewPager(mViewPager);
//		//mSlidingHorizontalScroll.setBackgroundColor(ExplorerActivity.BASE_BACKGROUND);
//	}

	public void notifyTitleChange() {
		mSlidingHorizontalScroll.setViewPager(mViewPager);
	}

	public Frag getCurrentFragment2() {
		if (mViewPager != null) {
			final int currentItem = mViewPager.getCurrentItem();
			Log.d(TAG, "getCurrentFragment2." + currentItem + ", " + this);
			return pagerAdapter.getItem(currentItem);
		} else {
			return null;
		}
	}

	public Frag getContentFragment2(final int idx) {
		Log.d(TAG, "getContentFragment2." + idx + ", " + mTabs + ", " + this);
		return pagerAdapter.getItem(idx);
	}

	/**
	 * This class represents a tab to be displayed by {@link ViewPager} and it's
	 * associated {@link SlidingTabLayout}.
	 */
	private class PagerItem2 implements Parcelable {
		private String title;
		private int type;
		Bundle bundle;
		String path;
		final String suffix = ".*";
		final boolean multi = true;
		private Frag frag;
		private Frag fakeFrag;

		private static final String TAG = "PagerItem2";

		protected PagerItem2(final Parcel in) {
			title = in.readString();
			type = in.readInt();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			dest.writeString(title);
			dest.writeInt(type);
		}

		@Override
		public Object clone() {
			SlidingTabsFragment2.PagerItem2 pagerItem = new PagerItem2(title, type, path, bundle);
			return pagerItem;
		}

		PagerItem2(final Fragment frag1) {
			Log.d(TAG, frag1 + ".");
			this.frag = (Frag) frag1;
			title = frag.title;
			type = frag.type;
			path = frag.CURRENT_PATH;
		}

		PagerItem2(final String title, int type, String path, Bundle bundle) {
			this.title = title;
			this.type = type;
			this.bundle = bundle;
			this.path = path;
		}

		/**
		 * @return A new {@link Fragment} to be displayed by a {@link ViewPager}
		 */
		Frag createFragment2() {
			Log.d(TAG, "createFragment2 " + title + ", " + type + ", " + path + frag);
			if (frag == null) {
				frag = Frag.newInstance(SlidingTabsFragment2.this, title, type, path, bundle);
			}
			return frag;
		}

		Frag createFakeFragment2() {
			Log.d(TAG, "createFakeFragment2 " + title + ", " + type + ", " + path + frag);
			if (frag == null && fakeFrag == null) {
				fakeFrag = Frag.newInstance(SlidingTabsFragment2.this, title, type, path, bundle);
			} else if (fakeFrag == null) {
				fakeFrag = Frag.newInstance(SlidingTabsFragment2.this, frag);
				//fakeFrag.clone(frag);
			} else if (fakeFrag != null && frag != null) {
				fakeFrag.clone(frag);
			}
			return fakeFrag;
		}

		/**
		 * @return the title which represents this tab. In this sample this is
		 *         used directly by
		 *         {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
		 */
		String getTitle() {
			Log.d(TAG, "getTitle2 " + title + ", " + type + ", " + frag);
			if (frag == null || frag.title == null || frag.title.length() == 0) {
				return title;
			} else {
				return frag.title;
			}
		}

		@Override
		public String toString() {
			return title + ", " + frag;
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
		public Frag getItem(int position) {
			Log.d(TAG, "getItem2 " + position);
			//return mTabs.get(position).createFragment2();
			if (position == 0) {
				return mTabs.get(mTabs.size() - 1).createFakeFragment2();
			} else if (position == mTabs.size() + 1) {
				return mTabs.get(0).createFakeFragment2();
			} else {
				return mTabs.get(position - 1).createFragment2();
			}
		}

		@Override
		public int getCount() {
			return mTabs.size() + 2;
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
		public CharSequence getPageTitle(int position) {
			//return mTabs.get(position).getTitle();
			if (position == 0) {
				return "";
			} else if (position == mTabs.size() + 1) {
				return "";
			} else {
				return mTabs.get(position - 1).getTitle();
			}
		}
		// END_INCLUDE (pageradapter_getpagetitle)
	}
}
