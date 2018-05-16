package app.reze1.ahmed.reze1.core.users.add;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;


public class AddUserPresenter implements app.reze1.ahmed.reze1.core.users.add.AddUserContract.Presenter, app.reze1.ahmed.reze1.core.users.add.AddUserContract.OnUserDatabaseListener {
    private app.reze1.ahmed.reze1.core.users.add.AddUserContract.View mView;
    private AddUserInteractor mAddUserInteractor;

    public AddUserPresenter(app.reze1.ahmed.reze1.core.users.add.AddUserContract.View view) {
        this.mView = view;
        mAddUserInteractor = new AddUserInteractor(this);
    }

    @Override
    public void addUser(Context context, FirebaseUser firebaseUser) {
        mAddUserInteractor.addUserToDatabase(context, firebaseUser);
    }

    @Override
    public void onSuccess(String message) {
        mView.onAddUserSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        mView.onAddUserFailure(message);
    }
}
