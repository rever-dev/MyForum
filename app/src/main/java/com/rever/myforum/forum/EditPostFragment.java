package com.rever.myforum.forum;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.rever.myforum.MainActivity;
import com.rever.myforum.R;
import com.rever.myforum.ReplyAdapter;
import com.rever.myforum.bean.Post;
import com.rever.myforum.model.MemberBase;
import com.rever.myforum.model.PostBase;
import com.rever.myforum.model.ReplyList;
import com.rever.myforum.util.mAlertDialog;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class EditPostFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CreatePostFragment";
    private Activity activity;
    private Button buttonSubmit, buttonAddImage, buttonCancel;
    private Bundle bundle;
    private EditText editTextTitle, editTextContent;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView textViewAddImage;
    private Spinner spinner;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private Uri contentUri;

    private Handler uiHandler = new Handler();
    private Post post;
    private List<byte[]> imageList;

    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    private final List<byte[]> IMAGE_LIST = new ArrayList<>();
    private int imageCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bundle = getArguments();
        alertDialogBuilder = new AlertDialog.Builder(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_write_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonSubmit = view.findViewById(R.id.writePost_buttonSubmit);
        buttonAddImage = view.findViewById(R.id.writePost_buttonAddImage);
        buttonCancel = view.findViewById(R.id.writePost_buttonCancel);
        editTextTitle = view.findViewById(R.id.writePost_editTextTitle);
        editTextContent = view.findViewById(R.id.writePost_editTextContent);
        linearLayout = view.findViewById(R.id.writePost_linearLayout);
        imageView = view.findViewById(R.id.writePost_imageView);
        progressBar = view.findViewById(R.id.writePost_progressBar);
        textViewAddImage = view.findViewById(R.id.writePost_textViewAddImage);
        spinner = view.findViewById(R.id.writePost_spinner);

        buttonSubmit.setOnClickListener(this);
        buttonAddImage.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        textViewAddImage.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        GetDataThread thread = new GetDataThread();
        thread.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        alertDialog.dismiss();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PICTURE:
                    handleCropResult(intent);
                    break;
            }
        }
    }

    public void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        UCrop.of(sourceImageUri, destinationUri)
                .withAspectRatio(1, 1) // 設定裁減比例
//                .withMaxResultSize(480, 270) // 設定結果尺寸不可超過指定寬高
                .start(activity, this, REQ_CROP_PICTURE);
    }

    public void handleCropResult(Intent intent) {
        Uri resultUri = UCrop.getOutput(intent);
        if (resultUri == null) {
            return;
        }
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmap = BitmapFactory.decodeStream(
                        activity.getContentResolver().openInputStream(resultUri));
            } else {
                ImageDecoder.Source source =
                        ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            IMAGE_LIST.add(out.toByteArray());
        } catch (IOException e) {
            Log.e(TAG, "openInputStream()/decodeBitmap(): " + e.toString());
        }
        if (bitmap != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    500, 500);
            layoutParams.gravity = Gravity.CENTER;
            ImageView imageView = new ImageView(activity);
            imageView.setMaxWidth(500);
            imageView.setMaxHeight(500);
            imageView.setPadding(0, 0, 20, 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageBitmap(bitmap);
            imageView.setTag(imageCount);
            imageView.setOnLongClickListener(v -> {
                mAlertDialog.createAlertDialog(activity, "是否刪除圖片？", "是", "否",
                        (dialog, which) -> {
                            IMAGE_LIST.set(Integer.parseInt(imageView.getTag().toString()), null);
                            linearLayout.removeView(imageView);
                            setLinearLayout();
                        }, (dialog, which) -> dialog.dismiss());
                return false;
            });
            linearLayout.addView(imageView);
            setLinearLayout();
            imageCount++;
        } else {
            setLinearLayout();
        }
    }

    private void setLinearLayout() {
        if (linearLayout.getChildCount() == 2) {
            Log.d(TAG, "setLinearLayout: " + linearLayout.getChildCount());
            imageView.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
            imageView.setVisibility(View.VISIBLE);
            textViewAddImage.setVisibility(View.VISIBLE);
            textViewAddImage.setText(R.string.textView_addImage);
        } else {
            Log.d(TAG, "setLinearLayout: " + linearLayout.getChildCount());
            imageView.setVisibility(View.GONE);
            textViewAddImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.writePost_buttonSubmit) {
            PostBase.updatePost(activity, post.getId(), editTextTitle.getText().toString(),
                    editTextContent.getText().toString(),
                    spinner.getSelectedItem().toString(),
                    imageList);
            Toast.makeText(activity, R.string.toast_updatePostSuccess, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).popBackStack();
        } else if (itemId == R.id.writePost_buttonCancel) {
            Navigation.findNavController(v).popBackStack();
        } else if (itemId == R.id.writePost_imageView ||
                itemId == R.id.writePost_textViewAddImage ||
                itemId == R.id.writePost_buttonAddImage) {
            View dialogView = View.inflate(activity, R.layout.dialog_choose_picture, null);
            // 拍照
            dialogView.findViewById(R.id.dialog_buttonTakePicture).setOnClickListener(n -> {

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA}, 1);
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定存檔路徑
                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                contentUri = FileProvider.getUriForFile(
                        activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                try {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(activity, R.string.toast_noCameraApp, Toast.LENGTH_SHORT).show();
                }
            });
            // 從相簿挑選
            dialogView.findViewById(R.id.dialog_buttonPickPicture).setOnClickListener(n -> {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_PICTURE);
            });
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setTitle("選擇加入圖片的方式");
            alertDialog = alertDialogBuilder.show();
        }
    }

    class GetDataThread extends Thread {
        @Override
        public void run() {
            PostBase.queryPost(activity, bundle.getInt("postId"));
            post = PostBase.getPost();
            imageList = PostBase.getImageList();
            /*
             *檔案下載完成後更新UI
             * */
            Runnable runnable = () -> {
                int spinnerPosition;
                String postType = post.getType();
                switch (postType) {
                    case "政治":
                        spinnerPosition = 0;
                        break;
                    case "3Ｃ":
                        spinnerPosition = 1;
                        break;
                    case "生活":
                        spinnerPosition = 2;
                        break;
                    case "閒聊":
                        spinnerPosition = 3;
                        break;
                    case "汽車":
                        spinnerPosition = 4;
                        break;
                    default:
                        spinnerPosition = 0;
                }
                editTextTitle.setText(post.getTitle());
                editTextContent.setText(post.getContent());
                spinner.setSelection(spinnerPosition);
                if (imageList.size() != 0) {
                    for (byte[] temp : imageList) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                500, 500);
                        layoutParams.gravity = Gravity.CENTER;
                        ImageView imageView = new ImageView(activity);
                        imageView.setMaxWidth(500);
                        imageView.setMaxHeight(500);
                        imageView.setPadding(0, 0, 20, 0);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setImageBitmap(bitmap);
                        imageView.setTag(imageCount);
                        imageView.setOnLongClickListener(v -> {
                            mAlertDialog.createAlertDialog(activity, "是否刪除圖片？", "是", "否",
                                    (dialog, which) -> {
                                        IMAGE_LIST.set(Integer.parseInt(imageView.getTag().toString()), null);
                                        linearLayout.removeView(imageView);
                                        setLinearLayout();
                                    }, (dialog, which) -> dialog.dismiss());
                            return false;
                        });
                        linearLayout.addView(imageView);
                    }
                }
                setLinearLayout();
                progressBar.setVisibility(View.GONE);
            };
            uiHandler.post(runnable);
        }
    }
}