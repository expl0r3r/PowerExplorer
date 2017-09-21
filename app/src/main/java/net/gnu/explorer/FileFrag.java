package net.gnu.explorer;

import java.util.ArrayList;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbException;
import com.amaze.filemanager.ui.LayoutElements;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.Formatter;
import com.amaze.filemanager.filesystem.BaseFile;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.support.v7.widget.RecyclerView;

import com.amaze.filemanager.utils.OpenMode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import com.amaze.filemanager.utils.DataUtils;
import com.amaze.filemanager.database.CloudHandler;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import android.support.v7.view.ActionMode;
import com.amaze.filemanager.utils.OTGUtil;
import android.os.Build;
import com.amaze.filemanager.utils.ServiceWatcherUtil;
import android.widget.Toast;
import com.amaze.filemanager.database.EncryptedEntry;
import android.content.Intent;
import com.amaze.filemanager.utils.provider.UtilitiesProviderInterface;
import com.amaze.filemanager.services.EncryptService;
import android.preference.PreferenceManager;
import android.content.Context;
import com.amaze.filemanager.database.CryptHandler;
import android.graphics.drawable.BitmapDrawable;
import android.content.res.Resources;
import android.graphics.Color;
import com.amaze.filemanager.utils.color.ColorUsage;
import android.content.ClipData;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.MenuInflater;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import com.amaze.filemanager.fragments.preference_fragments.Preffrag;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;
import com.amaze.filemanager.utils.MainActivityHelper;
import com.amaze.filemanager.utils.CloudUtil;
import com.amaze.filemanager.fragments.SearchAsyncHelper;
import com.amaze.filemanager.utils.CryptUtil;
import java.net.MalformedURLException;
import com.amaze.filemanager.filesystem.RootHelper;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import com.amaze.filemanager.ui.icons.MimeTypes;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.amaze.filemanager.filesystem.HFile;
import android.os.AsyncTask;
import com.amaze.filemanager.activities.BaseActivity;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.filemanager.utils.SmbStreamer.Streamer;
import com.amaze.filemanager.utils.Futils;
import com.amaze.filemanager.filesystem.MediaStoreHack;
import android.util.Log;
import android.media.RingtoneManager;
import com.amaze.filemanager.ui.icons.Icons;
import android.widget.ImageButton;
import net.gnu.androidutil.ImageThreadLoader;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import java.util.LinkedList;
import java.util.Map;
import android.os.FileObserver;
import java.util.TreeMap;
import android.support.v7.widget.GridLayoutManager;
import net.gnu.util.Util;
import android.view.Gravity;
import net.gnu.androidutil.AndroidUtils;
import android.view.View.OnLongClickListener;
import android.graphics.Typeface;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.widget.HorizontalScrollView;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import com.cloudrail.si.interfaces.CloudStorage;
import com.amaze.filemanager.exceptions.CloudPluginException;
import com.amaze.filemanager.filesystem.RootHelper;
import com.amaze.filemanager.exceptions.RootNotPermittedException;
import com.amaze.filemanager.fragments.CloudSheetFragment;
import android.provider.MediaStore;
import android.database.Cursor;
import java.util.Arrays;
import net.gnu.util.FileUtil;
import java.util.Collections;
import com.amaze.filemanager.utils.HistoryManager;
import java.util.Comparator;
import java.util.Calendar;
import java.util.Date;
import android.widget.Button;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import com.amaze.filemanager.utils.color.ColorPreference;

public abstract class FileFrag extends Frag {

	private static final String TAG = "FileFrag";

	public boolean GO_BACK_ITEM, SHOW_THUMBS, COLORISE_ICONS, SHOW_DIVIDERS;
	public boolean selection, results = false, SHOW_HIDDEN, CIRCULAR_IMAGES, SHOW_PERMISSIONS, SHOW_SIZE, SHOW_LAST_MODIFIED;

	public SwipeRefreshLayout mSwipeRefreshLayout;
    public int file_count, folder_count, columns;
	public int sortby, dsort, asc;
    public String smbPath;
	private DisplayMetrics displayMetrics;
	//public ArrayList<LayoutElements> LIST_ELEMENTS;
//    public com.amaze.filemanager.adapters.RecyclerAdapter adapter;
	ArrAdapter adapter;
    public ActionMode mActionMode;
    //public SharedPreferences sharedPref;
	public RecyclerView listView;
	//public com.amaze.filemanager.adapters.RecyclerAdapter adapter;
	//LoadList loadList;
	private View nofilesview;
//	public OpenMode openMode = OpenMode.FILE;
	public BitmapDrawable folder, apk, DARK_IMAGE, DARK_VIDEO;
	public Resources res;
	public boolean IS_LIST = true;

	public String iconskin;
    public float[] color;
    public int skin_color;
    public int skinTwoColor;
    public int icon_skin_color;
	private boolean addheader = false;
	private boolean stopAnims = true;
	public String home;//, CURRENT_PATH = "";//
	private boolean mRetainSearchTask = false;

	//from ContentFragment
	int spanCount = 1;
	ImageButton allCbx;
	ImageButton icons;
	TextView allName;
	TextView allDate;
	TextView allSize;
	TextView allType;
	TextView selectionStatus1;
	protected View horizontalDivider0;
	protected View horizontalDivider12;
	protected View horizontalDivider7;

	ImageButton searchButton;
	ImageButton clearButton;
	EditText quicksearch;
	ViewFlipper topflipper;
	boolean searchMode = false;
	String searchVal = "";
	LinearLayout quickLayout;

	View nofilelayout;
	ImageView noFileImage;
	TextView noFileText;

	RecyclerView listView1 = null;
	ArrAdapter srcAdapter;
	ImageThreadLoader imageLoader;

//	SearchFileNameTask searchTask = null;
	String dirTemp4Search = "";
	FileListSorter fileListSorter;
	LinkedList<Map<String, Object>> backStack = new LinkedList<>();
	LinkedList<String> history = new LinkedList<>();
	FileObserver mFileObserver;
	GridLayoutManager gridLayoutManager;
	TextView diskStatus;
	HorizontalScrollView scrolltext;
	LinearLayout mDirectoryButtons;
//	GridDividerItemDecoration dividerItemDecoration;
	LoadFiles loadList = new LoadFiles();
	private OpenMode openmode;
//	ViewGroup commands;
//	View horizontalDivider;
//	Button deletePastes;
	Drawable drawableDelete;
	Drawable drawablePaste;
	long lastScroll = System.currentTimeMillis();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Log.d(TAG, "onCreate " + savedInstanceState);
		super.onCreate(savedInstanceState);
		res = getResources();
		folder = new BitmapDrawable(res, BitmapFactory.decodeResource(res, R.drawable.ic_grid_folder_new));
		ColorPreference colorPreference = activity.getColorPreference();
		fabSkin = colorPreference.getColorAsString(ColorUsage.ACCENT);
        iconskin = colorPreference.getColorAsString(ColorUsage.ICON_SKIN);
        skin_color = colorPreference.getColor(ColorUsage.PRIMARY);
        skinTwoColor = colorPreference.getColor(ColorUsage.PRIMARY_TWO);
        icon_skin_color = Color.parseColor(iconskin);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Log.d(TAG, "onCreateView " + savedInstanceState);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    @Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		//Log.d(TAG, "onViewCreated " + savedInstanceState);
		super.onViewCreated(v, savedInstanceState);
		nofilelayout = v.findViewById(R.id.nofilelayout);
		noFileImage = (ImageView)v.findViewById(R.id.image);
		noFileText = (TextView)v.findViewById(R.id.nofiletext);

		allCbx = (ImageButton) v.findViewById(R.id.allCbx);
		icons = (ImageButton) v.findViewById(R.id.icons);
		allName = (TextView) v.findViewById(R.id.allName);
		allDate = (TextView) v.findViewById(R.id.allDate);
		allSize = (TextView) v.findViewById(R.id.allSize);
		allType = (TextView) v.findViewById(R.id.allType);
		selectionStatus1 = (TextView) v.findViewById(R.id.selectionStatus);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
		horizontalDivider0 = v.findViewById(R.id.horizontalDivider0);
		horizontalDivider12 = v.findViewById(R.id.horizontalDivider12);
		horizontalDivider7 = v.findViewById(R.id.horizontalDivider7);
		diskStatus = (TextView) v.findViewById(R.id.diskStatus);
		scrolltext = (HorizontalScrollView) v.findViewById(R.id.scroll_text);
		mDirectoryButtons = (LinearLayout) v.findViewById(R.id.directory_buttons);

		clearButton = (ImageButton) v.findViewById(R.id.clear);
		searchButton = (ImageButton) v.findViewById(R.id.search);
		quicksearch = (EditText) v.findViewById(R.id.search_box);
		topflipper = (ViewFlipper) v.findViewById(R.id.flipper_top);
		quickLayout = (LinearLayout) v.findViewById(R.id.quicksearch);
		SHOW_HIDDEN = sharedPref.getBoolean("showHidden", true);
	}

	//public void updateList() {}
	public void changeDir(final String curDir, final boolean doScroll) {}
	void updateDelPaste() {
		if (selectedInList1.size() > 0 && deletePastesBtn.getCompoundDrawables()[1] != drawableDelete) {
			deletePastesBtn.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
			deletePastesBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableDelete, null, null);
			deletePastesBtn.setText("Delete");
		} else if (selectedInList1.size() == 0 && deletePastesBtn.getCompoundDrawables()[1] != drawablePaste) {
			deletePastesBtn.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
			deletePastesBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawablePaste, null, null);
			deletePastesBtn.setText("Paste");
		}
		deletePastesBtn.getCompoundDrawables()[1].setColorFilter(ExplorerActivity.TEXT_COLOR, PorterDuff.Mode.SRC_IN);
	}

	@Override
    public void onResume() {
        Log.d(TAG, "onResume " + title);
		imageLoader = new ImageThreadLoader(activity);
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart " + title);
		imageLoader = new ImageThreadLoader(activity);
		super.onStart();
	}
//	public Map<String, Object> onSaveInstanceState() {
//		Map<String, Object> outState = new TreeMap<>();
//		Log.d(TAG, "onSaveInstanceState " + path + ", " + outState);
//		outState.put("path", path);
//
//		outState.put("selectedInList1", selectedInList1);
//		outState.put("dataSourceL1", dataSourceL1);
//		outState.put("searchMode", searchMode);
//		outState.put("searchVal", quicksearch.getText().toString());
//		outState.put("dirTemp4Search", dirTemp4Search);
//
//		outState.put("allCbx.isEnabled", allCbx.isEnabled());
//		outState.put(ExplorerActivity.EXTRA_SUFFIX, suffix);
//		outState.put(ExplorerActivity.EXTRA_MULTI_SELECT, multiFiles);
//
//		int index = gridLayoutManager.findFirstVisibleItemPosition();
//        final View vi = listView1.getChildAt(0); 
//        final int top = (vi == null) ? 0 : vi.getTop();
//		outState.put("index", index);
//		outState.put("top", top);
//		return outState;
//	}
	
