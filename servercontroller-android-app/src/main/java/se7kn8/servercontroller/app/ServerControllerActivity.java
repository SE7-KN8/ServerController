package se7kn8.servercontroller.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import se7kn8.servercontroller.app.fragment.ServerControllerOverviewFragment;

public class ServerControllerActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ServerControllerOverviewFragment()).commit();
	}
}
