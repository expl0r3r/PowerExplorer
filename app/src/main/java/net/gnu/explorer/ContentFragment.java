package net.gnu.explorer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.*;
import android.widget.*;
import android.os.*;
import android.graphics.drawable.*;
import android.content.*;
import android.view.View.*;
import android.util.*;
import java.io.*;
import android.text.*;
import android.graphics.*;
import android.app.*;
import android.view.*;
import android.net.*;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import net.gnu.util.*;
import net.gnu.androidutil.*;
import net.gnu.explorer.R;
import com.amaze.filemanager.ui.icons.*;
import com.amaze.filemanager.utils.*;
import com.amaze.filemanager.filesystem.BaseFile;
import com.amaze.filemanager.filesystem.HFile;
import com.amaze.filemanager.activities.*;
import com.amaze.filemanager.services.asynctasks.*;
import android.preference.*;
import com.tekinarslan.sample.*;
import net.gnu.texteditor.*;
import android.support.v4.view.*;

import android.view.animation.*;
import android.widget.LinearLayout.*;
import android.support.v7.view.menu.*;
import android.widget.ImageView.*;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.*;
import android.content.res.*;
import java.lang.ref.*;
import android.support.v4.widget.SwipeRefreshLayout;
import net.gnu.explorer.ContentFragment.*;
import android.view.inputmethod.InputMethodManager;
import net.dongliu.apk.parser.*;
import net.dongliu.apk.parser.bean.*;
import com.amaze.filemanager.ui.LayoutElements;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class ContentFragment extends FileFrag implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ContentFragment";

	//public Bundle bundle;
	
	//private FileObserver mFileObserver;
	
	//private SharedPreferences Sp;
	private int theme1;
	
	//private LinearLayout mDirectoryButtons;
	//private HorizontalScrollView scrolltext;
	//private TextView diskStatus;
	
	LinearLayout status;
//    private Drawable drawableDelete;
//	private Drawable drawablePaste;
	
	//GridLayoutManager gridLayoutManager;
	GridDividerItemDecoration dividerItemDecoration;
	//private LinkedList<Map<String, Object>> backStack = new LinkedList<>();
	boolean fake = false;
	//private FileListSorter fileListSorter;
    private TextSearch textSearch = new TextSearch();
	//private View selStatus;
	private InputMethodManager imm;
	//private LinkedList<String> history = new LinkedList<>();
	private ScaleGestureDetector mScaleGestureDetector;
	private ImageButton dirMore;
	//String dirTemp4Search = "";
	
	private SearchFileNameTask searchTask = null;
	//private ViewGroup commands;
	//private View horizontalDivider;
//	private ViewGroup left;
//	private ViewGroup right;
//	private int leftSize;
	//boolean mScaling;    // Whether the user is currently pinch zooming
	
	@Override
	public String toString() {
		return "fake=" + fake + ", suffix=" + suffix + ", multi=" + multiFiles + ", " + CURRENT_PATH + ", " + super.toString();
	}

	public static ContentFragment newInstance(final Width w, final String dir, final String suffix, final boolean multiFiles, Bundle bundle) {//, int se) {//FragmentActivity ctx, 
        //Log.d(TAG, "newInstance dir " + dir + ", suffix " + suffix + ", multiFiles " + multiFiles);
		
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(ExplorerActivity.EXTRA_DIR_PATH, dir);
		bundle.putString(ExplorerActivity.EXTRA_SUFFIX, suffix);
		bundle.putBoolean(ExplorerActivity.EXTRA_MULTI_SELECT, multiFiles);

		final ContentFragment fragment = new ContentFragment();
		fragment.setArguments(bundle);
		fragment.CURRENT_PATH = dir;
		fragment.suffix = suffix;
		fragment.multiFiles = multiFiles;
		fragment.width = w;
        Log.d(TAG, "newInstance " + fragment);
		return fragment;
    }

	@Override
	public void load(String path) {
		changeDir(path, false);
	}

	public void clone(final Frag frag2) {
		Log.d(TAG, "clone " + frag2);
		final ContentFragment frag = (ContentFragment) frag2;
		CURRENT_PATH = frag.CURRENT_PATH;
		if (!fake) {
			suffix = frag.suffix;
			multiFiles = frag.multiFiles;
			width = frag2.width;
			dataSourceL1 = frag.dataSourceL1;
			selectedInList1 = frag.selectedInList1;
			tempSelectedInList1 = frag.tempSelectedInList1;
		}
		fake = true;
		spanCount = frag.spanCount;
		dataSourceL2 = frag.dataSourceL2;
		tempPreviewL2 = frag.tempPreviewL2;
		searchMode = frag.searchMode;
		searchVal = frag.searchVal;
		dirTemp4Search = frag.dirTemp4Search;
		srcAdapter = frag.srcAdapter;
		if (listView1 != null && listView1.getAdapter() != srcAdapter) {
			listView1.setAdapter(srcAdapter);
		}
		if (allCbx != null) {
			setDirectoryButtons();
			final int size = selectedInList1.size();
			if (size == dataSourceL1.size()) {
				allCbx.setImageResource(R.drawable.ic_accept);
			} else if (size > 0) {
				allCbx.setImageResource(R.drawable.ready);
			} else {
				allCbx.setImageResource(R.drawable.dot);
			}
			//pi.fakeFrag.listView1.getAdapter().notifyDataSetChanged();
			//pi.fakeFrag.setRecyclerViewLayoutManager();
			if (gridLayoutManager == null || gridLayoutManager.getSpanCount() != spanCount) {
				listView1.removeItemDecoration(dividerItemDecoration);
				gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
				listView1.setLayoutManager(gridLayoutManager);
			}

			final int index = frag.gridLayoutManager.findFirstVisibleItemPosition();
			final View vi = frag.listView1.getChildAt(0); 
			final int top = (vi == null) ? 0 : vi.getTop();
			gridLayoutManager.scrollToPositionWithOffset(index, top);
			if (frag.selStatus != null) {
				final int visibility = frag.selStatus.getVisibility();
				if (selStatus.getVisibility() != visibility) {
					selStatus.setVisibility(visibility);
					horizontalDivider0.setVisibility(visibility);
					horizontalDivider12.setVisibility(visibility);
					status.setVisibility(visibility);
				}
				selectionStatus1.setText(frag.selectionStatus1.getText());
				diskStatus.setText(frag.diskStatus.getText());
			}
		}
	}
	
	public static ContentFragment newOriFakeInstance(final ContentFragment frag) {
        Log.d(TAG, "newOriFakeInstance " + frag);
		
		final ContentFragment fragment = new ContentFragment();
		fragment.clone(frag);
		
		final Bundle bundle = new Bundle();
		bundle.putString(ExplorerActivity.EXTRA_DIR_PATH, frag.CURRENT_PATH);
		bundle.putString(ExplorerActivity.EXTRA_SUFFIX, frag.suffix);
		bundle.putBoolean(ExplorerActivity.EXTRA_MULTI_SELECT, frag.multiFiles);
		fragment.setArguments(bundle);
		
        return fragment;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
        //Log.d(TAG, "onCreateView fake=" + fake + ", dir=" + dir + ", " + savedInstanceState);
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.pager_item, container, false);
    }

//	@Override
//	public boolean onTouch(View p1, MotionEvent p2) {
//		//Log.d(TAG, "onTouch " + p1 + ", " + p2);
//		//activity.slideFrag2.getCurrentFragment2().select(false);
//		if (!mScaling) {
//			super.onTouch(p1, p2);
//		}
////		if (type == -1) {
////			activity.slideFrag2.getCurrentFragment2().select(false);
////		} else {
////			activity.slideFrag2.getCurrentFragment2().select(true);
////		}
//		return false;
//	}

	@Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated " + toString() + ", savedInstanceState=" + savedInstanceState);
		super.onViewCreated(v, savedInstanceState);
		//setRetainInstance(true);
		//AndroidUtils.setOnTouchListener(v, this);
        final Bundle args = getArguments();
		
		activity = (ExplorerActivity)getActivity();
		final int fragIndex = activity.slideFrag.indexOf(this);
		Log.i(TAG, "onViewCreated." + fragIndex + ", " + toString() + ", " + "args=" + args);
		spanCount = AndroidUtils.getSharedPreference(activity, "ContentFrag.SPAN_COUNT" + fragIndex, 1);
		
		allCbx.setOnClickListener(this);
		icons.setOnClickListener(this);
		allName.setOnClickListener(this);
		allDate.setOnClickListener(this);
		allSize.setOnClickListener(this);
		allType.setOnClickListener(this);
		
		status = (LinearLayout)v.findViewById(R.id.status);
		dirMore = (ImageButton) v.findViewById(R.id.dirMore);
		drawableDelete = activity.getDrawable(R.drawable.ic_action_delete);
		drawablePaste = activity.getDrawable(R.drawable.ic_action_paste);
		//fastScroller = (VerticalRecyclerViewFastScroller)v.findViewById(R.id.fastscroll);
		selStatus = v.findViewById(R.id.selStatus);
//		if (activity.slideFrag2.pagerAdapter != null && activity.slideFrag2.getContentFragment2(Frag.TYPE.EXPLORER.ordinal()) == this) {
//			commands = rightCommands;
//			right = activity.left;
//			left = activity.right;
//			horizontalDivider = horizontalDivider6;
//			leftSize = -activity.leftSize;
//			deletePastes = activity.deletePastesBtn2;
//		} else {
//			commands = leftCommands;
//			right = activity.right;
//			left = activity.left;
//			horizontalDivider = horizontalDivider11;
//			leftSize = activity.leftSize;
//			deletePastes = activity.deletePastesBtn;
//		}
		
		listView1 = (RecyclerView) v.findViewById(R.id.files);
		listView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
					//Log.d(TAG, "onScrolled dx=" + dx + ", dy=" + dy + ", density=" + activity.density);
					if (System.currentTimeMillis() - lastScroll > 50) {//!mScaling && 
						if (dy > activity.density << 3 && selStatus.getVisibility() == View.VISIBLE) {
							selStatus.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_bottom));
							selStatus.setVisibility(View.GONE);
							horizontalDivider0.setVisibility(View.GONE);
							horizontalDivider12.setVisibility(View.GONE);
							status.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_bottom));
							status.setVisibility(View.GONE);
						} else if (dy < -activity.density << 3 && selStatus.getVisibility() == View.GONE) {
							selStatus.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_top));
							selStatus.setVisibility(View.VISIBLE);
							horizontalDivider0.setVisibility(View.VISIBLE);
							horizontalDivider12.setVisibility(View.VISIBLE);
							status.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_top));
							status.setVisibility(View.VISIBLE);
						}
						lastScroll = System.currentTimeMillis();
					}
				}
			});
		listView1.setHasFixedSize(true);
		//listView1.setFastScrollEnabled(true);
		listView1.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		//linearLayoutManager = new LinearLayoutManager(getContext());
		//linearLayoutManager.setSmoothScrollbarEnabled(true);
		//listView1.setLayoutManager(linearLayoutManager);
		
        DefaultItemAnimator animator = new DefaultItemAnimator();
		animator.setAddDuration(500);
		//animator.setRemoveDuration(500);
        listView1.setItemAnimator(animator);

		//scrolltext = (HorizontalScrollView) v.findViewById(R.id.scroll_text);
		//mDirectoryButtons = (LinearLayout) v.findViewById(R.id.directory_buttons);
		//diskStatus = (TextView) v.findViewById(R.id.diskStatus);

		clearButton.setOnClickListener(this);
		searchButton.setOnClickListener(this);
		dirMore.setOnClickListener(this);
		
		quicksearch.addTextChangedListener(textSearch);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
