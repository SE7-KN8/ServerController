package se7kn8.servercontroller.app.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se7kn8.servercontroller.app.R;

/**
 * Created by sebkn on 11.02.2018.
 *
 */

public class ServerOverviewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CoordinatorLayout layout = (CoordinatorLayout) inflater.inflate(R.layout.server_overview, container, false);
        RecyclerView view = layout.findViewById(R.id.server_overview_recycler);

        return layout;
    }
}
