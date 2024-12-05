package com.example.android_shoppinglistapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.android_shoppinglistapp.data.DBHelper;
import com.example.android_shoppinglistapp.data.Shopping;
import com.example.android_shoppinglistapp.databinding.ActivityShoppingBinding;
import java.util.Locale;

public class ShoppingActivity extends AppCompatActivity {
        public static final int INSERT = 0;
        public static final int UPDATE = 1;
        public static final String MODE = "mode";
        public static final String POSITION = "position";
        public static final String SHOPPING = "shopping";

        private int mode;
        private Integer shoppingId = 0;

        private ActivityShoppingBinding binding;

        @SuppressLint("NewApi")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            //Встановлення локалізованого тексту із ресурсів у заголовок актівіті
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.app_name);
            }

            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            //+
            binding = ActivityShoppingBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            //
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            //
            Intent intent = getIntent();
            mode = intent.getIntExtra(MODE, -1);

            if ( mode == UPDATE){
                shoppingId = intent.getSerializableExtra(SHOPPING, Shopping.class).getId();

                try( DBHelper helper = new DBHelper(this)){
                    Shopping shopping = helper.selectById(shoppingId);
                    binding.nameTextShopping.setText(shopping.getName());
                }
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
           // MenuItem save = menu.add("Save");
            MenuItem save = menu.add(getResources().getString(R.string.save));
            save.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            save.setOnMenuItemClickListener(item -> {
                //Form Test
                if (
                        !binding.nameTextShopping.getText().toString().isBlank()

                ) {
                    Shopping shopping= new Shopping(
                            shoppingId,
                            binding.nameTextShopping.getText().toString()

                    );
                    //DB
                    try (DBHelper helper = new DBHelper(this)) {
                        if (mode == INSERT)
                            shopping = helper.insert(shopping);
                        if (mode == UPDATE)
                            helper.update(shopping);
                        //
                        Intent resultIntent = new Intent();
                        int position = getIntent().getIntExtra(POSITION, -1);
                        resultIntent.putExtra(POSITION, position);
                        resultIntent.putExtra(MODE, mode);
                        resultIntent.putExtra(SHOPPING, shopping);
                        //
                        setResult(RESULT_OK, resultIntent);
                    }
                    //при клике на  save мы закрываем активити, иначе выходим через треугольник
                    finish();
                }
                return true;

            });
            return super.onCreateOptionsMenu(menu);
        }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("locale", Locale.ENGLISH.toLanguageTag());

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);
        super.attachBaseContext(newBase.createConfigurationContext(config));
    }

}