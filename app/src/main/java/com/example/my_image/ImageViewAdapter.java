package com.example.my_image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_image.Model.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ViewHolder> { //RecyclerView.Adapter : đây là nơi xử lý dữ liệu và gán cho View
    Context context; //gọi đến cái màn hình mà adapter này phục vụ
    ArrayList<Photo> photoArrayList;
    AdapterListener adapterListener;

    public ImageViewAdapter(Context context, ArrayList<Photo> photoArrayList, AdapterListener adapterListener) {
        this.context = context;
        this.photoArrayList = photoArrayList;
        this.adapterListener = adapterListener;
    }
    // tạo interfacr để lắng nghe sự kiện khi click vào 1 item trên recyclerView
    public interface AdapterListener{
        void OnClick(int position);
    }

    // bố cục xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dong_image, parent, false);

        return new ViewHolder(view);
    }

    // cập nhật dữ liệu
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Photo photo = photoArrayList.get(position);
        holder.tvView.setText(photo.getViews());
        Picasso.with(context).load(photo.getUrlL()).into(holder.imgPhoto);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterListener.OnClick(position);
            }
        });

    }
    // số lượng item sẽ hiển thị
    @Override
    public int getItemCount() {
        return photoArrayList.size();
    }

    // hàm ánh xạ, liên kết đên các view có trong file xml
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvView;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imageview);
            tvView = itemView.findViewById(R.id.textviewView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
