package org.isi.sudoku;

import java.util.ArrayList;
import java.util.List;

import org.isi.sudoku.utility.LogHelper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleBoard extends View implements View.OnLongClickListener, View.OnTouchListener {
	
	private final Game game;
	
	private enum CellType {
		
		CELL_NUMBAR, CELL_INVALID, CELL_FIXED, CELL_BLANK, CELL_MOVABLE, CELL_UNKNOWN;
	}
	
	private enum Mode {
		
		HOVER, DRAG;
	}
	
	private final Rect INVISIBLE = new Rect(-99, -99, -99, -99);
	
//	private final int ID = 43;	// unique id for this view
	
	private int[] cellNumbers = new int[9 * 11];	// one row for number bar, one extra row as separator and a 9*9 puzzle 
	
	protected List<OneStep> markList;
	
	public int[] getCellNumbersForStore() {
		return cellNumbers;
	}
	
	public void setCellNumbersForStore(String savedCellNumbers) {
		//LogHelper.d("setCellNumbersForStore start");
		for(int i=0;i<cellStatus.length;i++){
			
			cellNumbers[i]=Integer.parseInt(Character.toString(savedCellNumbers.charAt(i)));
			////LogHelper.d(Character.toString(savedCellNumbers.charAt(i)));
		}
		
	}

	private CellType[] cellStatus = new CellType[9 * 11];	// byte arrays indicating different types of cells
	
	public int[] getCellStatusForStore() {
		int[] result= new int[cellStatus.length];
		int tmp=-1;
		for(int i=0;i<cellStatus.length;i++){
			tmp=-1;
			if(cellStatus[i]==CellType.CELL_NUMBAR){
				tmp=0;
			}
			else if(cellStatus[i]==CellType.CELL_INVALID){
				tmp=1;
			}
			else if(cellStatus[i]==CellType.CELL_FIXED){
				tmp=2;
			}
			else if(cellStatus[i]==CellType.CELL_BLANK){
				tmp=3;
			}
			else if(cellStatus[i]==CellType.CELL_MOVABLE){
				tmp=4;
			}
			else if(cellStatus[i]==CellType.CELL_UNKNOWN){
				tmp=5;
			}
			result[i]=tmp;
		}
		return result;
	}
	
	public void setCellStatusForStore(String savedCellStatus) {
		//LogHelper.d("setCellStatusForStore start");
		int tmp=-1;
		for(int i=0;i<cellStatus.length;i++){
			tmp=-1;
			tmp=Integer.parseInt(Character.toString(savedCellStatus.charAt(i)));
			if(tmp==0){
				cellStatus[i]=CellType.CELL_NUMBAR;
			}
			else if(tmp==1){
				cellStatus[i]=CellType.CELL_INVALID;
			}
			else if(tmp==2){
				cellStatus[i]=CellType.CELL_FIXED;
			}
			else if(tmp==3){
				cellStatus[i]=CellType.CELL_BLANK;
			}
			else if(tmp==4){
				cellStatus[i]=CellType.CELL_MOVABLE;
			}
			else if(tmp==5){
				cellStatus[i]=CellType.CELL_UNKNOWN;
			}
		}
	}

	private int gridWidth;
	private int gridHeight;
	private float cellLength;
	
	// current selection
	private int selX = -1;
	private int selY = -1;
	private int selNum = 0;
	private final ArrayList<Rect> selRects = new ArrayList<Rect>();
	private final ArrayList<Rect> errorHints = new ArrayList<Rect>();
	private final Rect movRect = new Rect();	// for drawing the cell being dragged
	
	private Mode action;	// drag or tag & select
	private int longTouchThreshhold;	// small movement tolerance
	
	// various paint objects for different background, grid lines, numbers, etc.
	private Paint background, dragging;
	private Paint grid_dark, major_dark, minor_dark, grid_light, major_light, minor_light;
	private Paint number_fixed, number;
	private float textOffsetX, textOffsetY;
	private Paint selected;
	private Paint error;
	
	public PuzzleBoard(Context context) {
		
		this(context, null, 0);
	}
	
	public PuzzleBoard(Context context, AttributeSet attrs) {
		
		this(context, attrs, 0);
	}
	
	public PuzzleBoard(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		this.game = (Game) context;
		this.setOnTouchListener(this);
		this.setOnLongClickListener(this);
		
//		setId(ID);	// View.onSaveInstanceState() will be called on those views that have IDs
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		initPuzzle(game.getDiff_Level());
		initPaint();
	}

	private void initPuzzle(int diff_Level) {

		if(diff_Level!=-1){
		// get puzzle from logic unit
		int[] puzzle = game.getPuzzle();
		
		// initialize puzzle board
		for (int i = 0; i < 9; i++) {
			
			cellNumbers[i] = i + 1;
			cellStatus[i] = CellType.CELL_NUMBAR;
		}
		for (int i = 9; i < 18; i++) {
			
			cellNumbers[i] = 0;
			cellStatus[i] = CellType.CELL_INVALID;
		}
		for (int i = 0; i < 9; i++) {
			
			for (int j = 0; j < 9; j++) {
				
				if (puzzle[i + 9 * j] == 0) {
					
					cellNumbers[i + 9 * (j + 2)] = 0;
					cellStatus[i + 9 * (j + 2)] = CellType.CELL_BLANK;
				}
				else {
					
					cellNumbers[i + 9 * (j + 2)] = puzzle[i + 9 * j];
					cellStatus[i + 9 * (j + 2)] = CellType.CELL_FIXED;
				}
			}

		}
		}
		else if(diff_Level==-1){
			//LogHelper.d("start recovery");
			setCellStatusForStore(game.getpuzzlestatus());
			setCellNumbersForStore(game.getsavepuzzle());
		}
	}

	private void initPaint() {
		
		// initialize painting colors and text
		background = new Paint();
		background.setColor(
				getResources().getColor(R.color.cell_background));
		
		grid_dark = new Paint();
		major_dark = new Paint();
		major_dark.setColor(
				getResources().getColor(R.color.grid_major_dark));
		minor_dark = new Paint();
		minor_dark.setColor(
				getResources().getColor(R.color.grid_minor_dark));
		grid_light = new Paint();
		major_light = new Paint();
		major_light.setColor(
				getResources().getColor(R.color.grid_major_light));
		minor_light = new Paint();
		minor_light.setColor(
				getResources().getColor(R.color.grid_minor_light));
		
		number_fixed = new Paint(Paint.ANTI_ALIAS_FLAG);
		number_fixed.setColor(
				getResources().getColor(R.color.number_fixed));
		number_fixed.setStyle(Style.FILL);
		number_fixed.setTextAlign(Paint.Align.CENTER);
		
		number = new Paint(Paint.ANTI_ALIAS_FLAG);
		number.setColor(
				getResources().getColor(R.color.number));
		number.setStyle(Style.FILL);
		number.setTextAlign(Paint.Align.CENTER);
		
		selected = new Paint();
		selected.setColor(
				getResources().getColor(R.color.cell_selected));
		
		error = new Paint();
		error.setColor(
				getResources().getColor(R.color.cell_dragging)); // TODO
		
		dragging = new Paint();
		dragging.setColor(
				getResources().getColor(R.color.cell_dragging));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// keep aspect ratio
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int myHeight = (int) (parentWidth / 9.0 * 11.0);
		super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.EXACTLY));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		// calculate new board dimensions and text size
		gridWidth = w;
		gridHeight = h;
		cellLength= (w - 1) / 9f;
