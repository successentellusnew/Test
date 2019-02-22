package com.success.successEntellus.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.success.successEntellus.R;

public class ModuleNotAvailableActivity extends AppCompatActivity {
    Button btn_upgrade_cancel,btn_upgrade_ok,btn_upgrade_dissmiss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_module_available);
        getSupportActionBar().hide();

        openDialogDisplayAlert();
    }

    private void openDialogDisplayAlert() {

        final Dialog dialog = new Dialog(ModuleNotAvailableActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.upgrade_package_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setTitle("Confirmation");
        btn_upgrade_dissmiss=(Button) dialog.findViewById(R.id.btn_upgrade_dissmiss);
        btn_upgrade_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* btn_upgrade_cancel=(Button) dialog.findViewById(R.id.btn_upgrade_cancel);
        btn_upgrade_ok=(Button) dialog.findViewById(R.id.btn_upgrade_ok);


        btn_upgrade_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_upgrade_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ModuleNotAvailableActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
*/
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
