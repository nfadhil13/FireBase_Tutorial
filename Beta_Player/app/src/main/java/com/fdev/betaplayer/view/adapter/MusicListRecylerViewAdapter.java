package com.fdev.betaplayer.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fdev.betaplayer.R;
import com.fdev.betaplayer.service.model.Music;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MusicListRecylerViewAdapter extends RecyclerView.Adapter<MusicListRecylerViewAdapter.MusicViewHolder> {
    private List<Music> musicList;
    private OnPlayClickedListener onPlayClickedListener;

    public MusicListRecylerViewAdapter(OnPlayClickedListener onPlayClickedListener) {
        this.onPlayClickedListener = onPlayClickedListener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_row, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = musicList.get(position);
        String title= music.getTitle();
        String postBy = music.getPostBy();

        holder.mTextViewTitle.setText(title);
        holder.mTextViewPostBy.setText(postBy);

        String imageURL = music.getImageURL();

        Picasso.get()
                .load(imageURL)
                .into(holder.mImageView);

    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextViewTitle , mTextViewPostBy;
        public ImageView mImageView;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTitle = itemView.findViewById(R.id.music_item_tv_title);
            mTextViewPostBy = itemView.findViewById(R.id.music_item_tv_post_by);
            mImageView = itemView.findViewById(R.id.music_item_imageview);
            mImageView.setOnClickListener(this);
            mTextViewTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.music_item_imageview){
                onPlayClickedListener.onClickImageListener(getAdapterPosition());
            }
            if(v.getId()==R.id.music_item_tv_title){
                onPlayClickedListener.onClickMoreListener(getAdapterPosition());
            }
        }


    }

    public  interface OnPlayClickedListener{
        public void onClickImageListener(int position);
        public void onClickMoreListener(int position);
    }
}
