package ch.appswat.recycleassistant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.ByteArrayOutputStream;

import pl.itraff.TestApi.ItraffApi.ItraffApi;


public class ImageRecognitionActivity extends ActionBarActivity implements View.OnClickListener{

    private static final String TAG = ImageRecognitionActivity.class.getName();

    public static final String RECOGNIZEIM_CLIENT_API_KEY = "6d971b54d3";
    public static final Integer RECOGNIZEIM_CLIENT_API_ID = 44566;
    public static final String RECOGNIZEIM_CLAPI_KEY = "34fe06608b4946f5cd852a03ec719c93";

    private TextView responseView;
    private Button takePicAgainBtn, submitProductToServer;

    // minimum size of picture to scale
    final int REQUIRED_SIZE = 400;
    private static final int RESULT_BMP_DAMAGED = 128;

    private byte[] pictureData;



    ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition);

        responseView = (TextView) findViewById(R.id.take_pic_format);
        takePicAgainBtn = (Button) findViewById(R.id.take_pic_again_button);
        takePicAgainBtn.setOnClickListener(this);
        submitProductToServer = (Button) findViewById(R.id.insert_pic_to_server);
        submitProductToServer.setVisibility(View.INVISIBLE);
        submitProductToServer.setOnClickListener(this);

    }

    public void onClick(View v){
        if(v.getId()==R.id.take_pic_again_button){
            makePhotoClick();
        } else if (v.getId()==R.id.insert_pic_to_server){
            Intent i = new Intent(this, NewProductActivity.class);
            i.putExtra("bitmap", pictureData);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }
    }

    public void makePhotoClick() {

            // Intent to take a photo
            Intent takePictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, true);
            takePictureIntent.putExtra(MediaStore.EXTRA_SHOW_ACTION_ICONS,
                    false);
            startActivityForResult(takePictureIntent, 1234);
    }

    // handler that receives response from api
    @SuppressLint("HandlerLeak")
    private Handler itraffApiHandler = new Handler() {
        // callback from api
        @Override
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            Bundle data = msg.getData();
            if (data != null) {
                Integer status = data.getInt(ItraffApi.STATUS, -1);
                String response = data.getString(ItraffApi.RESPONSE);
                JsonParser jsonParser = new JsonParser();
                JsonObject jo = (JsonObject)jsonParser.parse(response);
                // status ok
                if (status == 0) {
                    responseView.setText(response);
                    // application error (for example timeout)
                } else if (status == -1) {
                    Log.d(TAG, "status = -1");
                    responseView.setText(getResources().getString(
                            R.string.app_error));
                    // error from api
                } else {
                    responseView.setText(jo.get("message").getAsString() + response);
                    submitProductToServer.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult reqcode, resultcode: " + requestCode + "  "
                + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Activity.RESULT_OK");
            if (data != null) {
                Log.d(TAG, "data != null");
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Log.d(TAG, "bundle != null");

                    // byte[] pictureData = bundle.getByteArray("pictureData");
                    Bitmap image = (Bitmap) bundle.get("data");
                    if (image != null) {
                        Log.d(TAG, "image != null");

                        // chceck internet connection
                        if (ItraffApi.isOnline(getApplicationContext())) {
                            showWaitDialog();
                            // send photo
                            ItraffApi api = new ItraffApi(RECOGNIZEIM_CLIENT_API_ID,
                                    RECOGNIZEIM_CLIENT_API_KEY, TAG, true);
                            Log.v("KEY", RECOGNIZEIM_CLAPI_KEY);
                            api.setMode(ItraffApi.MODE_SINGLE);
//                            if (prefs.getString("mode", "single").equals(
//                                    "multi")) {
//                                api.setMode(ItraffApi.MODE_MULTI);
//                            } else {
//                                api.setMode(ItraffApi.MODE_SINGLE);
//                            }

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG, 100,
                                    stream);
                            pictureData = stream.toByteArray();
                            api.sendPhoto(pictureData, itraffApiHandler,
                                    true);
                        } else {
                            // show message: no internet connection
                            // available.

                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources().getString(
                                            R.string.connection_error),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        } else if (resultCode == RESULT_BMP_DAMAGED) {
            Log.d(TAG, "RESULT_BMP_DAMAGEDl");
        }
    }

    private void showWaitDialog() {
        if (waitDialog != null) {
            if (!waitDialog.isShowing()) {
                waitDialog.show();
            }
        } else {
            waitDialog = new ProgressDialog(this);
            waitDialog.setMessage(getResources().getString(
                    R.string.wait_message));
            waitDialog.show();
        }
    }

    private void dismissWaitDialog() {
        try {
            if (waitDialog != null && waitDialog.isShowing()) {
                waitDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
