package spor.automato.com.sporprojecttest.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import spor.automato.com.sporprojecttest.R;

public class DisputeDetailActivity extends Activity {

    TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispute_detail);
        this.label = (TextView)findViewById(R.id.textView2);
    }
}
