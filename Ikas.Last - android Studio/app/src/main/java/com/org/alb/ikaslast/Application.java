package com.org.alb.ikaslast;

import com.org.alb.ikaslast.logger.Log;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class Application extends android.app.Application {

    private static final String TAG ="Application class" ;

    public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

	// clase para inicializar el parse sdk, aqui solo hago eso, en vez de meterlo en el oncreate

      try {
          Parse.initialize(this, "OLlVYEqXnaVNQRb8ve3SlcnuJKNoCWlrw3cc7JQs", "KmPxcSPgKlSzIA2zafX5h7IkWRnNHts5BQKITcFX");

          // Specify an Activity to handle all pushes by default.
          PushService.setDefaultPushCallback(this, MainActivity.class);
          ParseInstallation.getCurrentInstallation().saveInBackground();
          ParseUser.enableAutomaticUser();

      } catch (Exception e) {
          Log.i(TAG, "ERROR en parse inicalizacion " + e.getMessage());
      }
  }
}