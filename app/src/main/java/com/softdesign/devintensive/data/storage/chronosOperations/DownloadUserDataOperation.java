package com.softdesign.devintensive.data.storage.chronosOperations;

import android.support.annotation.NonNull;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by ihb on 20.07.16.
 */

public class DownloadUserDataOperation extends ChronosOperation<String> {
    private Response<UserListRes> mResponse;

    public DownloadUserDataOperation(Response<UserListRes> response) {
        mResponse = response;
    }

    @Nullable
    @Override
    public String run() {
        UserDao userDao = DataManager.getInstance().getDaoSession().getUserDao();
        RepositoryDao repositoryDao = DataManager.getInstance().getDaoSession().getRepositoryDao();
        List<Repository> allRepositories = new ArrayList<>();
        List<User> allUsers = new ArrayList<>();

        for (UserListRes.UserData userRes : mResponse.body().getData()) {
            allRepositories.addAll(getRepoListFromUserRes(userRes));
            allUsers.add(new User(userRes));
        }

        repositoryDao.insertOrReplaceInTx(allRepositories);
        userDao.insertOrReplaceInTx(allUsers);

        return null;
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();

        List<Repository> repositories = new ArrayList<>();
        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }

        return repositories;
    }

    @NonNull
    @Override
    public Class<Result> getResultClass() {
        return Result.class;
    }

    public static final class Result extends ChronosOperationResult<String> {

    }
}