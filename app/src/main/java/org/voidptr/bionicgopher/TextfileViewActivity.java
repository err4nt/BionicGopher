package org.voidptr.bionicgopher;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TextfileViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textfile_view);

        ((TextView)findViewById(R.id.textView))
                .setMovementMethod(ScrollableMovementMethod.createMovementMethod(this));

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar2));

        Intent intent = getIntent();
        Uri message = Uri.parse(intent.getStringExtra("uri"));

        TextView text = (TextView) findViewById(R.id.textView);
        File textFile = new File(message.getPath());

        try {
            FileInputStream fis = new FileInputStream(textFile);
            StringBuilder builder = new StringBuilder();
            int inchar = fis.read();
            while(inchar != -1) {
                builder.append((char)inchar);
                inchar = fis.read();
            }
            text.setText(builder.toString());
        } catch (FileNotFoundException e) {
            Log.d("TextfileViewActivity", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("TextfileViewActivity", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.textfile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_textfile_menu_item:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
