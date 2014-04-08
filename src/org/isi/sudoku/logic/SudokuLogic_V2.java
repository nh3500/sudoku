package org.isi.sudoku.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.isi.sudoku.utility.Constants;
import org.isi.sudoku.utility.LogHelper;

public class SudokuLogic_V2 {
	// used to represent the possible values of cells in the grid
	public int[][] actual = new int[SudokuLogicConstant.SIZE_OF_PUZZLE][SudokuLogicConstant.SIZE_OF_PUZZLE];

	// used to represent the possible values of cells in the grid
	private String[][] possible = new String[9][9];

	// indicate if the brute-force subroutine should stop
	private boolean BruteForceStop = false;
	private int totalscore = 0;
	// used to store the state of the grid
	private Stack<int[][]> ActualStack = new Stack<int[][]>();
	private Stack<String[][]> PossibleStack = new Stack<String[][]>();
	
	public boolean Abort=false;

	// backup a copy of the Actual array
	private int[][] actual_backup = new int[SudokuLogicConstant.SIZE_OF_PUZZLE][SudokuLogicConstant.SIZE_OF_PUZZLE];

	public SudokuLogic_V2() {
		ResetPossiblePuzzle();
	}

	public void Reset() {
		BruteForceStop = false;
		ActualStack.clear();
		PossibleStack.clear();
	}

