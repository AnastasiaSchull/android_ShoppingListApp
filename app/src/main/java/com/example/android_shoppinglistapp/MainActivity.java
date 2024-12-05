package com.example.android_shoppinglistapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import com.example.android_shoppinglistapp.adapters.ShoppingAdapter;
import com.example.android_shoppinglistapp.data.DBHelper;
import com.example.android_shoppinglistapp.data.Shopping;

import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {
    private ShoppingAdapter shoppingAdapter;

    public final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),//тут мы создаем новый лаунчер
            (ActivityResult result) -> {
                Intent data = result.getData();
                if (result.getResultCode() == RESULT_OK && data != null) {
                    int position = data.getIntExtra(ShoppingActivity.POSITION, -1);
                    int mode = data.getIntExtra(ShoppingActivity.MODE, -1);
                    Shopping shopping = data.getSerializableExtra(ShoppingActivity.SHOPPING, Shopping.class);
                    if (shopping != null) {
                        if (mode == ShoppingActivity.INSERT) {
                            shoppingAdapter.insertData(shopping);
                        }
                        if (mode == ShoppingActivity.UPDATE) {
                            shoppingAdapter.updateData(position, shopping);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        //Встановлення локалізованого тексту із ресурсів у заголовок актівіті
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        int themeId = prefs.getInt("theme_id", R.style.Color_Theme_Android_ShoppingListApp);  // умолчане
        setTheme(themeId);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //DB
        List<Shopping> list;
        try (DBHelper helper = new DBHelper(this)) {
            list = helper.selectAll();
        }
        //Adapter
        shoppingAdapter = new ShoppingAdapter(this, list);
        //
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(shoppingAdapter);
        //LayoutManager !!!!!!!!!!
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // MenuItem add = menu.add("Add Item");
        MenuItem add = menu.add(getResources().getString(R.string.add_item));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        add.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(this, ShoppingActivity.class);
            intent.putExtra(ShoppingActivity.MODE, ShoppingActivity.INSERT);
            activityResultLauncher.launch(intent);
            return true;
        });

        //cоздание OptionsMenu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //обробка натискання на пункти меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();//метод edit() дает нам обьект Editor'а который может работать с настройкоми

        if (itemId == R.id.portraitMenu) {
            Toast.makeText(this, R.string.portrait, Toast.LENGTH_SHORT).show();
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            return true;
        } else if (itemId == R.id.landscapeMenu) {
            Toast toast = Toast.makeText(this, R.string.landscape, Toast.LENGTH_LONG);
            toast.show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            return true;
        } else if (itemId == R.id.enLocaleMenu) {
            setLocale(Locale.ENGLISH);
            recreate();
            return true;
        } else if (itemId == R.id.ukLocaleMenu) {
            setLocale(new Locale("uk"));//, "UA"));
            recreate();
            return true;
        }
        //Themes
        else if (itemId == R.id.lightThemeOption) {
            Log.d("t", "Switching to light theme.");
            editor.putInt("theme_id", R.style.LightTheme).apply();
            recreate();
            Log.d("t", "Current colorPrimary: " + getResources().getColor(R.color.light_gray));
            return true;
        } else if (itemId == R.id.darkThemeOption) {
            Log.d("t", "Switching to dark theme.");
            editor.putInt("theme_id", R.style.DarkTheme).apply();
            recreate();
            Log.d("t", "Current colorPrimary: " + getResources().getColor(R.color.background_dark));
            return true;
        }
        else if (itemId == R.id.colorThemeOption) {
            Log.d("t", "Switching to colorful theme.");
            editor.putInt("theme_id", R.style.ColorfulTheme).apply();
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLocale(Locale locale) {
        Locale.setDefault(locale);
        Resources resources = getResources();//это метод от активити, который достает объект, через который мы можем управлять ресурсом( в данном случае локализацией)
        Configuration configuration = resources.getConfiguration();//достаем конфигурацию/конфiгурацiя це налаштування якi доступнi поки у нас программа запущена, а коли ми программу закриваемо, то i налаштування зтираються
        configuration.setLocale(locale);//в конфигурацию устанавливаем локализацию
        // resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        getBaseContext().getResources().updateConfiguration(
                configuration, resources.getDisplayMetrics()
        );
        Log.d("t", "Locale set to: " + locale.toString());

    }

    @Override
    protected void attachBaseContext(Context contextBase) {//в этом методе еще нет активити, але ми можем через  SharedPreference достать настройки
        //Приклад отримання налаштувань з SharedPreferences
        SharedPreferences preferences =
                contextBase.getSharedPreferences("settings", MODE_PRIVATE);//getSharedPreferences вызывается от contextBase , так как активити еще нет
        String localeTag =
                preferences.getString("locale", Locale.ENGLISH.toLanguageTag());
        //Новий спосіб встановлення локалізації
        Locale locale = Locale.forLanguageTag(localeTag);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        Context newContext =
                contextBase.createConfigurationContext(configuration);

        super.attachBaseContext(newContext);
    }

    private final ActivityResultLauncher<String> smsPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            permitted -> {
                if (permitted) {
                    Toast.makeText(this, "Permission to send SMS granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission to send SMS denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    public void attemptToSendSMS(String phoneNumber, String message) {
        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        } else {
            sendSMS(phoneNumber, message);
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS Sent!", Toast.LENGTH_LONG).show();
    }
}


