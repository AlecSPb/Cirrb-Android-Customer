package cirrb.com.cirrab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by yuva on 19/6/17.
 */

public class OrderCompleteActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        ((Button)findViewById(R.id.btn_confrm)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_confrm){
            startActivity(new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
    }
}