//                public boolean onScaleBegin(ScaleGestureDetector detector) {
//					return true;
//				}
//				public void onScaleEnd(ScaleGestureDetector detector) {
//					}
				@Override
                public boolean onScale(ScaleGestureDetector detector) {
                    Log.d(TAG, "onScale getCurrentSpan " + detector.getCurrentSpan() + ", getPreviousSpan " + detector.getPreviousSpan() + ", getTimeDelta " + detector.getTimeDelta());
					//if (detector.getCurrentSpan() > 300 && detector.getTimeDelta() > 50) {
					//Log.d(TAG, "onScale " + (detector.getCurrentSpan() - detector.getPreviousSpan()) + ", getTimeDelta " + detector.getTimeDelta());
					//mScaling = true;
					if (detector.getCurrentSpan() - detector.getPreviousSpan() < -80 * activity.density) {
						if (spanCount == 1) {
							spanCount = 2;
							setRecyclerViewLayoutManager();
							return true;
						} else if (spanCount == 2 && width.size >= 0) {
							if (right.getVisibility() == View.GONE || left.getVisibility() == View.GONE) {
								spanCount = 8;
							} else {
								spanCount = 4;
							}
							setRecyclerViewLayoutManager();
							return true;
						}
					} else if (detector.getCurrentSpan() - detector.getPreviousSpan() > 80 * activity.density) {
						if ((spanCount == 4 || spanCount == 8)) {
							spanCount = 2;
							setRecyclerViewLayoutManager();
							return true;
						} else if (spanCount == 2) {
							spanCount = 1;
							setRecyclerViewLayoutManager();
							return true;
						} 
					}
                    //}
                    //mScaling = false;
					return false;
                }
            });

		//set touch listener on recycler view
		listView1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                   	//Log.d(TAG, "onTouch " + event);
					if (type == -1) {
						activity.slideFrag2.getCurrentFragment2().select(false);
					} else {
						activity.slideFrag2.getCurrentFragment2().select(true);
					}
					mScaleGestureDetector.onTouchEvent(event);
                    return false;
                }
            });
			
		if (args != null) {
			if ("".equals(CURRENT_PATH) || CURRENT_PATH == null) {
				CURRENT_PATH = args.getString(ExplorerActivity.EXTRA_DIR_PATH);
			} else {
				args.putString(ExplorerActivity.EXTRA_DIR_PATH, CURRENT_PATH);
			}
			//Log.d(TAG, "onViewCreated.dir " + dir);
			suffix = args.getString(ExplorerActivity.EXTRA_SUFFIX);
			//Log.d(TAG, "onViewCreated.suffix " + suffix);
			multiFiles = args.getBoolean(ExplorerActivity.EXTRA_MULTI_SELECT);
			//Log.d(TAG, "onViewCreated.multiFiles " + multiFiles);
			if (savedInstanceState == null && args.getStringArrayList("dataSourceL1") != null) {
				savedInstanceState = args;
			}

			if (!multiFiles) {
				allCbx.setVisibility(View.GONE);
			}
        }
		
		final String order = AndroidUtils.getSharedPreference(activity, "ContentFragSortType" + fragIndex, "Name ▲");
		//Log.i(TAG, "sharedPreference " + fragIndex + ", " + order);
		allName.setText("Name");
		allSize.setText("Size");
		allDate.setText("Date");
		allType.setText("Type");
		switch (order) {
			case "Name ▼":
				fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.NAME, FileListSorter.DESCENDING);
				allName.setText("Name ▼");
				break;
			case "Date ▲":
				fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.DATE, FileListSorter.ASCENDING);
				allDate.setText("Date ▲");
				break;
			case "Date ▼":
				fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.DATE, FileListSorter.DESCENDING);
				allDate.setText("Date ▼");
				break;
			case "Size ▲":
				fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.SIZE, FileListSorter.ASCENDING);
				allSize.setText("Size ▲");
				break;
			case "Size ▼":
				fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.SIZE, FileListSorter.DESCENDING);
				allSize.setText("Size ▼");
				break;
			case "Type ▲":
				fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.TYPE, FileListSorter.ASCENDING);
				allType.setText("Type ▲");
				break;
			case "Type ▼":
				fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.TYPE, FileListSorter.DESCENDING);
				allType.setText("Type ▼");
				break;
			default:
				fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.NAME, FileListSorter.ASCENDING);
				allName.setText("Name ▲");
				break;
		}
		
		//Log.d(TAG, "onViewCreated " + this + ", ctx=" + getContext());
		if (savedInstanceState != null && savedInstanceState.getString(ExplorerActivity.EXTRA_DIR_PATH) != null) {
			CURRENT_PATH = savedInstanceState.getString(ExplorerActivity.EXTRA_DIR_PATH);
			suffix = savedInstanceState.getString(ExplorerActivity.EXTRA_SUFFIX, ".*");
			multiFiles = savedInstanceState.getBoolean(ExplorerActivity.EXTRA_MULTI_SELECT, true);
//			ArrayList<String> stringArrayList = savedInstanceState.getStringArrayList("selectedInList1");
//			if (stringArrayList != null) {
//				selectedInList1.clear();
//				selectedInList1.addAll(Util.collectionString2FileArrayList(stringArrayList));
//			}
//			stringArrayList = savedInstanceState.getStringArrayList("dataSourceL1");
//			if (stringArrayList != null) {
//				dataSourceL1.clear();
//				dataSourceL1.addAll(Util.collectionString2FileArrayList(stringArrayList));
//			}
//			//srcAdapter = new ArrAdapter(dataSourceL1);
//			//listView1.setAdapter(srcAdapter);
//			searchMode = savedInstanceState.getBoolean("searchMode", false);
//			searchVal = savedInstanceState.getString("searchVal", "");
//			dirTemp4Search = savedInstanceState.getString("dirTemp4Search", "");
//			
//			if (savedInstanceState.getString("tempPreviewL2") != null) {
//				tempPreviewL2 = new File(savedInstanceState.getString("tempPreviewL2"));
//			}
			
			allCbx.setEnabled(savedInstanceState.getBoolean("allCbx.isEnabled"));
			setRecyclerViewLayoutManager();
			updateDir(CURRENT_PATH, ContentFragment.this);
			final int index  = savedInstanceState.getInt("index");
			final int top  = savedInstanceState.getInt("top");
			Log.d(TAG, "index = " + index + ", " + top);
			gridLayoutManager.scrollToPositionWithOffset(index, top);
		} else {
			//srcAdapter = new ArrAdapter(dataSourceL1);
			//listView1.setAdapter(srcAdapter);
			setRecyclerViewLayoutManager();
			changeDir(CURRENT_PATH, false);
		}
//		Sp = PreferenceManager.getDefaultSharedPreferences(getContext());
//		int theme = Sp.getInt("theme", 2);
//		theme1 = theme == 2 ? PreferenceUtils.hourOfDay() : theme;
		//Log.d(TAG, "onViewCreated " + dir + ", " + suffix + ", " + multiFiles);
//		if (selectedInList1.size() == 0 && activity.COPY_PATH == null && activity.MOVE_PATH == null) {
		
//			if (commands.getVisibility() == View.VISIBLE) {
//				horizontalDivider.setVisibility(View.GONE);
//				commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//				commands.setVisibility(View.GONE);
//			}
//		} else if (commands.getVisibility() == View.GONE) {
//			commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
//			commands.setVisibility(View.VISIBLE);
//			horizontalDivider.setVisibility(View.VISIBLE);
//		}
		updateColor(v);
		
//		saveDrawable2Bitmap(R.drawable.undo, "undo");
//		saveDrawable2Bitmap(R.drawable.redo, "redo");
//		saveDrawable2Bitmap(R.drawable.ic_action_save, "ic_action_save");
//		saveDrawable2Bitmap(R.drawable.ic_menu_selectall_holo_light, "ic_menu_selectall_holo_light");
//		saveDrawable2Bitmap(R.drawable.ic_action_copy, "ic_action_copy");
//		saveDrawable2Bitmap(R.drawable.ic_action_search, "ic_action_search");
//		saveDrawable2Bitmap(R.drawable.ic_action_cut, "ic_action_cut");
//		saveDrawable2Bitmap(R.drawable.ic_action_paste, "ic_action_paste");
//		saveDrawable2Bitmap(R.drawable.ic_action_delete, "ic_action_delete");
//		saveDrawable2Bitmap(R.drawable.ic_action_rename, "ic_action_rename");
//		saveDrawable2Bitmap(R.drawable.ic_action_new, "ic_action_new");
//		saveDrawable2Bitmap(R.drawable.kindle_64, "root");
//		
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.CLEAR);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.SRC);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.DST);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.SRC_OVER);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.DST_OVER);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.SRC_IN);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.DST_IN);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.SRC_OUT);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.DST_OUT);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.SRC_ATOP);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.DST_ATOP);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.XOR);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.DARKEN);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.LIGHTEN);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.MULTIPLY);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.SCREEN);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.ADD);
//		saveDrawable2Bitmap(R.drawable.ic_launcher_microsd, "ic_launcher_microsd", PorterDuff.Mode.OVERLAY);
//		saveDrawable2Bitmap(R.drawable.ic_action_compress, "ic_action_compress");
//		saveDrawable2Bitmap(R.drawable.icon_light_share, "icon_light_share");
//		saveDrawable2Bitmap(R.drawable.ic_custom_hide, "ic_custom_hide");
//		saveDrawable2Bitmap(R.drawable.add_row_48, "add_row_48");
//		saveDrawable2Bitmap(R.drawable.add_to_favorites_48, "add_to_favorites_48");
//		saveDrawable2Bitmap(R.drawable.ic_lock_lock, "ic_lock_lock");
//		saveDrawable2Bitmap(R.drawable.operation_button_info, "operation_button_info");
		
//		int arr[] = new int[] {R.drawable.ic_action_undo, 
//			R.drawable.ic_action_redo, 
//			R.drawable.ic_action_save, 
//			R.drawable.ic_menu_selectall_holo_light, 
//			R.drawable.ic_action_copy, 
//			R.drawable.ic_action_search, 
//			R.drawable.ic_action_cut, 
//			R.drawable.ic_action_paste, 
//			R.drawable.ic_action_delete, 
//			R.drawable.ic_action_rename, 
//			R.drawable.ic_action_new, 
//			R.drawable.kindle_64, 
//			R.drawable.ic_launcher_microsd, 
//			R.drawable.ic_action_compress, 
//			R.drawable.ic_action_share, 
//			R.drawable.ic_custom_hide, 
//			R.drawable.add_row_48, 
//			R.drawable.add_to_favorites_48, 
//			R.drawable.ic_lock_lock, 
//			R.drawable.operation_button_info, 
//		};
//		String[] ss = new String[] {
//			"ic_action_undo.png", 
//			"ic_action_redo.png", 
//			"ic_action_save.png", 
//			"ic_menu_selectall_holo_light.png", 
//			"ic_action_copy.png", 
//			"ic_action_search.png", 
//			"ic_action_cut.png", 
//			"ic_action_paste.png", 
//			"ic_action_delete.png", 
//			"ic_action_rename.png", 
//			"ic_action_new.png", 
//			"kindle_64.png", 
//			"ic_launcher_microsd.png", 
//			"ic_action_compress.png", 
//			"ic_action_share.png", 
//			"ic_custom_hide.png", 
//			"add_row_48.png", 
//			"add_to_favorites_48.png", 
//			"ic_lock_lock.png", 
//			"operation_button_info.png", 
//		};
//		ImageView iv = new ImageView(activity);
//		for (int i = 0; i < arr.length; i++) {
//			Drawable d = getResources().getDrawable(arr[i]);
//			//imageView.setImageURI(Uri.fromFile(new File("/storage/emulated/0/AppProjects/0SearchExplore/PowerExplorer/PowerExplorer/src/main/res/drawable-xhdpi/ic_action_save.png")));
//
//			iv.setImageDrawable(d);
//			iv.setImageAlpha(0xff);
//			iv.setColorFilter(0xffffffff);
//
//			ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//			iv.setLayoutParams(p);
//
//			Bitmap bitmap = loadBitmapFromView(iv, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//
//			File file = new File(Environment.getExternalStorageDirectory(), ss[i]);
//			Log.d(TAG, file.getAbsolutePath());
//			FileOutputStream fos = null;
//			BufferedOutputStream bos = null;
//			try {
//				fos = new FileOutputStream(file);
//				bos = new BufferedOutputStream(fos);
//				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				FileUtil.flushClose(bos, fos);
//			}
//		}
		
	}

//	private Bitmap loadBitmapFromView(View v, final int w, final int h) {
//		final Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//		final Canvas c = new  Canvas(b);
//		v.layout(0, 0, w, h);
//		v.draw(c);
//		return b;
//	}
	
