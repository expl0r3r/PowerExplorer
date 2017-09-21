package net.gnu.explorer;

import android.webkit.*;
import android.os.*;
import android.view.*;
import android.content.*;
import android.widget.*;
import android.util.*;
import android.net.*;
import java.io.*;

public class WebFragment extends Frag {

	private static final String TAG = "WebFragment";
	
    private WebView wv;
	private TextView status;
	private String url;
	
	public WebFragment() {
		super();
		type = Frag.TYPE.WEB.ordinal();
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		//setRetainInstance(true);
        Log.d(TAG, "onCreateView " + CURRENT_PATH + ", " + savedInstanceState);
		final View v = inflater.inflate(R.layout.webview, container, false);
		return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
		
		wv = (WebView) v.findViewById(R.id.webview);
		status = (TextView) v.findViewById(R.id.statusView);
		//Bundle args = getArguments();
		//Log.d(TAG, "onViewCreated " + url + ", " + savedInstanceState + ", " + args);
		
		
        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		wv.setScrollbarFadingEnabled(true);
		//wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		wv.setVerticalScrollBarEnabled(true);
		wv.setVerticalScrollbarOverlay(true);
		//wv.setMapTrackballToArrowKeys(false);
		//wv.setScrollIndicators(View.SCROLL_INDICATOR_RIGHT);
		
		final WebSettings webSettings = wv.getSettings();
		webSettings.setBuiltInZoomControls(true);
		//webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSaveFormData(true);
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setAppCacheEnabled(false);
		webSettings.setDomStorageEnabled(false);
		webSettings.setSupportZoom(true);
		//webSettings.setLoadWithOverviewMode(false);
		//webSettings.setUseWideViewPort(true);
		updateColor(null);
		load2(savedInstanceState);
	}

	public void clone(final Frag frag) {
	}

	public void load(final String path) {
		this.CURRENT_PATH = path;
		this.url = Uri.fromFile(new File(path)).toString();
		load2(null);
	}
	
	public void load2(final Bundle savedInstanceState) {
		final Bundle args = getArguments();
		Log.d(TAG, "onViewCreated " + CURRENT_PATH + ", " + args);
		if (savedInstanceState != null) {
			url = savedInstanceState.getString("url");
			//path = savedInstanceState.getString("path");
			//title = savedInstanceState.getString("title");
			Log.d(TAG, "onViewCreated.savedInstanceState " + CURRENT_PATH + ", " + title);
		} else if (args != null) {
			if (args.getString(ExplorerActivity.EXTRA_DIR_PATH) != null) {
				CURRENT_PATH = args.getString(ExplorerActivity.EXTRA_DIR_PATH);
				url = Uri.fromFile(new File(CURRENT_PATH)).toString();
			} else if (args.getString("url") != null) {
				url = args.getString("url");
			}
			title = args.getString("title");
			Log.d(TAG, "onViewCreated.arg " + CURRENT_PATH + ", " + url + ", " + args);
		}
		if (url != null) {
			status.setText(url);
			Log.d(TAG, "OriginalUrl " + wv.getOriginalUrl());
			if (url.equals(wv.getOriginalUrl())) {
				wv.reload();
			} else {
				wv.loadUrl(url);
			}
		}
	}
	
    @Override
	public void onSaveInstanceState(final Bundle outState) {
		//outState.putString("path", path);
		outState.putString("url", url);
		//outState.putString("title", title);
		Log.d(TAG, "onSaveInstanceState" + CURRENT_PATH + ", " + outState);
		super.onSaveInstanceState(outState);
	}

	public void updateColor(View rootView) {
		getView().setBackgroundColor(ExplorerActivity.BASE_BACKGROUND);
		status.setBackgroundColor(ExplorerActivity.BASE_BACKGROUND);
        status.setTextColor(ExplorerActivity.TEXT_COLOR);
        wv.setBackgroundColor(ExplorerActivity.BASE_BACKGROUND);
	}

}


