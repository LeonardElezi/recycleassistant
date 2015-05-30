package ch.appswat.recycleassistant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.Proxy;


public class NewProductActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = NewProductActivity.class.getName();

    private static final String NAMESPACE = "https://my.craftar.net/api/v0/";
    private static final String SOAP_ACTION = "http://clapi.itraff.pl/auth";

    private EditText productName;
    private EditText productDescription;
    private ImageView productImage;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        byte[] data = getIntent().getExtras().getByteArray("bitmap");
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        productName = (EditText) findViewById(R.id.product_name);
        productDescription = (EditText) findViewById(R.id.product_description);
        productImage = (ImageView) findViewById(R.id.productImage);
        productImage.setScaleType(ImageView.ScaleType.FIT_XY);
        productImage.setImageBitmap(bitmap);
        sendBtn = (Button) findViewById(R.id.submit_button);
        sendBtn.setOnClickListener(this);
    }

    public void onClick(View v){
        if(v.getId()==R.id.submit_button){
            // submitting now
        }
    }

    public void submitProductImage(){
        String methodname = "auth";
        SoapObject request = new SoapObject(NAMESPACE, methodname);
        request.addProperty("client_id", ImageRecognitionActivity.RECOGNIZEIM_CLIENT_API_ID);
        request.addProperty("key_clapi", ImageRecognitionActivity.RECOGNIZEIM_CLAPI_KEY);
        request.addProperty("ip", null);
        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        HttpTransportSE ht = getHttpTransportSE();
        try {
            ht.call(SOAP_ACTION, envelope);
            SoapPrimitive resultsString = (SoapPrimitive)envelope.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }



    }

    private final HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,MAIN_REQUEST_URL,60000);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }

    private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_product, menu);
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