//	private void saveDrawable2Bitmap(int resId, String fName, PorterDuff.Mode mode) throws Resources.NotFoundException {
//		BitmapDrawable d = (BitmapDrawable) getResources().getDrawable(resId);
//		d.setColorFilter(ExplorerActivity.TEXT_COLOR, mode);
//		//d.setFilterBitmap(true);
//		d.setXfermode(new PorterDuffXfermode(mode));
//		d = (BitmapDrawable) d.mutate();
//		BitmapUtil.saveBitmap(d.getBitmap(), "/sdcard/Movies/" + fName + "." + mode.toString() + ".png");
//	}

	@Override
    public void onRefresh() {
        final Editable s = quicksearch.getText();
		if (s.length() > 0) {
			textSearch.afterTextChanged(s);
		} else {
        	changeDir(CURRENT_PATH, false);
		}
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		int indexOf = activity.slideFrag.indexOf(this);
		Log.d(TAG, "onSaveInstanceState " + indexOf + ", fake=" + fake + ", " + CURRENT_PATH + ", " + outState);
		if (fake) {
			return;
		}
		//Log.d(TAG, "SPAN_COUNT.ContentFrag" + activity.slideFrag.indexOf(this));
		AndroidUtils.setSharedPreference(getContext(), "SPAN_COUNT.ContentFrag" + indexOf, spanCount);
		
//		if (tempPreviewL2 != null) {
//			outState.putString("tempPreviewL2", tempPreviewL2.getAbsolutePath());
//		}
		outState.putString(ExplorerActivity.EXTRA_DIR_PATH, CURRENT_PATH);
		outState.putString(ExplorerActivity.EXTRA_SUFFIX, suffix);
		outState.putBoolean(ExplorerActivity.EXTRA_MULTI_SELECT, multiFiles);
//		outState.putStringArrayList("selectedInList1", Util.collectionFile2StringArrayList(selectedInList1));
//		outState.putStringArrayList("dataSourceL1", Util.collectionFile2StringArrayList(dataSourceL1));
//		outState.putBoolean("searchMode", searchMode);
//		outState.putString("searchVal", quicksearch.getText().toString());
//		outState.putString("dirTemp4Search", dirTemp4Search);
		outState.putBoolean("allCbx.isEnabled", allCbx.isEnabled());
		
		final int index = gridLayoutManager.findFirstVisibleItemPosition();
        final View vi = listView1.getChildAt(0); 
        final int top = (vi == null) ? 0 : vi.getTop();
		outState.putInt("index", index);
		outState.putInt("top", top);
		//Log.d(TAG, "onSaveInstanceState index = " + index + ", " + top);
		super.onSaveInstanceState(outState);
	}

//	@Override
//	public void onViewStateRestored(Bundle savedInstanceState) {
//		//Log.d(TAG, "onViewStateRestored " + savedInstanceState);
//		if (imageLoader == null) {
//			activity = (ExplorerActivity)getActivity();
//			imageLoader = new ImageThreadLoader(activity);
//		}
//		super.onViewStateRestored(savedInstanceState);
//	}

//	public Map<String, Object> onSaveInstanceState() {
//		Map<String, Object> outState = new TreeMap<>();
//		//Log.d(TAG, "Map onSaveInstanceState " + dir + ", " + outState);
//		outState.put(ExplorerActivity.EXTRA_DIR_PATH, path);
//		outState.put(ExplorerActivity.EXTRA_SUFFIX, suffix);
//		outState.put(ExplorerActivity.EXTRA_MULTI_SELECT, multiFiles);
//		outState.put("selectedInList1", selectedInList1);
//		outState.put("dataSourceL1", dataSourceL1);
//		outState.put("searchMode", searchMode);
//		outState.put("searchVal", quicksearch.getText().toString());
//		outState.put("dirTemp4Search", dirTemp4Search);
//		outState.put("allCbx.isEnabled", allCbx.isEnabled());
//		
//		final int index = gridLayoutManager.findFirstVisibleItemPosition();
//        final View vi = listView1.getChildAt(0); 
//        final int top = (vi == null) ? 0 : vi.getTop();
//		outState.put("index", index);
//		outState.put("top", top);
//		
//        return outState;
//	}
	
	void reload(Map<String, Object> savedInstanceState) {
		Log.d(TAG, "reload " + savedInstanceState + CURRENT_PATH);
		CURRENT_PATH = (String) savedInstanceState.get(ExplorerActivity.EXTRA_DIR_PATH);
		suffix = (String) savedInstanceState.get(ExplorerActivity.EXTRA_SUFFIX);
		multiFiles = savedInstanceState.get(ExplorerActivity.EXTRA_MULTI_SELECT);
		selectedInList1.clear();
		selectedInList1.addAll((ArrayList<LayoutElements>) savedInstanceState.get("selectedInList1"));
		dataSourceL1.clear();
		dataSourceL1.addAll((ArrayList<LayoutElements>) savedInstanceState.get("dataSourceL1"));
		
		searchMode = savedInstanceState.get("searchMode");
		searchVal = (String) savedInstanceState.get("searchVal");
		dirTemp4Search = (String) savedInstanceState.get("dirTemp4Search");
		//listView1.setSelectionFromTop(savedInstanceState.getInt("index"),
		//savedInstanceState.getInt("top"));
		allCbx.setEnabled(savedInstanceState.get("allCbx.isEnabled"));
		srcAdapter.notifyDataSetChanged();
		
		setRecyclerViewLayoutManager();
		gridLayoutManager.scrollToPositionWithOffset(savedInstanceState.get("index"), savedInstanceState.get("top"));
		
		updateDir(CURRENT_PATH, ContentFragment.this);
	}

	@Override
    public void onPause() {
        Log.d(TAG, "onPause " + toString());
		super.onPause();
		if (imageLoader != null) {
			imageLoader.stopThread();
		}
		loadList.cancel(true);
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
        }
    }

	@Override
	public void onStop() {
		Log.d(TAG, "onStop " + activity.slideFrag.indexOf(this) + ", fake=" + fake + ", " + toString());
		loadList.cancel(true);
		super.onStop();
	}

//	@Override
//	public void onStart() {
//		Log.d(TAG, "onStart " + toString());
//		super.onStart();
//	}

    @Override
    public void onResume() {
        Log.d(TAG, "onResume " + activity.slideFrag.indexOf(this) + ", fake=" + fake + ", " + CURRENT_PATH + ", dirTemp4Search=" + dirTemp4Search);
		super.onResume();
		getView().setBackgroundColor(ExplorerActivity.BASE_BACKGROUND);
		if (mFileObserver != null) {
            mFileObserver.stopWatching();
        }
		if (CURRENT_PATH == null) {
			mFileObserver = createFileObserver(dirTemp4Search);
		} else {
			mFileObserver = createFileObserver(CURRENT_PATH);
		}
		mFileObserver.startWatching();
		activity = (ExplorerActivity)getActivity();
		
		selectionStatus1.setText(selectedInList1.size() 
								 + "/" + dataSourceL1.size());
		final File curDir = new File(CURRENT_PATH == null ? dirTemp4Search : CURRENT_PATH);
		diskStatus.setText(
			"Free " + Util.nf.format(curDir.getFreeSpace() / (1 << 20))
			+ " MiB. Used " + Util.nf.format((curDir.getTotalSpace()-curDir.getFreeSpace()) / (1 << 20))
			+ " MiB. Total " + Util.nf.format(curDir.getTotalSpace() / (1 << 20)) + " MiB");
	}

	public void updateColor(View rootView) {
		rootView.setBackgroundColor(ExplorerActivity.BASE_BACKGROUND);
		icons.setColorFilter(ExplorerActivity.TEXT_COLOR);
		allName.setTextColor(ExplorerActivity.TEXT_COLOR);
		allDate.setTextColor(ExplorerActivity.TEXT_COLOR);
		allSize.setTextColor(ExplorerActivity.TEXT_COLOR);
		allType.setTextColor(ExplorerActivity.TEXT_COLOR);
		selectionStatus1.setTextColor(ExplorerActivity.TEXT_COLOR);
		diskStatus.setTextColor(ExplorerActivity.TEXT_COLOR);
		quicksearch.setTextColor(ExplorerActivity.TEXT_COLOR);
		clearButton.setColorFilter(ExplorerActivity.TEXT_COLOR);
		searchButton.setColorFilter(ExplorerActivity.TEXT_COLOR);
		noFileImage.setColorFilter(ExplorerActivity.TEXT_COLOR);
		noFileText.setTextColor(ExplorerActivity.TEXT_COLOR);
		dirMore.setColorFilter(ExplorerActivity.TEXT_COLOR);
		horizontalDivider0.setBackgroundColor(ExplorerActivity.DIVIDER_COLOR);
		horizontalDivider12.setBackgroundColor(ExplorerActivity.DIVIDER_COLOR);
		horizontalDivider7.setBackgroundColor(ExplorerActivity.DIVIDER_COLOR);
	}

    @Override
	public void onClick(final View p1) {
		//Log.d(TAG, "onClick " + this + ", " + type);
//		if (type == -1) {
//			activity.slideFrag2.getCurrentFragment2().select(false);
//		} else {
//			activity.slideFrag2.getCurrentFragment2().select(true);
//		}
		super.onClick(p1);
		switch (p1.getId()) {
			case R.id.allCbx:
				if (multiFiles) {
					selectedInList1.clear();
					if (!allCbx.isSelected()) {//}.isChecked()) {
						allCbx.setSelected(true);
						//String st;// = dir.endsWith("/") ? dir : dir + "/";
						for (LayoutElements f : dataSourceL1) {
							//st = f.getAbsolutePath();
							if (f.bf.f.canRead() && (dataSourceL2 == null || !dataSourceL2.contains(f))) {
								selectedInList1.add(f);
							}
						}
						if (selectedInList1.size() > 0 && commands.getVisibility() == View.GONE) {
							commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
							commands.setVisibility(View.VISIBLE);
							horizontalDivider.setVisibility(View.VISIBLE);
						}
					} else {
						allCbx.setSelected(false);
						if (activity.COPY_PATH == null && activity.MOVE_PATH == null && commands.getVisibility() == View.VISIBLE) {
							horizontalDivider.setVisibility(View.GONE);
							commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
							commands.setVisibility(View.GONE);
						}
					}
					selectionStatus1.setText(selectedInList1.size() 
											 + "/"+ dataSourceL1.size());
					srcAdapter.notifyDataSetChanged();
					updateDelPaste();
				}
				break;
			case R.id.allName:
				if (allName.getText().toString().equals("Name ▲")) {
					allName.setText("Name ▼");
					fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.NAME, FileListSorter.DESCENDING);
					AndroidUtils.setSharedPreference(activity, "ContentFragSortType" + activity.slideFrag.indexOf(ContentFragment.this), "Name ▼");
				} else {
					allName.setText("Name ▲");
					fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.NAME, FileListSorter.ASCENDING);
					AndroidUtils.setSharedPreference(activity, "ContentFragSortType" + activity.slideFrag.indexOf(ContentFragment.this), "Name ▲");
				}
				//Log.i(TAG, "activity.slideFrag.indexOf " + activity.slideFrag.indexOf(ContentFragment.this));
				allDate.setText("Date");
				allSize.setText("Size");
				allType.setText("Type");
				Collections.sort(dataSourceL1, fileListSorter);
				srcAdapter.notifyDataSetChanged();
				break;
			case R.id.allType:
				if (allType.getText().toString().equals("Type ▲")) {
					allType.setText("Type ▼");
					fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.TYPE, FileListSorter.DESCENDING);
					AndroidUtils.setSharedPreference(activity, "ContentFragSortType" + activity.slideFrag.indexOf(ContentFragment.this), "Type ▼");
				} else {
					allType.setText("Type ▲");
					fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.TYPE, FileListSorter.ASCENDING);
					AndroidUtils.setSharedPreference(activity, "ContentFragSortType" + activity.slideFrag.indexOf(ContentFragment.this), "Type ▲");
				}
				allName.setText("Name");
				allDate.setText("Date");
				allSize.setText("Size");
				Collections.sort(dataSourceL1, fileListSorter);
				srcAdapter.notifyDataSetChanged();
				break;
			case R.id.allDate:
				if (allDate.getText().toString().equals("Date ▲")) {
					allDate.setText("Date ▼");
					fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.DATE, FileListSorter.DESCENDING);
					AndroidUtils.setSharedPreference(activity, "ContentFragSortType" + activity.slideFrag.indexOf(ContentFragment.this), "Date ▼");
				} else {
					allDate.setText("Date ▲");
					fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.DATE, FileListSorter.ASCENDING);
					AndroidUtils.setSharedPreference(activity, "ContentFragSortType" + activity.slideFrag.indexOf(ContentFragment.this), "Date ▲");
				}
				allName.setText("Name");
				allSize.setText("Size");
				allType.setText("Type");
				Collections.sort(dataSourceL1, fileListSorter);
				srcAdapter.notifyDataSetChanged();
				break;
			case R.id.allSize:
				if (allSize.getText().toString().equals("Size ▲")) {
					allSize.setText("Size ▼");
					fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.SIZE, FileListSorter.DESCENDING);
					AndroidUtils.setSharedPreference(activity, "ContentFragSortType" + activity.slideFrag.indexOf(ContentFragment.this), "Size ▼");
				} else {
					allSize.setText("Size ▲");
					fileListSorter = new FileListSorter(FileListSorter.DIR_TOP, FileListSorter.SIZE, FileListSorter.ASCENDING);
					AndroidUtils.setSharedPreference(activity, "ContentFragSortType" + activity.slideFrag.indexOf(ContentFragment.this), "Size ▲");
				}
				allName.setText("Name");
				allDate.setText("Date");
				allType.setText("Type");
				Collections.sort(dataSourceL1, fileListSorter);
				srcAdapter.notifyDataSetChanged();
				break;
			case R.id.icons:
				mainmenu(p1);
				break;
			case R.id.search:
				searchButton();
				break;
			case R.id.clear:
				quicksearch.setText("");
				break;
			case R.id.dirMore:
				final MenuBuilder menuBuilder = new MenuBuilder(activity);
				final MenuInflater inflater = new MenuInflater(activity);
				inflater.inflate(R.menu.storage, menuBuilder);
				final MenuPopupHelper optionsMenu = new MenuPopupHelper(activity , menuBuilder, dirMore);
				optionsMenu.setForceShowIcon(true);
				MenuItem mi = menuBuilder.findItem(R.id.otg);
				if (true) {
					mi.setEnabled(true);
				} else {
					mi.setEnabled(false);
				}
				mi.getIcon().setFilterBitmap(true);
				mi.getIcon().setColorFilter(ExplorerActivity.TEXT_COLOR, PorterDuff.Mode.SRC_IN);

				mi = menuBuilder.findItem(R.id.addFolder);
				mi.getIcon().setFilterBitmap(true);
				mi.getIcon().setColorFilter(ExplorerActivity.TEXT_COLOR, PorterDuff.Mode.SRC_IN);
				
				mi = menuBuilder.findItem(R.id.sdcard);
				mi.getIcon().setFilterBitmap(true);
				mi.getIcon().setColorFilter(ExplorerActivity.TEXT_COLOR, PorterDuff.Mode.SRC_IN);
				
				mi = menuBuilder.findItem(R.id.microsd);
				if (new File("/storage/MicroSD").exists()) {
					mi.setEnabled(true);
				} else {
					mi.setEnabled(false);
				}
				
				menuBuilder.setCallback(new MenuBuilder.Callback() {
						@Override
						public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
							Log.d(TAG, item.getTitle() + ".");
							switch (item.getItemId())  {
								case R.id.sdcard:
									changeDir("/sdcard", true);
									break;
								case R.id.microsd:
									changeDir("/storage/MicroSD", true);
									break;
								case R.id.addFolder:
									activity.addFolder(p1);
									break;
							}
							return true;
						}
						@Override
						public void onMenuModeChange(MenuBuilder menu) {}
				});
				optionsMenu.show();
				break;
		}
	}

	private class TextSearch implements TextWatcher {
		public void beforeTextChanged(CharSequence s, int start, int end, int count) {
		}

		public void afterTextChanged(final Editable text) {
			final String filesearch = text.toString();
			Log.d(TAG, "quicksearch " + filesearch);
			if (filesearch.length() > 0) {
				if (searchTask != null
					&& searchTask.getStatus() == AsyncTask.Status.RUNNING) {
					searchTask.cancel(true);
				}
				if (!mSwipeRefreshLayout.isRefreshing()) {
					mSwipeRefreshLayout.post(new Runnable() {
							@Override
							public void run() {
								mSwipeRefreshLayout.setRefreshing(true);
							}
						});
				}
				searchTask = new SearchFileNameTask();
				searchTask.execute(filesearch);
			}
		}

		public void onTextChanged(CharSequence s, int start, int end, int count) {
		}
	}
	
	void refreshRecyclerViewLayoutManager() {
		setRecyclerViewLayoutManager();
		horizontalDivider0.setBackgroundColor(ExplorerActivity.DIVIDER_COLOR);
		horizontalDivider12.setBackgroundColor(ExplorerActivity.DIVIDER_COLOR);
		horizontalDivider7.setBackgroundColor(ExplorerActivity.DIVIDER_COLOR);
	}

	void setRecyclerViewLayoutManager() {
        Log.d(TAG, "setRecyclerViewLayoutManager " + gridLayoutManager);
		if (listView1 == null) {
			return;
		}
		int scrollPosition = 0, top = 0;
        // If a layout manager has already been set, get current scroll position.
        if (gridLayoutManager != null) {
			scrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
			final View vi = listView1.getChildAt(0); 
			top = (vi == null) ? 0 : vi.getTop();
		}
		final Context context = getContext();
		gridLayoutManager = new GridLayoutManager(context, spanCount);
		listView1.removeItemDecoration(dividerItemDecoration);
		if (spanCount <= 2) {
			dividerItemDecoration = new GridDividerItemDecoration(context, true);
			//dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST, true, true);
			listView1.addItemDecoration(dividerItemDecoration);
		}
		
		srcAdapter = new ArrAdapter(this, dataSourceL1, commands, horizontalDivider);
		listView1.setAdapter(srcAdapter);

		listView1.setLayoutManager(gridLayoutManager);
		gridLayoutManager.scrollToPositionWithOffset(scrollPosition, top);
	}

	void trimBackStack() {
		final int size = backStack.size() / 2;
		Log.d(TAG, "trimBackStack " + size);
		for (int i = 0; i < size; i++) {
			backStack.remove(0);
		}
	}

	@Override
	public void onLowMemory() {
		Log.d(TAG, "onLowMemory " + Runtime.getRuntime().freeMemory());
		super.onLowMemory();
		trimBackStack();
	}

	boolean back() {
		Map<String, Object> softBundle;
		Log.d(TAG, "back " + backStack.size());
		if (backStack.size() > 1 && (softBundle = backStack.pop()) != null && softBundle.get("dataSourceL1") != null) {
			Log.d(TAG, "back " + softBundle);
			reload(softBundle);
			return true;
		} else {
			return false;
		}
	}

	public void changeDir(final String curDir, final boolean doScroll) {
		Log.i(TAG, "changeDir " + curDir + ", " + doScroll);
		loadList.cancel(true);
		loadList = new LoadFiles();
		if (searchTask != null
			&& searchTask.getStatus() == AsyncTask.Status.RUNNING) {
			searchTask.cancel(true);
		}
		loadList.execute(curDir, doScroll);
		
	}
	
	

