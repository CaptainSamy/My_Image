package com.example.my_image.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.my_image.Adapter.ViewpagerAdapter;
import com.example.my_image.Model.FlickrPhoto;
import com.example.my_image.Model.Photo;
import com.example.my_image.R;
import com.example.my_image.Helper.SaveImageHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class ViewImageActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1000 ;
    private FloatingActionButton fab, fab1, fab2;
    public ViewPager viewPager;
    private ViewpagerAdapter viewpagerAdapter;
    private TextView tv1, tv2;
    private boolean An_Hien = false;
    private AlertDialog dialog;
    private String link; // phục vụ cho việc tải ảnh
    public int position; // nhận position của ảnh đã click bên main
    private List<Photo> photos;

    // người dùng đã cấp quyền hay chưa
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Ðã được cho phép", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        tv1 = findViewById(R.id.tv1);
        fab2 = findViewById(R.id.fab2);
        tv2 = findViewById(R.id.tv2);

        viewPager = findViewById(R.id.viewPager);
        Intent intent = this.getIntent();
        position = intent.getIntExtra("position", 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData();

        // xin quyền lưu vào bộ nhớ
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            },PERMISSION_REQUEST_CODE);

        //float action button
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_rotation1);
        final Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.anim_rotation2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (An_Hien == false){
                    Hien();
                    An_Hien = true;
                    fab.startAnimation(animation);
                } else {
                    An();
                    An_Hien = false;
                    fab.startAnimation(animation2);
                }
            }
        });

        //
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // xin quyền lưu trữ từ người dùng nếu trước đó họ chưa cấp quyền
                if (ActivityCompat.checkSelfPermission(ViewImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ViewImageActivity.this, "",Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_CODE);
                    return;
                } else {
                    dialog = new SpotsDialog.Builder().setContext(ViewImageActivity.this).build();
                    dialog.show();
                    dialog.setMessage("Downloading...");

                    String fileName = UUID.randomUUID().toString()+".jpg";
                    Picasso.with(getBaseContext())
                            .load(photos.get(viewPager.getCurrentItem()).getUrlL())
                            .into(new SaveImageHelper(getBaseContext(), dialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName, "Image description"));
                }

            }
        });

        //
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // xin quyền lưu trữ từ người dùng nếu trước đó họ chưa cấp quyền
                if (ActivityCompat.checkSelfPermission(ViewImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ViewImageActivity.this, "",Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_CODE);
                    return;
                } else {
                    dialog = new SpotsDialog.Builder().setContext(ViewImageActivity.this).build();
                    dialog.show();
                    dialog.setMessage("Downloading...");

                    String fileName2 = UUID.randomUUID().toString()+".png";
                    Picasso.with(getBaseContext()).load(photos.get(viewPager.getCurrentItem()).getUrlL()).into(new SaveImageHelper(getBaseContext(),
                            dialog,
                            getApplicationContext().getContentResolver(),
                            fileName2, "Image description2"));

                }

            }
        });
    }


    private void getData() {
        // thông báo trạng thái khi đợi dữ liệu trả về
        final ProgressDialog loading = new ProgressDialog(ViewImageActivity.this);
        loading.setMessage("Loading...");
        loading.show();
        //RequestQueue: nơi giữ các request trước khi gửi
        //tạo một RequestQueue bằng lệnh
        RequestQueue requestQueue =
                Volley.newRequestQueue(ViewImageActivity.this);
        //StringRequest: kế thừa từ Request, là class đại diện cho request trả về String
        // khai báo stringRepuest, phương thức POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://www.flickr.com/services/rest", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson(); //là một thư viện java giúp chuyển đổi qua lại giữa JSON và Java

                FlickrPhoto flickrPhoto =
                        gson.fromJson(response, FlickrPhoto.class);

                photos = flickrPhoto.getPhotos().getPhoto(); // lấy ảnh từ flick để cho vào list ảnh gắn lên view

                viewpagerAdapter = new ViewpagerAdapter(ViewImageActivity.this, photos); // gắn dữ liệu vào adapter
                link = photos.get(viewPager.getCurrentItem()).getUrlL(); // link ảnh phục vụ cho việc tải
                viewPager.setAdapter(viewpagerAdapter);
                viewPager.setCurrentItem(position, true);
                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        // Gọi khi sự kiện scroll bắt đầu diễn ra và đi tới page đó
                    }

                    @Override
                    public void onPageSelected(int position) {
                        // Được gọi khi một page đã được chọn
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        // Được gọi khi trang thái scol thay đổi
                    }
                });
                viewpagerAdapter.notifyDataSetChanged();
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(ViewImageActivity.this, error.toString(),Toast.LENGTH_LONG).show();
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
        requestQueue.add(stringRequest);
    }

    private void An() {
        fab1.hide();
        fab2.hide();
        tv1.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
    }

    private void Hien() {
        fab1.show();
        fab2.show();
        tv1.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.VISIBLE);
    }

}
