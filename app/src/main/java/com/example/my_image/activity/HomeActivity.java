package com.example.my_image.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.my_image.adapter.ImageViewAdapter;
import com.example.my_image.model.FlickrPhoto;
import com.example.my_image.model.Photo;
import com.example.my_image.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {
    private ImageViewAdapter imageViewAdapter;
    private RecyclerView recyclerViewImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int NUM_COLUMNS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewImage = findViewById(R.id.recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetData();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        GetData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.more:
                Intent intent = new Intent(HomeActivity.this, InformationActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void GetData() {
        swipeRefreshLayout.setRefreshing(true);
        //RequestQueue: nơi giữ các request trước khi gửi
        //tạo một RequestQueue bằng lệnh
        RequestQueue requestQueue =
                Volley.newRequestQueue(HomeActivity.this);
        //StringRequest: kế thừa từ Request, là class đại diện cho request trả về String
        // khai báo stringRepuest, phương thức POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://www.flickr.com/services/rest", new Response.Listener<String>() { //Nơi bạn nhận dữ liệu trả về từ server khi request hoàn thành
            @Override
            public void onResponse(String response) {
                //là một thư viện java giúp chuyển đổi qua lại giữa JSON và Java
                Gson gson = new Gson();

                FlickrPhoto flickrPhoto =
                        gson.fromJson(response, FlickrPhoto.class);

                List<Photo> photos = flickrPhoto.getPhotos().getPhoto();

                // gọi interface bên adapter để bắt sự kiện chuyển màn hình và truyền position của item đã click sang màn hình main2
                imageViewAdapter = new ImageViewAdapter(getApplication(), (ArrayList<Photo>) photos,
                        new ImageViewAdapter.AdapterListener() {
                            @Override
                            public void OnClick(int position) {
                                Intent intent = new Intent(HomeActivity.this, ViewImageActivity.class);
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });
                // 1 dạng layout trong recyclerView giúp view hiển thị theo dạng lưới tùy theo kích thước của item
                StaggeredGridLayoutManager staggeredGridLayoutManager = new
                        StaggeredGridLayoutManager(NUM_COLUMNS,LinearLayoutManager.VERTICAL);
                recyclerViewImage.setLayoutManager(staggeredGridLayoutManager);
                recyclerViewImage.setAdapter(imageViewAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // nơi nhận các lỗi xảy ra khi request
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(HomeActivity.this, error.toString(),Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // lưu giữ các giá trị theo cặp key/value
                Map<String, String> params = new HashMap<>();
                params.put("api_key", "7a4b5ef02077a1f5dd3f1fef0d14ecb6");
                params.put("user_id", "186424648@N06");
                params.put("extras", "views, media, path_alias, url_l, url_o");
                params.put("format", "json");
                params.put("method", "flickr.favorites.getList");
                params.put("nojsoncallback", "1");

                return params;
            }
        };
        requestQueue.add(stringRequest); // thêm vào nơi giữ các request để gửi lên server
    }
}