//	public void updateDir(String d, ContentFragment cf) {
//		Log.d(TAG, "updateDir " + d + ", " + cf + ", " + activity.slideFrag.getCurrentFragment());
//		setDirectoryButtons();
//		if (cf == activity.slideFrag2.getCurrentFragment2()) {
//			title = new File(d).getName();
//			activity.curExploreFrag = cf;
//			activity.slideFrag2.notifyTitleChange();
//		} else if (cf == activity.slideFrag.getCurrentFragment()) {
//			activity.dir = d;
//			activity.curContentFrag = cf;
//			activity.slideFrag.notifyTitleChange();
//		}
//	}

	/**
     * Refresh the contents of the directory that", "currently shown.
     */
//    public void refreshDirectory() {
//		Log.d(TAG, "refreshDirectory " + path + ", " + this);
//		if (path != null) {
//			changeDir(path, false);
//		} else {
//			updateDir(dirTemp4Search, this);
//		}
//    }

	public void manageUi(boolean search) {
		if (search == true) {
			quicksearch.setHint("Search " + ((CURRENT_PATH != null) ? new File(CURRENT_PATH).getName() : new File(dirTemp4Search).getName()));
			searchButton.setImageResource(R.drawable.ic_arrow_back_grey600);
			//topflipper.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_bottom));
			topflipper.setDisplayedChild(topflipper.indexOfChild(quickLayout));
			setSearchMode(true);
			quicksearch.requestFocus();
			//imm.showSoftInput(quicksearch, InputMethodManager.SHOW_FORCED);
			imm.showSoftInput(quicksearch, InputMethodManager.SHOW_IMPLICIT);
			//imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
			//activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		} else {
			//InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			//imm.showSoftInput(quicksearch, InputMethodManager.SHOW_IMPLICIT);
			imm.hideSoftInputFromWindow(quicksearch.getWindowToken(), 0);
			quicksearch.setText("");
			searchButton.setImageResource(R.drawable.ic_action_search);
			//topflipper.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_top));
			topflipper.setDisplayedChild(topflipper.indexOfChild(scrolltext));
			setSearchMode(false);//curContentFrag.
			refreshDirectory();//slideFrag.getCurrentFragment().
		}
	}

	public void searchButton() {
		searchMode = !searchMode;
		manageUi(searchMode);
	}

//	void setDirectoryButtons() {
//		Log.d(TAG, "setDirectoryButtons " + path);
//		//topflipper.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_top));
//
//		if (path != null) {
//			mDirectoryButtons.removeAllViews();
//			String[] parts = path.split("/");
//
//			activity = (ExplorerActivity) getActivity();
//			final TextView ib = new TextView(activity);
//			final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT);
//			layoutParams.gravity = Gravity.CENTER;
//			ib.setLayoutParams(layoutParams);
//			ib.setBackgroundResource(R.drawable.ripple);
//			ib.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//			ib.setText("/");
//			ib.setTag("/");
//			ib.setMinEms(2);
//			ib.setPadding(0, 4, 0, 4);
//			ib.setTextColor(ExplorerActivity.TEXT_COLOR);
//			// ib.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//			ib.setGravity(Gravity.CENTER);
//			ib.setOnClickListener(new View.OnClickListener() {
//					public void onClick(View view) {
//						changeDir("/", true);
//						updateDelPaste();
//					}
//				});
//			mDirectoryButtons.addView(ib);
//
//			String folder = "";
//			View v;
//			TextView b = null;
//			for (int i = 1; i < parts.length; i++) {
//				folder += "/" + parts[i];
//				v = activity.getLayoutInflater().inflate(R.layout.dir, null);
//				b = (TextView) v.findViewById(R.id.name);
//				b.setText(parts[i]);
//				b.setTag(folder);
//				b.setTextColor(ExplorerActivity.TEXT_COLOR);
//				b.setOnClickListener(new View.OnClickListener() {
//						public void onClick(View view) {
//							String dir2 = (String) view.getTag();
//							changeDir(dir2, true);
//							updateDelPaste();
//						}
//					});
//				mDirectoryButtons.addView(v);
//				scrolltext.postDelayed(new Runnable() {
//						public void run() {
//							scrolltext.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
//						}
//					}, 100L);
//			}
//			AndroidUtils.setTouch(mDirectoryButtons, this);
//			
//			if (b != null) {
//				b.setOnLongClickListener(new OnLongClickListener() {
//						@Override
//						public boolean onLongClick(View p1) {
//							final EditText editText = new EditText(activity);
//							final CharSequence clipboardData = AndroidUtils.getClipboardData(activity);
//							if (clipboardData.length() > 0 && clipboardData.charAt(0) == '/') {
//								editText.setText(clipboardData);
//							} else {
//								editText.setText(path);
//							}
//							final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//								LinearLayout.LayoutParams.MATCH_PARENT,
//								LinearLayout.LayoutParams.WRAP_CONTENT);
//							layoutParams.gravity = Gravity.CENTER;
//							editText.setLayoutParams(layoutParams);
//							editText.setSingleLine(true);
//							editText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//							editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//							editText.setMinEms(2);
//							//editText.setGravity(Gravity.CENTER);
//							final int density = 8 * (int)getResources().getDisplayMetrics().density;
//							editText.setPadding(density, density, density, density);
//
//							AlertDialog dialog = new AlertDialog.Builder(activity)
//								.setIconAttribute(android.R.attr.dialogIcon)
//								.setTitle("Go to...")
//								.setView(editText)
//								.setPositiveButton(R.string.ok,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog, int whichButton) {
//										String name = editText.getText().toString();
//										Log.d(TAG, "new " + name);
//										File newF = new File(name);
//										if (newF.exists()) {
//											if (newF.isDirectory()) {
//												path = name;
//												changeDir(path, true);
//											} else {
//												path = newF.getParent();
//												changeDir(newF.getParentFile().getAbsolutePath(), true);
//											}
//											dialog.dismiss();
//										} else {
//											showToast("\"" + newF + "\" does not exist. Please choose another name");
//										}
//									}
//								})
//								.setNegativeButton(R.string.cancel,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog, int whichButton) {
//										dialog.dismiss();
//									}
//								}).create();
//							dialog.show();
//							return true;
//						}
//					});
//			}
//		}
//	}

	private class SearchFileNameTask extends AsyncTask<String, Long, ArrayList<LayoutElements>> {

		protected void onPreExecute() {
			setSearchMode(true);// srcAdapter.dirStr = null;curContentFrag.
			searchVal = quicksearch.getText().toString();//curContentFrag.
			showToast("Searching...");
			dataSourceL1.clear();
			notifyDataSetChanged();// srcAdapter.notifyDataSetChanged();curContentFrag.
		}

		@Override
		protected ArrayList<LayoutElements> doInBackground(String... params) {
			Log.d("SearchFileNameTask", "dirTemp4Search " + dirTemp4Search);
			final ArrayList<LayoutElements> tempAppList = new ArrayList<>();
			File file = new File(dirTemp4Search);
			
			if (file.exists()) {
				Collection<File> c = FileUtil.getFilesBy(file.listFiles(),
														 params[0], true);
				Log.d(TAG, "getFilesBy " + Util.collectionToString(c, true, "\n"));
				for (File le : c) {
					tempAppList.add(new LayoutElements(le));
				}
				//addAllDS1(Util.collectionFile2CollectionString(c));// dataSourceL1.addAll(Util.collectionFile2CollectionString(c));curContentFrag.
				// Log.d("dataSourceL1 new task",
				// Util.collectionToString(dataSourceL1, true, "\n"));
			} else {
				showToast(dirTemp4Search + " is not existed");
			}
			return tempAppList;
		}

		@Override
		protected void onPostExecute(ArrayList<LayoutElements> result) {
			
			if (mSwipeRefreshLayout.isRefreshing()) {
				mSwipeRefreshLayout.setRefreshing(false);
			}
			Collections.sort(result, fileListSorter);
			dataSourceL1.addAll(result);
			selectedInList1.clear();
			srcAdapter.notifyDataSetChanged();
			selectionStatus1.setText(selectedInList1.size() 
									 + "/" + dataSourceL1.size());
			File file = new File(dirTemp4Search);
			diskStatus.setText(
				"Free " + Util.nf.format(file.getFreeSpace() / (1 << 20))
				+ " MiB. Used " + Util.nf.format((file.getTotalSpace()-file.getFreeSpace()) / (1 << 20))
				+ " MiB. Total " + Util.nf.format(file.getTotalSpace() / (1 << 20)) + " MiB");
			if (dataSourceL1.size() == 0) {
				nofilelayout.setVisibility(View.VISIBLE);
				mSwipeRefreshLayout.setVisibility(View.GONE);
			} else {
				nofilelayout.setVisibility(View.GONE);
				mSwipeRefreshLayout.setVisibility(View.VISIBLE);
			}
		}
	}

