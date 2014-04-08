package org.isi.sudoku;

import org.isi.sudoku.utility.Constants;
import org.isi.sudoku.utility.LogHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Sudoku extends Activity implements OnClickListener {
	
//	NfcAdapter nfcAdapter;
//	PendingIntent nfcPendingIntent;
//    IntentFilter[] ndefExchangeFilters;
	
	Button newGameButton;
	Button continueButton;
	Button createButton;	
	Button optionsButton;
	Button aboutButton;
	Button exitButton;
	Button savedpuzzleButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //LogHelper.d("onCreate from Sudoku.java");
        
//        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        nfcPendingIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//
//        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//        try {
//            ndefDetected.addDataType("text/plain");
//        } catch (MalformedMimeTypeException e) { }
//        ndefExchangeFilters = new IntentFilter[] { ndefDetected };
        
        newGameButton = (Button) findViewById(R.id.new_game_button);
        continueButton = (Button) findViewById(R.id.continue_game_button);
        createButton = (Button) findViewById(R.id.create_game_button);
        savedpuzzleButton = (Button) findViewById(R.id.savedpuzzle_button);
        aboutButton = (Button) findViewById(R.id.about_button);
        exitButton = (Button) findViewById(R.id.exit_button);
        newGameButton.setOnClickListener(this);
    	continueButton.setOnClickListener(this);
    	createButton.setOnClickListener(this);
    	savedpuzzleButton.setOnClickListener(this);
    	aboutButton.setOnClickListener(this);
    	exitButton.setOnClickListener(this);
    }

	@Override
	protected void onResume() {

		super.onResume();
//		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, ndefExchangeFilters, null);
	}

//	@Override
//	protected void onNewIntent(Intent intent) {
//
//		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//            NdefMessage[] msgs;
//            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            if (rawMsgs != null) {
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++) {
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                }
//            } else return;
//            String title = new String(msgs[0].getRecords()[0].getPayload());
//            String puzzle = new String(msgs[0].getRecords()[1].getPayload());
//            WriteReadFile.writefile(Sudoku.this, "record.txt", title, puzzle);
//        }
//	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.new_game_button:
				openNewGameDialog();
				break;
			case R.id.continue_game_button:
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				//LogHelper.d(prefs.getString("initpuzzle","error" ));
				if(prefs.getString("initpuzzle","error" ) != "error"){
					
					startGame(Constants.DIFFICULTY_CONTINUE);
				}
				else
					Toast.makeText(getBaseContext(), "No saved puzzle.", Toast.LENGTH_LONG).show();
				break;
			case R.id.create_game_button:
				// TODO
				startGame(4);
				break;
			case R.id.savedpuzzle_button:
				// TODO
				openSavedGameDialog();
				break;
			case R.id.about_button:
				Intent i = new Intent(this, About.class);
				startActivity(i);
				break;
			case R.id.exit_button:
				finish();
				break;
		}
	}
	
	private void openSavedGameDialog() {
		Context context=this.getApplicationContext();
		final CharSequence[] items = org.isi.sudoku.utility.WriteReadFile.readfile(context, "record.txt");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a puzzle");
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
		        startGame(100+item);
		    }
		});
		//AlertDialog alert = builder.create();
		builder.show();
	}

	private void openNewGameDialog() {

		new AlertDialog.Builder(this)
			.setTitle(R.string.new_game_title)
			.setItems(R.array.difficulty,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {

							startGame(which);
						}
					})
			.show();
	}
	
	private void startGame(int which) {

		//LogHelper.d("starting game of type #" + which);
		Intent intent = new Intent(this, Game.class);
		intent.putExtra(Constants.KEY_DIFFICULTY, which);
		startActivity(intent);
	}
}