package org.isi.sudoku.logic;

import java.util.List;
import org.isi.sudoku.utility.LogHelper;

import android.content.Context;

public class Sudoku {

	private static SudokuLogic_V2 sudoku_v2 = new SudokuLogic_V2();

	public static SudokuLogic_V2 sudoku_gen; 
	// validate the move of users when they input on puzzle
	public static List<Integer> CanMove(int row, int col, int num) {
		
		if(num==0){
			sudoku_v2.ClearNumber(row, col);
			return null;
		}else{
			return sudoku_v2.Validate(row, col, num);
		}
		
	}
	
	public static void StartPuzzleForUserCreate(){
	
		sudoku_v2.StartPuzzleForUserCreate();
	}

	//create puzzle by automatically
	public static String CreatePuzzle(int kindOfPuzzle) {
		String a=sudoku_v2.GetPuzzle(kindOfPuzzle);
		//LogHelper.d(a);
		return a;
	}
public static String CreatePuzzle2(int kindOfPuzzle){
	sudoku_gen = new SudokuLogic_V2();
	String a=sudoku_gen.GetPuzzle(kindOfPuzzle);
	//LogHelper.d(a);
	return a;
}
	//the algorithm to run
	public static void RunAlgorithm() {
		sudoku_v2.ResetPossiblePuzzle();
		try {
			sudoku_v2.Reset();
			if(!sudoku_v2.SolvePuzzle()){
				sudoku_v2.SolvePuzzleByBruteForce();
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//transform from arry 2D to array 1D
	public static int[] TransferToOneDimension() {
		int[] puzzle = new int[SudokuLogicConstant.SIZE_OF_PUZZLE
				* SudokuLogicConstant.SIZE_OF_PUZZLE];
		// transfer two dimensional array to one dimensional array
		for (int x = 0; x < sudoku_v2.actual.length; x++) {
			for (int y = 0; y < sudoku_v2.actual[0].length; y++) {
				puzzle[x * sudoku_v2.actual[0].length + y] = sudoku_v2.actual[x][y];
			}
		}
		return puzzle;
	}

	public static String Readfromfile(Context context,String filename,int puzzlenumber) {
		String puzzleStr=org.isi.sudoku.utility.WriteReadFile.Readfromfile(context,filename,puzzlenumber);
		//WriteReadFile.inputValueForArray((puzzlenumber - 1) * 9,puzzlenumber * 9 - 1, sudoku_v2.actual, context);
		//LogHelper.d("prepare to transfer");
		Transferstringtointarray(puzzleStr);
		return puzzleStr;
	}
	
	public static void Transferstringtointarray(String puzzleStr){
		int[] puz = new int[puzzleStr.length()];
		for (int i = 0; i < puz.length; i++) {
			puz[i] = puzzleStr.charAt(i) - '0';
		}
		for (int x = 0; x < sudoku_v2.actual.length; x++) {
			for (int y = 0; y < sudoku_v2.actual[0].length; y++) {
				sudoku_v2.actual[x][y]=puz[x * sudoku_v2.actual[0].length + y] ;
			
			}
		}
		sudoku_v2.StartPuzzleForUserCreate();
		//LogHelper.d(puzzleStr);
	}
 
}
