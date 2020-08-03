package com.upc.reciclemosreciclador.Inicio;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.upc.reciclemosreciclador.R;

public class CovidFragment extends Fragment {

    public CovidFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_covid, container, false);

        Button button = v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MeInformoActivity) getActivity()).cambiarFragment(1);
            }
        });

        VideoView videoView = v.findViewById(R.id.videoView);
        String video_path = "android.resource://com.example.reciclemosdemo/" + R.raw.videocovid;
        Uri uri = Uri.parse(video_path);
        videoView.setVideoURI(uri);

        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });*/

        /*tvLink = v.findViewById(R.id.tvLink);
        tvLink.setMovementMethod(LinkMovementMethod.getInstance());*/

        return v;
    }
}
