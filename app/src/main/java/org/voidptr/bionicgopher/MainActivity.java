package org.voidptr.bionicgopher;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Handler pageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Thread request = new Thread(new Runnable() {
            @Override
            public void run() {
                GopherClient client = new GopherClient();
                try {
                    GopherPage page = client.fetch(Uri.parse("gopher://voidptr.org:70"));
                    Message pageMessage = new Message();
                    pageMessage.obj = page;
                    pageHandler.dispatchMessage(pageMessage);
                    page.getMenu();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        pageHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message pageMessage) {
                GopherPage page = (GopherPage)pageMessage.obj;
                updatePageView(page);
            }
        };

        findViewById(R.id.go_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.start();
            }
        });
    }

    void updatePageView(GopherPage page) {
        for (GopherMenuItem line : page.getMenu()) {
            switch (line.getType()) {
                case INFORMATION:
                    SpannableString.valueOf(line.getTitle());
                    findViewById(R.id.pageText);
                    break;
            }
        }
    }
}