//	void updateDelPaste() {
//		if (selectedInList1.size() > 0 && deletePastes.getCompoundDrawables()[1] != drawableDelete) {
//			deletePastes.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//			deletePastes.setCompoundDrawablesWithIntrinsicBounds(null, drawableDelete, null, null);
//			deletePastes.setText("Delete");
//		} else if (selectedInList1.size() == 0 && deletePastes.getCompoundDrawables()[1] != drawablePaste) {
//			deletePastes.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//			deletePastes.setCompoundDrawablesWithIntrinsicBounds(null, drawablePaste, null, null);
//			deletePastes.setText("Paste");
//		}
//		deletePastes.getCompoundDrawables()[1].setColorFilter(ExplorerActivity.TEXT_COLOR, PorterDuff.Mode.SRC_IN);
//	}

//	public void up(View view) {
//
//		final File curDir = new File(path);
//		Log.d("up curDir", curDir.getAbsolutePath());
//		File parentFile = curDir.getParentFile();
//		if (parentFile != null) {
//			Log.d("curDir.getParentFile()", parentFile.getAbsolutePath());
//			changeDir(parentFile, true);
//		}
//	}


    /**
     * Sets up a FileObserver to watch the current directory.
     */
//    private FileObserver createFileObserver(String path) {
//        return new FileObserver(path, FileObserver.CREATE | FileObserver.DELETE
//								| FileObserver.MOVED_FROM | FileObserver.MOVED_TO
//								| FileObserver.DELETE_SELF | FileObserver.MOVE_SELF
//								| FileObserver.CLOSE_WRITE) {
//            @Override
//            public void onEvent(int event, String path) {
//                if (path != null) {
//                    Util.debug(TAG, "FileObserver received event %d, CREATE = 256;DELETE = 512;DELETE_SELF = 1024;MODIFY = 2;MOVED_FROM = 64;MOVED_TO = 128; path %s", event, path);
//					activity.runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								refreshDirectory();
//							}
//						});
//                }
//            }
//        };
//    }

	private void setSearchMode(final boolean search) {
		Log.d(TAG, "setSearchMode " + searchMode + ", " + CURRENT_PATH + ", " + dirTemp4Search);
		if (search) {
			if (CURRENT_PATH != null) {
				dirTemp4Search = CURRENT_PATH;
			}
			CURRENT_PATH = null;
			//pagerItem.searchMode = true;
			searchMode = true;
		} else {
			CURRENT_PATH = dirTemp4Search;
			//pagerItem.searchMode = false;
			searchMode = false;
		}
		Log.d(TAG, "setSearchMode " + searchMode + ", " + CURRENT_PATH + ", " + dirTemp4Search);
	}

//	void notifyDataSetChanged() {
//		srcAdapter.notifyDataSetChanged();
//	}

	void setAllCbxChecked(boolean en) {
		allCbx.setSelected(en);//.setChecked(en);
		if (en) {
			allCbx.setImageResource(R.drawable.ic_accept);
		} else {
			allCbx.setImageResource(R.drawable.dot);
		}
	}

	public void mainmenu(final View v) {
		final PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.panel_commands);
		final Menu menu = popup.getMenu();
		if (!activity.multiFiles) {
			menu.findItem(R.id.horizontalDivider5).setVisible(false);
		}
		MenuItem mi = menu.findItem(R.id.clearSelection);
		if (selectedInList1.size() == 0) {
			mi.setEnabled(false);
		} else {
			mi.setEnabled(true);
		}
		mi = menu.findItem(R.id.rangeSelection);
		if (selectedInList1.size() > 1) {
			mi.setEnabled(true);
		} else {
			mi.setEnabled(false);
		}
		mi = menu.findItem(R.id.undoClearSelection);
		if (tempSelectedInList1.size() > 0) {
			mi.setEnabled(true);
		} else {
			mi.setEnabled(false);
		}
        mi = menu.findItem(R.id.hide);
		if (right.getVisibility() == View.VISIBLE) {
			mi.setTitle("Hide");
		} else {
			mi.setTitle("2 panels");
		}
        mi = menu.findItem(R.id.biggerequalpanel);
		if (left.getVisibility() == View.GONE || right.getVisibility() == View.GONE) {
			mi.setEnabled(false);
		} else {
			mi.setEnabled(true);
			if (width.size <= 0) {
				mi.setTitle("Wider panel");
			} else {
				mi.setTitle("2 panels equal");
			}
		}
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					Log.d(TAG, item.getTitle() + ".");
					switch (item.getItemId())  {
						case R.id.rangeSelection:
							int min = Integer.MAX_VALUE, max = -1;
							int cur = -3;
							for (LayoutElements s : selectedInList1) {
								cur = dataSourceL1.indexOf(s);
								if (cur > max) {
									max = cur;
								}
								if (cur < min && cur >= 0) {
									min = cur;
								}
							}
							selectedInList1.clear();
							for (cur = min; cur <= max; cur++) {
								selectedInList1.add(dataSourceL1.get(cur));
							}
							srcAdapter.notifyDataSetChanged();
							break;
						case R.id.inversion:
							tempSelectedInList1.clear();
							for (LayoutElements f : dataSourceL1) {
								if (!selectedInList1.contains(f)) {
									tempSelectedInList1.add(f);
								}
							}
							selectedInList1.clear();
							selectedInList1.addAll(tempSelectedInList1);
							srcAdapter.notifyDataSetChanged();
							break;
						case R.id.clearSelection:
							tempSelectedInList1.clear();
							tempSelectedInList1.addAll(selectedInList1);
							selectedInList1.clear();
							srcAdapter.notifyDataSetChanged();
							break;
						case R.id.undoClearSelection:
							selectedInList1.clear();
							selectedInList1.addAll(tempSelectedInList1);
							tempSelectedInList1.clear();
							srcAdapter.notifyDataSetChanged();
							break;
						case R.id.swap:
//							if (spanCount == 8) {
//								spanCount = 4;
//							}
//							AndroidUtils.setSharedPreference(getContext(), "SPAN_COUNT", spanCount);
							activity.swap(v);
							break;
						case R.id.hide: 
							if (right.getVisibility() == View.VISIBLE && left.getVisibility() == View.VISIBLE) {
								if (spanCount == 4) {
									spanCount = 8;
									setRecyclerViewLayoutManager();
								}
								if (activity.swap) {
									left.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_left));
									right.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_left));
								} else {
									left.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_right));
									right.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_out_right));
								}
								if (type == -1)
									left.setVisibility(View.GONE);
								else
									right.setVisibility(View.GONE);
							} else {
								if (spanCount == 8) {
									spanCount = 4;
									setRecyclerViewLayoutManager();
								}
								if (activity.swap) {
									left.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_left));
									right.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_left));
								} else {
									left.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_right));
									right.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_out_right));
								}
								right.setVisibility(View.VISIBLE);
							}
							break;
						case R.id.biggerequalpanel:
							if (activity.leftSize <= 0) {
								if (type == -1) {
									LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)activity.left.getLayoutParams();
									params.weight = 1.0f;
									activity.left.setLayoutParams(params);
									params = (LinearLayout.LayoutParams)activity.right.getLayoutParams();
									params.weight = 2.0f;
									activity.right.setLayoutParams(params);
									activity.leftSize = 1;
									if (left == activity.left) {
										width.size = 1;
										//activity.leftSize = width.size;
										activity.slideFrag2.size = -width.size;
									} else {
										width.size = -1;
										//activity.leftSize = -width.size;
										activity.slideFrag2.size = -width.size;
									}
								} else {
									LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)activity.left.getLayoutParams();
									params.weight = 2.0f;
									activity.left.setLayoutParams(params);
									params = (LinearLayout.LayoutParams)activity.right.getLayoutParams();
									params.weight = 1.0f;
									activity.right.setLayoutParams(params);
									activity.leftSize = 1;
									if (left == activity.left) {
										width.size = -1;
										//activity.leftSize = -width.size;
										activity.slideFrag.size = -width.size;
									} else {
										width.size = 1;
										//activity.leftSize = width.size;
										activity.slideFrag.size = -width.size;
									}
								}
							} else {
								LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)left.getLayoutParams();
								params.weight = 1.0f;
								left.setLayoutParams(params);
								params = (LinearLayout.LayoutParams)right.getLayoutParams();
								params.weight = 1.0f;
								right.setLayoutParams(params);
								activity.leftSize = 0;
								//width.size = 0;
								activity.slideFrag.size = 0;
								activity.slideFrag2.size = 0;
							}
							activity.curContentFrag2.setRecyclerViewLayoutManager();
							activity.curExploreFrag.setRecyclerViewLayoutManager();
							AndroidUtils.setSharedPreference(activity, "biggerequalpanel", activity.leftSize);
							
					}
					return true;
				}
			});
		popup.show();
	}

//	public boolean copys(final View v) {
//		curDir = path;
//		activity.mode = OpenMode.FILE;
//		activity.copyl.clear();
//		activity.cutl.clear();
//		activity.copyl.addAll(selectedInList1);
//		return true;
//	}

//	public boolean cuts(final View v) {
//		curDir = path;
//		activity.mode = OpenMode.FILE;
//		activity.copyl.clear();
//		activity.cutl.clear();
//		activity.cutl.addAll(selectedInList1);
//		return true;
//	}

	private String curDir = "";
