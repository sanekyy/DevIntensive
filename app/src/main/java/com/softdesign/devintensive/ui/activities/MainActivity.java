package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UploadPhotoRes;
import com.softdesign.devintensive.utils.CircleTransform;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    DataManager mDataManager;
    private int mCurrentEditMode = 0;

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;

    // User Fields
    //private EditText mUserPhone_et, mUserEmail_et, mUserVk_et, mUserGithub_et, mUserBio_et;
    @BindViews({R.id.phone_et, R.id.email_et, R.id.vk_et, R.id.github_et, R.id.bio_et})
    List<EditText> mUserInfoViews;

    // Grey Box
    //private TextView mUserValueRating, mUserValueCodeLines, mUserValueProjects;
    @BindViews({R.id.rating_tv, R.id.code_lines_tv, R.id.projects_tv})
    List<TextView> mUserValueViews;

    // Image for Action
    @BindViews({R.id.call_iv, R.id.sendMessage_iv, R.id.openLinkVK_iv, R.id.openLinkGIT_iv})
    List<ImageView> mUserInfoImageForAction;



    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        Log.d(ConstantManager.TAG_PREFIX, DataManager.getInstance().getPreferencesManager().getAuthToken());
        Log.d(ConstantManager.TAG_PREFIX, DataManager.getInstance().getPreferencesManager().getUserId());

        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        for (ImageView imageView : mUserInfoImageForAction){
            imageView.setOnClickListener(this);
        }
        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValue();
        initDrawerHeaderInfo();
        loadPhotos();

        if (savedInstanceState == null) {
            // start first
        } else {
            // start second and more
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(DataManager.getInstance().getPreferencesManager().getUserFullName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserFields();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
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

    /**
     * Слушаем нажатие на иконки слева, и вызываем интенты.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.call_iv:
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mDataManager.getPreferencesManager().loadUserProfileData().get(0)));
                startActivity(intent);
                break;
            case R.id.sendMessage_iv:
                intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mDataManager.getPreferencesManager().loadUserProfileData().get(1), null));
                startActivity(intent);
                break;
            case R.id.openLinkVK_iv:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mDataManager.getPreferencesManager().loadUserProfileData().get(2)));
                startActivity(intent);
                break;
            case R.id.openLinkGIT_iv:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mDataManager.getPreferencesManager().loadUserProfileData().get(3)));
                startActivity(intent);
                break;
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;
            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            default:
                Log.d(TAG, "DEF");
                return;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.user_profile_menu:
                        break;
                    case R.id.team_menu:
                        setResult(ConstantManager.USER_LIST_ACTIVITY_CODE);
                        finish();
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

    private void loadPhotos() {
        final String userPhoto;
        final String userAvatar;

        if(mDataManager.getPreferencesManager().loadUserPhoto().toString().isEmpty()){
            userPhoto="null";
            Log.e(TAG, "loadPhotos: user with name " + mDataManager.getPreferencesManager().getUserFullName() + " has empty photo");
        } else{
            userPhoto = mDataManager.getPreferencesManager().loadUserPhoto().toString();
        }

        if(mDataManager.getPreferencesManager().loadUserAvatar().toString().isEmpty()){
            userAvatar="null";
            Log.e(TAG, "loadPhotos: user with name " + mDataManager.getPreferencesManager().getUserFullName() + " has empty avatar");
        } else {
            userAvatar = mDataManager.getPreferencesManager().loadUserAvatar().toString();
        }


        mDataManager.getPicasso()
                .load(userPhoto)
                .error(R.drawable.user_bg)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.user_bg)
                .into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        hideProgress();
                    }

                    @Override
                    public void onError() {

                    }
                });

        mDataManager.getPicasso()
                .load(userAvatar)
                .error(R.drawable.user_bg)
                .transform(new CircleTransform())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.user_bg)
                .into((ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.avatar_iv));
    }

    /**
     * Получение результата из другой активити ( фото из камеры или галереи )
     *
     * @param requestCode код активити
     * @param resultCode  код результата - успеха/неуспеха
     * @param data        данные
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    uploadPhoto(getFileFromUri(mSelectedImage));
                    uploadAvatar(getFileFromUri(mSelectedImage));
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    uploadPhoto(mPhotoFile);
                    uploadAvatar(mPhotoFile);
                }
                break;
        }
    }

    private File getFileFromUri(Uri uri){
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return new File(filePath);
    }

    /**
     * Переключаем в режим редактирования/просмотра. Настраиваем вьюхи, сохраняем данные
     * + работа с Toolbar
     *
     * @param mode режим на который нужно переключиться
     */
    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_mode_done_white_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }

            mUserInfoViews.get(0).requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mUserInfoViews.get(0), InputMethodManager.SHOW_IMPLICIT);
            showProfilePlaceholder();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        } else {
            for (EditText userValue : mUserInfoViews) {
                mFab.setImageResource(R.drawable.ic_mode_edit_white_24dp);
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            saveUserFields();
        }
    }

    /**
     * Подгружаем данные пользователя из SharedPreferences во вьюхи
     */
    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохраняем данные пользователя из вьюх в SharedPreferences
     */
    private void saveUserFields() {
        List<String> userData = new ArrayList<>();
        for (EditText userFielView : mUserInfoViews) {
            userData.add(userFielView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void initUserInfoValue() {
        List<String> userValues = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userValues.size(); i++) {
            mUserValueViews.get(i).setText(userValues.get(i));
        }
    }

    /**
     * Если есть права на чтение данных, запускаем интент для выбора фото/картинки из галереи
     * иначе, просим пользователя дать на это права
     */
    private void loadPhotoFromGallery() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            takeGalleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_chose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);


        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
            }, ConstantManager.GALLERY_REQUEST_PERMISSION_CODE);
        }


    }

    /**
     * Если есть права на запись данных на телефон и доступ к камере, запускаем интент/камеруи
     * и сохраняем полученный снимок.
     * иначе, просим пользователя дать необходимые права.
     */
    private void loadPhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 02.07.16 обработать ошибку
            }

            if (mPhotoFile != null) {
                // // TODO: 02.07.16  передать файл для фото в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);
        }
    }

    /**
     * Обработка результата запроса прав.
     *
     * @param requestCode  код запроса
     * @param permissions  запрашиваемые права
     * @param grantResults результаты
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // TODO: 02.07.16 тут обрабатываем разрешение ( разрешение получено ) например вывести сообщение или обработать какой-то логикой если нужно
                // запускаем камеру
            } else {
                Snackbar.make(mCoordinatorLayout, "Для корректной работы необходимо дать требуемуе разрешения", Snackbar.LENGTH_LONG)
                        .setAction("Разрешить", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openApplicationSettings();
                            }
                        }).show();
            }
        }
        if (requestCode == ConstantManager.GALLERY_REQUEST_PERMISSION_CODE && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: 02.07.16 тут обрабатываем разрешение ( разрешение получено ) например вывести сообщение или обработать какой-то логикой если нужно
                // открываем галерею
            } else {
                Snackbar.make(mCoordinatorLayout, "Для корректной работы необходимо дать требуемуе разрешения", Snackbar.LENGTH_LONG)
                        .setAction("Разрешить", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openApplicationSettings();
                            }
                        }).show();
            }
        }

    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     * Блокируем прокрутку Toolbar и делаем его максимально развернутым, при включении режима редактирования, чтобы он не прокручивался,
     * т.к там находится поле для изменения фотографии пользователя
     */
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Разблокируем Toolbar ( включаем скролл ), при выключении режима редактирования
     */
    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Создаём диалог в зависимости от id и выводим его пользователю
     *
     * @param id - идентификатор диалога, который нужно создать
     * @return
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                // TODO: 02.07.16 Загрузить из галлереи
                                loadPhotoFromGallery();
                                //showSnackbar("Загрузить из галлереи");
                                break;
                            case 1:
                                // TODO: 02.07.16 Загрузить из камеры
                                loadPhotoFromCamera();
                                //showSnackbar("Загрузить из камеры");
                                break;
                            case 2:
                                // TODO: 02.07.16 Отмена, вернуться к активити
                                dialog.cancel();
                                //showSnackbar("Отмена, вернуться к активити");
                                break;
                            default:
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    /**
     * Создаём файл для сохранения в него фото пользователя из камеры.
     *
     * @return созданный файл
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    private void uploadPhoto(File file){
        if (!NetworkStatusChecker.isNetworkAvailable(mDataManager.getContext())) {
            showSnackbar(getString(R.string.error_network_not_available));
            return;
        }

        showProgress();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        Call<UploadPhotoRes> call = mDataManager.uploadPhoto(
                mDataManager.getPreferencesManager().getUserId(), body);
        call.enqueue(new retrofit2.Callback<UploadPhotoRes>() {
            @Override
            public void onResponse(Call<UploadPhotoRes> call, Response<UploadPhotoRes> response) {

                if (response.code() == 404) {
                    hideProgress();
                    setResult(ConstantManager.LOGOUT_CODE);
                    finish();
                } else if (response.code() == 200&&response.body().isSuccess()){
                    mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(response.body().getData().getPhoto()));
                    mDataManager.getPicasso().invalidate(mDataManager.getPreferencesManager().loadUserPhoto());
                    loadPhotos();
                    showSnackbar(getString(R.string.photo_uploaded_successfully));
                } else {
                    hideProgress();
                    showSnackbar(getString(R.string.error_all_bad));
                }
            }

            @Override
            public void onFailure(Call<UploadPhotoRes> call, Throwable t) {
                hideProgress();
                    showSnackbar(getString(R.string.error_all_bad));
            }
        });
    }

    private void uploadAvatar(File file){
        if (!NetworkStatusChecker.isNetworkAvailable(MainActivity.this)) {
            showSnackbar(getString(R.string.error_network_not_available));
            return;
        }

        showProgress();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
        Call<UploadPhotoRes> call = mDataManager.uploadAvatar(
                mDataManager.getPreferencesManager().getUserId(), body);
        call.enqueue(new retrofit2.Callback<UploadPhotoRes>() {
            @Override
            public void onResponse(Call<UploadPhotoRes> call, Response<UploadPhotoRes> response) {

                if (response.code() == 404) {
                    hideProgress();
                    setResult(ConstantManager.LOGOUT_CODE);
                    finish();
                } else if (response.code() == 200&&response.body().isSuccess()) {
                    mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(response.body().getData().getPhoto()));
                    mDataManager.getPicasso().invalidate(mDataManager.getPreferencesManager().loadUserAvatar());
                    loadPhotos();
                    showSnackbar(getString(R.string.photo_uploaded_successfully));
                } else{
                    hideProgress();
                    showSnackbar(getString(R.string.error_all_bad));
                }
            }

            @Override
            public void onFailure(Call<UploadPhotoRes> call, Throwable t) {
                hideProgress();
                showSnackbar(getString(R.string.error_all_bad));
            }
        });
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }
}

