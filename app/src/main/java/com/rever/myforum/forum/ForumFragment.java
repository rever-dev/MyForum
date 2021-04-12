package com.rever.myforum.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rever.myforum.MainActivity;
import com.rever.myforum.PostAdapter;
import com.rever.myforum.R;
import com.rever.myforum.bean.Post;
import com.rever.myforum.model.PostBase;
import com.rever.myforum.model.PostList;
import com.rever.myforum.util.mAlertDialog;

import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment {

    private Activity activity;
    private SearchView searchView;
    private RadioGroup radioGroup;
    private RadioButton radioButtonHot, radioButtonNew;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private SharedPreferences shp;
    private List<Post> postList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        shp = MainActivity.getShp(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.forum_searchView);
        radioGroup = view.findViewById(R.id.forum_radioGroup);
        radioButtonHot = view.findViewById(R.id.forum_radioButtonHot);
        radioButtonNew = view.findViewById(R.id.forum_radioButtonNew);
        spinner = view.findViewById(R.id.forum_spinnerType);
        recyclerView = view.findViewById(R.id.forum_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        floatingActionButton = view.findViewById(R.id.forum_floatingActionButton);

        if (!shp.getBoolean("signIn", false)) {
            floatingActionButton.setVisibility(View.GONE);
        }
        floatingActionButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.createPostFragment));

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            setPostList();
            setRecyclerView(PostList.getPostList());
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    setRecyclerView(postList);
                } else {
                    List<Post> searchPost = new ArrayList<>();
                    for (Post post : postList) {
                        if (post.getTitle().toUpperCase().contains(newText.toUpperCase())) {
                            searchPost.add(post);
                        }
                    }
                    ((PostAdapter) recyclerView.getAdapter()).update(searchPost);
                }
                return true;
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setPostList();
                setRecyclerView(PostList.getPostList());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setPostList();
        postList = PostList.getPostList();
        setRecyclerView(PostList.getPostList());
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int postId = ((PostAdapter) recyclerView.getAdapter()).getPostId();
        int position = ((PostAdapter) recyclerView.getAdapter()).getPosition();
        if (item.getItemId() == R.id.postMenu_edit) {
            Bundle bundle = new Bundle();
            bundle.putInt("postId", postId);
            Navigation.findNavController(getView()).navigate(R.id.editPostFragment, bundle);
        } else if (item.getItemId() == R.id.postMenu_delete) {
            mAlertDialog.createAlertDialog(activity, "確定刪除文章？", "確定", "再想想", (dialog, which) -> {
                PostBase.deletePost(activity, postId);
                PostList.getPostList().remove(position);
                setRecyclerView(PostList.getPostList());
            }, (dialog, which) -> dialog.dismiss());
        } else if (item.getItemId() == R.id.postMenu_fav) {
            PostBase.addPostFav(activity, postId);
            setPostList();
            setRecyclerView(PostList.getPostList());
        } else if (item.getItemId() == R.id.postMenu_unFav) {
            PostBase.deletePostFav(activity, postId);
            setPostList();
            setRecyclerView(PostList.getPostList());
        }
        return super.onContextItemSelected(item);
    }

    private void setPostList() {
        /*
         * 取得spinner type 分類, radioGroup sort 排序
         * */
        String type = spinner.getSelectedItem().toString();
        String sort =
                ((RadioButton) View.inflate(activity, R.layout.fragment_forum, null)
                        .findViewById(radioGroup.getCheckedRadioButtonId()))
                        .getText().toString();
        PostList.queryPostListByType(activity, type, sort);
    }

    /*
     * 設置recyclerView
     * */
    private void setRecyclerView(List<Post> postList) {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new PostAdapter(activity, postList));
        } else {
            ((PostAdapter) recyclerView.getAdapter()).update(postList);
        }
    }
}