//	public boolean pastes(final View v) {
//		if (activity.copyl.size() > 0) {
//			ArrayList<BaseFile> copies = new ArrayList<>();
//			for (LayoutElements f : activity.copyl) {
//				//File f = new File(st);
//				BaseFile baseFile=new BaseFile(f.getPath(), "", f.lastModified(), f.length(), f.isDirectory());
//				baseFile.setMode(OpenMode.FILE);
//				baseFile.setName(f.getName());
//				copies.add(baseFile);
//			}
//			//MAIN_ACTIVITY.COPY_PATH = copies;
//			//String path = path;
//
//			ArrayList<BaseFile> arrayList;// = new ArrayList<>();
//			arrayList = copies;//COPY_PATH;
//			new CopyFileCheck(this, path, false, activity, BaseActivity.rootMode).executeOnExecutor(AsyncTask
//																									  .THREAD_POOL_EXECUTOR, arrayList);
//		} else if (activity.cutl.size() > 0) {//MOVE_PATH != null) {
//			ArrayList<BaseFile> moves = new ArrayList<>();
//			for (LayoutElements f : activity.cutl) {
//				//File f = new File(st);
//				BaseFile baseFile=new BaseFile(f.getPath(), "", f.lastModified(), f.length(), f.isDirectory());
//				baseFile.setMode(OpenMode.FILE);
//				baseFile.setName(f.getName());
//				moves.add(baseFile);
//			}
//			//MAIN_ACTIVITY.COPY_PATH = copies;
//			//String path = path;
//
//			ArrayList<BaseFile> arrayList;// = new ArrayList<>();
//			arrayList = moves;
//			new CopyFileCheck(this, path, true, activity, BaseActivity.rootMode).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arrayList);
//			activity.curContentFrag2.dataSourceL1.removeAll(activity.cutl);
//			activity.curContentFrag2.tempOriDataSourceL1.removeAll(activity.cutl);
//			activity.curContentFrag2.selectedInList1.removeAll(activity.cutl);
//			activity.cutl.clear();
//		}
//		activity.curContentFrag2.srcAdapter.notifyDataSetChanged();
//
////		if (copyl.size() > 0) {
////			List<File> l = new LinkedList<>();
////			for (String st : copyl) {
////				l.addAll(FileUtil.getFiles(st, false));
////			}
////			for
////			Futils.moveCopy(getActivity(), false, dir, Util.collection2Array(copyl));
////		} else if (cutl.size() > 0) {
////			Futils.moveCopy(getActivity(), true, dir, Util.collection2Array(cutl));
////		} else {
////			showToast("No file selected");
////		}
//		return true;
//	}

	public boolean renames(final View v) {
		return true;
	}

//	public boolean addScreens(final View v) {
//		activity = (ExplorerActivity) getActivity();
//		for (LayoutElements f : selectedInList1) {
//			AndroidUtils.addShortcut(activity, f.bf.f);
//		}
//		return true;
//	}

//	public boolean deletes(final View v) {
//		//new Futils().deleteFileList(selectedInList1, activity);
//		activity.curContentFrag2.dataSourceL1.removeAll(selectedInList1);
//		activity.curContentFrag2.tempOriDataSourceL1.removeAll(selectedInList1);
//		activity.curContentFrag2.selectedInList1.removeAll(selectedInList1);
//		return true;
//	}

//	public boolean compresss(final View v) {
//		return true;
//	}
//
//	public boolean encrypts(final View v) {
//		return true;
//	}

//	public boolean sends(final View v) {
//		if (selectedInList1.size() > 0) {
//			ArrayList<Uri> uris = new ArrayList<Uri>(selectedInList1.size());
//			Intent send_intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//			send_intent.setFlags(0x1b080001);
//
//			send_intent.setType("*/*");
//			for(File file : selectedInList1) {
//				uris.add(Uri.fromFile(file));
//			}
//			Log.d(TAG, Util.collectionToString(uris, true, "\n") + ".");
//			send_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
//			Log.d("send_intent", send_intent + ".");
//			Log.d("send_intent.getExtras()", AndroidUtils.bundleToString(send_intent.getExtras()));
//			Intent createChooser = Intent.createChooser(send_intent, "Send via..");
//			Log.d("createChooser", createChooser + ".");
//			Log.d("createChooser.getExtras()", AndroidUtils.bundleToString(createChooser.getExtras()));
//			// Verify that the intent will resolve to an activity
//			if (createChooser.resolveActivity(getContext().getPackageManager()) != null) {
//				startActivity(createChooser);
//			}
//			startActivity(createChooser);
//		} else {
//			showToast("No file selected");
//		}
//		return true;
//	}

//	public boolean shares(final View v) {
//		if (selectedInList1.size() > 0) {
//
//			ArrayList<LayoutElements> arrayList = new ArrayList<>();
//			for (LayoutElements s : selectedInList1) {
//				arrayList.add(s);
//			}
//			if (selectedInList1.size() > 100)
//				Toast.makeText(getContext(), "Can't share more than 100 files", Toast.LENGTH_SHORT).show();
//			else {
//				final ArrayList<File> lf = new ArrayList<>(selectedInList1.size());
//				for (LayoutElements le : selectedInList1) {
//					lf.add(le.bf.f);
//				}
//				//new Futils().shareFiles(lf, activity, theme1, Color.BLUE);
//				new Futils().shareFiles(lf, activity, activity.getAppTheme(), Color.parseColor(fabSkin));
//			}
//		} else {
//			showToast("No file selected");
//		}
//		return true;
//	}

//	public boolean infos(final View v) {
//		if (selectedInList1.size() > 0) {
//
//			ArrayList<LayoutElements> arrayList = new ArrayList<>();
//			for (LayoutElements s : selectedInList1) {
//				arrayList.add(s);
//			}
//			if (selectedInList1.size() > 100)
//				Toast.makeText(getContext(), "Can't share more than 100 files", Toast.LENGTH_SHORT).show();
//			else {
//				final ArrayList<File> lf = new ArrayList<>(selectedInList1.size());
//				for (LayoutElements le : selectedInList1) {
//					lf.add(le.bf.f);
//				}
//				//new Futils().shareFiles(lf, activity, theme1, Color.BLUE);
//				new Futils().shareFiles(lf, activity, activity.getAppTheme(), Color.parseColor(fabSkin));
//			}
//		} else {
//			showToast("No file selected");
//		}
//		return true;
//	}

