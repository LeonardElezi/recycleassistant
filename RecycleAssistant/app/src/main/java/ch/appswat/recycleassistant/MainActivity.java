package ch.appswat.recycleassistant;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Button scanBtn, takePicBtn, searchManuallyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = (Button)findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(this);

        takePicBtn = (Button)findViewById(R.id.picture_button);
        takePicBtn.setOnClickListener(this);

        searchManuallyBtn = (Button)findViewById(R.id.search_button);
        searchManuallyBtn.setOnClickListener(this);

    }

    public void onClick(View v){
        if(v.getId()==R.id.scan_button){
            Intent intent = new Intent(this, BarcodeActivity.class);
            startActivity(intent);
        } else if(v.getId()==R.id.picture_button){
            Intent intent = new Intent(this, ImageRecognitionActivity.class);
            startActivity(intent);
        } else if(v.getId()==R.id.search_button){
//            Intent intent = new Intent(this, ImageRecognitionActivity.class);
//            startActivity(intent);
        }
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

}
