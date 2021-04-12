package com.rever.myforum.forum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.rever.myforum.MainActivity;
import com.rever.myforum.R;
import com.rever.myforum.ReplyAdapter;
import com.rever.myforum.bean.Member;
import com.rever.myforum.bean.Post;
import com.rever.myforum.bean.Reply;
import com.rever.myforum.model.MemberBase;
import com.rever.myforum.model.PostBase;
import com.rever.myforum.model.PostList;
import com.rever.myforum.model.ReplyBase;
import com.rever.myforum.model.ReplyList;
import com.rever.myforum.util.mAlertDialog;

import java.sql.Timestamp;
import java.util.List;

public class PostViewFragment extends Fragment {

    private static final String TAG = "PostViewFragment";
    private Activity activity;
    private Bundle bundle;
    private Button buttonReplyBox;
    private CheckBox checkBoxLike, checkBoxFav;
    private ImageView imageViewUserAvatar, imageViewReplyBox;
    private LinearLayout linearLayout;
    private ConstraintLayout replyBox;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textViewTitle, textViewUserNickname, textViewDatetime, textViewContent, textViewReplyBox;
    private EditText editTextReplyBox;

    private Handler uiHandler = new Handler();
    private SharedPreferences shp;
    private Post post;
    private List<Reply> replyList;
    private List<byte[]> imageList;

