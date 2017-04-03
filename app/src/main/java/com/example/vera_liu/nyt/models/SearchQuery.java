package com.example.vera_liu.nyt.models;

import android.util.Log;

import com.loopj.android.http.RequestParams;

/**
 * Created by vera_liu on 4/1/17.
 */

public class SearchQuery {
    private String q="", fq="", sort="", begin_date="";
    public SearchQuery() {

    }
    public SearchQuery(String q) {
        this.q = q;
    }
    public SearchQuery(String q, String fq, String sort, String begin_date) {
        this.q = q;
        this.fq = fq;
        this.sort = sort;
        this.begin_date = begin_date;
    }
    public RequestParams getParams() {
        RequestParams params = new RequestParams("api-key", "a057966afd53489a8a4b89f3a8bd9dd6");
        if (!this.q.isEmpty()) {
            params.add("q", q);
        }
        if (!this.fq.isEmpty()) {
            params.add("fq", fq);
        }
        if(!this.sort.isEmpty()) {
            params.add("sort", sort);
        }
        if(!this.begin_date.isEmpty()) {
            params.add("begin_date", begin_date);
        }
        Log.d("params", params.toString());
        return params;
    }
}
