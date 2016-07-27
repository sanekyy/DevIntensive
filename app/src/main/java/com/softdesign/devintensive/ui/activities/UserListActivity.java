package com.softdesign.devintensive.ui.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.ui.fragments.UserListRetainFragment;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.CircleTransform;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends BaseActivity {

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.user_list)
    RecyclerView mRecyclerView;

    MenuItem mSearchItem;

    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private List<User> mUsers;
    private UserListRetainFragment mRetainFragment;
    private String mQuery;

    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Log.d(ConstantManager.TAG_PREFIX, DataManager.getInstance().getPreferencesManager().getAuthToken());
        Log.d(ConstantManager.TAG_PREFIX, DataManager.getInstance().getPreferencesManager().getUserId());

        mDataManager = DataManager.getInstance();

        ButterKnife.bind(this);

        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mHandler = new Handler();

        setupToolbar();
        setupDrawer();
        initDrawerHeaderInfo();
        loadAvatarInDrawable();

        initUserList();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRetainFragment != null) {
            mRetainFragment.setUserList(mUsersAdapter.getUsers());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationView.isShown()) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            setResult(ConstantManager.EXIT_APP_CODE);
            finish();
        }
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData){
        final String userId = userData.getId();

        List<Repository> repositories = new ArrayList<>();
        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()){
            repositories.add(new Repository(repositoryRes, userId));
        }

        return repositories;
    }

    private void loadUsersFromDb() {

        List<User> userList = mDataManager.getUserListFromDb();

        if(userList.size() == 0){
            showSnackbar("Список пользователей не может быть загружен");
        } else{
            // TODO: 19.07.16 поиск по базе
            showUsers(userList);
        }

    }

    private void downloadUsersInDb(){
        showProgress();

        Call<UserListRes> call = mDataManager.getUserListFromNetwork();
        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                hideProgress();
                if(response.code()==200) {

                    List<Repository> allRepositories = new ArrayList<Repository>();
                    List<User> allUsers = new ArrayList<User>();

                    for (UserListRes.UserData userRes : response.body().getData()){
                        allRepositories.addAll(getRepoListFromUserRes(userRes));
                        allUsers.add(new User(userRes));
                    }

                    mRepositoryDao.insertOrReplaceInTx(allRepositories);
                    mUserDao.insertOrReplaceInTx(allUsers);

                    loadUsersFromDb();
                    hideProgress();
                } else {
                    showSnackbar("Список пользователей не может быть получен");
                    Log.e(TAG, "onResponse: " + String.valueOf(response.errorBody().source()));
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                hideProgress();
                showSnackbar("Error UserListReq");
            }
        });
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.user_profile_menu:
                        setResult(ConstantManager.MAIN_ACTIVITY_CODE);
                        finish();
                        break;
                    case R.id.team_menu:
                        break;
                    case R.id.logout_menu:
                        setResult(ConstantManager.LOGOUT_CODE);
                        finish();
                        break;
                    default: showSnackbar(item.getTitle().toString());
                }
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void initDrawerHeaderInfo() {
        ((TextView) mNavigationView.getHeaderView(0).findViewById(R.id.user_name_txt)).setText(DataManager.getInstance().getPreferencesManager().getUserFullName());
        ((TextView) mNavigationView.getHeaderView(0).findViewById(R.id.user_email_txt)).setText(DataManager.getInstance().getPreferencesManager().getUserEmail());
    }

    private void loadAvatarInDrawable() {
        final String userAvatar;

        if(mDataManager.getPreferencesManager().loadUserAvatar().toString().isEmpty()){
            userAvatar="null";
            Log.e(TAG, "loadPhotos: user with name " + mDataManager.getPreferencesManager().getUserFullName() + " has empty avatar");
        } else {
            userAvatar = mDataManager.getPreferencesManager().loadUserAvatar().toString();
        }
        mDataManager.getPicasso()
                .load(userAvatar)
                .fit()
                .centerCrop()
                .transform(new CircleTransform())
                .placeholder(R.drawable.user_bg)
                .error(R.drawable.user_bg)
                .into((ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.avatar_iv), new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, " load from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(userAvatar)
                                .error(R.drawable.user_bg)
                                .placeholder(R.drawable.user_bg)
                                .fit()
                                .centerCrop()
                                .into((ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.avatar_iv), new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "Could not fetch image");
                                    }
                                });
                    }
                });
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        mSearchItem = menu.findItem(R.id.search_action);
        android.widget.SearchView searchView = (android.widget.SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setQueryHint("Введите имя пользователя");
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override

            public boolean onQueryTextChange(String newText) {
                // TODO: 19.07.16 поиск вызываем тут
                showUsersByQuery(newText);
                return false;
            }
        });


        return super.onPrepareOptionsMenu(menu);
    }

    private void showUsers(List<User> users){
        mUsers = users;
        mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int position) {
                UserDTO userDTO = new UserDTO(mUsers.get(position));

                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);

                startActivity(profileIntent);
            }
        });
        mRecyclerView.swapAdapter(mUsersAdapter, false);
        mRetainFragment.setUserList(mUsersAdapter.getUsers());
    }

    private void showUsersByQuery(String query){
        mQuery = query;

        Runnable searchUsers = new Runnable() {
            @Override
            public void run() {
                showUsers(mDataManager.getUserListByName(mQuery));
            }
        };

        mHandler.removeCallbacks(searchUsers);
        if(query.isEmpty()){
            mHandler.postDelayed(searchUsers, 0);
        } else{
            mHandler.postDelayed(searchUsers, AppConfig.SEARCH_DELAY);
        }




    }


    private void initUserList() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        mRetainFragment = (UserListRetainFragment) fm.findFragmentByTag("users_data");
        if (mRetainFragment == null) {
            mRetainFragment = new UserListRetainFragment();
            fm.beginTransaction().add(mRetainFragment, "users_data").commit();
            downloadUsersInDb();
        } else {
            mUsers = mRetainFragment.getUserList();
            mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
                @Override
                public void onUserItemClickListener(int position) {
                    UserDTO userDTO = new UserDTO(mUsers.get(position));

                    Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                    profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);

                    startActivity(profileIntent);
                }
            });
            mRecyclerView.setAdapter(mUsersAdapter);
        }
    }

}
