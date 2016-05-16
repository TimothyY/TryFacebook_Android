package timothyyudi.tryfacebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;
import java.util.List;

public class SharingActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    List<String> permissionNeeds;
    AccessTokenTracker accessTokenTracker;
    boolean fbUserLoginStatus;
    Button btnLogin, btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sharing);

        callbackManager = CallbackManager.Factory.create();
        permissionNeeds = Arrays.asList("publish_actions");

//        this loginManager helps you eliminate adding a LoginButton to your UI
        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(SharingActivity.this, Profile.getCurrentProfile().getId()+Profile.getCurrentProfile().getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel()
                {
                    System.out.println("onCancel");
                }

            @Override
            public void onError(FacebookException exception)
                {
                    System.out.println("onError");
                }

        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                fbUserLoginStatus = getFbUserLoginStatus(newAccessToken);
            }
        };

        fbUserLoginStatus = getFbUserLoginStatus(AccessToken.getCurrentAccessToken());

        btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(this);
        btnShare = (Button) findViewById(R.id.share);
        btnShare.setOnClickListener(this);

    }

    private boolean getFbUserLoginStatus(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            Log.v("fbLoginStatus","user logged in");
            return true;
        } else {
            Log.v("fbLoginStatus","user not logged in");
            return false;
        }
    }

    private void sharePhotoToFacebook(){
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("This is a test to share text and image to facebook via android programmatically. #test")
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#FacebookAndroidShareDialog")
                        .build()).build();
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.show(content); //need facebook native
        }else{
            //check if user is logged in
            if(fbUserLoginStatus)ShareApi.share(content, null); //do not need facebook native. silent post, but need login first.
            else Toast.makeText(this, "Please login first before sharing a photo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login:
                loginManager.logInWithPublishPermissions(this, permissionNeeds);
                break;
            case R.id.share:
                sharePhotoToFacebook();
                break;
        }
    }

}
