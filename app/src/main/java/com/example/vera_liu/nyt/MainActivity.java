package com.example.vera_liu.nyt;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.vera_liu.nyt.adapters.ArticleAdapter;
import com.example.vera_liu.nyt.models.Article;
import com.example.vera_liu.nyt.models.SearchQuery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements SearchFilterDialogFragment.EditFilterListener {
    public String BASE_API_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private ArrayList<Article> articles = new ArrayList<Article>();
    private ArticleAdapter articleAdapter;
    private RecyclerView recyclerView;
    private HandlerThread handlerThread;
    private Handler handler;
    String prevQuery = "";
    private EndlessRecyclerViewScrollListener scrollListener;
    private WebView myWebView;
    private String begin_date;
    private String sort = "Newest";
    private boolean arts = false, fashion = false, sports = false;
    private void loadArticles(SearchQuery query, boolean isNewSearch) {
        if (isNewSearch) {
            articles.clear();
        }
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BASE_API_URL, query.getParams(), new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject articleResponse = response.getJSONObject("response");
                    JSONArray articlesJson = articleResponse.getJSONArray("docs");
                    for(int i=0; i<articlesJson.length(); i++) {
                        JSONObject object = articlesJson.getJSONObject(i);
                        Gson gson = new GsonBuilder().create();
                        String jsonString = object.toString();
                        articles.add(gson.fromJson(jsonString, Article.class));
                    }
                    articleAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("error", e.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
    private void loadInitialArticles() {
        loadArticles(new SearchQuery(), true) ;
    }
    private void loadNextDataFromApi(int page) {
        handler.postDelayed(getSearchArticles(new SearchQuery(prevQuery), false), 500);
    }
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }
    protected ArticleAdapter.OnItemClickListener getClickListener(final ArrayList<Article> articles, final ArticleAdapter adapter) {
        return (new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Article article = articles.get(position);
                myWebView = (WebView) findViewById(R.id.webview);
                myWebView.setVisibility(View.VISIBLE);
                // Configure related browser settings
                myWebView.getSettings().setLoadsImagesAutomatically(true);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                // Configure the client to use when opening URLs
                myWebView.setWebViewClient(new MyBrowser());
                 // Load the initial URL
                myWebView.loadUrl(article.getWebUrl());
                // Enable responsive layout
                myWebView.getSettings().setUseWideViewPort(true);
        // Zoom out if the content width is greater than the width of the viewport
                myWebView.getSettings().setLoadWithOverviewMode(true);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadInitialArticles();
        articleAdapter = new ArticleAdapter(this, articles);
        recyclerView = (RecyclerView) findViewById(R.id.articlesView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);;
        recyclerView.setAdapter(articleAdapter);
        articleAdapter.setOnItemClickListener(getClickListener(articles, articleAdapter));
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        handlerThread = new HandlerThread("RequestHandler");
        handlerThread.start();
        handler = new Handler(Looper.getMainLooper());

        Calendar c = Calendar.getInstance();
        String mm = Integer.toString(c.get(Calendar.MONTH));
        String dd = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        if (c.get(Calendar.MONTH) < 10) mm = "0" + mm;
        if (c.get(Calendar.DAY_OF_MONTH) < 10) dd = "0" + dd;
        begin_date = Integer.toString(c.get(Calendar.YEAR))
                + mm + dd;
        Log.d("begin_date", begin_date);
    }
    private Runnable getSearchArticles(final SearchQuery query, final boolean isNewSearch) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadArticles(query, isNewSearch);
            }
        };
        return runnable;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search_text);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                prevQuery = query;
                handler.postDelayed(getSearchArticles(new SearchQuery(prevQuery), true), 500);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_text) {
            return true;
        } else if (id == R.id.search_filter) {
            SearchFilterDialogFragment.newInstance(begin_date, sort, arts, fashion, sports).show(getSupportFragmentManager(), "editFilter");
        }

        return super.onOptionsItemSelected(item);
    }
    public void onSaveEdit(String order, String begin_date, boolean arts, boolean fashion, boolean sports) {
        sort = order;
        this.begin_date = begin_date;
        this.arts = arts;
        this.fashion = fashion;
        this.sports = sports;
        String fq = "";
        StringBuilder sb = new StringBuilder();
        if (arts || fashion || sports) {
        fq = sb.append("news_desk:(")
                .append(arts ? "\"Arts\"" : "")
                .append(fashion ? "\"Fashion &amp; Style\" " : "")
                .append(sports ? "\"Sports\" " : "")
                .append(")")
                .toString();
        }
        handler.postDelayed(getSearchArticles(new SearchQuery(prevQuery, fq, order, begin_date), true)  , 500);
    }
    public boolean onKeyDown(int keyCode, KeyEvent evt) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            myWebView.setVisibility(View.INVISIBLE);
            return true;
        }

        return false;
    }
}
