package com.edge.coin.MainPackage.AnalyticsPackage;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.edge.coin.MainPackage.MainActivity;
import com.edge.coin.MainPackage.OnBackListener;
import com.edge.coin.MainPackage.OnScrollTopListener;
import com.edge.coin.R;
import com.edge.coin.Utils.SharedPreference;

/**
 * Created by user1 on 2018-03-22.
 */

public class AnalyFragment extends Fragment implements OnBackListener ,OnScrollTopListener, SwipeRefreshLayout.OnRefreshListener {
    WebView webView;
    String backColor;
    String headerColor;
    SharedPreference sharedPreference= new SharedPreference();
    boolean isDarkTheme= false;
    ProgressBar progressBar;
    RelativeLayout loading;
    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView scrollView;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity mainActivity;

        if (context instanceof MainActivity){
            mainActivity= (MainActivity) context;
            mainActivity.setOnBackListener(this);
            mainActivity.setOnScrollTopListener(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_analy,container,false);
        isDarkTheme = sharedPreference.getValue(getActivity(),"theme",false);
        if (isDarkTheme){
            backColor =Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            headerColor = "#"+backColor.substring(3);
        } else {
            backColor = Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.white));
            headerColor = "#555555";
        }
        backColor = "#"+backColor.substring(2);
        progressBar= view.findViewById(R.id.progress);
        webView = view.findViewById(R.id.webView);
        loading =view.findViewById(R.id.loading);
        scrollView =view.findViewById(R.id.nest_scroll);
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        String html =getHtml();
        webView.loadData(html, "text/html; charset=utf-8", "UTF-8");
        webView.setWebViewClient(new WebviewClient());
        webView.setWebChromeClient(new WebChromeClient());
        return view;
    }




    private String getHtml (){
        String html ="<!-- TradingView Widget BEGIN -->\n" +
                "<body style=\"background-color:"+backColor+";\">"+
                "<body topmargin=\"0\"  leftmargin=\"0\" marginwidth=\"0\" marginheight=\"0\">"+
                "<div id=\"tv-ideas-stream-0cbe7\"></div>\n" +
                "<script type=\"text/javascript\" src=\"https://s3.tradingview.com/tv.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +

                "new TradingView.IdeasStreamWidget({\n" +
                "  \"container_id\": \"tv-ideas-stream-0cbe7\",\n" +
                "  \"startingCount\": 100,\n" +
                "  \"width\": \"100%\",\n" +
                "  \"height\": 200,\n" +
                "  \"mode\": \"integrate\",\n" +
                "  \"bgColor\": \""+backColor+"\",\n" +
                "  \"headerColor\": \""+headerColor+"\",\n" +
                "  \"borderColor\": \""+backColor+"\",\n" +
                "  \"locale\": \"kr\",\n" +
                "  \"sort\": \"trending\",\n" +
                "  \"time\": \"day\",\n" +
                "  \"interval\": \"all\",\n" +
                "  \"stream\": \"bitcoin\"\n" +
                "});\n" +
                "</script>\n" +
                "<!-- TradingView Widget END -->";
        return html;
    }

    private String getDetailHtml(String id){
        String html ="<!-- TradingView Widget BEGIN -->\n" +
                "<body topmargin=\"0\"  leftmargin=\"0\" marginwidth=\"0\" marginheight=\"0\">"+
                "<div id=\"tv-idea-preview-6eb95\"></div>\n" +
                "<script type=\"text/javascript\" src=\"https://s3.tradingview.com/tv.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "new TradingView.IdeaWidget({\n" +
                "  \"container_id\": \"tv-idea-preview-6eb95\",\n" +
                "  \"width\": \"100%\",\n" +
                "  \"height\": 450,\n" +
                "  \"idea\": \""+id+"\",\n" +
                "  \"locale\": \"kr\"\n" +
                "});\n" +
                "</script>\n" +
                "<!-- TradingView Widget END -->";
        return html;
    }

    @Override
    public void goBack() {
        if (webView.canGoBack()){
            webView.canGoBack();
        } else {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                activity.setOnBackListener(null);
                activity.onBackPressed();
            }
        }
    }

    @Override
    public void scrollTop() {
        ObjectAnimator anim = ObjectAnimator.ofInt(scrollView, "scrollY", scrollView.getScrollY(), 0);
        anim.setDuration(350).start();
    }

    @Override
    public void onRefresh() {
        webView.reload();
    }


    class WebChromeClient extends android.webkit.WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
            if (newProgress==100){

                progressBar.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
            } else {
                loading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
            super.onProgressChanged(view, newProgress);
        }


    }
    class WebviewClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.contains("https://s.tradingview.com/v/")){
                String id= url.replace("https://s.tradingview.com/v/","").split("/")[0];
                showDialog(id);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loading.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    private void showDialog(String id){
        final Dialog dialog = new Dialog(getActivity());
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dial_web);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        WebView webDetail = dialog.findViewById(R.id.web_detail);
        WebSettings settings = webDetail.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webDetail.loadData(getDetailHtml(id), "text/html; charset=utf-8", "UTF-8");
        RelativeLayout close = dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
