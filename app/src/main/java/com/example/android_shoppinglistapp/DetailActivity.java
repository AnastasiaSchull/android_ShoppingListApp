package com.example.android_shoppinglistapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.android_shoppinglistapp.data.DBHelper;
import com.example.android_shoppinglistapp.data.Shopping;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        TextView tvID = findViewById(R.id.tvID);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvDetails = findViewById(R.id.tvDetails);

        // текст для вывода
        String[] messages = new String[]{
                "This product is distinguished by its high quality and reliability.",
                "Exceptional craftsmanship ensures a long product life.",
                "Experience the premium quality with every use.",
                "Designed for durability and performance.",
                "Reliable quality that you can trust unconditionally.",
                "Every detail is crafted to perfection.",
                "A commitment to quality that is unmatched.",
                "Superior performance with every interaction.",
                "Built to last and exceed expectations.",
                "Quality and reliability at its best."
        };

        // ID из Intent
        int shoppingId = getIntent().getIntExtra("SHOPPING_ID", -1);
        if (shoppingId != -1) {
            Shopping shopping = dbHelper.selectById(shoppingId);
            tvID.setText("ID: " + shopping.getId());
            tvName.setText("Name of item: " + shopping.getName());
           // случайный текст из массива
            Random random = new Random();
            int index = random.nextInt(messages.length);
            tvDetails.setText(messages[index]);
        }
    }
}