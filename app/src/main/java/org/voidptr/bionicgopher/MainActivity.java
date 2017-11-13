package org.voidptr.bionicgopher;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Handler pageHandler;
    private Uri url;
    private final PathStack stack = new PathStack();

    private final Thread fetchThread = new Thread(new Runnable() {
        @Override
        public void run() {
            GopherClient client = new GopherClient();
            try {
                final GopherPage page = client.fetch(url);
                Message pageMessage = pageHandler.obtainMessage();
                pageMessage.obj = page;
                pageHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updatePageView(page);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView)findViewById(R.id.pageText)).setMovementMethod(LinkMovementMethod.getInstance());

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
                stack.clear();
                url = Uri.parse(((TextView)findViewById(R.id.addressText)).getText().toString());
                loadPage();
            }
        });

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stack.pop();
                loadPage();
            }
        });
    }

    void loadPage() {
        Uri.Builder builder = url.buildUpon();
        builder.path(stack.getPath());
        if (url.getScheme() == null) {
            builder.scheme("gopher");
        }
        url = builder.build();
        ((TextView)findViewById(R.id.addressText)).setText(url.toString());
        fetchThread.start();
    }

    void updatePageView(GopherPage page) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (final GopherMenuItem line : page.getMenu()) {
            switch (line.getType()) {
                case INFORMATION:
                    builder.append(line.getTitle());
                    break;
                case GOPHER_SUBMENU:
                    int index = builder.length();
                    builder.append(line.getTitle());
                    builder.setSpan(new ClickableSpan(){
                        @Override
                        public void onClick(View widget) {
                            stack.push(line.getPath());
                            loadPage();
                        }
                    }, index, index+line.getTitle().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            builder.append("\r\n");
        }
        ((TextView)findViewById(R.id.pageText)).setText(builder);
    }
}