//	private class ArrAdapter extends RecyclerAdapter<File, ArrAdapter.ViewHolder> implements OnLongClickListener, OnClickListener {
//
//		private final int backgroundResource;
//
//		private class ViewHolder extends RecyclerView.ViewHolder {
//			private TextView name;
//			private TextView size;
//			private TextView attr;
//			private TextView lastModified;
//			private TextView type;
//			private ImageButton cbx;
//			private ImageView image;
//			private ImageButton more;
//			private View convertedView;
//
//			public ViewHolder(View convertView) {
//				super(convertView);
//				name = (TextView) convertView.findViewById(R.id.name);
//				size = (TextView) convertView.findViewById(R.id.items);
//				attr = (TextView) convertView.findViewById(R.id.attr);
//				lastModified = (TextView) convertView.findViewById(R.id.lastModified);
//				type = (TextView) convertView.findViewById(R.id.type);
//				cbx = (ImageButton) convertView.findViewById(R.id.cbx);
//				image = (ImageView)convertView.findViewById(R.id.icon);
//				more = (ImageButton)convertView.findViewById(R.id.more);
//
//				convertView.setOnClickListener(ArrAdapter.this);
//				cbx.setOnClickListener(ArrAdapter.this);
//				image.setOnClickListener(ArrAdapter.this);
//				more.setOnClickListener(ArrAdapter.this);
//
//				convertView.setOnLongClickListener(ArrAdapter.this);
//				cbx.setOnLongClickListener(ArrAdapter.this);
//				image.setOnLongClickListener(ArrAdapter.this);
//				more.setOnLongClickListener(ArrAdapter.this);
//
//				more.setColorFilter(ExplorerActivity.TEXT_COLOR);
//
//				name.setTextColor(ExplorerActivity.DIR_COLOR);
//				size.setTextColor(ExplorerActivity.TEXT_COLOR);
//				attr.setTextColor(ExplorerActivity.TEXT_COLOR);
//				lastModified.setTextColor(ExplorerActivity.TEXT_COLOR);
//				type.setTextColor(ExplorerActivity.TEXT_COLOR);
//
//				image.setScaleType(ImageView.ScaleType.FIT_CENTER);
//				convertView.setTag(this);
//				this.convertedView = convertView;
//			}
//		}
//
//		public ArrAdapter(ArrayList<File> objects) {
//			super(objects);
//			Log.d(TAG, "ArrAdapter " + objects);
//			int[] attrs = new int[]{R.attr.selectableItemBackground};
//			TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
//			backgroundResource = typedArray.getResourceId(0, 0);
//			typedArray.recycle();
//		}
//
//		@Override
//		public int getItemViewType(int position) {
//			if (dataSourceL1.size() == 0) {
//				return 0;
//			} else if (spanCount == 1 || (spanCount == 2 && right.getVisibility() == View.GONE)) {
//				return 1;
//			} else if (spanCount == 2) {
//				return 2;
//			} else {
//				return 3;
//			}
//		}
//	
//		@Override
//		public ArrAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
//														int viewType) {
//			View v;
//			if (viewType <= 1) {
//				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
//			} else {
//				if (viewType == 2) {
//					v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_small, parent, false);
//				} else {
//					v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
//				}
//			}
//			// set the view's size, margins, paddings and layout parameters
//			final ViewHolder vh = new ViewHolder(v);
//			return vh;
//		}
//
//		// Replace the contents of a view (invoked by the layout manager)
//		@Override
//		public void onBindViewHolder(ViewHolder holder, int position) {
//			final File f = mDataset.get(position);
//			//Log.d(TAG, "getView " + fileName);
//
//			final String fPath = f.getAbsolutePath();
//			holder.name.setText(f.getName());
//			holder.image.setContentDescription(fPath);
//			holder.name.setContentDescription(fPath);
//			holder.size.setContentDescription(fPath);
//			holder.attr.setContentDescription(fPath);
//			holder.lastModified.setContentDescription(fPath);
//			holder.type.setContentDescription(fPath);
//			holder.cbx.setContentDescription(fPath);
//			holder.more.setContentDescription(fPath);
//			holder.more.setTag(holder);
//			holder.convertedView.setContentDescription(fPath);
//
//			if (dir == null || dir.length() > 0) {
//				holder.name.setEllipsize(TextUtils.TruncateAt.MIDDLE);
//			} else {
//				holder.name.setEllipsize(TextUtils.TruncateAt.START);
//			}
//
////	        if (!f.exists()) {
////				dataSourceL1.remove(f);
////				selectedInList1.remove(f);
////				notifyItemRemoved(position);
////				return;//convertView;
////			}
//
//			boolean inDataSource2 = false;
//			boolean isPartial = false;
//			//Log.d(TAG, "dataSource2" + Util.collectionToString(dataSourceL2, true, "\n"));
//			if (multiFiles && dataSourceL2 != null) {
//				final String fPathD = fPath + "/";
//				String f2Path;
//				for (File f2 : dataSourceL2) {
//					f2Path = f2.getAbsolutePath();
//					if (f2.equals(f) || fPath.startsWith(f2Path + "/")) {
//						inDataSource2 = true;
//						break;
//					} else if (f2Path.startsWith(fPathD)) {
//						isPartial = true;
//					}
//				}
//			}
////			if (activity.theme1 == 1) {
////				holder.convertedView.setBackgroundResource(R.drawable.safr_ripple_white);
////			} else {
////				holder.convertedView.setBackgroundResource(R.drawable.safr_ripple_black);
////			}
//			//Log.d(TAG, "inDataSource2 " + inDataSource2 + ", " + dir);
//			//Log.d("f.getAbsolutePath()", f.getAbsolutePath());
//			//Log.d("curSelectedFiles", curSelectedFiles.toString());
//			if (inDataSource2) {
//				holder.convertedView.setBackgroundColor(ExplorerActivity.IN_DATA_SOURCE_2);
//				holder.cbx.setImageResource(R.drawable.ic_accept);
//				holder.cbx.setSelected(true);
//				holder.cbx.setEnabled(false);
//				if ((dir == null || dir.length() > 0) && selectedInList1.size() == dataSourceL1.size()) {
//					allCbx.setSelected(true);//.setChecked(true);
//					allCbx.setImageResource(R.drawable.ic_accept);
//				}
//			} else if (selectedInList1.contains(f)) {
//				holder.convertedView.setBackgroundColor(ExplorerActivity.SELECTED_IN_LIST);
//				holder.cbx.setImageResource(R.drawable.ic_accept);
//				holder.cbx.setSelected(true);
//				holder.cbx.setEnabled(true);
//				if ((dir == null || dir.length() > 0) && selectedInList1.size() == dataSourceL1.size()) {
//					allCbx.setSelected(true);//.setChecked(true);
//					allCbx.setImageResource(R.drawable.ic_accept);
//				}
//			} else if (isPartial) {
//				holder.convertedView.setBackgroundColor(ExplorerActivity.IS_PARTIAL);
//				holder.cbx.setImageResource(R.drawable.ready);
//				holder.cbx.setSelected(false);
//				holder.cbx.setEnabled(true);
//				allCbx.setSelected(false);//.setChecked(false);
//				if (selectedInList1.size() == 0) {
//					allCbx.setImageResource(R.drawable.dot);
//				} else {
//					allCbx.setImageResource(R.drawable.ready);
//				}
//	        } else {
//				holder.convertedView.setBackgroundResource(backgroundResource);
//				if (selectedInList1.size() > 0) {
//					holder.cbx.setImageResource(R.drawable.ready);
//					allCbx.setImageResource(R.drawable.ready);
//				} else {
//					holder.cbx.setImageResource(R.drawable.dot);
//					allCbx.setImageResource(R.drawable.dot);
//				}
//				holder.cbx.setSelected(false);
//				holder.cbx.setEnabled(true);
//				allCbx.setSelected(false);
//				
//	        }
//			if (tempPreviewL2 != null && tempPreviewL2.equals(f)) {
//				holder.convertedView.setBackgroundColor(ExplorerActivity.LIGHT_GREY);
//			}
//
//			final boolean canRead = f.canRead();
//			final boolean canWrite = f.canWrite();
//			if (!f.isDirectory()) {
//				long length = f.length();
//				holder.size.setText(Util.nf.format(length) + " B");
//				String st;
//				if (canWrite) {
//					st = "-rw";
//				} else if (canRead) {
//					st = "-r-";
//				} else {
//					st = "---";
//					holder.cbx.setEnabled(false);
//				}
//				final String namef = f.getName();
//				final int lastIndexOf = namef.lastIndexOf(".");
//				holder.type.setText(lastIndexOf >= 0 && lastIndexOf < namef.length() - 1 ? namef.substring(lastIndexOf + 1) : "");
//				holder.attr.setText(st);
//				holder.lastModified.setText(Util.dtf.format(f.lastModified()));
//			} else {
//				final String[] list = f.list();
//				int length = list == null ? 0 : list.length;
//				holder.size.setText(Util.nf.format(length) + " item");
//				final String st;
//				if (canWrite) {
//					st = "drw";
//				} else if (canRead) {
//					st = "dr-";
//				} else {
//					st = "d--";
//					holder.cbx.setEnabled(false);
//				}
//				holder.type.setText("Folder");
//				holder.attr.setText(st);
//				holder.lastModified.setText(Util.dtf.format(f.lastModified()));
//			}
//			imageLoader.displayImage(f, getContext(), holder.image, spanCount);
//		}
//
//		public boolean copy(final View item) {
//			activity.mode = OpenMode.FILE;
//			activity.copyl.clear();
//			activity.cutl.clear();
//			final ArrayList<File> al = new ArrayList<>(1);
//			al.add(new File((String)item.getContentDescription()));
//			activity.copyl.addAll(al);
//			return true;
//		}
//
//		public boolean cut(final View item) {
//			activity.mode = OpenMode.FILE;
//			activity.copyl.clear();
//			activity.cutl.clear();
//			final ArrayList<File> al = new ArrayList<>(1);
//			al.add(new File((String)item.getContentDescription()));
//			activity.cutl.addAll(al);
//			return true;
//		}
//
//		public boolean rename(final View item) {
//			final File oldF = new File((String)item.getContentDescription());
//			final String oldPath = oldF.getAbsolutePath();
//			Log.d(TAG, "oldPath " + oldPath + ", " + item);
//			final EditText editText = new EditText(getContext());
//			editText.setText(oldF.getName());
//			final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT);
//			layoutParams.gravity = Gravity.CENTER;
//			editText.setLayoutParams(layoutParams);
//			editText.setSingleLine(true);
//			editText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//			editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//			editText.setMinEms(2);
//			//editText.setGravity(Gravity.CENTER);
//			final int density = 8 * (int)getResources().getDisplayMetrics().density;
//			editText.setPadding(density, density, density, density);
//
//			AlertDialog dialog = new AlertDialog.Builder(getContext())
//				.setIconAttribute(android.R.attr.dialogIcon)
//				.setTitle("New Name")
//				.setView(editText)
//				.setPositiveButton(R.string.ok,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						String name = editText.getText().toString();
//						File newF = new File(oldF.getParent(), name);
//						String newPath = newF.getAbsolutePath();
//						Log.d("newF", newPath);
//						if (newF.exists()) {
//							showToast("\"" + newF + "\" is existing. Please choose another name");
//						} else {
//							boolean ok = AndroidPathUtils.renameFolder(oldF, newF, ContentFragment.this.getContext());//oldF.renameTo(newF);
//							if (ok) {
//								ViewHolder holder = (ViewHolder)item.getTag();
//								TextView nameTV = holder.name;
//								Log.d(TAG, "nameTV " + nameTV);
//								int i = 0;
//								for (File fn : dataSourceL1) {
//									if (fn.equals(oldPath)) {
//										dataSourceL1.set(i, newF);
//									}
//									i++;
//								}
//								srcAdapter.notifyDataSetChanged();
//
//								nameTV.setContentDescription(newPath);
//								holder.size.setContentDescription(newPath);
//								holder.attr.setContentDescription(newPath);
//								holder.lastModified.setContentDescription(newPath);
//								holder.type.setContentDescription(newPath);
//								holder.cbx.setContentDescription(newPath);
//								holder.image.setContentDescription(newPath);
//								item.setContentDescription(newPath);
//								holder.convertedView.setContentDescription(newPath);
//								showToast("Rename successfully");
//							} else {
//								showToast("Rename unsuccessfully");
//							}
//							dialog.dismiss();
//						}
//					}
//				})
//				.setNegativeButton(R.string.cancel,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						dialog.dismiss();
//					}
//				}).create();
//			dialog.show();
//			return true;
//		}
//
//		public boolean delete(final View item) {
//			ArrayList<String> arr = new ArrayList<>();
//			arr.add((String)item.getContentDescription());
//			//new Futils().deleteFiles(arr, activity);
//			return true;
//		}
//
//		public boolean share(View item) {
//			ArrayList<File> arrayList = new ArrayList<File>();
//			arrayList.add(new File((String)item.getContentDescription()));
//			new Futils().shareFiles(arrayList, activity, theme1, Color.BLUE);
//			return true;
//		}
//
//		public boolean send(View item) {
//			File f = new File((String)item.getContentDescription());
//			Uri uri = Uri.fromFile(f);
//			Intent i = new Intent(Intent.ACTION_SEND);
//			i.setFlags(0x1b080001);
//
//			i.setData(uri);
//			Log.d("i.setData(uri)", uri + "." + i);
//			String mimeType = MimeTypes.getMimeType(f);
//			i.setDataAndType(uri, mimeType);  //floor.getValue()
//			Log.d(TAG, f + " = " + mimeType);
//
//			Log.d("send", i + ".");
//			Log.d("send.getExtras()", AndroidUtils.bundleToString(i.getExtras()));
//			Intent createChooser = Intent.createChooser(i, "Send via..");
//			Log.d("createChooser", createChooser + ".");
//			Log.d("createChooser.getExtras()", AndroidUtils.bundleToString(createChooser.getExtras()));
//			startActivity(createChooser);
//			return true;
//		}
//
//		public boolean copyName(final View item) {
//			final String data = new File((String)item.getContentDescription()).getName();
//			AndroidUtils.copyToClipboard(getContext(), data);
//			return true;
//		}
//
//		public boolean copyPath(final View item) {
//			final String data = new File((String)item.getContentDescription()).getParent();
//			AndroidUtils.copyToClipboard(getContext(), data);
//			return true;
//		}
//
//		public boolean copyFullName(final View item) {
//			final String data = (String) item.getContentDescription();
//			AndroidUtils.copyToClipboard(getContext(), data);
//			return true;
//		}
//
//		public void onClick(final View v) {
//			activity.slideFrag2.getCurrentFragment2().select(false);
//			if (v.getId() == R.id.more) {
//
//				final MenuBuilder menuBuilder = new MenuBuilder(activity);
//				final MenuInflater inflater = new MenuInflater(activity);
//				inflater.inflate(R.menu.file_commands, menuBuilder);
//				final MenuPopupHelper optionsMenu = new MenuPopupHelper(activity , menuBuilder, allSize);
//				optionsMenu.setForceShowIcon(true);
//
//				menuBuilder.setCallback(new MenuBuilder.Callback() {
//						@Override
//						public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
//							switch (item.getItemId()) {
//								case R.id.copy:
//									copy(v);
//									break;
//								case R.id.cut:
//									cut(v);
//									break;
//								case R.id.rename:
//									rename(v);
//									break;
//								case R.id.delete:
//									delete(v);
//									break;
//								case R.id.share:
//									share(v);
//									break;
//								case R.id.send:
//									send(v);
//									break;
//								case R.id.name:
//									copyName(v);
//									break;
//								case R.id.path:
//									copyPath(v);
//									break;
//								case R.id.fullname:
//									copyFullName(v);
//									break;
//							}
//							return false ;
//						}
//						@Override
//						public void onMenuModeChange(MenuBuilder menu) {}
//					});
//				optionsMenu.show();
//				return;
//			}
//			final String fPath = (String) v.getContentDescription();
//			Log.d(TAG, "onClick, " + fPath + ", " + v);
//			if (fPath == null) {
//				return;
//			}
//			final File f = new File(fPath);
//			//Log.d(TAG, "currentSelectedList " + Util.collectionToString(selectedInList1, true, "\r\n"));
//			//Log.d(TAG, "selectedInList.contains(f) " + selectedInList1.contains(f));
//			//Log.d(TAG, "multiFiles " + multiFiles);
//			//Log.d(TAG, "f.exists() " + f.exists());
//			if (f.exists()) {
//				if (!f.canRead()) {
//					showToast(f + " cannot be read");
//				} else {
//					boolean inSelected = false;
//					if (dataSourceL2 != null)
//						for (File st : dataSourceL2) {
//							if (f.equals(st) || fPath.startsWith(st.getAbsolutePath() + "/")) {
//								inSelected = true;
//								break;
//							}
//						}
//					if (!inSelected) {
//						if (multiFiles || suffix.length() == 0) {
//							if (v.getId() == R.id.icon) {
//								tempPreviewL2 = f;
//								if (f.isFile()) {
//									load(f, fPath);
//								} else {
//									activity.slideFrag2.mViewPager.setCurrentItem(Frag.TYPE.EXPLORER.ordinal(), true);
//									activity.curExploreFrag.changeDir(f, true);
//								}
//								if (selectedInList1.size() > 0) {
//									if (selectedInList1.contains(f)) {
//										selectedInList1.remove(f);
//										if (selectedInList1.size() == 0 && activity.copyl.size() == 0 && activity.cutl.size() == 0 && commands.getVisibility() == View.VISIBLE) {
//											horizontalDivider.setVisibility(View.GONE);
//											commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//											commands.setVisibility(View.GONE);
//										}
//									} else {
//										selectedInList1.add(f);
//									}
//								}
//							} else if (v.getId() == R.id.cbx) {//file and folder
//								if (selectedInList1.contains(f)) {
//									selectedInList1.remove(f);
//									if (selectedInList1.size() == 0 && activity.copyl.size() == 0 && activity.cutl.size() == 0 && commands.getVisibility() == View.VISIBLE) {
//										horizontalDivider.setVisibility(View.GONE);
//										commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//										commands.setVisibility(View.GONE);
//									}
//								} else {
//									selectedInList1.add(f);
//									if (commands.getVisibility() == View.GONE) {
//										commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
//										commands.setVisibility(View.VISIBLE);
//										horizontalDivider.setVisibility(View.VISIBLE);
//									}
//								}
//							} else if (f.isDirectory()) { 
//								if (selectedInList1.size() == 0) { 
//									changeDir(f, true);
//								} else {
//									if (selectedInList1.contains(f)) {
//										selectedInList1.remove(f);
//										if (selectedInList1.size() == 0 && activity.copyl.size() == 0 && activity.cutl.size() == 0 && commands.getVisibility() == View.VISIBLE) {
//											horizontalDivider.setVisibility(View.GONE);
//											commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//											commands.setVisibility(View.GONE);
//										} 
//									} else {
//										selectedInList1.add(f);
//										if (commands.getVisibility() == View.GONE) {
//											commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
//											commands.setVisibility(View.VISIBLE);
//											horizontalDivider.setVisibility(View.VISIBLE);
//										}
//									}
//								}
//							} else if (f.isFile()) { 
//								if (selectedInList1.size() == 0) { 
//									openFile(f);
//								} else {
//									if (selectedInList1.contains(f)) {
//										selectedInList1.remove(f);
//										if (selectedInList1.size() == 0 && activity.copyl.size() == 0 && activity.cutl.size() == 0 && commands.getVisibility() == View.VISIBLE) {
//											horizontalDivider.setVisibility(View.GONE);
//											commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//											commands.setVisibility(View.GONE);
//										} 
//									} else {
//										selectedInList1.add(f);
//										if (commands.getVisibility() == View.GONE) {
//											commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
//											commands.setVisibility(View.VISIBLE);
//											horizontalDivider.setVisibility(View.VISIBLE);
//										}
//									}
//								}
//							}
//							if ((dir == null || dir.length() > 0)) {
//								selectionStatus1.setText(selectedInList1.size() 
//														 + "/" + dataSourceL1.size());
//							}
//						} else { //!multifile no preview
//							if (f.isFile()) {
//								// chọn mới đầu tiên
//								if (v.getId() == R.id.cbx) {
//									if (selectedInList1.size() == 0) {
//										selectedInList1.add(f);
//										if (commands.getVisibility() == View.GONE) {
//											commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
//											commands.setVisibility(View.VISIBLE);
//											horizontalDivider.setVisibility(View.VISIBLE);
//										}
//									} else if (selectedInList1.size() > 0) {
//										if (selectedInList1.contains(f)) { // đã chọn
//											selectedInList1.clear();
//											if (selectedInList1.size() == 0 && activity.copyl.size() == 0 && activity.cutl.size() == 0 && commands.getVisibility() == View.VISIBLE) {
//												horizontalDivider.setVisibility(View.GONE);
//												commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//												commands.setVisibility(View.GONE);
//											}
//										} else { // chọn mới bỏ cũ
//											selectedInList1.clear();
//											selectedInList1.add(f);
//										}
//									}
//								} else {
//									openFile(f);
//								}
//							} else { //", "Directory
//								selectedInList1.clear();
//								if (dir == null || dir.length() > 0) {
//									changeDir(f, true);
//								}
//							}
//						}
//						notifyDataSetChanged();
//					} else { // inselected
//						if (f.isFile()) {
//							if (v.getId() == R.id.icon) {
//								load(f, fPath);
//							} else {
//								openFile(f);
//							}
//						} else if (v.getId() == R.id.icon) { //dir
//							tempPreviewL2 = f;
//							activity.slideFrag2.mViewPager.setCurrentItem(Frag.TYPE.EXPLORER.ordinal(), true);
//							activity.curExploreFrag.changeDir(f, true);
//						} 
//					}
//				}
//			} else {
//				changeDir(f.getParentFile(), true);
//			}
//			updateDelPaste();
//		}
//		
//		private void load(final File f, final String fPath) throws IllegalStateException {
//			final String mime = MimeTypes.getMimeType(f);
//			Log.d(TAG, fPath + "=" + mime);
//			int i = 0;
//			SlidingTabsFragment2.PagerAdapter pagerAdapter = activity.slideFrag2.pagerAdapter;
//			if (mime.startsWith("text/html") || mime.startsWith("text/xhtml")) {
//				pagerAdapter.getItem(i = Frag.TYPE.TEXT.ordinal()).load(fPath);
//				pagerAdapter.getItem(i = Frag.TYPE.WEB.ordinal()).load(fPath);
//			} else if (mime.startsWith("application/vnd.android.package-archive")) {
//				final StringBuilder sb = new StringBuilder(ExplorerActivity.DOCTYPE);
//				try {
//					ApkParser apkParser = new ApkParser(f);
//					sb.append(AndroidUtils.getSignature(getContext(), fPath));
//					sb.append("\nVerify apk " + apkParser.verifyApk());
//					sb.append("\nMeta data " + apkParser.getApkMeta());
//					
//					String sb1 = sb.toString();
//					
//					String sb2 = "\nAndroidManifest.xml \n" + apkParser.getManifestXml().replaceAll("&", "&amp;")
//						.replaceAll("\"", "&quot;")
//						.replaceAll("'", "&#39;")
//						.replaceAll("<", "&lt;")
//						.replaceAll(">", "&gt;");
//					sb.append(sb2);
//					sb.append(ExplorerActivity.END_PRE);
//					final String name = ExplorerApplication.PRIVATE_PATH + "/" + f.getName() + ".html";
//					FileUtil.writeFileAsCharset(new File(name), sb.toString(), "utf-8");
//					pagerAdapter.getItem(i = Frag.TYPE.WEB.ordinal()).load(name);
//					byte[] bytes = FileUtil.readFileToMemory(f);
//					new FillClassesNamesThread(activity, bytes, f, sb1, sb2, ExplorerActivity.END_PRE).start();
//				} catch (Throwable e) {
//					e.printStackTrace();
//				}
//			} else if (mime.startsWith("application/pdf")) {
//				pagerAdapter.getItem(i = Frag.TYPE.PDF.ordinal()).load(fPath);
//			} else if (mime.startsWith("image/svg+xml")) {
//				pagerAdapter.getItem(i = Frag.TYPE.TEXT.ordinal()).load(fPath);
//				pagerAdapter.getItem(i = Frag.TYPE.PHOTO.ordinal()).load(fPath);
//			} else if (mime.startsWith("text")) {
//				pagerAdapter.getItem(i = Frag.TYPE.TEXT.ordinal()).load(fPath);
//			} else if (mime.startsWith("video")) {
//				pagerAdapter.getItem(i = Frag.TYPE.MEDIA.ordinal()).load(fPath);
//			} else if (mime.startsWith("image")) {
//				pagerAdapter.getItem(i = Frag.TYPE.PHOTO.ordinal()).load(fPath);
//			} else if (mime.startsWith("audio")) {
//				pagerAdapter.getItem(i = Frag.TYPE.MEDIA.ordinal()).load(fPath);
//			} else {
//				tempPreviewL2 = null;
//			}
//			activity.slideFrag2.mViewPager.setCurrentItem(i, true);
//		}
//
//		private void openFile(final File f) {
//			try {
//				final Uri uri = Uri.fromFile(f);
//				final Intent i = new Intent(Intent.ACTION_VIEW); 
//				i.addCategory(Intent.CATEGORY_DEFAULT);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//				i.setData(uri);
//				Log.d("i.setData(uri)", uri + "." + i);
//				final String mimeType = MimeTypes.getMimeType(f);
//				i.setDataAndType(uri, mimeType);//floor.getValue()
//				Log.d(TAG, f + "=" + mimeType);
//				final Intent createChooser = Intent.createChooser(i, "View");
//				Log.i("createChooser.getExtras()", AndroidUtils.bundleToString(createChooser.getExtras()));
//				startActivity(createChooser);
//			} catch (Throwable e) {
//				Toast.makeText(getContext(), "unable to view !\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
//			}
//		}
//
//		public boolean onLongClick(final View v) {
//			activity.slideFrag2.getCurrentFragment2().select(false);
//			final String fPath = (String) v.getContentDescription();
//			final File f = new File(fPath);
//
//			if (!f.exists()) {
//				changeDir(f, true);
//				return true;
//			} else if (!f.canRead()) {
//				showToast(f + " cannot be read");
//				return true;
//			}
//			Log.d(TAG, "onLongClick, " + fPath);
//			Log.d(TAG, "currentSelectedList" + Util.collectionToString(selectedInList1, true, "\r\n"));
//			Log.d(TAG, "selectedInList.contains(f) " + selectedInList1.contains(f));
//			Log.d(TAG, "multiFiles " + multiFiles);
//
//			boolean inSelectedFiles = false;
//			if (dataSourceL2 != null)
//				for (File st : dataSourceL2) {
//					if (f.equals(st) || fPath.startsWith(st.getAbsolutePath() + "/")) {
//						inSelectedFiles = true;
//						break;
//					}
//				}
//			if (!inSelectedFiles) {
//				if (multiFiles || suffix.length() == 0) {
//					if (selectedInList1.contains(f)) {
//						selectedInList1.remove(f);
//						if (selectedInList1.size() == 0 && activity.copyl.size() == 0 && activity.cutl.size() == 0 && commands.getVisibility() == View.VISIBLE) {
//							horizontalDivider.setVisibility(View.GONE);
//							commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//							commands.setVisibility(View.GONE);
//						} 
//					} else {
//						selectedInList1.add(f);
//						if (commands.getVisibility() == View.GONE) {
//							commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
//							commands.setVisibility(View.VISIBLE);
//							horizontalDivider.setVisibility(View.VISIBLE);
//						}
//					}
//					if ((dir == null || dir.length() > 0)) {
//						selectionStatus1.setText(selectedInList1.size() 
//												 + "/" + dataSourceL1.size());
//					}
//				} else { // single file
//					if (f.isFile()) {
//						// chọn mới đầu tiên
//						if (selectedInList1.size() == 0) {
//							selectedInList1.add(f);
//							if (commands.getVisibility() == View.GONE) {
//								commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
//								commands.setVisibility(View.VISIBLE);
//								horizontalDivider.setVisibility(View.VISIBLE);
//							}
//						} else if (selectedInList1.size() > 0) {
//							if (selectedInList1.contains(f)) {
//								// đã chọn
//								selectedInList1.clear();
//								if (selectedInList1.size() == 0 && activity.copyl.size() == 0 && activity.cutl.size() == 0 && commands.getVisibility() == View.VISIBLE) {
//									horizontalDivider.setVisibility(View.GONE);
//									commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
//									commands.setVisibility(View.GONE);
//								} 
//							} else {
//								// chọn mới bỏ cũ
//								selectedInList1.clear();
//								selectedInList1.add(f);
//							}
//						}
//					} else { //", "Directory
//						selectedInList1.clear();
//						if (dir == null || dir.length() > 0) {
//							changeDir(f, true);
//						}
//					}
//				}
//				notifyDataSetChanged();
//			} 
//			updateDelPaste();
//			return true;
//		}
//		
//
//		/**
//		 * Queries database to map path and password.
//		 * Starts the encryption process after database query
//		 * @param path the path of file to encrypt
//		 * @param password the password in plaintext
//		 */
//		private void startEncryption(final String path, final String password, Intent intent) throws Exception {
//
//			CryptHandler cryptHandler = new CryptHandler(activity);
//			EncryptedEntry encryptedEntry = new EncryptedEntry(path.concat(CryptUtil.CRYPT_EXTENSION),
//															   password);
//			cryptHandler.addEntry(encryptedEntry);
//
//			// start the encryption process
//			ServiceWatcherUtil.runService(activity, intent);
//		}
//
//		public interface EncryptButtonCallbackInterface {
//
//			/**
//			 * Callback fired when we've just gone through warning dialog before encryption
//			 * @param intent
//			 * @throws Exception
//			 */
//			void onButtonPressed(Intent intent) throws Exception;
//
//			/**
//			 * Callback fired when user has entered a password for encryption
//			 * Not called when we've a master password set or enable fingerprint authentication
//			 * @param intent
//			 * @param password the password entered by user
//			 * @throws Exception
//			 */
//			void onButtonPressed(Intent intent, String password) throws Exception;
//		}
//
//		public interface DecryptButtonCallbackInterface {
//			/**
//			 * Callback fired when we've confirmed the password matches the database
//			 * @param intent
//			 */
//			void confirm(Intent intent);
//
//			/**
//			 * Callback fired when password doesn't match the value entered by user
//			 */
//			void failed();
//		}
//
//		
//	}
}
