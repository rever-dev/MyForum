package com.rever.myforum.forum;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.CellIdentityGsm;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rever.myforum.PostAdapter;
import com.rever.myforum.R;
import com.rever.myforum.bean.Post;
import com.rever.myforum.model.PostList;

import java.util.List;

public class ForumFragment extends Fragment {

    private Activity activity;
    private SearchView searchView;
    private RadioGroup radioGroup;
    private RadioButton radioButtonHot, radioButtonNew;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private List<Post> postList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        setPostList();
        postList = PostList.getPostList();
        setRecyclerView();
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
    private void setRecyclerView() {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new PostAdapter(activity,postList));
        } else {
            ((PostAdapter) recyclerView.getAdapter()).update(postList);
        }
    }
}