package org.voidptr.bionicgopher;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import org.voidptr.bionicgopher.model.GopherMenuItem;
import org.voidptr.bionicgopher.model.GopherPage;
import org.voidptr.bionicgopher.model.GopherUri;
import org.voidptr.bionicgopher.model.PathStack;
import org.voidptr.bionicgopher.network.GopherClient;

import java.io.File;
import java.io.IOException;

import static android.support.v4.content.FileProvider.getUriForFile;

public class MainActivity extends AppCompatActivity {
    private Handler pageHandler;
    private GopherUri downloadTarget;
    private GopherPage currentPage;
    private PathStack stack;

    private final Thread fetchThread = new Thread(new Runnable() {
        @Override
        public void run() {
            GopherClient client = new GopherClient();
            try {
                final GopherPage page = client.fetch(stack.getCurrent());
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

    private final Thread gifThread = new Thread(new Runnable() {
        @Override
        public void run() {
            if(downloadTarget == null) {
                return;
            }
            GopherClient client = new GopherClient();
            try {
                File tempDir = getExternalCacheDir();
                File tempFile = File.createTempFile("bg_", ".gif", tempDir);
                client.downloadTemporary(tempFile, downloadTarget);

                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                Uri contentUri = getUriForFile(MainActivity.this,
                        "org.voidptr.fileprovider",
                        tempFile);
                openIntent.setDataAndType(contentUri, "image/gif");
                openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(openIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    private final Thread textfileThread = new Thread(new Runnable() {
        @Override
        public void run() {
            if(downloadTarget == null) {
                return;
            }
            GopherClient client = new GopherClient();
            try {
                File tempDir = getCacheDir();
                File tempFile = File.createTempFile("bg_", ".txt", tempDir);
                client.downloadTemporary(tempFile, downloadTarget);

                Intent openIntent = new Intent(MainActivity.this,
                        TextfileViewActivity.class);
                openIntent.putExtra("uri", tempFile.toURI().toString());
                startActivity(openIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView)findViewById(R.id.pageText))
                .setMovementMethod(ScrollableMovementMethod.createMovementMethod(this));

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        pageHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message pageMessage) {
                GopherPage page = (GopherPage)pageMessage.obj;
                updatePageView(page);
            }
        };

        ((TextView)findViewById(R.id.addressText)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( i == EditorInfo.IME_ACTION_GO) {
                    GopherUri newUri = new GopherUri(((TextView)findViewById(R.id.addressText))
                            .getText()
                            .toString());
                    stack.push(newUri);
                    loadPage();
                }
                return false;
            }
        });

        stack = new PathStack();

        if(savedInstanceState != null){
            stack.fromBundle(savedInstanceState.getBundle("stack"));
            updatePageView(GopherPage.fromBundle(savedInstanceState.getBundle("page")));
        }else {
            GopherUri startPage = new GopherUri("gopher", "voidptr.org", 70);
            stack.push(startPage);
            loadPage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.back_menu_item:
                stack.back();
                loadPage();
                return true;
            case R.id.settings_menu_item:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(stack.havePrevious()) {
                stack.back();
                loadPage();
                return true;
            }else{
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBundle("stack", stack.toBundle());
        outState.putBundle("page", currentPage.toBundle());
    }

    void loadPage() {
        GopherUri uri = stack.getCurrent();
        if(uri != null) {
            if (uri.getProtocol() == null) {
                uri.setProtocol("gopher");
            }
            if (uri.getPort() == null) {
                uri.setPort(70);
            }
            ((TextView) findViewById(R.id.addressText)).setText(uri.toString());
            fetchThread.start();
        }
    }

    void updatePageView(GopherPage page) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        findViewById(R.id.pageText).scrollTo(0, 0);
        for (final GopherMenuItem line : page.getMenu()) {
            int index = builder.length();
            switch (line.getType()) {
                case INFORMATION:
                    builder.append(line.getTitle());
                    break;
                case GOPHER_SUBMENU:
                    builder.append(line.getTitle());
                    builder.setSpan(new ClickableSpan(){
                        @Override
                        public void onClick(View widget) {
                            GopherUri current = stack.getCurrent();
                            if(current != null) {
                                stack.push(line.getUri());
                                loadPage();
                            }
                        }
                    }, index, index+line.getTitle().length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case GIF:
                    builder.append(line.getTitle());
                    builder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            downloadTarget = line.getUri();
                            gifThread.start();
                        }
                    }, index, index+line.getTitle().length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case TEXT_FILE:
                    builder.append(line.getTitle());
                    builder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            downloadTarget = line.getUri();
                            textfileThread.start();
                        }
                    }, index, index+line.getTitle().length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case SEARCH:
                    builder.append(line.getTitle());
                    builder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            SearchDialogFragment dialog = new SearchDialogFragment();
                            dialog.setListener(new SearchDialogFragment.SearchDialogListener() {
                                @Override
                                public void onDialogPositiveClick(DialogFragment dialog) {
                                    GopherUri target = line.getUri();
                                    String searchText = ((TextView)dialog
                                            .getDialog()
                                            .findViewById(R.id.search_text))
                                            .getText()
                                            .toString();
                                    target.setSearch(searchText);
                                    stack.push(target);
                                    loadPage();
                                }

                                @Override
                                public void onDialogNegativeClick(DialogFragment dialog) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show(getSupportFragmentManager(), "SearchDialogFragment");
                        }
                    }, index, index+line.getTitle().length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
            }
            builder.append("\r\n");
        }
        ((TextView)findViewById(R.id.pageText)).setText(builder);
        currentPage = page;
    }
}
