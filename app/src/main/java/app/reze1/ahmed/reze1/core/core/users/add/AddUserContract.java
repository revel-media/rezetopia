package app.reze1.ahmed.reze1.core.core.users.add;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;


public interface AddUserContract {
    interface View {
        //todo
        void onAddUserSuccess(String message);

        void onAddUserFailure(String message);
    }

    interface Presenter {
        void addUser(Context context, FirebaseUser firebaseUser);
    }

    interface Interactor {
        void addUserToDatabase(Context context, FirebaseUser firebaseUser);
    }

    interface OnUserDatabaseListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