//	void reload(Map<String, Object> savedInstanceState) {
//		Log.d(TAG, "reload " + savedInstanceState + path);
//		path = (String) savedInstanceState.get(ExplorerActivity.EXTRA_DIR_PATH);
//		suffix = (String) savedInstanceState.get(ExplorerActivity.EXTRA_SUFFIX);
//		multiFiles = savedInstanceState.get(ExplorerActivity.EXTRA_MULTI_SELECT);
//		selectedInList1.clear();
//		selectedInList1.addAll((ArrayList<LayoutElements>) savedInstanceState.get("selectedInList1"));
//		dataSourceL1.clear();
//		dataSourceL1.addAll((ArrayList<LayoutElements>) savedInstanceState.get("dataSourceL1"));
//
//		searchMode = savedInstanceState.get("searchMode");
//		searchVal = (String) savedInstanceState.get("searchVal");
//		dirTemp4Search = (String) savedInstanceState.get("dirTemp4Search");
//		//listView1.setSelectionFromTop(savedInstanceState.getInt("index"),
//		//savedInstanceState.getInt("top"));
//		allCbx.setEnabled(savedInstanceState.get("allCbx.isEnabled"));
//		srcAdapter.notifyDataSetChanged();
//
//		setRecyclerViewLayoutManager();
//		gridLayoutManager.scrollToPositionWithOffset(savedInstanceState.get("index"), savedInstanceState.get("top"));
//
//		updateDir(path, this);
//	}
	
//	void setRecyclerViewLayoutManager() {
//        Log.d(TAG, "setRecyclerViewLayoutManager " + gridLayoutManager);
//		if (listView1 == null) {
//			return;
//		}
//		int scrollPosition = 0, top = 0;
//        // If a layout manager has already been set, get current scroll position.
//        if (gridLayoutManager != null) {
//			scrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
//			final View vi = listView1.getChildAt(0); 
//			top = (vi == null) ? 0 : vi.getTop();
//		}
//		final Context context = getContext();
//		gridLayoutManager = new GridLayoutManager(context, spanCount);
//		listView1.removeItemDecoration(dividerItemDecoration);
//		if (spanCount <= 2) {
//			dividerItemDecoration = new GridDividerItemDecoration(context, true);
//			//dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST, true, true);
//			listView1.addItemDecoration(dividerItemDecoration);
//		}
//
//		srcAdapter = new ArrAdapter(this, dataSourceL1, activity.leftCommands, activity.horizontalDivider11);
//		listView1.setAdapter(srcAdapter);
//
//		listView1.setLayoutManager(gridLayoutManager);
//		gridLayoutManager.scrollToPositionWithOffset(scrollPosition, top);
//	}
	
    /**
     * Sets up a FileObserver to watch the current directory.
     */
	FileObserver createFileObserver(String path) {
        return new FileObserver(path, FileObserver.CREATE | FileObserver.DELETE
								| FileObserver.MOVED_FROM | FileObserver.MOVED_TO
								| FileObserver.DELETE_SELF | FileObserver.MOVE_SELF
								| FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, String path) {
                if (path != null) {
                    Util.debug(TAG, "FileObserver received event %d, CREATE = 256;DELETE = 512;DELETE_SELF = 1024;MODIFY = 2;MOVED_FROM = 64;MOVED_TO = 128; path %s", event, path);
					activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								refreshDirectory();
							}
						});
                }
            }
        };
    }

	public void updateDir(String d, FileFrag cf) {//ExploreFragment
		Log.d(TAG, "updateDir " + d);
		setDirectoryButtons();
		if (cf == activity.slideFrag.getCurrentFragment()) {
			activity.dir = d;
			activity.curContentFrag = (ContentFragment) cf;
			activity.slideFrag.notifyTitleChange();
		} else if (cf == activity.slideFrag2.getCurrentFragment2()) {
			title = new File(d).getName();
			activity.curExploreFrag = (ContentFragment) cf;
			activity.slideFrag2.notifyTitleChange();
		}
	}

	public void refreshDirectory() {
		Log.d(TAG, "refreshDirectory " + CURRENT_PATH + ", " + this);
		if (CURRENT_PATH != null) {
			changeDir(CURRENT_PATH, false);
		} else {
			updateDir(dirTemp4Search, this);
		}
    }

	void setDirectoryButtons() {
		Log.d(TAG, "setDirectoryButtons " + CURRENT_PATH);
		//topflipper.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_top));

		if (CURRENT_PATH != null) {
			mDirectoryButtons.removeAllViews();
			String[] parts = CURRENT_PATH.split("/");

			activity = (ExplorerActivity) getActivity();
			final TextView ib = new TextView(activity);
			final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = Gravity.CENTER;
			ib.setLayoutParams(layoutParams);
			ib.setBackgroundResource(R.drawable.ripple);
			ib.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			ib.setText("/");
			ib.setTag("/");
			ib.setMinEms(2);
			ib.setPadding(0, 4, 0, 4);
			ib.setTextColor(ExplorerActivity.TEXT_COLOR);
			// ib.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			ib.setGravity(Gravity.CENTER);
			ib.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						changeDir("/", true);
						
					}
				});
			mDirectoryButtons.addView(ib);

			String folder = "";
			View v;
			TextView b = null;
			for (int i = 1; i < parts.length; i++) {
				folder += "/" + parts[i];
				v = activity.getLayoutInflater().inflate(R.layout.dir, null);
				b = (TextView) v.findViewById(R.id.name);
				b.setText(parts[i]);
				b.setTag(folder);
				b.setTextColor(ExplorerActivity.TEXT_COLOR);
				b.setOnClickListener(new View.OnClickListener() {
						public void onClick(View view) {
							String dir2 = (String) view.getTag();
							changeDir(dir2, true);
							
						}
					});
				mDirectoryButtons.addView(v);
				scrolltext.postDelayed(new Runnable() {
						public void run() {
							scrolltext.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
						}
					}, 100L);
			}
			AndroidUtils.setOnTouchListener(mDirectoryButtons, this);
			if (b != null) {
				b.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View p1) {
							final EditText editText = new EditText(activity);
							final CharSequence clipboardData = AndroidUtils.getClipboardData(activity);
							if (clipboardData.length() > 0 && clipboardData.charAt(0) == '/') {
								editText.setText(clipboardData);
							} else {
								editText.setText(CURRENT_PATH);
							}
							final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
							layoutParams.gravity = Gravity.CENTER;
							editText.setLayoutParams(layoutParams);
							editText.setSingleLine(true);
							editText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
							editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
							editText.setMinEms(2);
							//editText.setGravity(Gravity.CENTER);
							final int density = 8 * (int)getResources().getDisplayMetrics().density;
							editText.setPadding(density, density, density, density);

							AlertDialog dialog = new AlertDialog.Builder(activity)
								.setIconAttribute(android.R.attr.dialogIcon)
								.setTitle("Go to...")
								.setView(editText)
								.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										String name = editText.getText().toString();
										Log.d(TAG, "new " + name);
										File newF = new File(name);
										if (newF.exists()) {
											if (newF.isDirectory()) {
												CURRENT_PATH = name;
												changeDir(CURRENT_PATH, true);
											} else {
												CURRENT_PATH = newF.getParent();
												changeDir(newF.getParentFile().getAbsolutePath(), true);
											}
											dialog.dismiss();
										} else {
											showToast("\"" + newF + "\" does not exist. Please choose another name");
										}
									}
								})
								.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										dialog.dismiss();
									}
								}).create();
							dialog.show();
							return true;
						}
					});
			}
		}
	}