	public void ResetPossiblePuzzle() {
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				possible[r][c] = "";
			}
		}
	}

	// Generate a random number between the specified range
	public int RandomNumber(int min, int max) {

		int a = 2;

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

	public void StartPuzzleForUserCreate() {
		for (int i = 0; i < SudokuLogicConstant.SIZE_OF_PUZZLE; i++) {
			for (int j = 0; j < SudokuLogicConstant.SIZE_OF_PUZZLE; j++) {
				try {
					CalculatePossibleValues(i, j);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// Create empty cells in the grid
	public void CreateEmptyCells(int empty) {
		int c = 0, r = 0;
		String[] emptyCells = new String[empty];
		for (int i = 0; i < empty / 2; i++) {
			boolean duplicate = false;
			do {
				duplicate = false;
				do {
					c = RandomNumber(0,8);
					r = RandomNumber(0,8);
				} while (r == 4 && c > 4);
				for (int j = 0; j < i; j++) {
					if (emptyCells[j] == Integer.toString(c)
							+ Integer.toString(r)) {
						duplicate = true;
						break;
					}
				}
				if (!duplicate) {
					// set the empty cell
					emptyCells[i] = Integer.toString(c) + Integer.toString(r);
					actual[r][c] = 0;
					possible[r][c] = "";
					// reflect the top half of the grid and make it symmetrical
					emptyCells[empty - i - 1] = Integer.toString(8 - c)
							+ Integer.toString(8 - r);
					actual[8 - r][8 - c] = 0;
					possible[8 - r][8 - c] = "";

				}
			} while (duplicate);
		}
	}
	public void SetCell(int row, int column,int number, boolean erasable){
		actual[row][column]=number;
		if(number==0){
			for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
				for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
					if(actual[r][c]==0){
						possible[r][c]="";
					}
				}
			}
		}else{
			possible[row][column]=Integer.toString(number);
		}
		
	}
	private void UpdatePuzzleWhenZero(int row, int column, int oldnumber) {
		String oldnumberstring = Integer.toString(oldnumber);
		if (possible[row][column].contains(Integer
				.toString(actual[row][column]))) {
			possible[row][column] = possible[row][column].replace(
					Integer.toString(actual[row][column]), "");
		}
		// check rowS
		for (int i = 0; i < SudokuLogicConstant.SIZE_OF_PUZZLE; i++) {
			if (!possible[row][i].contains(oldnumberstring)) {
				possible[row][i] += oldnumberstring;
				break;
			}
		}

		// check column
		for (int i = 0; i < SudokuLogicConstant.SIZE_OF_PUZZLE; i++) {
			if (!possible[i][column].contains(oldnumberstring)) {
				possible[i][column] += oldnumberstring;
				break;
			}
		}
		// check box
		int startR = row - row % 3;
		int startC = column - column % 3;
		// check box
		for (int r = startR; r < startR + 3; r++)
			for (int c = startC; c < startC + 3; c++)
				if (!possible[r][c].contains(oldnumberstring)) {
					possible[r][c] += oldnumberstring;
					break;
				}
	}

	public void ClearNumber(int row, int column){
		if(actual[row][column]==0){
			return;
		}else{
			SetCell(row, column, 0, true);
		}
		
	
	}
	public List<Integer> Validate(int row, int column, int number) {
		List<Integer> re = new ArrayList<Integer>();
			// check rowS
		for (int i = 0; i < SudokuLogicConstant.SIZE_OF_PUZZLE; i++) {
			if (actual[row][i] == number) {
				re.add(row * 9 + i);
				break;
			}
		}

		// check column
		for (int i = 0; i < SudokuLogicConstant.SIZE_OF_PUZZLE; i++) {
			if (actual[i][column] == number) {
				re.add(i * 9 + column);
				break;
			}
		}
		// check box
		int startR = row - row % 3;
		int startC = column - column % 3;
		// check box
		for (int r = startR; r < startR + 3; r++)
			for (int c = startC; c < startC + 3; c++)
				if (number == actual[r][c]) {
					re.add(r * 9 + c);
					break;
				}

		if (re.isEmpty()) {
			if (possible[row][column].contains(Integer.toString(number))) {
				possible[row][column] = possible[row][column].replace(
						Integer.toString(number), "");
			}
			actual[row][column] = number;
		}

		return re;
	}

	public String CalculatePossibleValues(int row, int col) throws Exception {
		String str = "";
		if (possible[row][col] == null || possible[row][col] == "") {
			str = SudokuLogicConstant.ALL_POSS_NUMBERS;
		} else {
			str = possible[row][col];
		}

		// check by column
		for (int i = 0; i < SudokuLogicConstant.SIZE_OF_PUZZLE; i++) {
			if (actual[row][i] != 0) {
				str = str.replace(Integer.toString(actual[row][i]), "");
			}
		}
		// check by row
		for (int i = 0; i < SudokuLogicConstant.SIZE_OF_PUZZLE; i++) {
			if (actual[i][col] != 0) {
				str = str.replace(Integer.toString(actual[i][col]), "");
			}
		}
		// check into minigrid

		int startC = col - (col % 3);
		int startR = row - (row % 3);
		for (int i = startR; i < startR + 3; i++) {
			for (int j = startC; j < startC + 3; j++) {
				if (actual[i][j] != 0) {
					str = str.replace(Integer.toString(actual[i][j]), "");
				}
			}
		}
		if (str == "") {
			throw new Exception(SudokuLogicConstant.INVALID_MOVE);
		}
		return str;
	}

	// Calculates the possible values for all the cell
	public boolean CheckColumnsAndRows() throws Exception {
		boolean change = false;
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				if (actual[r][c] == 0) {
					try {
						possible[r][c] = CalculatePossibleValues(r, c);
					} catch (Exception ex) {
						throw new Exception(SudokuLogicConstant.INVALID_MOVE);
					}
					if (possible[r][c].length() == 1) {
						actual[r][c] = Integer.parseInt(possible[r][c]);
						change = true;
						totalscore += 1;
					}
				}

			}
		}
		return change;

	}

	// Look for Lone Rangers in Rows
	public boolean LookForLoneRangersinRows() {
		boolean changes = false;
		int occurrence = 0, cPos = 0, rPos = 0;
		// check by row
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int n = 1; n <= SudokuLogicConstant.SIZE_OF_PUZZLE; n++) {
				occurrence = 0;
				for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
					if (actual[r][c] == 0
							&& possible[r][c].contains(Integer.toString(n))) {
						occurrence += 1;
						// if multiple occurrence, not a lone ranger anymore
						if (occurrence > 1) {
							break;
						}
						cPos = c;
						rPos = r;
					}
				}
				if (occurrence == 1) {
					// number is confirmed
					actual[rPos][cPos] = n;
					changes = true;
					totalscore += 2;
				}
			}
		}
		return changes;

	}

	// Look for Lone Rangers in Columns
	public boolean LookForLoneRangersinColumns() {
		boolean changes = false;
		int occurrence = 0, cPos = 0, rPos = 0;
		// check by row
		for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
			for (int n = 1; n <= SudokuLogicConstant.SIZE_OF_PUZZLE; n++) {
				occurrence = 0;
				for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
					if (actual[r][c] == 0
							&& possible[r][c].contains(Integer.toString(n))) {
						occurrence += 1;
						// if multiple occurrence, not a lone ranger anymore
						if (occurrence > 1) {
							break;
						}
						cPos = c;
						rPos = r;
					}
				}
				if (occurrence == 1) {
					// number is confirmed
					actual[rPos][cPos] = n;
					changes = true;
					totalscore += 2;
				}
			}
		}
		return changes;
	}

	// Look for lone rangers in Minigrids
	public boolean LookForLoneRangersinMinigrids() {
		boolean changes = false;
		int occurrence = 0, cPos = 0, rPos = 0;
		boolean NextMiniGrid;
		for (int n = 1; n <= SudokuLogicConstant.SIZE_OF_PUZZLE; n++) {
			for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r = r + 3) {
				for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c = c + 3) {
					NextMiniGrid = false;
					occurrence = 0;
					for (int rr = 0; rr < 3; rr++) {
						for (int cc = 0; cc < 3; cc++) {
							if (actual[r + rr][c + cc] == 0
									&& possible[r + rr][c + cc]
											.contains(Integer.toString(n))) {
								occurrence += 1;
								cPos = c + cc;
								rPos = r + rr;
								if (occurrence > 1) {
									NextMiniGrid = true;
									break;
								}
							}
						}
						if (NextMiniGrid) {
							break;
						}
					}
					// number is confirmed
					if (!NextMiniGrid && occurrence == 1) {
						actual[rPos][cPos] = n;
						changes = true;
						totalscore += 2;
					}
				}
			}
		}

		return changes;
	}

	// Check if the puzzle is solved
	public boolean IsPuzzleSolved() {
		String pattern = "";

		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			pattern = SudokuLogicConstant.ALL_POSS_NUMBERS;
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				pattern = pattern.replace(Integer.toString(actual[r][c]), "");
			}
			if (pattern.length() > 0) {
				return false;
			}
		}
		for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
			pattern = SudokuLogicConstant.ALL_POSS_NUMBERS;
			for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
				pattern = pattern.replace(Integer.toString(actual[r][c]), "");
			}
			if (pattern.length() > 0) {
				return false;
			}
		}
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r = r + 3) {
			pattern = SudokuLogicConstant.ALL_POSS_NUMBERS;
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c = c + 3) {
				for (int rr = 0; rr < 3; rr++) {
					for (int cc = 0; cc < 3; cc++) {
						pattern = pattern.replace(
								Integer.toString(actual[r + rr][c + cc]), "");

					}
				}
			}
			if (pattern.length() > 0) {
				return false;
			}
		}
		return true;
	}

	// Look for Twins in Rows
	public boolean LookForTwinsinRows() throws Exception {
		boolean changes = false;

		// for each row, check each column in the row
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				// if two possible values check for twins
				if (actual[r][c] == 0 && possible[r][c].length() == 2) {
					// scan columns in this row
					for (int cc = c + 1; cc < SudokuLogicConstant.SIZE_OF_PUZZLE; cc++) {
						if (possible[r][cc] == possible[r][c]) {
							for (int ccc = 0; ccc < SudokuLogicConstant.SIZE_OF_PUZZLE; ccc++) {
								if (actual[r][ccc] == 0 && (c != ccc)
										&& (ccc != cc)) {
									String original_possible = possible[r][ccc];
									possible[r][ccc] = possible[r][ccc]
											.replace(Character
													.toString(possible[r][c]
															.charAt(0)), "");
									possible[r][ccc] = possible[r][ccc]
											.replace(Character
													.toString(possible[r][c]
															.charAt(1)), "");
									if (original_possible != possible[r][ccc]) {
										changes = true;
									}
									if (possible[r][ccc] == "") {
										throw new Exception(
												SudokuLogicConstant.INVALID_MOVE);
									}

									if (possible[r][ccc].length() == 1) {
										actual[r][ccc] = Integer
												.parseInt(possible[r][ccc]);
										totalscore += 3;
									}
								}
							}
						}
					}
				}
			}
		}
		return changes;
	}

	// Look for Twins in Columns
	public boolean LookForTwinsinColumns() throws Exception {
		boolean changes = false;

		// for each row, check each column in the row
		for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
			for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
				// if two possible values check for twins
				if (actual[r][c] == 0 && possible[r][c].length() == 2) {
					// scan columns in this row
					for (int rr = r + 1; rr < SudokuLogicConstant.SIZE_OF_PUZZLE; rr++) {
						if (possible[rr][c] == possible[r][c]) {
							for (int rrr = 0; rrr < SudokuLogicConstant.SIZE_OF_PUZZLE; rrr++) {
								if (actual[rrr][c] == 0 && (r != rrr)
										&& (rrr != rr)) {
									String original_possible = possible[rrr][c];
									possible[rrr][c] = possible[rrr][c]
											.replace(Character
													.toString(possible[r][c]
															.charAt(0)), "");
									possible[rrr][c] = possible[rrr][c]
											.replace(Character
													.toString(possible[r][c]
															.charAt(1)), "");
									if (original_possible != possible[rrr][c]) {
										changes = true;
									}
									if (possible[rrr][c] == "") {
										throw new Exception(
												SudokuLogicConstant.INVALID_MOVE);
									}

									if (possible[rrr][c].length() == 1) {
										actual[rrr][c] = Integer
												.parseInt(possible[rrr][c]);
										totalscore += 3;
									}
								}
							}
						}
					}
				}
			}
		}
		return changes;
	}

	// Look for Twins in Minigrids
	public boolean LookForTwinsinMinigrids() throws Exception {

		boolean changes = false;

		// look for twins in each cell
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				// if two possible values, check for twins
				if (actual[r][c] == 0 && possible[r][c].length() == 2) {
					int startR = r - r % 3;
					int startC = c - c % 3;
					for (int rr = startR; rr < startR + 3; rr++) {
						for (int cc = startC; cc < startC + 3; cc++) {
							if ((cc != c || rr != r)
									&& possible[rr][cc] == possible[r][c]) {
								for (int rrr = startR; rrr < startR + 3; rrr++) {
									for (int ccc = startC; ccc < startC + 3; ccc++) {
										if (actual[rrr][ccc] == 0
												&& possible[r][c] != possible[rrr][ccc]) {
											String original_possible = possible[rrr][ccc];
											possible[rrr][ccc] = possible[rrr][ccc]
													.replace(
															Character
																	.toString(possible[r][c]
																			.charAt(0)),
															"");
											possible[rrr][ccc] = possible[rrr][ccc]
													.replace(
															Character
																	.toString(possible[r][c]
																			.charAt(1)),
															"");
											if (original_possible != possible[rrr][ccc]) {
												changes = true;
											}
											if (possible[rrr][ccc].length() == 0) {
												throw new Exception(
														SudokuLogicConstant.INVALID_MOVE);
											}
											if (possible[rrr][ccc].length() == 1) {
												actual[rrr][ccc] = Integer
														.parseInt(possible[rrr][ccc]);
												totalscore += 3;
											}
										}

									}
								}

							}
						}
					}
				}
			}
		}
		return changes;
	}

	// Look for Triplets in Minigrids
	public boolean LookForTripletsinMinigrids() throws Exception {
		boolean changes = false;
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				if (actual[r][c] == 0 && possible[r][c].length() == 3) {
					String tripletsLocation = Integer.toString(r)
							+ Integer.toString(c);
					int startR = r - r % 3;
					int startC = c - c % 3;
					for (int rr = startR; rr < startR + 3; rr++) {
						for (int cc = startC; cc < startC + 3; cc++) {
							if (((rr != r || cc != c) && possible[rr][cc] == possible[r][c])
									|| (possible[rr][cc].length() == 2
											&& possible[r][c]
													.contains(Character
															.toString(possible[rr][cc]
																	.charAt(0))) && possible[r][c]
												.contains(Character
														.toString(possible[rr][cc]
																.charAt(1))))) {
								tripletsLocation += Integer.toString(rr)
										+ Integer.toString(cc);

							}

						}
					}
					if (tripletsLocation.length() == 6) {
						for (int rrr = startR; rrr < startR + 3; rrr++) {
							for (int ccc = startC; ccc < startC + 3; ccc++) {
								if (actual[rrr][ccc] == 0
										&& rrr != Integer.parseInt(Character
												.toString(tripletsLocation
														.charAt(0)))
										&& ccc != Integer.parseInt(Character
												.toString(tripletsLocation
														.charAt(1)))
										&& rrr != Integer.parseInt(Character
												.toString(tripletsLocation
														.charAt(2)))
										&& ccc != Integer.parseInt(Character
												.toString(tripletsLocation
														.charAt(3)))
										&& rrr != Integer.parseInt(Character
												.toString(tripletsLocation
														.charAt(4)))
										&& ccc != Integer.parseInt(Character
												.toString(tripletsLocation
														.charAt(5)))) {
									String original_possible = possible[rrr][ccc];
									possible[rrr][ccc] = possible[rrr][ccc]
											.replace(Character
													.toString(possible[r][c]
															.charAt(0)), "");
									possible[rrr][ccc] = possible[rrr][ccc]
											.replace(Character
													.toString(possible[r][c]
															.charAt(1)), "");
									possible[rrr][ccc] = possible[rrr][ccc]
											.replace(Character
													.toString(possible[r][c]
															.charAt(2)), "");

									if (original_possible != possible[rrr][ccc]) {
										changes = true;
									}
									if (possible[rrr][ccc].length() == 0) {
										throw new Exception(
												SudokuLogicConstant.INVALID_MOVE);
									}
									if (possible[rrr][ccc].length() == 1) {
										actual[rrr][ccc] = Integer
												.parseInt(possible[rrr][ccc]);
										totalscore += 4;
									}
								}

							}
						}
					}
				}
			}
		}
		return changes;
	}

	// Look for Triplets in Rows
	public boolean LookForTripletsinRows() throws Exception {
		boolean changes = false;
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				if (actual[r][c] == 0 && possible[r][c].length() == 3) {
					String tripletsLocation = Integer.toString(r)
							+ Integer.toString(c);
					for (int cc = 0; cc < SudokuLogicConstant.SIZE_OF_PUZZLE; cc++) {
						if ((c != cc && possible[r][cc] == possible[r][c])
								|| (possible[r][cc].length() == 2
										&& possible[r][c].contains(Character
												.toString(possible[r][cc]
														.charAt(0))) && possible[r][c]
											.contains(Character
													.toString(possible[r][cc]
															.charAt(1))))) {
							tripletsLocation += Integer.toString(r)
									+ Integer.toString(cc);

						}
					}
					if (tripletsLocation.length() == 6) {
						for (int ccc = 0; ccc < SudokuLogicConstant.SIZE_OF_PUZZLE; ccc++) {
							if (actual[r][ccc] == 0
									&& ccc != Integer.parseInt(Character
											.toString(tripletsLocation
													.charAt(1)))
									&& ccc != Integer.parseInt(Character
											.toString(tripletsLocation
													.charAt(3)))
									&& ccc != Integer.parseInt(Character
											.toString(tripletsLocation
													.charAt(5)))) {
								String original_possible = possible[r][ccc];
								possible[r][ccc] = possible[r][ccc].replace(
										Character.toString(possible[r][c]
												.charAt(0)), "");
								possible[r][ccc] = possible[r][ccc].replace(
										Character.toString(possible[r][c]
												.charAt(1)), "");
								possible[r][ccc] = possible[r][ccc].replace(
										Character.toString(possible[r][c]
												.charAt(2)), "");
								if (original_possible != possible[r][ccc]) {
									changes = true;
								}
								if (possible[r][ccc].length() == 0) {
									throw new Exception(
											SudokuLogicConstant.INVALID_MOVE);
								}
								if (possible[r][ccc].length() == 1) {
									actual[r][ccc] = Integer
											.parseInt(possible[r][ccc]);
									totalscore += 4;
								}
							}
						}
					}
				}
			}
		}
		return changes;

	}

	// Look for Triplets in Columns
	public boolean LookForTripletsinColumns() throws Exception {
		boolean changes = false;
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				if (actual[r][c] == 0 && possible[r][c].length() == 3) {
					String tripletsLocation = Integer.toString(r)
							+ Integer.toString(c);
					for (int rr = 0; rr < SudokuLogicConstant.SIZE_OF_PUZZLE; rr++) {
						if ((r != rr && possible[rr][c] == possible[r][c])
								|| (possible[rr][c].length() == 2
										&& possible[r][c].contains(Character
												.toString(possible[rr][c]
														.charAt(0))) && possible[r][c]
											.contains(Character
													.toString(possible[rr][c]
															.charAt(1))))) {
							tripletsLocation += Integer.toString(rr)
									+ Integer.toString(c);

						}
					}
					if (tripletsLocation.length() == 6) {
						for (int rrr = 0; rrr < SudokuLogicConstant.SIZE_OF_PUZZLE; rrr++) {
							if (actual[rrr][c] == 0
									&& rrr != Integer.parseInt(Character
											.toString(tripletsLocation
													.charAt(0)))
									&& rrr != Integer.parseInt(Character
											.toString(tripletsLocation
													.charAt(2)))
									&& rrr != Integer.parseInt(Character
											.toString(tripletsLocation
													.charAt(4)))) {
								String original_possible = possible[rrr][c];
								possible[rrr][c] = possible[rrr][c].replace(
										Character.toString(possible[r][c]
												.charAt(0)), "");
								possible[rrr][c] = possible[rrr][c].replace(
										Character.toString(possible[r][c]
												.charAt(1)), "");
								possible[rrr][c] = possible[rrr][c].replace(
										Character.toString(possible[r][c]
												.charAt(2)), "");
								if (original_possible != possible[rrr][c]) {
									changes = true;
								}
								if (possible[rrr][c].length() == 0) {
									throw new Exception(
											SudokuLogicConstant.INVALID_MOVE);
								}
								if (possible[rrr][c].length() == 1) {
									actual[rrr][c] = Integer
											.parseInt(possible[rrr][c]);
									totalscore += 4;
								}
							}
						}
					}
				}
			}
		}
		return changes;

	}

	// Steps to solve the puzzle
	public boolean SolvePuzzle() throws Exception {
		boolean changes = false;
		boolean ExitLoop = false;
		try {
			do {// Look for Triplets in rows
				do {// Look for Triplets in columns
					do {// Look for Triplets in Minigrids
						do {// Look for Twins in rows
							do {// Look for Twins in columns
								do {// Look for Twins in Minigrids
									do {// Look for Lone Rangers in rows
										do {// Look for Lone Rangers in columns
											do {// Look for Lone Rangers in
												// Minigrids
												do {// CRME
													changes = CheckColumnsAndRows();
													if (IsPuzzleSolved()) {
														ExitLoop = true;
														break;
													}
												} while (changes);
												if (ExitLoop) {
													break;
												}
												changes = LookForLoneRangersinMinigrids();
												if (IsPuzzleSolved()) {
													ExitLoop = true;
													break;
												}
											} while (changes);
											if (ExitLoop) {
												break;
											}
											changes = LookForLoneRangersinColumns();
											if (IsPuzzleSolved()) {
												ExitLoop = true;
												break;
											}
										} while (changes);
										if (ExitLoop) {
											break;
										}
										changes = LookForLoneRangersinRows();
										if (IsPuzzleSolved()) {
											ExitLoop = true;
											break;
										}
									} while (changes);
									if (ExitLoop) {
										break;
									}
									changes = LookForTwinsinMinigrids();
									if (IsPuzzleSolved()) {
										ExitLoop = true;
										break;
									}
								} while (changes);
								if (ExitLoop) {
									break;
								}
								changes = LookForTwinsinColumns();
								if (IsPuzzleSolved()) {
									ExitLoop = true;
									break;
								}
							} while (changes);
							if (ExitLoop) {
								break;
							}
							changes = LookForTwinsinRows();
							if (IsPuzzleSolved()) {
								ExitLoop = true;
								break;
							}
						} while (changes);
						if (ExitLoop) {
							break;
						}
						changes = LookForTripletsinMinigrids();
						if (IsPuzzleSolved()) {
							ExitLoop = true;
							break;
						}
					} while (changes);
					if (ExitLoop) {
						break;
					}
					changes = LookForTripletsinColumns();
					if (IsPuzzleSolved()) {
						ExitLoop = true;
						break;
					}
				} while (changes);
				if (ExitLoop) {
					break;
				}
				changes = LookForTripletsinRows();
				if (IsPuzzleSolved()) {
					ExitLoop = true;
					break;
				}
			} while (changes);

		} catch (Exception e) {
			throw new Exception(SudokuLogicConstant.INVALID_MOVE);
		}
		return IsPuzzleSolved();
	}

	// Find the cell with the small number of possible values
	public void FindCellWithFewestPossibleValues(int[] data) {
		int min = 10;
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				if (actual[r][c] == 0 && possible[r][c].length() < min) {
					min = possible[r][c].length();
					data[0] = r;
					data[1] = c;
				}
			}
		}
	}

	public int[][] CopyActual(int[][] actu) {
		int[][] re = new int[SudokuLogicConstant.SIZE_OF_PUZZLE][SudokuLogicConstant.SIZE_OF_PUZZLE];
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				re[r][c] = actu[r][c];
			}
		}
		return re;
	}

	public String[][] CopyPoss(String[][] poss) {
		String[][] re = new String[SudokuLogicConstant.SIZE_OF_PUZZLE][SudokuLogicConstant.SIZE_OF_PUZZLE];
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				re[r][c] = poss[r][c];
			}
		}
		return re;
	}

	// Solve puzzle by brute force
	public void SolvePuzzleByBruteForce() {
		////LogHelper.d("begin BruteForce search");
		int c = 0, r = 0;
		totalscore += 5;
		int[] tmp = new int[2];
		FindCellWithFewestPossibleValues(tmp);
		r = tmp[0];
		c = tmp[1];
		String possibleValues = possible[r][c];

		ActualStack.push(CopyActual(actual));
		PossibleStack.push(CopyPoss(possible));
		// select one value and try
		for (int i = 0; i < possibleValues.length(); i++) {
			actual[r][c] = Integer.parseInt(Character.toString(possibleValues
					.charAt(i)));
			try {
				if (SolvePuzzle()) {
					BruteForceStop = true;
					return;
				} else {
					SolvePuzzleByBruteForce();
					if (BruteForceStop) {
						return;
					}
				}
			} catch (Exception e) {
				// accumulate the total score
				totalscore += 5;
				actual = (int[][]) ActualStack.pop();
				possible = (String[][]) PossibleStack.pop();
			}
		}
	}

	// Get Puzzle
	public String GetPuzzle(int level) {
		int score = 0;
		int[] score_tmp = new int[1];
		String result = "";
		do {
			result = GenerateNewPuzzle(level, score_tmp);
			//LogHelper.d("success get string from generater");
			score = score_tmp[0];
			if (result != "") {
				switch (level) {
				case Constants.DIFFICULTY_EASY:
					//return result;
					if (score >= 42 && score <= 46) {
						return result;
					}
					break;
				case Constants.DIFFICULTY_MEDIUM:
					if (score >= 49 && score <= 53) {
						return result;
					}
					break;
				case Constants.DIFFICULTY_HARD:
					if (score >= 56 && score <= 60) {
						return result;
					}
					break;
				case Constants.DIFFICULTY_EVIL:
					if (score >= 112 && score <= 116) {
						return result;
					}
					break;
				default:
					return result;
				}
			}
		} while (true&&!Abort);
		return "";
	}

	private String GenerateNewPuzzle(int level, int[] score) {

		String str = "";
		int numberofemptycells = 0;
		// initialize the entire board
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				actual[r][c] = 0;
				possible[r][c] = "";
			}
		}
		// clear stacks
		ActualStack.clear();
		PossibleStack.clear();
		// fill the board with numbers by solving an empty grid
		try {
			if (!SolvePuzzle()) {
				SolvePuzzleByBruteForce();
			}
		} catch (Exception e) {
			return "";
		}

		// make a backup copy of the actual array
		actual_backup = actual.clone();
		switch (level) {
		case Constants.DIFFICULTY_EASY:
			// //LogHelper.d("before random");
			numberofemptycells = RandomNumber(40, 45);
			// //LogHelper.d("after random");
			break;
		case Constants.DIFFICULTY_MEDIUM:
			numberofemptycells = RandomNumber(46, 49);
			break;
		case Constants.DIFFICULTY_HARD:
			numberofemptycells = RandomNumber(50, 53);
			break;
		case Constants.DIFFICULTY_EVIL:
			numberofemptycells = RandomNumber(54, 58);
			break;
		default:
			break;
		}
		//LogHelper.d("random finished");
		ActualStack.clear();
		PossibleStack.clear();
		BruteForceStop = false;

		// create empty cells
		CreateEmptyCells(numberofemptycells);
		// convert the values in teh actual array to a string
		str = "";
		for (int r = 0; r < SudokuLogicConstant.SIZE_OF_PUZZLE; r++) {
			for (int c = 0; c < SudokuLogicConstant.SIZE_OF_PUZZLE; c++) {
				str += Integer.toString(actual[r][c]);

			}
		}
		// verify the puzzle has only one solution
		//LogHelper.d("success to start check only one solution");
		int tries = 0;
		do {
			totalscore = 0;
			try {
				if (!SolvePuzzle()) {
					// if puzzle is not solved and this is a level 1 to 3 puzzle
					if (level < 4) {
						VacateAnotherPairOfCells(str);
						tries += 1;

					} else {
						// level 4 puzzle does not guarantee single solution and
						// potentially need guessing
						SolvePuzzleByBruteForce();
						break;
					}
				} else {
					// puzzle indeed has 1 solution

					break;
				}
			} catch (Exception e) {
				return "";
			}
			// try maximimun 50 times
			if (tries > SudokuLogicConstant.MAX_TRY) {
				return "";
			}
		} while (true);

		score[0] = totalscore;
		return str;
	}

	private void VacateAnotherPairOfCells(String str) {
		int c = 0, r = 0;
		do {
			c = RandomNumber(0, 8);
			r = RandomNumber(0, 8);

		} while (!Character.toString(str.charAt(c + r * 9)).equalsIgnoreCase(
				"0"));
		// restore the value of the cell from the actual_backup array
		char[] tmp = str.toCharArray();
		tmp[c + r * 9] = (char) actual_backup[r][c];
		str = tmp.toString();
		// look for another pair of cells to vacate
		do {
			c = RandomNumber(0, 8);
			r = RandomNumber(0, 8);
		} while (Character.toString(str.charAt(c + r * 9))
				.equalsIgnoreCase("0"));

		// remove the cell from the str
		tmp = str.toCharArray();
		tmp[c + r * 9] = '0';
		str = tmp.toString();
		// remove the symmetrical cell from the str
		tmp = str.toCharArray();
		tmp[9 - c + (9 - r) * 9] = '0';
		str = tmp.toString();
		// reinitialize the board
		int counter = 0;
		for (int rowr = 0; rowr < SudokuLogicConstant.SIZE_OF_PUZZLE; rowr++) {
			for (int column = 0; column < SudokuLogicConstant.SIZE_OF_PUZZLE; column++) {
				if (Integer.parseInt(Character.toString(str.charAt(counter))) != 0) {
					actual[rowr][column] = Integer.parseInt(Character
							.toString(str.charAt(counter)));
					possible[rowr][column] = Integer.toString(str
							.charAt(counter));

				} else {
					actual[rowr][column] = 0;
					possible[rowr][column] = "";
				}
				counter += 1;
			}
		}
	}

	public boolean Solve() {

		return false;

	}
}
