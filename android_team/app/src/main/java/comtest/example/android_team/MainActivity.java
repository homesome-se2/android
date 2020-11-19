package comtest.example.android_team;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import comtest.example.android_team.voiceSystem.ResourceHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Info";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ResourceHelper.resources == null) {
            ResourceHelper.resources = getResources();
        }

        Log.i(TAG, "MainActivity: In the onCreate() event");

     /*   Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); */


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "MainActivity: In the onRestart() event");
    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "MainActivity: In the onResume() event");
    }

    protected void onPause() {
        super.onPause();
        Log.i(TAG, "MainActivity: In the onPause() event");
    }

    protected void onStop() {
        super.onStop();
        Log.i(TAG, "MainActivity: In the onStop() event");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity: In the onDestroy() event");
        if (AppManager.getInstance().networkNotNull()) {
            AppManager.getInstance().endConnection();
        }
    }
}