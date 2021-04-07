package com.rever.myforum.member;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.rever.myforum.R;
import com.rever.myforum.model.MemberBase;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class EditMemberDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "EditMemberDetail";
    private Activity activity;
    private Button buttonEditAvatar, buttonSubmit, buttonCancel;
    private EditText editTextNickname, editTextOldPassword, editTextNewPassword, editTextPasswordAgain;
    private ImageView imageViewMemberAvatar;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private Uri contentUri;

    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    private byte[] image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        alertDialogBuilder = new AlertDialog.Builder(activity);
        MemberBase.getMemberDetail(activity, MemberBase.getMember().getAccount());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_member_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonEditAvatar = view.findViewById(R.id.editMemberDetail_buttonEditAvatar);
        buttonSubmit = view.findViewById(R.id.editMemberDetail_buttonSubmit);
        buttonCancel = view.findViewById(R.id.editMemberDetail_buttonCancel);
        imageViewMemberAvatar = view.findViewById(R.id.editMemberDetail_imageViewMemberAvatar);
        editTextNickname = view.findViewById(R.id.editMemberDetail_editTextNickname);
        editTextOldPassword = view.findViewById(R.id.editMemberDetail_editTextOldPassword);
        editTextNewPassword = view.findViewById(R.id.editMemberDetail_editTextNewPassword);
        editTextPasswordAgain = view.findViewById(R.id.editMemberDetail_editTextPasswordAgain);
        /* *
         * 判斷頭像是否為null
         * */
        if (MemberBase.getMemberAvatar() == null) {
            imageViewMemberAvatar.setImageResource(R.drawable.account_default_image);
        } else {
            Glide.with(this)
                    .applyDefaultRequestOptions(new RequestOptions().override(120, 120))
                    .load(MemberBase.getMemberAvatar())
                    .transform(new CircleCrop())
                    .into(imageViewMemberAvatar);
        }

        editTextNickname.setText(MemberBase.getMember().getNickname());
        buttonEditAvatar.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

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

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        UCrop.of(sourceImageUri, destinationUri)
//                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(480, 270) // 設定結果尺寸不可超過指定寬高
                .start(activity, this, REQ_CROP_PICTURE);
    }

    private void handleCropResult(Intent intent) {
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
            image = out.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, "openInputStream()/decodeBitmap(): " + e.toString());
        }

        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions().override(120, 120))
                .load(image)
                .transform(new CircleCrop())
                .into(imageViewMemberAvatar);

    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        /* *
         * 修改頭像 button
         * */
        if (itemId == R.id.editMemberDetail_buttonEditAvatar) {
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
            /* *
             * 提交 button
             * */
        } else if (itemId == R.id.editMemberDetail_buttonSubmit) {
            boolean checkResult = true;
            /* 檢查暱稱 */
            if (editTextNickname.getText().toString().trim().isEmpty()) {
                editTextNickname.setError("無效暱稱");
                checkResult = false;
            }
            /* 檢查是否與舊密碼相符 */
            if (!editTextOldPassword.getText().toString().trim()
                    .equals(MemberBase.getMember().getPassword())) {
                editTextOldPassword.setError("與舊密碼不符");
                checkResult = false;
            }
            /* 檢查新密碼 必須大於6字元 */
            if (editTextNewPassword.getText().toString().trim().isEmpty() ||
                    editTextNewPassword.getText().length() < 6) {
                editTextNewPassword.setError("無效密碼");
                checkResult = false;
            }
            /* 檢查再次輸入密碼 必須與新密碼一致 */
            if (editTextPasswordAgain.getText().toString().trim().isEmpty() ||
                    !editTextPasswordAgain.getText().toString().trim().equals(editTextNewPassword.getText().toString().trim())) {
                editTextPasswordAgain.setError("與新密碼不符");
                checkResult = false;
            }
            if (checkResult) {
                if (image != null) {
                    MemberBase.updateMemberDetail(activity,
                            editTextNickname.getText().toString(),
                            editTextNewPassword.getText().toString(),
                            image);
                    Navigation.findNavController(v).popBackStack();
                } else {
                    MemberBase.updateMemberDetail(activity,
                            editTextNickname.getText().toString(),
                            editTextNewPassword.getText().toString());
                    Navigation.findNavController(v).popBackStack();
                }
            }
            /* *
             * 取消 button
             * */
        } else if (itemId == R.id.editMemberDetail_buttonCancel) {
            Navigation.findNavController(v).popBackStack();
        }
    }
}