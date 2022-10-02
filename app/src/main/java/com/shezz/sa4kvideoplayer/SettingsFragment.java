package com.shezz.sa4kvideoplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.jgabrielfreitas.core.BlurImageView;
import com.mxplayer.paksoft.R;
import com.shezz.sa4kvideoplayer.musicplayer.MPPreferences;
import com.shezz.sa4kvideoplayer.musicplayer.helper.ThemeHelper;
import com.shezz.sa4kvideoplayer.musicplayer.viewmodel.MainViewModel;
import com.shezz.sa4kvideoplayer.musicplayer.viewmodel.MainViewModelFactory;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private MainViewModel viewModel;
    private RecyclerView accentView;
    private boolean state;
    private LinearLayout chipLayout;
    private ImageView currentThemeMode;


    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), new MainViewModelFactory(requireActivity())).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        LinearLayout refreshOption = view.findViewById(R.id.refresh_options);

        state = MPPreferences.getAlbumRequest(requireActivity().getApplicationContext());
        BlurImageView blurImageView = (BlurImageView) view.findViewById(R.id.blurImage1);
        blurImageView.setBlur(20);
        setCurrentThemeMode();
        refreshOption.setOnClickListener(this);

        return view;
    }


    private void setCurrentThemeMode() {
        int mode = MPPreferences.getThemeMode(requireActivity().getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

         if (id == R.id.refresh_options) {
            Toast.makeText(requireActivity(), "Looking ...", Toast.LENGTH_SHORT).show();
            ThemeHelper.applySettings(requireActivity());
        }
    }


    private void selectTheme(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        MPPreferences.storeThemeMode(requireActivity().getApplicationContext(), theme);
    }

    private void setAlbumRequest() {
        MPPreferences.storeAlbumRequest(requireActivity().getApplicationContext(), (!state));
        ThemeHelper.applySettings(getActivity());
    }
}