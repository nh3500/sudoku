package org.isi.sudoku;

import java.util.ArrayList;

import org.isi.sudoku.utility.LogHelper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class NumberPad extends Dialog {
	
	private final View numbers[] = new View[9];
	private View numberPad, clear, mark, cancel;
	
	private final int unavailable[];
	private final PuzzleBoard puzzleBoard;
	
	public NumberPad(Context context, int unavailable[], 
			PuzzleBoard puzzleBoard) {
		
		super(context);
		this.unavailable = unavailable;
		this.puzzleBoard = puzzleBoard;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setTitle(R.string.numberpad_title);
		setContentView(R.layout.numberpad);
		findViews();
		for (int element : unavailable) {
			if (element != 0) {
				
				numbers[element - 1].setVisibility(View.INVISIBLE);
			}
		}
		setListeners();
	}

	private void setListeners() {

		for (int i = 0; i < numbers.length; i++) {
			
			final int n = i + 1;
			numbers[i].setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {

					//LogHelper.d("number #" + n + " clicked");
					returnResult(n);
				}
			});
		}
		numberPad.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				returnResult(0);
			}
		});
		clear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				returnResult(0);
			}
		});
		mark.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				NumberPad.this.puzzleBoard.markList = new ArrayList<OneStep>();
				returnResult(-1);
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				dismiss();
			}
		});
		
	}

	private void returnResult(int n) {

		puzzleBoard.setSelectedCell(n);
		dismiss();
	}
	
	private void findViews() {

		numberPad = findViewById(R.id.numberpad);
		numbers[0] = findViewById(R.id.numberpad_1);
		numbers[1] = findViewById(R.id.numberpad_2);
		numbers[2] = findViewById(R.id.numberpad_3);
		numbers[3] = findViewById(R.id.numberpad_4);
		numbers[4] = findViewById(R.id.numberpad_5);
		numbers[5] = findViewById(R.id.numberpad_6);
		numbers[6] = findViewById(R.id.numberpad_7);
		numbers[7] = findViewById(R.id.numberpad_8);
		numbers[8] = findViewById(R.id.numberpad_9);
		clear = findViewById(R.id.numberpad_clear);
		mark = findViewById(R.id.numberpad_mark);
		cancel = findViewById(R.id.numberpad_cancel);
	}
}
