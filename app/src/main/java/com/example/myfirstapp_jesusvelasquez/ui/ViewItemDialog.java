package com.example.myfirstapp_jesusvelasquez.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.myfirstapp_jesusvelasquez.R;
import com.example.myfirstapp_jesusvelasquez.data.Item;

public class ViewItemDialog {

    public interface Callback {
        void onResult(Item resultOrNullForDelete);
    }

    public static void show(Context ctx, Item existing, Callback cb) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_item_edit, null, false);

        EditText etName = v.findViewById(R.id.etName);
        EditText etSku  = v.findViewById(R.id.etSku);
        EditText etQty  = v.findViewById(R.id.etQty);
        EditText etLoc  = v.findViewById(R.id.etLocation);
        EditText etThr  = v.findViewById(R.id.etThreshold);

        if (existing != null) {
            etName.setText(existing.name);
            etSku.setText(existing.sku);
            etQty.setText(String.valueOf(existing.qty));
            etLoc.setText(existing.location);
            etThr.setText(String.valueOf(existing.threshold));
        }

        AlertDialog.Builder b = new AlertDialog.Builder(ctx)
                .setTitle(existing == null ? "Add Item" : "Edit Item")
                .setView(v)
                .setPositiveButton(existing == null ? "Add" : "Save", (d, which) -> {
                    Item it = new Item();
                    it.name = etName.getText().toString().trim();
                    it.sku = etSku.getText().toString().trim();
                    it.qty = parseInt(etQty.getText().toString(), 0);
                    it.location = etLoc.getText().toString().trim();
                    it.threshold = parseInt(etThr.getText().toString(), 0);
                    cb.onResult(it);
                })
                .setNegativeButton("Cancel", (d, w) -> {});

        if (existing != null) {
            b.setNeutralButton("Delete", (d, w) -> cb.onResult(null));
        }

        // Ensure focus + keyboard
        AlertDialog dialog = b.create();
        dialog.setOnShowListener(dlg -> {
            etName.requestFocus();
            etName.post(() -> {
                InputMethodManager imm =
                        (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        });
        dialog.show();
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}



