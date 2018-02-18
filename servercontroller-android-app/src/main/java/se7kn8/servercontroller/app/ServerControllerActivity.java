package se7kn8.servercontroller.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import se7kn8.servercontroller.app.fragment.ServerControllerOverviewFragment;

public class ServerControllerActivity extends AppCompatActivity {

	public static final String FRAGMENT_TAG_SERVERCONTROLLER_OVERVIEW = "servercontroller_overview_fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ServerControllerOverviewFragment(), FRAGMENT_TAG_SERVERCONTROLLER_OVERVIEW).commit();
		}
	}
}