    private boolean signInStatus;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        activity = getActivity();
        bundle = getArguments();
        shp = MainActivity.getShp(activity);
        signInStatus = shp.getBoolean("signIn", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_post_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        buttonReplyBox = view.findViewById(R.id.replyBox_buttonSubmit);
        checkBoxLike = view.findViewById(R.id.postView_checkBoxLike);
        checkBoxFav = view.findViewById(R.id.postView_checkBoxFav);
        editTextReplyBox = view.findViewById(R.id.replyBox_editTextContent);
        imageViewUserAvatar = view.findViewById(R.id.postView_imageViewUserAvatar);
        imageViewReplyBox = view.findViewById(R.id.replyBox_imageViewMemberAvatar);
        linearLayout = view.findViewById(R.id.postView_linearLayoutPostImage);
        progressBar = view.findViewById(R.id.postView_progressBar);
        recyclerView = view.findViewById(R.id.postView_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        textViewTitle = view.findViewById(R.id.postView_textViewTitle);
        textViewUserNickname = view.findViewById(R.id.postView_textViewUserNickname);
        textViewDatetime = view.findViewById(R.id.postView_textViewDatetime);
        textViewContent = view.findViewById(R.id.postView_textViewContent);
        textViewReplyBox = view.findViewById(R.id.replyBox_textViewMemberNickname);
        replyBox = view.findViewById(R.id.replyBox);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        GetDataThread thread = new GetDataThread();
        thread.start();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int replyId = ((ReplyAdapter) recyclerView.getAdapter()).getReplyId();
        int position = ((ReplyAdapter) recyclerView.getAdapter()).getPosition();
        if (item.getItemId() == R.id.replyMenu_delete) {
            mAlertDialog.createAlertDialog(activity, "確定刪除留言？", "確定", "再想想", (dialog, which) -> {
                ReplyBase.deleteReply(activity, replyId);
                ReplyList.getReplyList().remove(position);
                post.setReplyCount(post.getReplyCount() - 1);
                PostBase.updatePost(activity, post.getId(), post.getReplyCount());
                ((ReplyAdapter) recyclerView.getAdapter()).update(replyList);
            }, (dialog, which) -> dialog.dismiss());
        }
        return super.onContextItemSelected(item);
    }

    private void setListener() {
        /*
         * 按讚監聽設置
         * */
        if (!signInStatus) {
            checkBoxLike.setClickable(false);
        }
        checkBoxLike.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                post.setLikeCount(post.getLikeCount() + 1);
                checkBoxLike.setText(String.valueOf(post.getLikeCount()));
                PostBase.addPostLike(activity, post.getId(), post.getLikeCount());
            } else {
                post.setLikeCount(post.getLikeCount() - 1);
                checkBoxLike.setText(String.valueOf(post.getLikeCount()));
                PostBase.deletePostLike(activity, post.getId(), post.getLikeCount());
            }
        });
        /*
         * 收藏監聽設置
         * */
        if (!signInStatus) {
            checkBoxFav.setClickable(false);
        }
        checkBoxFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                PostBase.addPostFav(activity, post.getId());
            } else {
                PostBase.deletePostFav(activity, post.getId());
            }
        });
        /*
         * ReplyBox button設置
         * */
        buttonReplyBox.setOnClickListener(v -> {
            post.setReplyCount(post.getReplyCount() + 1);
            int replyId = ReplyBase.insertReply(activity, post.getId(),
                    editTextReplyBox.getText().toString());
            PostBase.updatePost(activity, post.getId(), post.getReplyCount());
            replyList.add(new Reply(replyId, MemberBase.getMember().getId(),
                    post.getId(), MemberBase.getMember().getNickname(),
                    editTextReplyBox.getText().toString(), new Timestamp(System.currentTimeMillis()), 0));
            editTextReplyBox.clearFocus();
            editTextReplyBox.setText("");
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            ((ReplyAdapter) recyclerView.getAdapter()).update(replyList);
        });
    }

    class GetDataThread extends Thread {
        @Override
        public void run() {
            PostBase.queryPost(activity, bundle.getInt("postId"));
            post = PostBase.getPost();
            ReplyList.queryReplyList(activity, post.getId());
            replyList = ReplyList.getReplyList();
            imageList = PostBase.getImageList();
            /*
             *檔案下載完成後更新UI
             * */
            Runnable runnable = () -> {
                progressBar.setVisibility(View.VISIBLE);
                Bitmap avatar = MemberBase.getMemberAvatar(activity, MemberBase.getMember().getId());
                if (avatar != null) {
                    Glide.with(activity)
                            .applyDefaultRequestOptions(new RequestOptions().override(100, 100))
                            .load(avatar)
                            .transform(new CircleCrop())
                            .into(imageViewUserAvatar);
                } else {
                    imageViewUserAvatar.setImageResource(R.drawable.account_default_image);
                }
                textViewTitle.setText(post.getTitle());
                textViewUserNickname.setText(post.getMemberNickname());
                textViewDatetime.setText(post.getDatetime());
                textViewContent.setText(post.getContent());
                checkBoxLike.setButtonDrawable(R.drawable.like_selector);
                checkBoxFav.setButtonDrawable(R.drawable.fav_selector);
                checkBoxLike.setText(String.valueOf(post.getLikeCount()));
                checkBoxLike.setChecked(PostBase.checkedLikeStatus(MemberBase.getMember().getId()));
                checkBoxFav.setChecked(PostBase.checkedFavStatus(MemberBase.getMember().getId()));
                if (imageList.size() != 0) {
                    for (byte[] temp : imageList) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                        ImageView imageView = new ImageView(activity);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setImageBitmap(bitmap);
                        imageView.setPadding(0, 20, 0, 0);
                        linearLayout.addView(imageView);
                    }
                }

                if (signInStatus) {
                    replyBox.setVisibility(View.VISIBLE);
                    Bitmap mAvatar = MemberBase.getMemberAvatar();
                    if (mAvatar != null) {
                        Glide.with(activity)
                                .applyDefaultRequestOptions(new RequestOptions().override(100, 100))
                                .load(avatar)
                                .transform(new CircleCrop())
                                .into(imageViewReplyBox);
                    } else {
                        imageViewReplyBox.setImageResource(R.drawable.account_default_image);
                    }
                    textViewReplyBox.setText(MemberBase.getMember().getNickname());
                }

                if (recyclerView.getAdapter() == null) {
                    recyclerView.setAdapter(new ReplyAdapter(activity, replyList, shp));
                } else {
                    ((ReplyAdapter) recyclerView.getAdapter()).update(replyList);
                }
                setListener();
                progressBar.setVisibility(View.GONE);
            };
            uiHandler.post(runnable);
        }
    }

}