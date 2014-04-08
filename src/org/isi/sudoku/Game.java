package org.isi.sudoku;

import java.util.List;
import java.util.Random;

import org.isi.sudoku.utility.Constants;
import org.isi.sudoku.utility.LogHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Game extends Activity {

	
	private int[] puzzle = new int[9 * 9];
	
	private PuzzleBoard puzzleBoard;
	private Button solvePuzzle;
	private Button savePuzzle;
	private Button saveNewPuzzle;
	private Button goBack;
	private int diff_Level=-2;
	private String puzzleStr;
	private Thread thread;
	
	public int getDiff_Level() {
		return diff_Level;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//LogHelper.d("onCreate from Game.java");
		
		diff_Level = getIntent().getIntExtra(Constants.KEY_DIFFICULTY, Constants.DIFFICULTY_EASY);
		puzzle = createPuzzle(diff_Level);
		setContentView(R.layout.game);
		puzzleBoard = (PuzzleBoard) findViewById(R.id.puzzleBoard);
		//puzzleBoard.requestFocus();
		
		goBack = (Button) findViewById(R.id.go_back);
		goBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Game.this.puzzleBoard.goBack();
			}
		});
		//disable button in create puzzle UI
		if(diff_Level==4)
			goBack.setVisibility(View.GONE);
		
		//create solve button
		solvePuzzle = (Button) findViewById(R.id.solvePuzzle);
		solvePuzzle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				solveButtonDialog();
			}
		});
		//disable button in create puzzle UI
		if(diff_Level==4)
			solvePuzzle.setVisibility(View.GONE);
		
		//create save button
		savePuzzle = (Button) findViewById(R.id.savePuzzle);
		savePuzzle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				String initpuzzle=puzzleToString(puzzle);
				String savepuzzle=puzzleToString(puzzleBoard.getCellNumbersForStore());
				String puzzlestatus=puzzleToString(puzzleBoard.getCellStatusForStore());
				savepuzzle(savepuzzle,initpuzzle,puzzlestatus);
			}
		});
		//disable button in create puzzle UI
		if(diff_Level==4)
			savePuzzle.setVisibility(View.GONE);
		
		//create saveNewPUzzle button
		saveNewPuzzle = (Button) findViewById(R.id.save_new_puzzle);
		saveNewPuzzle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				saveNewPuzzle();
			}
		});
		//only open in create puzzle UI
		if(diff_Level!=4)
			saveNewPuzzle.setVisibility(View.GONE);
		}

	

	
	private void solveButtonDialog() {
		new AlertDialog.Builder(this)
			.setTitle("Warning")
			.setMessage("Are you sure?")
			.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
	 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						org.isi.sudoku.logic.Sudoku.Transferstringtointarray(puzzleStr);
						puzzleBoard.finishPuzzle(puzzle);
					}
				}
			)
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Nohing to do
				}
			})
			.show();
	}
	
	protected void savepuzzle(String savepuzzle,String initpuzzle,String puzzlestatus){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		SharedPreferences.Editor editor = prefs.edit();

		editor.putString("savepuzzle", savepuzzle);
		editor.putString("initpuzzle", initpuzzle);
		editor.putString("puzzlestatus", puzzlestatus);

		editor.commit();
		//LogHelper.d("save puzzle:"+"\n"+savepuzzle+"\n"+initpuzzle+"\n"+puzzlestatus);
	}
	
	protected void saveNewPuzzle(){
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String title= input.getText().toString().trim();
			savefile(title);
		}
		});

		alert.setNegativeButton("Cancel",
		new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		dialog.cancel();
		}
		});
		alert.show();
	}
	
	
	protected void savefile(String title){
		Context context=this.getApplicationContext();
		String newpuzzle=puzzleToString(puzzleBoard.getCellNumbersForStore());
		newpuzzle=newpuzzle.substring(18, 99);
		org.isi.sudoku.utility.WriteReadFile.writefile(context,"record.txt",title,newpuzzle);
		//LogHelper.d("save new puzzle:"+title+"/@/"+puzzleToString(puzzleBoard.getCellNumbersForStore()));
		
		byte[] puzzleBytes = newpuzzle.getBytes();
		byte[] titleBytes = title.getBytes();
        NdefRecord puzzleRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[] {}, puzzleBytes);
        NdefRecord titleRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[] {}, titleBytes);
        NdefMessage puzzle = new NdefMessage(new NdefRecord[] {
            titleRecord, puzzleRecord
        });
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        nfcAdapter.enableForegroundNdefPush(Game.this, puzzle);
	} 
	
	protected int[] getUsedNumbers(int x, int y) {

		// TODO: calculate unavailable numbers for cell(x, y)
		return new int[] {};
	}
	
	private int[] createPuzzle(int diff) {
		//LogHelper.d("crteate puzzle");
		Context context=this.getApplicationContext();
		Random rn = new Random();
		int choice;
		switch (diff) {
		case Constants.DIFFICULTY_CONTINUE:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			puzzleStr= prefs.getString("initpuzzle","000000000000000000000000000000000000000000000000000000000000000000000000000000000");
			org.isi.sudoku.logic.Sudoku.Transferstringtointarray(puzzleStr);
			//LogHelper.d(puzzleStr);
			break;
		case Constants.DIFFICULTY_CREATE:
			puzzleStr="000000000000000000000000000000000000000000000000000000000000000000000000000000000";
			org.isi.sudoku.logic.Sudoku.Transferstringtointarray(puzzleStr);
			break;
		case Constants.DIFFICULTY_EVIL:
			choice = rn.nextInt(org.isi.sudoku.utility.WriteReadFile.getAmountofPuzzle(context,diff+".txt"))+1;
			if(choice<=10)
				puzzleStr=org.isi.sudoku.logic.Sudoku.Readfromfile(context,diff+".txt",choice-1);
			else{
				puzzleStr=org.isi.sudoku.utility.WriteReadFile.readfile(context,diff+".txt",choice-11);
				org.isi.sudoku.logic.Sudoku.Transferstringtointarray(puzzleStr);
			}
			generateNewPuzzleToStore();
			break;
		case Constants.DIFFICULTY_HARD:
			choice = rn.nextInt(org.isi.sudoku.utility.WriteReadFile.getAmountofPuzzle(context,diff+".txt"))+1;
			if(choice<=10)
				puzzleStr=org.isi.sudoku.logic.Sudoku.Readfromfile(context,diff+".txt",choice-1);
			else{
				puzzleStr=org.isi.sudoku.utility.WriteReadFile.readfile(context,diff+".txt",choice-11);
				org.isi.sudoku.logic.Sudoku.Transferstringtointarray(puzzleStr);
			}
			generateNewPuzzleToStore();
			break;
		case Constants.DIFFICULTY_MEDIUM:
			choice = rn.nextInt(org.isi.sudoku.utility.WriteReadFile.getAmountofPuzzle(context,diff+".txt"))+1;
			//LogHelper.d("choice is "+choice);
			if(choice<=10)
				puzzleStr=org.isi.sudoku.logic.Sudoku.Readfromfile(context,diff+".txt",choice-1);
			else{
				puzzleStr=org.isi.sudoku.utility.WriteReadFile.readfile(context,diff+".txt",choice-11);
				org.isi.sudoku.logic.Sudoku.Transferstringtointarray(puzzleStr);
			}
			generateNewPuzzleToStore();
			break;
		case Constants.DIFFICULTY_EASY:
			//choice = rn.nextInt(org.isi.sudoku.utility.WriteReadFile.getAmountofPuzzle(context,diff+".txt"))+1;
			//LogHelper.d("choice is "+choice);
			//choice=11; 
			
			choice = rn.nextInt(org.isi.sudoku.utility.WriteReadFile.getAmountofPuzzle(context,diff+".txt"))+1;
			//LogHelper.d("choice is "+choice);
			if(choice<=10)
				puzzleStr=org.isi.sudoku.logic.Sudoku.Readfromfile(context,diff+".txt",choice-1);
			else{
				puzzleStr=org.isi.sudoku.utility.WriteReadFile.readfile(context,diff+".txt",choice-11);
				org.isi.sudoku.logic.Sudoku.Transferstringtointarray(puzzleStr);
			}
			generateNewPuzzleToStore();
			break;
		default:
			puzzleStr=org.isi.sudoku.utility.WriteReadFile.readfile(context, "record.txt", diff-100);
			org.isi.sudoku.logic.Sudoku.Transferstringtointarray(puzzleStr);
			//LogHelper.d("Get from file:"+puzzleStr);
			break;
		}
		return stringToPuzzle(puzzleStr);
	}
	private int RandomNumber(int min, int max) {

		int a = 0;

		try {
			Random rn = new Random();
			int range = max - min + 1;
			////LogHelper.d("randomnumber works");
			a = rn.nextInt(range) + min;
			////LogHelper.d(String.valueOf(a));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return min + a;
	}

	private void generateNewPuzzleToStore(){
		// TODO
		thread = new Thread()
		{
		    @Override
		    public void run() {

		    	Looper.prepare();
		    	String Newpuzzle=org.isi.sudoku.logic.Sudoku.CreatePuzzle2(getDiff_Level());
	        	//LogHelper.d("Thread generates puzzle"+Newpuzzle);
	        	if(Newpuzzle!="")
	        		org.isi.sudoku.utility.WriteReadFile.writefile(getBaseContext(), getDiff_Level()+".txt", "no", Newpuzzle);

		    }
		};

		thread.start();	

	}
	
	public String getpuzzlestatus(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String puzzlestatus= prefs.getString("puzzlestatus","");
		return puzzlestatus;
	}
	
	public String getsavepuzzle(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String savepuzzle= prefs.getString("savepuzzle","");
		return savepuzzle;
	}
	
	protected int[] getPuzzle() {
		puzzle=org.isi.sudoku.logic.Sudoku.TransferToOneDimension();
		//LogHelper.d("get puzzle");
		return puzzle;
	}

	protected int[] setNumberInCell(int x, int y, int number) {
		
		List<Integer> tmp=org.isi.sudoku.logic.Sudoku.CanMove(y, x, number);
		if(tmp==null){
			 return null;
		}
		if(tmp.isEmpty()){
			return null;
		}
		
		int[] intArray = new int[tmp.size()];
		for (int i = 0; i < tmp.size(); i++) {
		intArray[i] = tmp.get(i);
		}

		return intArray;
	}
	
	private String puzzleToString(int[] puzzle) {
		
		StringBuilder buf = new StringBuilder();
		for (int element : puzzle) {
			
			buf.append(element);
		}
		return buf.toString();
	}
	
	private int[] stringToPuzzle(String puzzleStr) {

		int[] puz = new int[puzzleStr.length()];
		for (int i = 0; i < puz.length; i++) {
			puz[i] = puzzleStr.charAt(i) - '0';
		}
		return puz;
	}
	
	@Override
	public void onBackPressed() {
		if(getDiff_Level()==0 || getDiff_Level()==1|| getDiff_Level()==2 || getDiff_Level()==3){
			if(thread.isAlive()){
				Thread tmp=thread;
				thread=null;
				tmp.interrupt();
				//LogHelper.d("thread stop");
				org.isi.sudoku.logic.Sudoku.sudoku_gen.Abort=true;
			}
			
			
		}
		
		Game.this.finish();
		return;
	}
}