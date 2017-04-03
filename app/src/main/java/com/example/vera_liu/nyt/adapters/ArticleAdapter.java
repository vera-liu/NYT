package com.example.vera_liu.nyt.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vera_liu.nyt.R;
import com.example.vera_liu.nyt.models.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vera_liu on 3/30/17.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private ArrayList<Article> articles;
    private Context mContext;
    private String IMAGE_BASE_URL = "https://nytimes.com/";
    private String API_KEY = "?api-key=a057966afd53489a8a4b89f3a8bd9dd6";
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView articleTitle;
            public ImageView articleImage;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);
                final View iView = itemView;
                iView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(iView, position);
                            }
                        }
                    }
                });
                articleTitle = (TextView) itemView.findViewById(R.id.article_title);
                articleImage = (ImageView) itemView.findViewById(R.id.article_image);
            }
    }
    public ArticleAdapter(Context context, ArrayList<Article> articles) {
        this.articles = articles;
        mContext = context;
    }

        private Context getContext() {
            return mContext;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View articleView = inflater.inflate(R.layout.item_article, parent, false);
            ViewHolder viewHolder = new ViewHolder(articleView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Article article = articles.get(position);
//            String thumbnail = article.getTypeOfMaterial();
            viewHolder.articleTitle.setText(article.getHeadline().getMain());
            String imageUrl = article.getImageUrl();
            if(!imageUrl.isEmpty()) {
                Log.d("imageUrl", IMAGE_BASE_URL+imageUrl);
                Picasso.Builder builder = new Picasso.Builder(mContext);
                builder.listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        exception.printStackTrace();
                    }
                });
                Picasso.with(mContext).load(IMAGE_BASE_URL+imageUrl+API_KEY)
                        .fit().centerCrop().placeholder(R.mipmap.ic_launcher)
                        .into(viewHolder.articleImage,
                                new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Log.d("error", "error");
                            }
                        }
                        );
            }
        }

        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return articles.size();
        }
}

