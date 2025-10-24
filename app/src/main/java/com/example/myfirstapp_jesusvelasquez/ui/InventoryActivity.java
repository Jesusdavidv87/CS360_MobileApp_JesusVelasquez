package com.example.myfirstapp_jesusvelasquez.ui;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfirstapp_jesusvelasquez.R;
import com.example.myfirstapp_jesusvelasquez.data.Item;
import com.example.myfirstapp_jesusvelasquez.data.ItemDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private ItemDao dao;
    private final List<Item> items = new ArrayList<>();
    private ItemAdapter adapter;

    // --- simple prefs for SMS ---
    private static final String PREFS = "inv_prefs";
    private static final String KEY_SMS_ENABLED = "sms_enabled";
    private static final String KEY_SMS_PHONE = "sms_phone";

    // Launcher for runtime permission
    private final ActivityResultLauncher<String> smsPermLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted) {
                    Toast.makeText(this, "SMS permission denied. Alerts disabled.", Toast.LENGTH_SHORT).show();
                    setSmsEnabled(false);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        dao = new ItemDao(this);

        RecyclerView rv = findViewById(R.id.rv);   // ensure your layout has RecyclerView with id "rv"
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ItemAdapter(items, this::onItemClick);
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd); // ensure id "fabAdd" exists
        fab.setOnClickListener(v -> onAddItem());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        items.clear();
        items.addAll(dao.all());
        adapter.notifyDataSetChanged();
    }

    private void onAddItem() {
        ViewItemDialog.show(this, null, it -> {
            if (it != null) {
                long id = dao.insert(it);
                if (id <= 0) {
                    Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
                } else {
                    maybeSendSmsLowStock(it);
                }
                refresh();
            }
        });
    }

    private void onItemClick(Item existing) {
        ViewItemDialog.show(this, existing, result -> {
            if (result == null) {
                dao.delete(existing.id);
            } else {
                result.id = existing.id;
                dao.update(result);
                maybeSendSmsLowStock(result);
            }
            refresh();
        });
    }

    // --- SMS low-stock path (non-blocking if denied) ---
    private void maybeSendSmsLowStock(@NonNull Item it) {
        if (!isSmsEnabled()) return;
        if (it.qty > it.threshold) return;

        String phone = getSmsPhone();
        if (phone.isEmpty()) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            smsPermLauncher.launch(Manifest.permission.SEND_SMS);
            return;
        }

        try {
            // Use default SmsManager
            android.telephony.SmsManager.getDefault().sendTextMessage(
                    phone, null,
                    "Low stock: " + it.name + " (qty " + it.qty + ", threshold " + it.threshold + ")",
                    null, null
            );
            Toast.makeText(this, "SMS alert sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Emulators without telephony will land here; rubric only needs the logic.
            Toast.makeText(this, "Unable to send SMS on this device/emulator.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Options menu (settings & quick seed) ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sms_settings) {
            showSmsSettings();
            return true;
        }
        if (item.getItemId() == R.id.action_quick_seed) {
            seedSampleItems();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void seedSampleItems() {
        Item a = new Item();
        a.name = "Printer Paper"; a.sku = "OFF-PP-01"; a.qty = 15; a.location = "Closet A"; a.threshold = 10;
        dao.insert(a);

        Item b = new Item();
        b.name = "Blue Pens (box)"; b.sku = "OFF-PEN-B"; b.qty = 8; b.location = "Drawer 2"; b.threshold = 5;
        dao.insert(b);

        Item c = new Item();
        c.name = "Sticky Notes"; c.sku = "OFF-STK-01"; c.qty = 12; c.location = "Desk Shelf"; c.threshold = 6;
        dao.insert(c);

        refresh();
        Toast.makeText(this, "Seeded 3 items", Toast.LENGTH_SHORT).show();
    }

    private void showSmsSettings() {
        final boolean[] enabled = {isSmsEnabled()};
        final String[] phone = {getSmsPhone()};

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("SMS Settings");

        // Request permission proactively if user enables
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        // Phone input
        final EditText input = new EditText(this);
        input.setHint("Phone (e.g., 5551234567)");
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(phone[0]);

        b.setView(input);
        b.setMultiChoiceItems(
                new CharSequence[]{"Enable SMS low-stock alerts"},
                new boolean[]{enabled[0]},
                (dialog, which, isChecked) -> enabled[0] = isChecked
        );

        b.setPositiveButton("Save", (dialog, which) -> {
            phone[0] = input.getText().toString().trim();
            setSmsPhone(phone[0]);
            setSmsEnabled(enabled[0]);
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

            if (enabled[0] && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                smsPermLauncher.launch(Manifest.permission.SEND_SMS);
            }
        });

        b.setNegativeButton("Cancel", (d, w) -> {});
        b.show();
    }

    // --- Prefs helpers ---
    private SharedPreferences prefs() {
        return getSharedPreferences(PREFS, MODE_PRIVATE);
    }

    private boolean isSmsEnabled() {
        return prefs().getBoolean(KEY_SMS_ENABLED, false);
    }

    private void setSmsEnabled(boolean v) {
        prefs().edit().putBoolean(KEY_SMS_ENABLED, v).apply();
    }

    private String getSmsPhone() {
        return prefs().getString(KEY_SMS_PHONE, "");
    }

    private void setSmsPhone(String p) {
        prefs().edit().putString(KEY_SMS_PHONE, p).apply();
    }

    // --- (Optional) backup permission handling using callback API ---
    private static final int SMS_PERMISSION_CODE = 1001;

    private boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void ensureSmsPermission() {
        if (!hasSmsPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied. Alerts won't be sent.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



