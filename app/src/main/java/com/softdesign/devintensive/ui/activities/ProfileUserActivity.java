package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileUserActivity extends AppCompatActivity {

    private static final String TAG = ConstantManager.TAG_PREFIX + " ProfileUserActivit";

    private Toolbar mToolbar;
    private ImageView mProfileImage;
    private EditText mUserBio;
    private TextView mUserRating, mUserCodeLines, mUserProjects;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private CoordinatorLayout mCoordinatorLayout;

    ListView mRepoListView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);
        mUserBio = (EditText) findViewById(R.id.bio_et);
        mUserRating = (TextView) findViewById(R.id.rating_tv);
        mUserCodeLines = (TextView) findViewById(R.id.code_lines_tv);
        mUserProjects = (TextView) findViewById(R.id.projects_tv);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);

        mRepoListView = (ListView) findViewById(R.id.repositories_list);

        setupToolbar();
        initProfileData();

    }

    private void setupToolbar(){
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initProfileData(){
        final UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final List<String> repositories = userDTO.getRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(repositories, this);
        mRepoListView.setAdapter(repositoriesAdapter);
        mRepoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + repositories.get(position)));
                startActivity(gitIntent);
            }
        });
        int baseListViewHeight = getResources().getDimensionPixelSize(R.dimen.size_large_72);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRepoListView.getLayoutParams();
        lp.height = baseListViewHeight * repositories.size();
        mRepoListView.setLayoutParams(lp);

        mUserBio.setText(userDTO.getBio());
        mUserRating.setText(userDTO.getRating());
        mUserCodeLines.setText(userDTO.getCodeLines());
        mUserProjects.setText(userDTO.getProjects());

        mCollapsingToolbarLayout.setTitle(userDTO.getFullName());

        DataManager.getInstance().getPicasso()
                .load(userDTO.getPhoto())
                .error(R.drawable.user_bg)
                .placeholder(R.drawable.user_bg)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, " load from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(userDTO.getPhoto())
                                .error(R.drawable.user_bg)
                                .placeholder(R.drawable.user_bg)
                                .fit()
                                .centerCrop()
                                .into(mProfileImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, " Could not fetch image");
                                    }
                                });
                    }
                });
    }
}