//	void setSearchMode(final boolean search) {
//		Log.d(TAG, "setSearchMode " + searchMode + ", " + path + ", " + dirTemp4Search);
//		if (search) {
//			if (path != null) {
//				dirTemp4Search = path;
//			}
//			path = null;
//			//pagerItem.searchMode = true;
//			searchMode = true;
//		} else {
//			path = dirTemp4Search;
//			//pagerItem.searchMode = false;
//			searchMode = false;
//		}
//		Log.d(TAG, "setSearchMode " + searchMode + ", " + path + ", " + dirTemp4Search);
//	}

	void notifyDataSetChanged() {
		srcAdapter.notifyDataSetChanged();
	}

	public ArrayList<LayoutElements> addToSmb(SmbFile[] mFile, String path) throws SmbException {
        ArrayList<LayoutElements> ret = new ArrayList<>();
        //if (searchHelper.size() > 500) searchHelper.clear();
        for (int i = 0; i < mFile.length; i++) {
            if (DataUtils.hiddenfiles.contains(mFile[i].getPath()))
                continue;
            String name = mFile[i].getName();
            name = (mFile[i].isDirectory() && name.endsWith("/")) ? name.substring(0, name.length() - 1) : name;
            if (path.equals(smbPath)) {
                if (name.endsWith("$")) continue;
            }
            if (mFile[i].isDirectory()) {
                folder_count++;
                LayoutElements layoutElements = new LayoutElements(name, mFile[i].getPath(),
																   "", "", /*"", */mFile[i].length(), /*false, */mFile[i].lastModified(), true);
                layoutElements.setMode(OpenMode.SMB);
                //searchHelper.add(layoutElements.generateBaseFile());
                ret.add(layoutElements);
            } else {
                file_count++;
                try {
                    LayoutElements layoutElements = new LayoutElements(
						//Icons.loadMimeIcon(mFile[i].getPath(), !IS_LIST, res), 
						name,
						mFile[i].getPath(), "", "", //Formatter.formatFileSize(getContext(), mFile[i].length()), 
						mFile[i].length(), //false,
						mFile[i].lastModified(), false);
                    layoutElements.setMode(OpenMode.SMB);
                    //searchHelper.add(layoutElements.generateBaseFile());
                    ret.add(layoutElements);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    public void reauthenticateSmb() {
        if (smbPath != null) {
            try {
                activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							int i=-1;
							if ((i = DataUtils.containsServer(smbPath)) != -1) {
								activity.showSMBDialog(DataUtils.getServers().get(i)[0], smbPath, true);
							}
						}
					});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	public boolean checkforpath(String path) {
        boolean grid = false, both_contain = false;
        int index1 = -1, index2 = -1;
        for (String s : DataUtils.gridfiles) {
            index1++;
            if ((path).contains(s)) {
                grid = true;
                break;
            }
        }
        for (String s : DataUtils.listfiles) {
            index2++;
            if ((path).contains(s)) {
                if (grid) both_contain = true;
                grid = false;
                break;
            }
        }
        if (!both_contain) return grid;
        String path1 = DataUtils.gridfiles.get(index1), path2 = DataUtils.listfiles.get(index2);
        if (path1.contains(path2))
            return true;
        else if (path2.contains(path1))
            return false;
        else
            return grid;
    }

    /**
     * Loading adapter after getting a list of elements
     * @param bitmap the list of objects for the adapter
     * @param back
     * @param path the path for the adapter
     * @param openMode the type of file being created
     * @param results is the list of elements a result from search
     * @param grid whether to set grid view or list view
     */
//    public void createViews(ArrayList<LayoutElements> bitmap, boolean back, String path, OpenMode
//							openMode, boolean results, boolean grid) {
//        try {
//            if (bitmap != null) {
//                if (GO_BACK_ITEM)
//                    if (!path.equals("/") && (openMode == OpenMode.FILE || openMode == OpenMode.ROOT)
//						&& !path.equals(OTGUtil.PREFIX_OTG + "/")
//						&& !path.equals(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/")
//						&& !path.equals(CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/")
//						&& !path.equals(CloudHandler.CLOUD_PREFIX_BOX + "/")
//						&& !path.equals(CloudHandler.CLOUD_PREFIX_DROPBOX + "/")) {
////                        if (bitmap.size() == 0 || !bitmap.get(0).getSize().equals(goback)) {
////
////                            Bitmap iconBitmap = BitmapFactory.decodeResource(res, R.drawable.ic_arrow_left_white_24dp);
////                            bitmap.add(0,
////									   activity.getFutils().newElement(new BitmapDrawable(res, iconBitmap),
////																	   "..", "", "", goback, 0, false, true, ""));
////                        }
//                    }
//
//                if (bitmap.size() == 0 && !results) {
//                    nofilesview.setVisibility(View.VISIBLE);
//                    listView.setVisibility(View.GONE);
//                    mSwipeRefreshLayout.setEnabled(false);
//                } else {
//                    mSwipeRefreshLayout.setEnabled(true);
//                    nofilesview.setVisibility(View.GONE);
//                    listView.setVisibility(View.VISIBLE);
//
//                }
//                dataSourceL1 = bitmap;
//                if (grid && IS_LIST)
//                    switchToGrid();
//                else if (!grid && !IS_LIST) 
//					switchToList();
//                if (adapter == null)
//					adapter = new ArrAdapter(this, activity, bitmap, activity);
//                else {
//                    adapter.generate(dataSourceL1);
//                }
//                stopAnims = true;
//                this.openMode = openMode;
//                if (openMode != OpenMode.CUSTOM)
//                    DataUtils.addHistoryFile(path);
//                //mSwipeRefreshLayout.setRefreshing(false);
//                try {
//                    listView.setAdapter(adapter);
////                    if (!addheader) {
////                        listView.removeItemDecoration(headersDecor);
////                        listView.removeItemDecoration(dividerItemDecoration);
////                        addheader = true;
////                    }
////                    if (addheader && IS_LIST) {
////                        dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, true, SHOW_DIVIDERS);
////                        listView.addItemDecoration(dividerItemDecoration);
////                        headersDecor = new StickyRecyclerHeadersDecoration(adapter);
////                        listView.addItemDecoration(headersDecor);
////                        addheader = false;
////                    }
//                    if (!results) this.results = false;
//                    this.path = path;
////                    if (back) {
////                        if (scrolls.containsKey(CURRENT_PATH)) {
////                            Bundle b = scrolls.get(CURRENT_PATH);
////                            if (IS_LIST)
////                                mLayoutManager.scrollToPositionWithOffset(b.getInt("index"), b.getInt("top"));
////                            else
////                                mLayoutManagerGrid.scrollToPositionWithOffset(b.getInt("index"), b.getInt("top"));
////                        }
////                    }
//                    //floatingActionButton.show();
////                    activity.updatePaths(no);
////                    listView.stopScroll();
////                    fastScroller.setRecyclerView(listView, IS_LIST ? 1 : columns);
////                    mToolbarContainer.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
////							@Override
////							public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
////								fastScroller.updateHandlePosition(verticalOffset, 112);
////								//    fastScroller.setPadding(fastScroller.getPaddingLeft(),fastScroller.getTop(),fastScroller.getPaddingRight(),112+verticalOffset);
////								//      fastScroller.updateHandlePosition();
////							}
////						});
////                    fastScroller.registerOnTouchListener(new FastScroller.onTouchListener() {
////							@Override
////							public void onTouch() {
////								if (stopAnims && adapter != null) {
////									stopAnimation();
////									stopAnims = false;
////								}
////							}
////						});
////                    if (buttons.getVisibility() == View.VISIBLE) activity.bbar(this);
//                    //activity.invalidateFab(openMode);
//                } catch (Exception e) {
//                }
//            } else {
//                // list loading cancelled
//                // TODO: Add support for cancelling list loading
//                loadlist(home, true, OpenMode.FILE);
//            }
//        } catch (Exception e) {
//        }
//    }

//    void switchToGrid() {
////        IS_LIST = false;
////
////        ic = new IconHolder(getActivity(), SHOW_THUMBS, !IS_LIST);
////        folder = new BitmapDrawable(res, mFolderBitmap);
////        fixIcons(true);
////
////        if (activity.getAppTheme().equals(AppTheme.LIGHT)) {
////
////            // will always be grid, set alternate white background
////            listView.setBackgroundColor(getResources().getColor(R.color.grid_background_light));
////        }
////
////        if (mLayoutManagerGrid == null)
////            if (columns == -1 || columns == 0)
////                mLayoutManagerGrid = new GridLayoutManager(getActivity(), 3);
////            else
////                mLayoutManagerGrid = new GridLayoutManager(getActivity(), columns);
////        listView.setLayoutManager(mLayoutManagerGrid);
////        adapter = null;
//    }

//    void switchToList() {
////        IS_LIST = true;
////
////        if (activity.getAppTheme().equals(AppTheme.LIGHT)) {
////
////            listView.setBackgroundDrawable(null);
////        }
////
////        ic = new IconHolder(getActivity(), SHOW_THUMBS, !IS_LIST);
////        folder = new BitmapDrawable(res, mFolderBitmap);
////        fixIcons(true);
////        if (mLayoutManager == null)
////            mLayoutManager = new LinearLayoutManager(getActivity());
////        listView.setLayoutManager(mLayoutManager);
////        adapter = null;
//    }

	public static void decryptFile(final FileFrag main, OpenMode openMode, BaseFile sourceFile,
                                   String decryptPath,
                                   UtilitiesProviderInterface activity) {

        Intent decryptIntent = new Intent(main.getContext(), EncryptService.class);
        decryptIntent.putExtra(EncryptService.TAG_OPEN_MODE, openMode.ordinal());
        decryptIntent.putExtra(EncryptService.TAG_CRYPT_MODE,
							   EncryptService.CryptEnum.DECRYPT.ordinal());
        decryptIntent.putExtra(EncryptService.TAG_SOURCE, sourceFile);
        decryptIntent.putExtra(EncryptService.TAG_DECRYPT_PATH, decryptPath);

        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(main.getContext());

        EncryptedEntry encryptedEntry;
        try {
            encryptedEntry = findEncryptedEntry(main.getContext(), sourceFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            encryptedEntry = null;
        }

        if (encryptedEntry == null) {

            // we couldn't find any entry in database or lost the key to decipher
            Toast.makeText(main.getContext(),
						   main.getActivity().getResources().getString(R.string.crypt_decryption_fail),
						   Toast.LENGTH_LONG).show();
            return;
        }

        ArrAdapter.DecryptButtonCallbackInterface decryptButtonCallbackInterface =
			new ArrAdapter.DecryptButtonCallbackInterface() {
			@Override
			public void confirm(Intent intent) {

				ServiceWatcherUtil.runService(main.getContext(), intent);
			}

			@Override
			public void failed() {
				Toast.makeText(main.getContext(), main.getActivity().getResources().getString(R.string.crypt_decryption_fail_password),
							   Toast.LENGTH_LONG).show();
			}
		};

        switch (encryptedEntry.getPassword()) {
            case Preffrag.ENCRYPT_PASSWORD_FINGERPRINT:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        activity.getFutils().showDecryptFingerprintDialog(decryptIntent,
																		  main, activity.getAppTheme(), decryptButtonCallbackInterface);
                    } else throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(main.getContext(),
								   main.getResources().getString(R.string.crypt_decryption_fail),
								   Toast.LENGTH_LONG).show();
                }
                break;
            case Preffrag.ENCRYPT_PASSWORD_MASTER:
                activity.getFutils().showDecryptDialog(decryptIntent,
													   main, activity.getAppTheme(),
													   preferences1.getString(Preffrag.PREFERENCE_CRYPT_MASTER_PASSWORD,
																			  Preffrag.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT),
													   decryptButtonCallbackInterface);
                break;
            default:
                activity.getFutils().showDecryptDialog(decryptIntent,
													   main, activity.getAppTheme(),
													   encryptedEntry.getPassword(),
													   decryptButtonCallbackInterface);
                break;
        }
    }

    /**
     * Queries database to find entry for the specific path
     * @param path  the path to match with
     * @return the entry
     */
    private static EncryptedEntry findEncryptedEntry(Context context, String path) throws Exception {

        CryptHandler handler = new CryptHandler(context);

        EncryptedEntry matchedEntry = null;
        // find closest path which matches with database entry
        for (EncryptedEntry encryptedEntry : handler.getAllEntries()) {
            if (path.contains(encryptedEntry.getPath())) {

                if (matchedEntry == null || (matchedEntry != null &&
					matchedEntry.getPath().length() < encryptedEntry.getPath().length())) {
                    matchedEntry = encryptedEntry;
                }
            }
        }
        return matchedEntry;
    }

//    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
//        private void hideOption(int id, Menu menu) {
//            MenuItem item = menu.findItem(id);
//            item.setVisible(false);
//        }
//
//        private void showOption(int id, Menu menu) {
//            MenuItem item = menu.findItem(id);
//            item.setVisible(true);
//        }
//
//        public void initMenu(Menu menu) {
//            /*menu.findItem(R.id.cpy).setIcon(icons.getCopyDrawable());
//			 menu.findItem(R.id.cut).setIcon(icons.getCutDrawable());
//			 menu.findItem(R.id.delete).setIcon(icons.getDeleteDrawable());
//			 menu.findItem(R.id.all).setIcon(icons.getAllDrawable());*/
//        }
//
//        // called when the action mode is created; startActionMode() was called
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            // Inflate a menu resource providing context menu items
//            MenuInflater inflater = mode.getMenuInflater();
//            actionModeView = getActivity().getLayoutInflater().inflate(R.layout.actionmode, null);
//            mode.setCustomView(actionModeView);
//
//            activity.setPagingEnabled(false);
//            activity.floatingActionButton.hideMenuButton(true);
//
//            // translates the drawable content down
//            // if (activity.isDrawerLocked) activity.translateDrawerList(true);
//
//            // assumes that you have "contexual.xml" menu resources
//            inflater.inflate(R.menu.contextual, menu);
//            initMenu(menu);
//            hideOption(R.id.addshortcut, menu);
//            hideOption(R.id.share, menu);
//            hideOption(R.id.openwith, menu);
//            if (activity.mReturnIntent)
//                showOption(R.id.openmulti, menu);
//            //hideOption(R.id.setringtone,menu);
//            mode.setTitle(getResources().getString(R.string.select));
//
//            activity.updateViews(new ColorDrawable(res.getColor(R.color.holo_dark_action_mode)));
//
//            // do not allow drawer to open when item gets selected
//            if (!activity.isDrawerLocked) {
//
//                activity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED,
//														 activity.mDrawerLinear);
//            }
//            return true;
//        }
//
//        // the following method is called each time
//        // the action mode is shown. Always called after
//        // onCreateActionMode, but
//        // may be called multiple times if the mode is invalidated.
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            ArrayList<Integer> positions = adapter.getCheckedItemPositions();
//            TextView textView1 = (TextView) actionModeView.findViewById(R.id.item_count);
//            textView1.setText(positions.size() + "");
//            textView1.setOnClickListener(null);
//            mode.setTitle(positions.size() + "");
//            hideOption(R.id.openmulti, menu);
//            if (openMode == OpenMode.SMB) {
//                hideOption(R.id.addshortcut, menu);
//                hideOption(R.id.openwith, menu);
//                hideOption(R.id.share, menu);
//                hideOption(R.id.compress, menu);
//                return true;
//            }
//            if (activity.mReturnIntent)
//                if (Build.VERSION.SDK_INT >= 16)
//                    showOption(R.id.openmulti, menu);
//            //tv.setText(positions.size());
//            if (!results) {
//                hideOption(R.id.openparent, menu);
//                if (positions.size() == 1) {
//                    showOption(R.id.addshortcut, menu);
//                    showOption(R.id.openwith, menu);
//                    showOption(R.id.share, menu);
//
//                    File x = new File(LIST_ELEMENTS.get(adapter.getCheckedItemPositions().get(0))
//
//									  .getPath());
//
//                    if (x.isDirectory()) {
//                        hideOption(R.id.openwith, menu);
//                        hideOption(R.id.share, menu);
//                        hideOption(R.id.openmulti, menu);
//                    }
//
//                    if (activity.mReturnIntent)
//                        if (Build.VERSION.SDK_INT >= 16)
//                            showOption(R.id.openmulti, menu);
//
//                } else {
//                    try {
//                        showOption(R.id.share, menu);
//                        if (activity.mReturnIntent)
//                            if (Build.VERSION.SDK_INT >= 16) showOption(R.id.openmulti, menu);
//                        for (int c : adapter.getCheckedItemPositions()) {
//                            File x = new File(LIST_ELEMENTS.get(c).getPath());
//                            if (x.isDirectory()) {
//                                hideOption(R.id.share, menu);
//                                hideOption(R.id.openmulti, menu);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    hideOption(R.id.openwith, menu);
//
//                }
//            } else {
//                if (positions.size() == 1) {
//                    showOption(R.id.addshortcut, menu);
//                    showOption(R.id.openparent, menu);
//                    showOption(R.id.openwith, menu);
//                    showOption(R.id.share, menu);
//
//                    File x = new File(LIST_ELEMENTS.get(adapter.getCheckedItemPositions().get(0))
//
//									  .getPath());
//
//                    if (x.isDirectory()) {
//                        hideOption(R.id.openwith, menu);
//                        hideOption(R.id.share, menu);
//                        hideOption(R.id.openmulti, menu);
//                    }
//                    if (activity.mReturnIntent)
//                        if (Build.VERSION.SDK_INT >= 16)
//                            showOption(R.id.openmulti, menu);
//
//                } else {
//                    hideOption(R.id.openparent, menu);
//
//                    if (activity.mReturnIntent)
//                        if (Build.VERSION.SDK_INT >= 16)
//                            showOption(R.id.openmulti, menu);
//                    try {
//                        for (int c : adapter.getCheckedItemPositions()) {
//                            File x = new File(LIST_ELEMENTS.get(c).getPath());
//                            if (x.isDirectory()) {
//                                hideOption(R.id.share, menu);
//                                hideOption(R.id.openmulti, menu);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    hideOption(R.id.openwith, menu);
//
//                }
//            }
//
//            return true; // Return false if nothing is done
//        }
//
//        // called when the user selects a contextual menu item
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            computeScroll();
//            ArrayList<Integer> plist = adapter.getCheckedItemPositions();
//            switch (item.getItemId()) {
//                case R.id.openmulti:
//                    if (Build.VERSION.SDK_INT >= 16) {
//                        Intent intentresult = new Intent();
//                        ArrayList<Uri> resulturis = new ArrayList<>();
//                        for (int k : plist) {
//                            try {
//                                resulturis.add(Uri.fromFile(new File(LIST_ELEMENTS.get(k).getPath())));
//                            } catch (Exception e) {
//
//                            }
//                        }
//                        final ClipData clipData = new ClipData(
//							null, new String[]{"*/*"}, new ClipData.Item(resulturis.get(0)));
//                        for (int i = 1; i < resulturis.size(); i++) {
//                            clipData.addItem(new ClipData.Item(resulturis.get(i)));
//                        }
//                        intentresult.setClipData(clipData);
//                        mode.finish();
//                        getActivity().setResult(getActivity().RESULT_OK, intentresult);
//                        getActivity().finish();
//                    }
//                    return true;
//                case R.id.about:
//                    LayoutElements x;
//                    x = LIST_ELEMENTS.get((plist.get(0)));
//                    activity.getFutils().showProps((x).generateBaseFile(), x.getPermissions(), ma, BaseActivity.rootMode, activity.getAppTheme());
//                    /*PropertiesSheet propertiesSheet = new PropertiesSheet();
//					 Bundle arguments = new Bundle();
//					 arguments.putParcelable(PropertiesSheet.KEY_FILE, x.generateBaseFile());
//					 arguments.putString(PropertiesSheet.KEY_PERMISSION, x.getPermissions());
//					 arguments.putBoolean(PropertiesSheet.KEY_ROOT, BaseActivity.rootMode);
//					 propertiesSheet.setArguments(arguments);
//					 propertiesSheet.show(getFragmentManager(), PropertiesSheet.TAG_FRAGMENT);*/
//                    mode.finish();
//                    return true;
//					/*case R.id.setringtone:
//					 File fx;
//					 if(results)
//					 fx=new File(slist.get((plist.get(0))).getPath());
//					 else
//					 fx=new File(list.get((plist.get(0))).getPath());
//
//					 ContentValues values = new ContentValues();
//					 values.put(MediaStore.MediaColumns.DATA, fx.getAbsolutePath());
//					 values.put(MediaStore.MediaColumns.TITLE, "Amaze");
//					 values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
//					 //values.put(MediaStore.MediaColumns.SIZE, fx.);
//					 values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
//					 values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//					 values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
//					 values.put(MediaStore.Audio.Media.IS_ALARM, false);
//					 values.put(MediaStore.Audio.Media.IS_MUSIC, false);
//
//					 Uri uri = MediaStore.Audio.Media.getContentUriForPath(fx.getAbsolutePath());
//					 Uri newUri = getActivity().getContentResolver().insert(uri, values);
//					 try {
//					 RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_RINGTONE, newUri);
//					 //Settings.System.putString(getActivity().getContentResolver(), Settings.System.RINGTONE, newUri.toString());
//					 Toast.makeText(getActivity(), "Successful" + fx.getAbsolutePath(), Toast.LENGTH_LONG).show();
//					 } catch (Throwable t) {
//
//					 Log.d("ringtone", "failed");
//					 }
//					 return true;*/
//                case R.id.delete:
//                    activity.getFutils().deleteFiles(LIST_ELEMENTS, ma, plist, activity.getAppTheme());
//                    return true;
//                case R.id.share:
//                    ArrayList<File> arrayList = new ArrayList<>();
//                    for (int i : plist) {
//                        arrayList.add(new File(LIST_ELEMENTS.get(i).getPath()));
//                    }
//                    if (arrayList.size() > 100)
//                        Toast.makeText(getActivity(), getResources().getString(R.string.share_limit),
//									   Toast.LENGTH_SHORT).show();
//                    else {
//
//                        switch (LIST_ELEMENTS.get(0).getMode()) {
//                            case DROPBOX:
//                            case BOX:
//                            case GDRIVE:
//                            case ONEDRIVE:
//                                activity.getFutils().shareCloudFile(LIST_ELEMENTS.get(0).getPath(),
//																	LIST_ELEMENTS.get(0).getMode(), getContext());
//                                break;
//                            default:
//                                activity.getFutils().shareFiles(arrayList, getActivity(), activity.getAppTheme(), Color.parseColor(fabSkin));
//                                break;
//                        }
//                    }
//                    return true;
//                case R.id.openparent:
//                    loadlist(new File(LIST_ELEMENTS.get(plist.get(0)).getPath()).getParent(), false, OpenMode.FILE);
//                    return true;
//                case R.id.all:
//                    if (adapter.areAllChecked(CURRENT_PATH)) {
//                        adapter.toggleChecked(false, CURRENT_PATH);
//                    } else {
//                        adapter.toggleChecked(true, CURRENT_PATH);
//                    }
//                    mode.invalidate();
//
//                    return true;
//                case R.id.rename:
//
//                    final ActionMode m = mode;
//                    final BaseFile f;
//                    f = (LIST_ELEMENTS.get(
//						(plist.get(0)))).generateBaseFile();
//                    rename(f);
//                    mode.finish();
//                    return true;
//                case R.id.hide:
//                    for (int i1 = 0; i1 < plist.size(); i1++) {
//                        hide(LIST_ELEMENTS.get(plist.get(i1)).getPath());
//                    }
//                    updateList();
//                    mode.finish();
//                    return true;
//                case R.id.ex:
//                    activity.mainActivityHelper.extractFile(new File(LIST_ELEMENTS.get(plist.get(0)).getPath()));
//                    mode.finish();
//                    return true;
//                case R.id.cpy:
//                    activity.MOVE_PATH = null;
//                    ArrayList<BaseFile> copies = new ArrayList<>();
//                    for (int i2 = 0; i2 < plist.size(); i2++) {
//                        copies.add(LIST_ELEMENTS.get(plist.get(i2)).generateBaseFile());
//                    }
//                    activity.COPY_PATH = copies;
//                    activity.supportInvalidateOptionsMenu();
//                    mode.finish();
//                    return true;
//                case R.id.cut:
//                    activity.COPY_PATH = null;
//                    ArrayList<BaseFile> copie = new ArrayList<>();
//                    for (int i3 = 0; i3 < plist.size(); i3++) {
//                        copie.add(LIST_ELEMENTS.get(plist.get(i3)).generateBaseFile());
//                    }
//                    activity.MOVE_PATH = copie;
//                    activity.supportInvalidateOptionsMenu();
//                    mode.finish();
//                    return true;
//                case R.id.compress:
//                    ArrayList<BaseFile> copies1 = new ArrayList<>();
//                    for (int i4 = 0; i4 < plist.size(); i4++) {
//                        copies1.add(LIST_ELEMENTS.get(plist.get(i4)).generateBaseFile());
//                    }
//                    activity.getFutils().showCompressDialog((MainActivity) getActivity(), copies1, CURRENT_PATH);
//                    mode.finish();
//                    return true;
//                case R.id.openwith:
//                    activity.getFutils().openunknown(new File(LIST_ELEMENTS.get((plist.get(0))).getPath()), getActivity(), true);
//                    return true;
//                case R.id.addshortcut:
//                    addShortcut(LIST_ELEMENTS.get(plist.get(0)));
//                    mode.finish();
//                    return true;
//                default:
//                    return false;
//            }
//        }
//
//        // called when the user exits the action mode
//        public void onDestroyActionMode(ActionMode mode) {
//            mActionMode = null;
//            selection = false;
//
//            // translates the drawer content up
//            //if (activity.isDrawerLocked) activity.translateDrawerList(false);
//
//            activity.floatingActionButton.showMenuButton(true);
//            if (!results) adapter.toggleChecked(false, CURRENT_PATH);
//            else adapter.toggleChecked(false);
//            activity.setPagingEnabled(true);
//
//            activity.updateViews(new ColorDrawable(MainActivity.currentTab == 1 ?
//												   skinTwoColor : skin_color));
//
//            if (!activity.isDrawerLocked) {
//                activity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
//														 activity.mDrawerLinear);
//            }
//        }
//    };

//	void cpy() {
//		//ArrayList<Integer> plist = adapter.getCheckedItemPositions();
//		activity.MOVE_PATH = null;
//		ArrayList<BaseFile> copies = new ArrayList<>();
//		for (LayoutElements le : selectedInList1) {//int i2 = 0; i2 < plist.size(); i2++
//			copies.add(le.generateBaseFile());//dataSourceL1.get(plist.get(i2))
//		}
//		activity.COPY_PATH = copies;
//	}

//	void cut() {
//		//ArrayList<Integer> plist = adapter.getCheckedItemPositions();
//		activity.COPY_PATH = null;
//		ArrayList<BaseFile> copie = new ArrayList<>();
//		for (LayoutElements le : selectedInList1) {//int i3 = 0; i3 < plist.size(); i3++
//			copie.add(le.generateBaseFile());//dataSourceL1.get(plist.get(i3))
//		}
//		activity.MOVE_PATH = copie;
//		//activity.supportInvalidateOptionsMenu();
//	}

//	void compress() {
//		//ArrayList<Integer> plist = adapter.getCheckedItemPositions();
//		ArrayList<BaseFile> copies1 = new ArrayList<>();
//		for (LayoutElements le : selectedInList1) {//int i4 = 0; i4 < plist.size(); i4++
//			copies1.add(le.generateBaseFile());//dataSourceL1.get(plist.get(i4)
//		}
//		activity.getFutils().showCompressDialog(activity, copies1, CURRENT_PATH);
//	}

	void openwith() {
		//ArrayList<Integer> plist = adapter.getCheckedItemPositions();
		activity.getFutils().openunknown(new File(selectedInList1.get(0).getPath()), getActivity(), true);//dataSourceL1.plist.get(
	}

	void all() {
		if (dataSourceL1.size() == selectedInList1.size()) {//}adapter.areAllChecked(path)) {
			adapter.toggleChecked(false);//, path);
		} else {
			adapter.toggleChecked(true);//, path);
		}
	}

	void ex() {
		//ArrayList<Integer> plist = adapter.getCheckedItemPositions();
		activity.mainActivityHelper.extractFile(new File(selectedInList1.get(0).getPath()));//plist.get(
	}

//	void share() {
//		ArrayList<File> arrayList = new ArrayList<>();
//		//ArrayList<Integer> plist = adapter.getCheckedItemPositions();
//		for (LayoutElements i : selectedInList1) {//plist
//			arrayList.add(new File(i.getPath()));//dataSourceL1.get(
//		}
//		if (selectedInList1.size() > 100)
//			Toast.makeText(getActivity(), getResources().getString(R.string.share_limit),
//						   Toast.LENGTH_SHORT).show();
//		else {
//
//			switch (dataSourceL1.get(0).getMode()) {
//				case DROPBOX:
//				case BOX:
//				case GDRIVE:
//				case ONEDRIVE:
//					activity.getFutils().shareCloudFile(dataSourceL1.get(0).getPath(),
//														dataSourceL1.get(0).getMode(), getContext());
//					break;
//				default:
//					activity.getFutils().shareFiles(arrayList, getActivity(), activity.getAppTheme(), Color.parseColor(fabSkin));
//					break;
//			}
//		}
//	}

//	void delete() {
//		//ArrayList<Integer> plist = adapter.getCheckedItemPositions();
//		activity.getFutils().deleteFiles(selectedInList1, this, /*plist, */activity.getAppTheme());
//	}
    /**
     * Show dialog to rename a file
     * @param f the file to rename
     */
//    public void rename(final BaseFile f) {
//        //ArrayList<Integer> plist = adapter.getCheckedItemPositions();
//		//final BaseFile f = (LIST_ELEMENTS.get((plist.get(0)))).generateBaseFile();
//		MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
//        String name = f.getName();
//        builder.input("", name, false, new MaterialDialog.InputCallback() {
//				@Override
//				public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
//
//				}
//			});
//        builder.theme(activity.getAppTheme().getMaterialDialogTheme());
//        builder.title(getResources().getString(R.string.rename));
//        builder.callback(new MaterialDialog.ButtonCallback() {
//				@Override
//				public void onPositive(MaterialDialog materialDialog) {
//					String name = materialDialog.getInputEditText().getText().toString();
//					if (f.isSmb())
//						if (f.isDirectory() && !name.endsWith("/"))
//							name = name + "/";
//
//					activity.mainActivityHelper.rename(openMode, f.getPath(),
//													   CURRENT_PATH + "/" + name, getActivity(), BaseActivity.rootMode);
//				}
//
//				@Override
//				public void onNegative(MaterialDialog materialDialog) {
//
//					materialDialog.cancel();
//				}
//			});
//        builder.positiveText(R.string.save);
//        builder.negativeText(R.string.cancel);
//        int color = Color.parseColor(fabSkin);
//        builder.positiveColor(color).negativeColor(color).widgetColor(color);
//        builder.build().show();
//    }

//    public void hide(String path) {
//
//        DataUtils.addHiddenFile(path);
//        if (new File(path).isDirectory()) {
//			File f1 = new File(path + "/" + ".nomedia");
//            if (!f1.exists()) {
//                try {
//                    activity.mainActivityHelper.mkFile(new HFile(OpenMode.FILE, f1.getPath()), this);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            Futils.scanFile(path, getActivity());
//        }
//
//    }

//    void addShortcut() {
//		//ArrayList<Integer> plist = adapter.getCheckedItemPositions();
//		LayoutElements path = selectedInList1.get(0);//dataSourceL1
//        //Adding shortcut for MainActivity
//        //on Home screen
//        Intent shortcutIntent = new Intent(getActivity().getApplicationContext(),
//										   ExplorerActivity.class);
//        shortcutIntent.putExtra(ExplorerActivity.EXTRA_DIR_PATH, path.getPath());
//        shortcutIntent.setAction(Intent.ACTION_MAIN);
//        shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        Intent addIntent = new Intent();
//        addIntent
//			.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, new File(path.getPath()).getName());
//
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
//						   Intent.ShortcutIconResource.fromContext(getActivity(),
//																   R.mipmap.ic_launcher));
//
//        addIntent
//			.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//        getActivity().sendBroadcast(addIntent);
//    }

    /**
     * method called when list item is clicked in the adapter
     * @param position the {@link int} position of the list item
     * @param imageView the check {@link RoundedImageView} that is to be animated
     */
    public void onListItemClicked(int position, ImageView imageView) {
        if (position >= dataSourceL1.size()) return;

//        if (results) {
//
//            // check to initialize search results
//            // if search task is been running, cancel it
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            SearchAsyncHelper fragment = (SearchAsyncHelper) fragmentManager
//				.findFragmentByTag(MainActivity.TAG_ASYNC_HELPER);
//            if (fragment != null) {
//
//                if (fragment.mSearchTask.getStatus() == AsyncTask.Status.RUNNING) {
//
//                    fragment.mSearchTask.cancel(true);
//                }
//                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//            }
//
//            mRetainSearchTask = true;
//            results = false;
//        } else {
//            mRetainSearchTask = false;
//            MainActivityHelper.SEARCH_TEXT = null;
//        }
//        if (selection) {
//            if (!LIST_ELEMENTS.get(position).getSize().equals(goback)) {
//                // the first {goback} item if back navigation is enabled
//                adapter.toggleChecked(position, imageView);
//            } else {
//                selection = false;
//                if (mActionMode != null)
//                    mActionMode.finish();
//                mActionMode = null;
//            }
//
//        } else {
//            if (!LIST_ELEMENTS.get(position).getSize().equals(goback)) {
//
//                // hiding search view if visible
//                if (ExplorerActivity.isSearchViewEnabled)   activity.hideSearchView();
//
//                String path;
//                LayoutElements l = LIST_ELEMENTS.get(position);
//                if (!l.hasSymlink()) {
//
//                    path = l.getPath();
//                } else {
//
//                    path = l.getSymlink();
//                }
//
//                // check if we're trying to click on encrypted file
//                if (!LIST_ELEMENTS.get(position).isDirectory() &&
//					LIST_ELEMENTS.get(position).getPath().endsWith(CryptUtil.CRYPT_EXTENSION)) {
//                    // decrypt the file
//                    activity.isEncryptOpen = true;
//
//                    activity.encryptBaseFile = new BaseFile(getActivity().getExternalCacheDir().getPath()
//															+ "/"
//															+ LIST_ELEMENTS.get(position).generateBaseFile().getName().replace(CryptUtil.CRYPT_EXTENSION, ""));
//
//                    decryptFile(this, openMode, LIST_ELEMENTS.get(position).generateBaseFile(),
//								getActivity().getExternalCacheDir().getPath(),
//								activity);
//                    return;
//                }
//
//                if (LIST_ELEMENTS.get(position).isDirectory()) {
//                    computeScroll();
//                    loadlist(path, false, openMode);
//                } else {
//                    if (l.getMode() == OpenMode.SMB) {
//                        try {
//                            SmbFile smbFile = new SmbFile(l.getPath());
//                            launchSMB(smbFile, l.length(), activity);
//                        } catch (MalformedURLException e) {
//                            e.printStackTrace();
//                        }
//                    } else if (l.getMode() == OpenMode.OTG) {
//
//                        activity.getFutils().openFile(RootHelper.getDocumentFile(l.getPath(), getContext(), false),
//													  (ExplorerActivity) getActivity());
//                    } else if (l.getMode() == OpenMode.DROPBOX
//							   || l.getMode() == OpenMode.BOX
//							   || l.getMode() == OpenMode.GDRIVE
//							   || l.getMode() == OpenMode.ONEDRIVE) {
//
//                        Toast.makeText(getContext(), getResources().getString(R.string.please_wait), Toast.LENGTH_LONG).show();
//                        CloudUtil.launchCloud(LIST_ELEMENTS.get(position).generateBaseFile(), openMode, activity);
//                    } else if (activity.mReturnIntent) {
//                        returnIntentResults(new File(l.getPath()));
//                    } else {
//
//                        activity.getFutils().openFile(new File(l.getPath()), activity);
//                    }
//                    DataUtils.addHistoryFile(l.getPath());
//                }
//            } else {
//                goBackItemClick();
//            }
//        }
    }

//    public void goBackItemClick() {
//        if (openMode == OpenMode.CUSTOM) {
//            loadlist(home, false, OpenMode.FILE);
//            return;
//        }
//        HFile currentFile = new HFile(openMode, CURRENT_PATH);
//        if (!results) {
//            if (selection) {
//                adapter.toggleChecked(false);
//            } else {
//                if (openMode == OpenMode.SMB) {
//
//                    try {
//                        if (!CURRENT_PATH.equals(smbPath)) {
//                            String path = (new SmbFile(CURRENT_PATH).getParent());
//                            loadlist((path), true, OpenMode.SMB);
//                        } else loadlist(home, false, OpenMode.FILE);
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//                } else if (CURRENT_PATH.equals("/") || CURRENT_PATH.equals(home) ||
//                            CURRENT_PATH.equals(OTGUtil.PREFIX_OTG)
//                            || CURRENT_PATH.equals(CloudHandler.CLOUD_PREFIX_BOX + "/")
//                            || CURRENT_PATH.equals(CloudHandler.CLOUD_PREFIX_DROPBOX + "/")
//                            || CURRENT_PATH.equals(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/")
//                            || CURRENT_PATH.equals(CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/")
//                            )
//                        MAIN_ACTIVITY.exit();
//                    else if (utils.canGoBack(getContext(), currentFile)) {
//                        loadlist(currentFile.getParent(getContext()), true, openMode);
//                    } else MAIN_ACTIVITY.exit();
//            }
//        } else {
//            loadlist(currentFile.getPath(), true, openMode);
//        }
//    }

    private void returnIntentResults(File file) {
        activity.mReturnIntent = false;

        Intent intent = new Intent();
        if (activity.mRingtonePickerIntent) {

            Uri mediaStoreUri = MediaStoreHack.getUriFromFile(file.getPath(), getActivity());
            System.out.println(mediaStoreUri.toString() + "\t" + MimeTypes.getMimeType(file));
            intent.setDataAndType(mediaStoreUri, MimeTypes.getMimeType(file));
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, mediaStoreUri);
            getActivity().setResult(getActivity().RESULT_OK, intent);
            getActivity().finish();
        } else {

            Log.d("pickup", "file");
            intent.setData(Uri.fromFile(file));
            getActivity().setResult(getActivity().RESULT_OK, intent);
            getActivity().finish();
        }
    }

    /**
     * Assigns sort modes
     * A value from 0 to 3 defines sort mode as name/last modified/size/type in ascending order
     * Values from 4 to 7 defines sort mode as name/last modified/size/type in descending order
     *
     * Final value of {@link #sortby} varies from 0 to 3
     */
    public void getSortModes() {
        int t = Integer.parseInt(sharedPref.getString("sortby", "0"));
        if (t <= 3) {
            sortby = t;
            asc = 1;
        } else if (t > 3) {
            asc = -1;
            sortby = t - 4;
        }

        dsort = Integer.parseInt(sharedPref.getString("dirontop", "0"));
    }

	public static void launchSMB(final SmbFile smbFile, final long si, final Activity activity) {
        final Streamer s = Streamer.getInstance();
        new Thread() {
            public void run() {
                try {
                    /*List<SmbFile> subtitleFiles = new ArrayList<SmbFile>();

					 // finding subtitles
					 for (Layoutelements layoutelement : LIST_ELEMENTS) {
					 SmbFile smbFile = new SmbFile(layoutelement.getDesc());
					 if (smbFile.getName().contains(smbFile.getName())) subtitleFiles.add(smbFile);
					 }*/

                    s.setStreamSrc(smbFile, si);
                    activity.runOnUiThread(new Runnable() {
							public void run() {
								try {
									Uri uri = Uri.parse(Streamer.URL + Uri.fromFile(new File(Uri.parse(smbFile.getPath()).getPath())).getEncodedPath());
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setDataAndType(uri, MimeTypes.getMimeType(new File(smbFile.getPath())));
									PackageManager packageManager = activity.getPackageManager();
									List<ResolveInfo> resInfos = packageManager.queryIntentActivities(i, 0);
									if (resInfos != null && resInfos.size() > 0)
										activity.startActivity(i);
									else
										Toast.makeText(activity,
													   activity.getResources().getString(R.string.smb_launch_error),
													   Toast.LENGTH_SHORT).show();
								} catch (ActivityNotFoundException e) {
									e.printStackTrace();
								}
							}
						});

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void goBack() {
        if (openMode == OpenMode.CUSTOM) {
            loadlist(home, /*false, */OpenMode.FILE);
            return;
        }

        HFile currentFile = new HFile(openMode, CURRENT_PATH);
        if (!results && !mRetainSearchTask) {

            // normal case
            if (selection) {
                adapter.toggleChecked(false);
            } else {

                if (openMode == OpenMode.SMB) {
                    try {
                        if (!smbPath.equals(CURRENT_PATH)) {
                            String path = (new SmbFile(this.CURRENT_PATH).getParent());
                            loadlist(path, /*true, */openMode);
                        } else {
							loadlist(home, /*false, */OpenMode.FILE);
						}
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                } else if (CURRENT_PATH.equals("/") || CURRENT_PATH.equals(home) ||
						   CURRENT_PATH.equals(OTGUtil.PREFIX_OTG + "/")
						   || CURRENT_PATH.equals(CloudHandler.CLOUD_PREFIX_BOX + "/")
						   || CURRENT_PATH.equals(CloudHandler.CLOUD_PREFIX_DROPBOX + "/")
						   || CURRENT_PATH.equals(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/")
						   || CURRENT_PATH.equals(CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/")
						   )
                    activity.onBackPressed();
                else if (activity.getFutils().canGoBack(getContext(), currentFile)) {
                    loadlist(currentFile.getParent(getContext()), /*true, */openMode);
                } else {
					activity.onBackPressed();
				}
            }
//        } else if (!results && mRetainSearchTask) {
//
//            // case when we had pressed on an item from search results and wanna go back
//            // leads to resuming the search task
//
//            if (MainActivityHelper.SEARCH_TEXT != null) {
//
//                // starting the search query again :O
//                MAIN_ACTIVITY.mainFragment = (MainFragment) MAIN_ACTIVITY.getFragment().getTab();
//                FragmentManager fm = MAIN_ACTIVITY.getSupportFragmentManager();
//
//                // getting parent path to resume search from there
//                String parentPath = new HFile(openMode, CURRENT_PATH).getParent(getActivity());
//                // don't fuckin' remove this line, we need to change
//                // the path back to parent on back press
//                CURRENT_PATH = parentPath;
//
//                MainActivityHelper.addSearchFragment(fm, new SearchAsyncHelper(),
//													 parentPath, MainActivityHelper.SEARCH_TEXT, openMode, BaseActivity.rootMode,
//													 sharedPref.getBoolean(SearchAsyncHelper.KEY_REGEX, false),
//													 sharedPref.getBoolean(SearchAsyncHelper.KEY_REGEX_MATCHES, false));
//            } else loadlist(CURRENT_PATH, true, OpenMode.UNKNOWN);
//
//            mRetainSearchTask = false;
//        } else {
//            // to go back after search list have been popped
//            FragmentManager fm = getActivity().getSupportFragmentManager();
//            SearchAsyncHelper fragment = (SearchAsyncHelper) fm.findFragmentByTag(MainActivity.TAG_ASYNC_HELPER);
//            if (fragment != null) {
//                if (fragment.mSearchTask.getStatus() == AsyncTask.Status.RUNNING) {
//                    fragment.mSearchTask.cancel(true);
//                }
//            }
//            loadlist(new File(CURRENT_PATH).getPath(), true, OpenMode.UNKNOWN);
//            results = false;
        }
    }

	public void loadlist(String path, /*boolean back, */OpenMode openMode) {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        /*if(openMode==-1 && android.util.Patterns.EMAIL_ADDRESS.matcher(path).matches())
		 bindDrive(path);
		 else */
//        if (loadList != null) loadList.cancel(true);
//        loadList = new LoadFiles();//LoadList(activity, activity, /*back, */this, openMode);
//        loadList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (path));
		changeDir(path, true);
		this.openMode = openMode;
    }
	
	public Map<String, Object> onSaveInstanceState() {
		Map<String, Object> outState = new TreeMap<>();
		//Log.d(TAG, "Map onSaveInstanceState " + dir + ", " + outState);
		outState.put(ExplorerActivity.EXTRA_DIR_PATH, CURRENT_PATH);
		outState.put(ExplorerActivity.EXTRA_SUFFIX, suffix);
		outState.put(ExplorerActivity.EXTRA_MULTI_SELECT, multiFiles);
		outState.put("selectedInList1", selectedInList1);
		outState.put("dataSourceL1", dataSourceL1);
		outState.put("searchMode", searchMode);
		outState.put("searchVal", quicksearch.getText().toString());
		outState.put("dirTemp4Search", dirTemp4Search);
		outState.put("allCbx.isEnabled", allCbx.isEnabled());

		final int index = gridLayoutManager.findFirstVisibleItemPosition();
        final View vi = listView1.getChildAt(0); 
        final int top = (vi == null) ? 0 : vi.getTop();
		outState.put("index", index);
		outState.put("top", top);

        return outState;
	}
	
	class LoadFiles extends AsyncTask<Object, String, Void> {
		//private File curDir;
		private Boolean doScroll;
		private ArrayList<LayoutElements> dataSourceL1a = new ArrayList<>();

		@Override
		protected void onPreExecute() {
			if (!mSwipeRefreshLayout.isRefreshing()) {
				mSwipeRefreshLayout.setRefreshing(true);
			}
		}

		@Override
		protected Void doInBackground(final Object... params) {
			CURRENT_PATH = (String) params[0];
			doScroll = (Boolean) params[1];
			if (CURRENT_PATH == null) {
				return null;
			}
			Log.d("FileFrag", "LoadFiles.doInBackground " + CURRENT_PATH + ", " + FileFrag.this);
			folder_count = 0;
			file_count = 0;
			if (openmode == OpenMode.UNKNOWN) {
				HFile hFile = new HFile(OpenMode.UNKNOWN, CURRENT_PATH);
				hFile.generateMode(activity);
				if (hFile.isLocal()) {
					openmode = OpenMode.FILE;
				} else if (hFile.isSmb()) {
					openmode = OpenMode.SMB;
					smbPath = CURRENT_PATH;
				} else if (hFile.isOtgFile()) {
					openmode = OpenMode.OTG;
				} else if (hFile.isBoxFile()) {
					openmode = OpenMode.BOX;
				} else if (hFile.isDropBoxFile()) {
					openmode = OpenMode.DROPBOX;
				} else if (hFile.isGoogleDriveFile()) {
					openmode = OpenMode.GDRIVE;
				} else if (hFile.isOneDriveFile()) {
					openmode = OpenMode.ONEDRIVE;
				} else if (hFile.isCustomPath())
					openmode = OpenMode.CUSTOM;
				else if (android.util.Patterns.EMAIL_ADDRESS.matcher(CURRENT_PATH).matches()) {
					openmode = OpenMode.ROOT;
				}
			}

			switch (openmode) {
				case SMB:
					HFile hFile = new HFile(OpenMode.SMB, CURRENT_PATH);
					try {
						SmbFile[] smbFile = hFile.getSmbFile(5000).listFiles();
						dataSourceL1a = addToSmb(smbFile, CURRENT_PATH);
						openmode = OpenMode.SMB;
					} catch (SmbAuthException e) {
						if(!e.getMessage().toLowerCase().contains("denied"))
							reauthenticateSmb();
						publishProgress(e.getLocalizedMessage());
					} catch (SmbException | NullPointerException e) {
						publishProgress(e.getLocalizedMessage());
						e.printStackTrace();
					}
					break;
				case CUSTOM:
					ArrayList<BaseFile> arrayList = null;
					switch (Integer.parseInt(CURRENT_PATH)) {
						case 0:
							arrayList = listImages();
							break;
						case 1:
							arrayList = listVideos();
							break;
						case 2:
							arrayList = listaudio();
							break;
						case 3:
							arrayList = listDocs();
							break;
						case 4:
							arrayList = listApks();
							break;
						case 5:
							arrayList = listRecent();
							break;
						case 6:
							arrayList = listRecentFiles();
							break;
					}

					CURRENT_PATH = String.valueOf(Integer.parseInt(CURRENT_PATH));

					try {
						if (arrayList != null)
							dataSourceL1a = addTo(arrayList);
						else return null;// new ArrayList<LayoutElements>();
					} catch (Exception e) {
					}
					break;
				case OTG:
					dataSourceL1a = addTo(listOtg(CURRENT_PATH));
					openmode = OpenMode.OTG;
					break;
				case DROPBOX:

					CloudStorage cloudStorageDropbox = DataUtils.getAccount(OpenMode.DROPBOX);

					try {
						dataSourceL1a = addTo(listCloud(CURRENT_PATH, cloudStorageDropbox, OpenMode.DROPBOX));
					} catch (CloudPluginException e) {
						e.printStackTrace();
						return null;// new ArrayList<LayoutElements>();
					}
					break;
				case BOX:
					CloudStorage cloudStorageBox = DataUtils.getAccount(OpenMode.BOX);

					try {
						dataSourceL1a = addTo(listCloud(CURRENT_PATH, cloudStorageBox, OpenMode.BOX));
					} catch (CloudPluginException e) {
						e.printStackTrace();
						return null;// new ArrayList<LayoutElements>();
					}
					break;
				case GDRIVE:
					CloudStorage cloudStorageGDrive = DataUtils.getAccount(OpenMode.GDRIVE);

					try {
						dataSourceL1a = addTo(listCloud(CURRENT_PATH, cloudStorageGDrive, OpenMode.GDRIVE));
					} catch (CloudPluginException e) {
						e.printStackTrace();
						return null;// new ArrayList<LayoutElements>();
					}
					break;
				case ONEDRIVE:
					CloudStorage cloudStorageOneDrive = DataUtils.getAccount(OpenMode.ONEDRIVE);

					try {
						dataSourceL1a = addTo(listCloud(CURRENT_PATH, cloudStorageOneDrive, OpenMode.ONEDRIVE));
					} catch (CloudPluginException e) {
						e.printStackTrace();
						return null;// new ArrayList<LayoutElements>();
					}
					break;
				default:
					// we're neither in OTG not in SMB, load the list based on root/general filesystem
					try {

						File curDir = new File(CURRENT_PATH);
						while (curDir != null && !curDir.exists()) {
							publishProgress(curDir.getAbsolutePath() + " is not existed");
							curDir = curDir.getParentFile();
						}
						if (curDir == null) {
							publishProgress("Current directory is not existed. Change to root");
							curDir = new File("/");
						}

						final String curPath = curDir.getAbsolutePath();
						if (!dirTemp4Search.equals(curPath)) {
							if (backStack.size() > ExplorerActivity.NUM_BACK) {
								backStack.remove(0);
							}
							final Map<String, Object> bun = onSaveInstanceState();
							backStack.push(bun);

							history.remove(curPath);
							if (history.size() > ExplorerActivity.NUM_BACK) {
								history.remove(0);
							}
							history.push(curPath);

							activity.historyList.remove(curPath);
							if (activity.historyList.size() > ExplorerActivity.NUM_BACK) {
								activity.historyList.remove(0);
							}
							activity.historyList.push(curPath);
							tempPreviewL2 = null;
						}
						CURRENT_PATH = curPath;
						dirTemp4Search = CURRENT_PATH;
						//Log.d(TAG, Util.collectionToString(history, true, "\n"));

						if (mFileObserver != null) {
							mFileObserver.stopWatching();
						}
						mFileObserver = createFileObserver(CURRENT_PATH);
						mFileObserver.startWatching();
						if (tempPreviewL2 != null && !tempPreviewL2.bf.f.exists()) {
							tempPreviewL2 = null;
						}

						ArrayList<BaseFile> files = RootHelper.getFilesList(CURRENT_PATH, BaseActivity.rootMode, SHOW_HIDDEN,
                            new RootHelper.GetModeCallBack() {
								@Override
								public void getMode(OpenMode mode) {
									openmode = mode;
								}
							});
						//List<File> files = FileUtil.currentFileFolderListing(curDir);
						//Log.d("filesListing", Util.collectionToString(files, true, "\r\n"));
						String[] suffixes = {".*"};
						if (suffix != null) {	// always dir, already checked
							// tìm danh sách các file có ext thích hợp
							//Log.d("suffix", suffix);
							suffixes = suffix.toLowerCase().split("; *");
						}
						int lastIndexOfDot;
						String fName;
						String ext;
						Arrays.sort(suffixes);
						for (BaseFile f : files) {
							if (f.exists()) {
								fName = f.getName();
								//Log.d("changeDir fName", fName + ", isDir " + f.isDirectory());
								if (f.isDirectory()) {
									dataSourceL1a.add(new LayoutElements(f));
								} else {
									if (suffix.length() > 0) {
										if (".*".equals(suffix)) {
											dataSourceL1a.add(new LayoutElements(f));
										} else {
											lastIndexOfDot = fName.lastIndexOf(".");
											if (lastIndexOfDot >= 0) {
												ext = fName.substring(lastIndexOfDot);
												if ((Arrays.binarySearch(suffixes, ext.toLowerCase()) >= 0)) {
													dataSourceL1a.add(new LayoutElements(f));
												}
											}
										}
									}
								}
							}
						}
						// điền danh sách vào allFiles

						//dataSourceL1a = addTo(files);
						dirTemp4Search = CURRENT_PATH;
					} catch (RootNotPermittedException e) {
						//AppConfig.toast(c, c.getString(R.string.rootfailure));
						return null;
					}
					break;
			}

			if (dataSourceL1a != null && !(openmode == OpenMode.CUSTOM && ((CURRENT_PATH).equals("5") || (CURRENT_PATH).equals("6"))))
				Collections.sort(dataSourceL1a, fileListSorter);//(dsort, sortby, asc));

			if (openMode != OpenMode.CUSTOM)
				DataUtils.addHistoryFile(CURRENT_PATH);

			//curDir = (File) params[0];
			//curDir = new File(path);
//			doScroll = (Boolean) params[1];
//			
//			while (curDir != null && !curDir.exists()) {
//				publishProgress(curDir.getAbsolutePath() + " is not existed");
//				curDir = curDir.getParentFile();
//			}
//			if (curDir == null) {
//				publishProgress("Current directory is not existed. Change to root");
//				curDir = new File("/");
//			}
//			
//			final String curPath = curDir.getAbsolutePath();
//			if (!dirTemp4Search.equals(curPath)) {
//				if (backStack.size() > ExplorerActivity.NUM_BACK) {
//					backStack.remove(0);
//				}
//				final Map<String, Object> bun = onSaveInstanceState();
//				backStack.push(bun);
//				
//				history.remove(curPath);
//				if (history.size() > ExplorerActivity.NUM_BACK) {
//					history.remove(0);
//				}
//				history.push(curPath);
//				
//				activity.historyList.remove(curPath);
//				if (activity.historyList.size() > ExplorerActivity.NUM_BACK) {
//					activity.historyList.remove(0);
//				}
//				activity.historyList.push(curPath);
//				tempPreviewL2 = null;
//			}
//			path = curPath;
//			dirTemp4Search = path;
//			//Log.d(TAG, Util.collectionToString(history, true, "\n"));
//			
//			if (mFileObserver != null) {
//				mFileObserver.stopWatching();
//			}
//			mFileObserver = createFileObserver(path);
//			mFileObserver.startWatching();
//			if (tempPreviewL2 != null && !tempPreviewL2.bf.f.exists()) {
//				tempPreviewL2 = null;
//			}
//			
//			List<File> files = FileUtil.currentFileFolderListing(curDir);
//			//Log.d("filesListing", Util.collectionToString(files, true, "\r\n"));
//			if (files != null) {	// always dir, already checked
//				// tìm danh sách các file có ext thích hợp
//				//Log.d("suffix", suffix);
//				String[] suffixes = suffix.toLowerCase().split("; *");
//				Arrays.sort(suffixes);
//				for (File f : files) {
//					if (f.exists()) {
//						String fName = f.getName();
//						//Log.d("changeDir fName", fName + ", isDir " + f.isDirectory());
//						if (f.isDirectory()) {
//							dataSourceL1a.add(new LayoutElements(f));
//						} else {
//							if (suffix.length() > 0) {
//								if (".*".equals(suffix)) {
//									dataSourceL1a.add(new LayoutElements(f));
//								} else {
//									int lastIndexOf = fName.lastIndexOf(".");
//									if (lastIndexOf >= 0) {
//										String ext = fName.substring(lastIndexOf);
//										boolean chosen = Arrays.binarySearch(suffixes, ext.toLowerCase()) >= 0;
//										if (chosen) {
//											dataSourceL1a.add(new LayoutElements(f));
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//				// điền danh sách vào allFiles
//			}
			//Log.d(TAG, "changeDir dataSourceL1a.size=" + dataSourceL1a.size() + ", fake=" + fake + ", " + path);
			//String dirSt = dir.getText().toString();

			return null;
		}

		private ArrayList<LayoutElements> addTo(ArrayList<BaseFile> baseFiles) {
			ArrayList<LayoutElements> a = new ArrayList<>();
			for (int i = 0; i < baseFiles.size(); i++) {
				BaseFile baseFile = baseFiles.get(i);
				//File f = new File(ele.getPath());
				//String size = "";
				if (!DataUtils.hiddenfiles.contains(baseFile.getPath())) {
					if (baseFile.isDirectory()) {
						//size = "";

//                    Bitmap lockBitmap = BitmapFactory.decodeResource(ma.getResources(),
//                            R.drawable.ic_folder_lock_white_36dp);
						//BitmapDrawable lockBitmapDrawable = new BitmapDrawable(ma.getResources(), lockBitmap);

						LayoutElements layoutElements = activity.getFutils()
                            .newElement(//baseFile.getName().endsWith(CryptUtil.CRYPT_EXTENSION) ? lockBitmapDrawable : ma.folder,
							baseFile.getPath(), baseFile.getPermission(), baseFile.getLink(), /*size, */baseFile.f.length(), true, //false,
                            baseFile.getDate());
						layoutElements.setMode(baseFile.getMode());
						a.add(layoutElements);
						folder_count++;
					} else {
						long longSize = 0;
						try {
							if (baseFile.getSize() != -1) {
								longSize = baseFile.getSize();
								//size = Formatter.formatFileSize(c, longSize);
							} else {
								//size = "";
								longSize = 0;
							}
						} catch (NumberFormatException e) {
							//e.printStackTrace();
						}
						try {
							LayoutElements layoutElements = activity.getFutils().newElement(//Icons.loadMimeIcon(baseFile.getPath(), !ma.IS_LIST, ma.res), 
								baseFile.getPath(), baseFile.getPermission(),
                                baseFile.getLink(), /*size, */longSize, false, /*false, */baseFile.getDate());
							layoutElements.setMode(baseFile.getMode());
							a.add(layoutElements);
							file_count++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			return a;
		}

		protected void onProgressUpdate(String...values) {
			showToast(values[0]);
		}

		protected void onPostExecute(Object result) {
			if (CURRENT_PATH != null) {
				if (CURRENT_PATH.startsWith("/")) {
					final File curDir = new File(CURRENT_PATH);
					diskStatus.setText(
						"Free " + Util.nf.format(curDir.getFreeSpace() / (1 << 20))
						+ " MiB. Used " + Util.nf.format((curDir.getTotalSpace()-curDir.getFreeSpace()) / (1 << 20))
						+ " MiB. Total " + Util.nf.format(curDir.getTotalSpace() / (1 << 20)) + " MiB");
				}
				dataSourceL1.clear();
				Collections.sort(dataSourceL1a, fileListSorter);
				dataSourceL1.addAll(dataSourceL1a);
				dataSourceL1a.clear();
				selectedInList1.clear();
			}
			if (status.getVisibility() == View.GONE) {
				if (selStatus != null) {
					selStatus.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_top));
					selStatus.setVisibility(View.VISIBLE);
				} else {
					selectionStatus1.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_top));
					selectionStatus1.setVisibility(View.VISIBLE);
				}
				horizontalDivider0.setVisibility(View.VISIBLE);
				horizontalDivider12.setVisibility(View.VISIBLE);
				status.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_top));
				status.setVisibility(View.VISIBLE);
			}
			
			if (multiFiles) {
				boolean allInclude = (dataSourceL2 != null && dataSourceL1a.size() > 0) ? true : false;
				if (allInclude) {
					for (LayoutElements st : dataSourceL1a) {
						if (!dataSourceL2.contains(st)) {
							allInclude = false;
							break;
						}
					}
				}

				if (allInclude) {
					allCbx.setSelected(true);//.setChecked(true);
					allCbx.setImageResource(R.drawable.ic_accept);
					allCbx.setEnabled(false);
				} else {
					allCbx.setSelected(false);//setChecked(false);
					allCbx.setImageResource(R.drawable.dot);
					allCbx.setEnabled(true);
				}
			}
			
			if (activity.COPY_PATH == null && activity.MOVE_PATH == null && commands != null && commands.getVisibility() == View.VISIBLE) {
				horizontalDivider.setVisibility(View.GONE);
				commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.shrink_from_top));
				commands.setVisibility(View.GONE);
			} else if ((activity.COPY_PATH != null || activity.MOVE_PATH != null) && commands != null && commands.getVisibility() == View.GONE) {
				horizontalDivider.setVisibility(View.VISIBLE);
				commands.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.grow_from_bottom));
				commands.setVisibility(View.VISIBLE);
				updateDelPaste();
			}
			
			//Log.d("changeDir dataSourceL1", Util.collectionToString(dataSourceL1, true, "\r\n"));
			listView1.setActivated(true);
			srcAdapter.notifyDataSetChanged();
			if (doScroll) {
				gridLayoutManager.scrollToPosition(0);
			}

			if (allCbx.isSelected()) {//}.isChecked()) {
				selectionStatus1.setText(dataSourceL1.size() 
										 + "/" + dataSourceL1.size());
			} else {
				selectionStatus1.setText(selectedInList1.size() 
										 + "/" + dataSourceL1.size());
			}
			Log.d(TAG, "LoadFiles.onPostExecute " + CURRENT_PATH);
			updateDir(CURRENT_PATH, FileFrag.this);
			if (mSwipeRefreshLayout.isRefreshing()) {
				mSwipeRefreshLayout.setRefreshing(false);
			}
			if (dataSourceL1.size() == 0) {
				nofilelayout.setVisibility(View.VISIBLE);
				mSwipeRefreshLayout.setVisibility(View.GONE);
			} else {
				nofilelayout.setVisibility(View.GONE);
				mSwipeRefreshLayout.setVisibility(View.VISIBLE);
			}

		}

		ArrayList<BaseFile> listaudio() {
			String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
			String[] projection = {
                MediaStore.Audio.Media.DATA
			};

			Cursor cursor = activity.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

			ArrayList<BaseFile> songs = new ArrayList<>();
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				do {
					String path = cursor.getString(cursor.getColumnIndex
												   (MediaStore.Files.FileColumns.DATA));
					BaseFile strings = RootHelper.generateBaseFile(new File(path), SHOW_HIDDEN);
					if (strings != null) songs.add(strings);
				} while (cursor.moveToNext());
			}
			cursor.close();
			return songs;
		}

		ArrayList<BaseFile> listImages() {
			ArrayList<BaseFile> songs = new ArrayList<>();
			final String[] projection = {MediaStore.Images.Media.DATA};
			final Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
																	  projection, null, null, null);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				do {
					String path = cursor.getString(cursor.getColumnIndex
												   (MediaStore.Files.FileColumns.DATA));
					BaseFile strings = RootHelper.generateBaseFile(new File(path), SHOW_HIDDEN);
					if (strings != null) songs.add(strings);
				} while (cursor.moveToNext());
			}
			cursor.close();
			return songs;
		}

		ArrayList<BaseFile> listVideos() {
			ArrayList<BaseFile> songs = new ArrayList<>();
			final String[] projection = {MediaStore.Images.Media.DATA};
			final Cursor cursor = activity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
																	  projection, null, null, null);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				do {
					String path = cursor.getString(cursor.getColumnIndex
												   (MediaStore.Files.FileColumns.DATA));
					BaseFile strings = RootHelper.generateBaseFile(new File(path), SHOW_HIDDEN);
					if (strings != null) songs.add(strings);
				} while (cursor.moveToNext());
			}
			cursor.close();
			return songs;
		}

		ArrayList<BaseFile> listRecentFiles() {
			ArrayList<BaseFile> songs = new ArrayList<>();
			final String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_MODIFIED};
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 2);
			Date d = c.getTime();
			Cursor cursor = activity.getContentResolver().query(MediaStore.Files
																.getContentUri("external"), projection,
																null,
																null, null);
			if (cursor == null) return songs;
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				do {
					String path = cursor.getString(cursor.getColumnIndex
												   (MediaStore.Files.FileColumns.DATA));
					File f = new File(path);
					if (d.compareTo(new Date(f.lastModified())) != 1 && !f.isDirectory()) {
						BaseFile strings = RootHelper.generateBaseFile(new File(path), SHOW_HIDDEN);
						if (strings != null) songs.add(strings);
					}
				} while (cursor.moveToNext());
			}
			cursor.close();
			Collections.sort(songs, new Comparator<BaseFile>() {
					@Override
					public int compare(BaseFile lhs, BaseFile rhs) {
						return -1 * Long.valueOf(lhs.getDate()).compareTo(rhs.getDate());

					}
				});
			if (songs.size() > 20)
				for (int i = songs.size() - 1; i > 20; i--) {
					songs.remove(i);
				}
			return songs;
		}

		ArrayList<BaseFile> listApks() {
			ArrayList<BaseFile> songs = new ArrayList<>();
			final String[] projection = {MediaStore.Files.FileColumns.DATA};

			Cursor cursor = activity.getContentResolver().query(MediaStore.Files
																.getContentUri("external"), projection,
																null,
																null, null);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				do {
					String path = cursor.getString(cursor.getColumnIndex
												   (MediaStore.Files.FileColumns.DATA));
					if (path != null && path.endsWith(".apk")) {
						BaseFile strings = RootHelper.generateBaseFile(new File(path), SHOW_HIDDEN);
						if (strings != null) songs.add(strings);
					}
				} while (cursor.moveToNext());
			}
			cursor.close();
			return songs;
		}

		ArrayList<BaseFile> listRecent() {
			final HistoryManager history = new HistoryManager(activity, "Table2");
			final ArrayList<String> paths = history.readTable(DataUtils.HISTORY);
			history.end();
			ArrayList<BaseFile> songs = new ArrayList<>();
			for (String f : paths) {
				if (!f.equals("/")) {
					BaseFile a = RootHelper.generateBaseFile(new File(f), SHOW_HIDDEN);
					a.generateMode(activity);
					if (a != null && !a.isSmb() && !(a).isDirectory() && a.exists())
						songs.add(a);
				}
			}
			return songs;
		}

		ArrayList<BaseFile> listDocs() {
			ArrayList<BaseFile> songs = new ArrayList<>();
			final String[] projection = {MediaStore.Files.FileColumns.DATA};
			Cursor cursor = activity.getContentResolver().query(MediaStore.Files.getContentUri("external"),
																projection, null, null, null);
			String[] types = new String[]{".pdf", ".xml", ".html", ".asm", ".text/x-asm", ".def", ".in", ".rc",
                ".list", ".log", ".pl", ".prop", ".properties", ".rc",
                ".doc", ".docx", ".msg", ".odt", ".pages", ".rtf", ".txt", ".wpd", ".wps"};
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				do {
					String path = cursor.getString(cursor.getColumnIndex
												   (MediaStore.Files.FileColumns.DATA));
					if (path != null && contains(types, path.toLowerCase())) {
						BaseFile strings = RootHelper.generateBaseFile(new File(path), SHOW_HIDDEN);
						if (strings != null) songs.add(strings);
					}
				} while (cursor.moveToNext());
			}
			cursor.close();
			return songs;
		}

		/**
		 * Lists files from an OTG device
		 * @param path the path to the directory tree, starts with prefix {@link com.amaze.filemanager.utils.OTGUtil#PREFIX_OTG}
		 *             Independent of URI (or mount point) for the OTG
		 * @return a list of files loaded
		 */
		ArrayList<BaseFile> listOtg(String path) {

			return OTGUtil.getDocumentFilesList(path, activity);
		}

		boolean contains(String[] types, String path) {
			for (String string : types) {
				if (path.endsWith(string)) return true;
			}
			return false;
		}

		private ArrayList<BaseFile> listCloud(String path, CloudStorage cloudStorage, OpenMode openMode)
		throws CloudPluginException {
			if (!CloudSheetFragment.isCloudProviderAvailable(activity))
				throw new CloudPluginException();

			return CloudUtil.listFiles(path, cloudStorage, openMode);
		}
	}
}
