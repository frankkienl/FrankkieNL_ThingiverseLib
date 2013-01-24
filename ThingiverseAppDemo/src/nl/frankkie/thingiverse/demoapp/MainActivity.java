package nl.frankkie.thingiverse.demoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import nl.frankkie.thingiverse.lib.ThingiverseClient;

public class MainActivity extends Activity {

  public static String clientId = "f21dbb030fb0bed3a132";
  public static String clientSecret = "531908cf06ddbfb09e9406fe0ec6061d";
  public static String clientCallback = "http://frankkie.nl/thingiverse/api_android.php";
  public ThingiverseClient client;
  String accessTokenString;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initUI();
    init();
    //check if normal start, or coming back from browser
    Intent myIntent = getIntent();
    Uri uri = myIntent.getData();
    if (uri != null){ //back from browser
      String browserCode = "";
      //get code from uri
      browserCode = uri.getQuery().substring(5);
      showAlertDialog("Back from browser, browser code is:\n" + browserCode + "\nWe exchange the browser code for a accesToken.\nThen you can make other API-calls");
      accessTokenString = client.loginWithBrowserCode(browserCode);
      saveAccesToken(accessTokenString);
      Toast.makeText(this, "done", Toast.LENGTH_LONG).show();
    } else { //here form launcher
      //nothing to do here
    }
  }

  protected void initUI() {
    setContentView(R.layout.main);
    Button login1 = (Button) findViewById(R.id.btn_login1);
    Button login3 = (Button) findViewById(R.id.btn_login3);
    Button featured = (Button) findViewById(R.id.btn_featured);
    Button newest = (Button) findViewById(R.id.btn_newest);

    login3.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        accessTokenString = getSavedAccesToken();
        if (accessTokenString == null || accessTokenString.equalsIgnoreCase("")) {
          showAlertDialog("No accesToken! login first !");
        }
       Toast.makeText(MainActivity.this, "done", Toast.LENGTH_LONG).show(); 
      }
    });

    login1.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String authUrl = client.loginFirstTime();
        try {
          //start the browser
          Intent i = new Intent(Intent.ACTION_VIEW);
          i.setData(Uri.parse(authUrl));
          startActivity(i);
        } catch (Exception ex) {
          System.err.println("Browser does not work.");
          Toast.makeText(MainActivity.this, "Cannot start Browser", Toast.LENGTH_LONG).show();
          ex.printStackTrace();
        }
      }
    });

    featured.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (accessTokenString == null || accessTokenString.equalsIgnoreCase("")) {
          showAlertDialog("No accesToken! login first !");
        }
        String s = client.featured();
        showAlertDialog("Featured:\n" + s);
      }
    });
    newest.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (accessTokenString == null || accessTokenString.equalsIgnoreCase("")) {
          showAlertDialog("No accesToken! login first !");
        }
        String s = client.newest();
        showAlertDialog("Newest:\n" + s);
      }
    });
  }

  public void showAlertDialog(String message) {
    AlertDialog.Builder b = new AlertDialog.Builder(this);
    b.setTitle("Thingiverse Demo");
    b.setMessage(message);
    b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        //nothing, just remove dialog
      }
    });
    b.create().show();
  }

  private void init() {
    client = new ThingiverseClient(clientId, clientSecret, clientCallback);
  }

  public void saveAccesToken(String accesToken) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    Editor edit = prefs.edit();
    edit.putString("accesToken", accesToken);
    edit.commit();
  }

  public String getSavedAccesToken() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    return prefs.getString("accesToken", "");
  }
}
