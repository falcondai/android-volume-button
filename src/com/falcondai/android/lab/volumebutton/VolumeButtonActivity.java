/*
 * Name:		VolumeButtonActivity.java
 * Author:		Falcon Dai
 * Date: 		3/3/2012
 * Description: This android application provides a quick
 * 				adjustment of the volumes of various streams
 * 				(serving as a replacement of the hardware buttons)
 */

package com.falcondai.android.lab.volumebutton;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class VolumeButtonActivity extends Activity {

	public static final String tag = "volume button";

	private final int flag = AudioManager.FLAG_ALLOW_RINGER_MODES
			| AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI
			| AudioManager.FLAG_VIBRATE;
	private AudioManager am;

	private Button raise_btn;
	private Button lower_btn;
	// on when in silent ringer mode
	private ToggleButton vibrate_btn;
	private SeekBar volume_bar;

	private BroadcastReceiver rsr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		raise_btn = (Button) findViewById(R.id.raise_btn);
		lower_btn = (Button) findViewById(R.id.lower_btn);
		vibrate_btn = (ToggleButton) findViewById(R.id.vibrate_btn);
		volume_bar = (SeekBar) findViewById(R.id.volume_bar);

		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		raise_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				am.adjustVolume(AudioManager.ADJUST_RAISE, flag);
				volume_bar.setProgress(am
						.getStreamVolume(AudioManager.STREAM_MUSIC));
			}
		});

		lower_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				am.adjustVolume(AudioManager.ADJUST_LOWER, flag);
				volume_bar.setProgress(am
						.getStreamVolume(AudioManager.STREAM_MUSIC));
			}
		});

		vibrate_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// toggle between ringer vibrate and normal modes
				if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
					// switch to vibrate ringer mode
					am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
					vibrate_btn.setChecked(true);
					am.adjustStreamVolume(AudioManager.STREAM_RING,
							AudioManager.ADJUST_SAME, flag);
				} else {
					// switch to normal ringer mode
					am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					am.setStreamVolume(AudioManager.STREAM_RING,
							am.getStreamMaxVolume(AudioManager.STREAM_RING),
							flag);
					vibrate_btn.setChecked(false);
				}
			}
		});

//		Log.d(tag,
//				"max volume: "
//						+ String.valueOf(am
//								.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
		volume_bar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volume_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar sb, int volume, boolean user) {
				if (user) {
					am.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
							AudioManager.FLAG_SHOW_UI);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
		});

		rsr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				vibrate_btn
						.setChecked(AudioManager.RINGER_MODE_NORMAL != intent
								.getIntExtra(AudioManager.EXTRA_RINGER_MODE,
										AudioManager.RINGER_MODE_NORMAL));
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(rsr, new IntentFilter(
				AudioManager.RINGER_MODE_CHANGED_ACTION));
		volume_bar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(rsr);
	}
}