//		getRect(selRects, selX, selY);
		
		number_fixed.setTextSize(cellLength * 0.75f);
		number.setTextSize(cellLength * 0.75f);
		FontMetrics fm = number_fixed.getFontMetrics();
		textOffsetX = cellLength / 2;
		textOffsetY = cellLength / 2 - (fm.ascent + fm.descent) / 2;
		
		movRect.set(INVISIBLE);
		
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// draw the background
		canvas.drawRect(0, 0, gridWidth, gridHeight, background);
		
		drawBoard(canvas);
		
		drawNumbers(canvas);
		
		drawSelArea(canvas);
		
		drawErrorHints(canvas);
		
		drawMovRect(canvas);
	}

	private void drawBoard(Canvas canvas) {
		
		// draw grid lines for the bar
		canvas.drawLine(0, 0, gridWidth, 0, major_light);
		canvas.drawLine(0, cellLength, gridWidth, cellLength, major_dark);
		for (int i = 0; i <= 9; i++) {

			canvas.drawLine(i * cellLength, 0, 
					i * cellLength, cellLength, major_dark);
			canvas.drawLine(i * cellLength + 1, 0, 
					i * cellLength + 1, cellLength, major_light);
		}
		
		// draw grid lines for the puzzle area
		for (int i = 0; i <= 9; i++) {
			
			if (i % 3 == 0) {	// 3*3 grid
				grid_dark = major_dark;
				grid_light = major_light;
			}
			else {	// 9*9 cell
				grid_dark = minor_dark;
				grid_light = minor_light;
			}
			
			// horizontal grid lines
			canvas.drawLine(0, (i + 2) * cellLength, 
					gridWidth, (i + 2) * cellLength, grid_dark);
			canvas.drawLine(0, (i + 2) * cellLength + 1, 
					gridWidth, (i + 2) * cellLength + 1, grid_light);
			// vertical grid lines
			canvas.drawLine(i * cellLength, 2 * cellLength, 
					i * cellLength, 2 * cellLength + gridWidth, grid_dark);
			canvas.drawLine(i * cellLength + 1, 2 * cellLength, 
					i * cellLength + 1, 2 * cellLength + gridWidth, grid_light);
		}
	}

	private void drawNumbers(Canvas canvas) {
		
		// draw numbers in the bar
		for (int i = 0; i < 9; i++) {

			canvas.drawText((i + 1) + "", 
					i * cellLength + textOffsetX, textOffsetY, 
					number_fixed);
		}

		// draw numbers in the puzzle area
		for (int i = 0; i < 9; i++) {

			for (int j = 2; j < 11; j++) {

				if (cellStatus[i + 9 * j] == CellType.CELL_FIXED) {	// draw fixed numbers
					
					canvas.drawText(getNumberInCell(i, j) + "", 
							i * cellLength + textOffsetX, 
							j * cellLength + textOffsetY, number_fixed);
				}
				else {	// draw user filled numbers
					
					int n = getNumberInCell(i, j);
					canvas.drawText(n != 0 ? n + "" : "", 
							i * cellLength + textOffsetX, 
							j * cellLength + textOffsetY, number);
				}
			}
		}
	}
	
	private void drawSelArea(Canvas canvas) {
		
		// draw the selected area
		for (Rect rect : selRects) {
			
			canvas.drawRect(rect, selected);
		}
	}
	
	private void drawErrorHints(Canvas canvas) {
		
		// draw hints when an invalid move happens
		for (Rect rect : errorHints) {
			
			canvas.drawRect(rect, error);
		}
	}
	
	private void drawMovRect(Canvas canvas) {
		
		canvas.drawRect(movRect, dragging);
		canvas.drawText(selNum + "", 
				movRect.centerX(), 
				movRect.centerY() + textOffsetY - cellLength / 2, 
				number_fixed);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		errorHints.clear();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			
				switch (getCellStatus(event.getX(), event.getY())) {
					case CELL_NUMBAR:	// ACTION_DOWN + CELL_NUMBER : start dragging, disable longClick
						
						action = Mode.DRAG;
						selNum = getNumberInCell(selX, selY);
						longTouchThreshhold = 0;
						break;
					case CELL_BLANK:	// ACTION_DOWN + CELL_BLANK : possible longClick
						
						longTouchThreshhold = 10;
						break;
					case CELL_MOVABLE:	// ACTION_DOWN + CELL_MOVABLE : possible longClick
						
						longTouchThreshhold = 10;
						break;
					default:
						
						selRects.clear();
						invalidate();
						return true;
				}
				
				break;

			case MotionEvent.ACTION_MOVE:
				
				// update moving cell if in the action is DRAG
				if (action == Mode.DRAG) {
					
					notifyMovRectChanged(event.getX(), event.getY(), movRect);
				}
				// make longTouch tolerate little movement
				longTouchThreshhold--;
				return super.onTouchEvent(event);
				
			case MotionEvent.ACTION_UP:
				
				switch (getCellStatus(event.getX(), event.getY())) {
					case CELL_BLANK:
					case CELL_MOVABLE:
						
						if (action == Mode.DRAG) {
							
							setSelectedCell(selNum);
						}
						break;
					case CELL_FIXED:
						
						selRects.clear();	// do not select fixed cells
					default:
						break;
				}
				// clear unnecessary selection 
				selNum = 0;
				action = Mode.HOVER;
				movRect.set(INVISIBLE);
				invalidate();
				
				break;
		}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {

		if (longTouchThreshhold > 0) {
			
			//LogHelper.d("popup numberpad for cell (" + selX + ", " + selY + ")");
			showNumberPad(selX, selY);
		}
		return true;
	}

	private void notifyMovRectChanged(float x, float y, Rect rect) {

		// does not actually draw the Rect, simple
		// update the Rect area and notify onDraw()
		invalidate(rect);
		// get the rectangle with the touch point as its center
		rect.set((int) (x - cellLength / 2), 
				(int) (y - cellLength / 2), 
				(int) (x + cellLength / 2), 
				(int) (y + cellLength / 2));
		invalidate(rect);
	}
	
	private int getNumberInCell(int x, int y) {

		return cellNumbers[x + 9 * y];
	}
	
	private void setNumberInCell(int x, int y, int num) {
		
		int index = x + 9 * y;
		cellNumbers[index] = num;
		cellStatus[index] = CellType.CELL_MOVABLE;
	}
	
	private void showNumberPad(int x, int y) {
		
		int unavailable[] = this.game.getUsedNumbers(x, y - 2);
		if (unavailable.length == 9) {
			
			// no numbers available
			// TODO
		} else {

			Dialog kp = new NumberPad(game, unavailable, this);
			kp.show();
		}
	}
	
	public void setSelectedCell(int n) {
		
		if (n == -1) {
			
			markList.add(new OneStep(selX, selY, getNumberInCell(selX, selY)));
			return;
		}
		
		// check if it's valid before actually setting the number 
		int[] index = this.game.setNumberInCell(selX, selY - 2, n);
		if (index == null) {
			
			//LogHelper.d("setting cell (" + selX + ", " + selY + ") to " + n);
			setNumberInCell(selX, selY, n);
			
			if (markList != null) {
				markList.add(new OneStep(selX, selY, n));
			}
		} else {
			
			errorHints.clear();
			Rect rect = new Rect();
			for (int i : index) {
				
				rect = getRect(i % 9, i / 9 + 2);
				errorHints.add(rect);
			}
		}
		invalidate();
		
		// TODO
		// recalculate used numbers
	}
	
	protected void goBack() {
		
		if (markList == null) {
			return;
		}
		for (OneStep o : markList) {
			
			this.game.setNumberInCell(o.x, o.y - 2, 0);
			setNumberInCell(o.x, o.y, 0);
		}
		invalidate();
		markList.clear();
		markList = null;
	}
	
	private CellType getCellStatus(float x, float y) {
		
		boolean validCood = (x > 0) & (x < gridWidth) & (y > 0) & (y < gridHeight);
		if (!validCood) return CellType.CELL_UNKNOWN;
		
		selX = (int) (x / cellLength);
		selY = (int) (y / cellLength);
		getRect(selRects, selX, selY);
		
		return cellStatus[selX + 9 * selY];
	}
	
	private Rect getRect(int x, int y) {
		
		Rect rect = new Rect();
		rect.set((int) (x * cellLength), (int) (y * cellLength), (int) (x * cellLength + cellLength), (int) (y * cellLength + cellLength));
		return rect;
	}
	
	private void getRect(ArrayList<Rect> rects, int x, int y) {

		rects.clear();
		
		// add the touched cell
		Rect cell = new Rect();
		cell.set((int) (x * cellLength), (int) (y * cellLength), (int) (x * cellLength + cellLength), (int) (y * cellLength + cellLength));
		rects.add(cell);
		
		if (y == 0) {	// touched number bar, no need for the "row-column-grid" highlight
			
			invalidate();
			return;
		}
		
		// calculate the coordinates of the grid
		int blockLeft = (int) (x / 3  * 3 * cellLength);
		int blockRight = (int) (blockLeft + 3 * cellLength);
		int blockTop = (int) (((y - 2) / 3 * 3 + 2) * cellLength);
		int blockBottom = (int) (blockTop + 3 * cellLength);
		
		// add the row at the left side of the grid
		if (blockLeft != 0) {
			
			Rect rowL = new Rect();
			rowL.set(0, (int) (y * cellLength), blockLeft, (int) (y * cellLength + cellLength));
			rects.add(rowL);
		}
		
		// add the row at the right side of the grid
		if (blockRight != gridWidth) {
			
			Rect rowR = new Rect();
			rowR.set(blockRight, (int) (y * cellLength), gridWidth, (int) (y * cellLength + cellLength));
			rects.add(rowR);
		}
		
		// add the row at the upper side of the grid
		if (blockTop != 2 * cellLength) {
			
			Rect columnU = new Rect();
			columnU.set((int) (x * cellLength), (int) (2 * cellLength), (int) (x * cellLength + cellLength), blockTop);
			rects.add(columnU);
		}
		
		// add the row at the lower side of the grid
		if (blockBottom != gridHeight) {
			
			Rect columnD = new Rect();
			columnD.set((int) (x * cellLength), blockBottom, (int) (x * cellLength + cellLength), (int) gridHeight);
			rects.add(columnD);
		}
		
		// add the grid
		Rect block = new Rect();
		block.set(blockLeft, blockTop, blockRight, blockBottom);
		rects.add(block);
		
		invalidate();
	}

	protected void finishPuzzle(int[] solvedPuzzle) {
		org.isi.sudoku.logic.Sudoku.RunAlgorithm();
		int[] puzzletmp=org.isi.sudoku.logic.Sudoku.TransferToOneDimension();
		
		for (int i = 0; i < 9; i++) {
			
			for (int j = 0; j < 9; j++) {
				solvedPuzzle[i + 9 * j]=puzzletmp[i + 9 * j];
				cellNumbers[i + 9 * (j + 2)] = solvedPuzzle[i + 9 * j];
				cellStatus[i + 9 * (j + 2)] = CellType.CELL_FIXED;
			}
		}
		invalidate();
	}
}
