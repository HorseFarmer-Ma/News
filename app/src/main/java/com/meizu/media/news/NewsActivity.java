package com.meizu.media.news;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsActivity extends AppCompatActivity {
    private WebView webView;
    private MyListView listView;
    private List<Map<String, String>> listData;
    SimpleAdapter mSimpleAdapter;
    // 屏幕宽度
    private int sDisplayWidth = -1;
    // 屏幕高度
    private int sDisplayHeight = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ensureDisplaySize();
        init();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            switch (i){
                case 100:
                    listView.setAdapter(mSimpleAdapter);
                    break;
                default:
                    break;
            }
        }
    };

    private void init(){
        listView = (MyListView) findViewById(R.id.lv_comment);
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                listData = new ArrayList<>();
                for(int i = 0; i < 1000; i++){
                    HashMap<String, String> map = new HashMap<>();
                    map.put("txt", String.valueOf(i));
                    listData.add(map);
                }
                mSimpleAdapter = new SimpleAdapter(NewsActivity.this, listData,
                        android.R.layout.simple_list_item_1, new String[]{"txt"}, new int[]{android.R.id.text1});
                mHandler.sendEmptyMessage(100);
            }
        });
        a.start();
        webView = new WebView(this);
        webView.loadUrl("https://s4.uczzd.cn/webview/news?" +
                "app=meizunews-iflow&aid=13291089527580519628&cid=100&zzd_f" +
                "rom=meizunews-iflow&uc_param_str=dndsfrvesvntnwpfgicpbi&rec" +
                "oid=8504011015955446065&rd_type=share&sp_gz=0&&btifl=100&sha" +
                "reApp=com.alibaba.android.rimet.biz.BokuiActivity ");
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        listView.addHeaderView(webView);
    }

    private void ensureDisplaySize() {
        if (sDisplayWidth <= 0 || sDisplayHeight <= 0) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            sDisplayHeight = metrics.heightPixels;
            sDisplayWidth = metrics.widthPixels;
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if(webView.canGoBack()) {
                webView.goBack();//返回上一页面
                return true;
            } else {
                finish();//退出程序
//                System.exit(0);//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
