package entidades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.carlos.biketrip.CrearPuntoEmpresa;
import com.example.carlos.biketrip.MenuPrincipal;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by carlos on 18/11/17.
 */

public class FacebookShare {

    Context context;
    Activity activity;
    SharePhoto photo;
    SharePhotoContent content;
    ShareDialog shareDialog;
    CallbackManager callbackManager;

    String message;

    public FacebookShare(Activity act, Context con, CallbackManager call, String me){
        activity = act;
        context = con;
        shareDialog = new ShareDialog(activity);
        callbackManager = call;
        message = me;
    }


    public void sharePhoto(Bitmap image){

        if(ShareDialog.canShow(SharePhotoContent.class)){

            try{

                photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .build();
                content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                shareDialog.show(content);


                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

                    @Override
                    public void onSuccess(Sharer.Result result) {

                        Toast.makeText(context,
                                message, Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(context, MenuPrincipal.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }

                    @Override
                    public void onCancel() {

                        Toast.makeText(context,
                                message,
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, MenuPrincipal.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(context,
                                "Error inesperado al compartir",
                                Toast.LENGTH_SHORT).show();
                    }


                });

            }catch (Exception e){Log.i("ERROR_SHARING",e.toString());}

        }

    }

}
