package org.isi.sudoku.logic;

public class SudokuLogic {
	static int row, col, filled = 1;
	static int nosolve = 0, totalfilled = 0;
	static int ur, uc, mn;
	static int posnum[][] = new int[9][9];
    static int poss[][][] = new int[9][9][9];
	static int single[] = new int[81];

	// set new puzzl
	public static int puzzle_map[][] = { { 0, 7, 0, 6, 0, 0, 1, 0, 0 },
			{ 8, 0, 9, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 7, 4, 0, 0, 2, 3 },
			{ 2, 0, 8, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 6, 5, 2, 0, 4 },
			{ 0, 4, 0, 3, 0, 0, 7, 0, 0 }, { 0, 0, 0, 0, 1, 0, 0, 0, 2 },
			{ 9, 0, 0, 2, 0, 6, 0, 0, 8 }, { 0, 0, 4, 0, 8, 0, 9, 6, 0 } };

	// validate puzzle map (check whether num exists in map or not)
	public static boolean validate(int num) {
		int r, c, boxr = 0, boxc = 0, x, y;

		// check column
		for (r = 0; r < 9; r++)
			if (num == puzzle_map[r][col])
				return false;
		// check row
		for (c = 0; c < 9; c++)
			if (num == puzzle_map[row][c])
				return false;

		if (row >= 0 && row < 3)
			boxr = 0;
		if (row >= 3 && row < 6)
			boxr = 3;
		if (row >= 6 && row < 9)
			boxr = 6;
		if (col >= 0 && col < 3)
			boxc = 0;
		if (col >= 3 && col < 6)
			boxc = 3;
		if (col >= 6 && col < 9)
			boxc = 6;
		// check box
		for (x = boxr; x < boxr + 3; x++)
			for (y = boxc; y < boxc + 3; y++)
				if (num == puzzle_map[x][y])
					return false;
		return true;
	}

	public static boolean solve() {
		nosolve++;
		int count2 = 0, fill = 0, c = 0, cl = 0, rw = 0;
		boolean cond = false;

		for (row = 0; row < 9; row++) {
			for (col = 0; col < 9; col++) {

				if (puzzle_map[row][col] == 0) {
					count2 = 0;
					for (int nm = 1; nm <= 9; nm++) {
						cond = validate(nm);
						if (cond) {
							fill = nm;
							count2++;
						}
					}
					if (count2 == 1) {
						puzzle_map[row][col] = fill;
						mn = fill;
						filled++;
						ur = row;
						uc = col;
						return true;
					}
				}

			}
		}

		for (row = 0; row < 9; row++) {
			for (int nm = 1; nm <= 9; nm++) {
				c = 0;
				for (col = 0; col < 9; col++) {
					if (puzzle_map[row][col] == 0)
						if (validate(nm)) {
							cl = col;
							c++;
						}
				}
				if (c == 1) {
					puzzle_map[row][cl] = nm;
					mn = nm;
					filled++;
					ur = row;
					uc = cl;
					return true;
				}

			}
		}

		for (col = 0; col < 9; col++) {
			for (int nm = 1; nm <= 9; nm++) {
				c = 0;
				for (row = 0; row < 9; row++) {
					if (puzzle_map[row][col] == 0)
						if (validate(nm)) {
							c++;
							rw = row;
						}
				}
				if (c == 1) {
					puzzle_map[rw][col] = nm;
					mn = nm;
					filled++;
					ur = rw;
					uc = col;
					return true;
				}

			}
		}

		totalfilled += filled;
		return false;
	}

	// generate the map puzzle
	public static void generateCandidates() {
		int num, p, n, ht;
		for (row = 0; row < 9; row++) {
			for (col = 0; col < 9; col++) {
				ht = 0;
				if (puzzle_map[row][col] == 0)
					for (n = 1; n <= 9; n++)
						if (validate(n))
							poss[row][col][ht++] = n;
			}
		}
		for (int rf = 0; rf < 9; rf++) {
			for (int cf = 0; cf < 9; cf++) {
				p = 0;
				num = 0;
				for (int h = 0; h < 9; h++) {
					if (poss[rf][cf][h] == 0)
						break;
					num += poss[rf][cf][h] * Math.pow(10, p++);
				}
				posnum[rf][cf] = num;
			}
		}
	}

	public static void updateCandidates() {
		int upr, uph = 0, uh, upc, x, y, boxr = 0, boxc = 0, p, num;
		for (upr = 0; upr < 9; upr++) {
			uph = 0;
			while (poss[upr][uc][uph] != 0) {
				if (poss[upr][uc][uph] == mn && upr != ur) {
					uh = uph;
					while (poss[upr][uc][uh] != 0 && uh < 8)
						poss[upr][uc][uh] = poss[upr][uc][++uh];
					if (uh == 8)
						poss[upr][uc][8] = 0;
					p = 0;
					num = 0;
					for (int htf = 0; htf < 9; htf++) {
						if (poss[upr][uc][htf] == 0)
							break;
						num += poss[upr][uc][htf] * Math.pow(10, p++);
					}
					posnum[upr][uc] = num;
				}
				if (poss[upr][uc][uph] == mn && upr == ur) {
					int hp = -1;
					while (poss[upr][uc][++hp] != 0)
						poss[upr][uc][hp] = 0;
					posnum[upr][uc] = 0;
				}
				uph++;
			}
		}
		for (upc = 0; upc < 9; upc++) {
			uph = 0;
			while (poss[ur][upc][uph] != 0) {
				if (poss[ur][upc][uph] == mn && upc != uc) {
					uh = uph;
					while (poss[ur][upc][uh] != 0 && uh < 8)
						poss[ur][upc][uh] = poss[ur][upc][++uh];
					if (uh == 8)
						poss[ur][upc][8] = 0;
					p = 0;
					num = 0;
					for (int htf = 0; htf < 9; htf++) {
						if (poss[ur][upc][htf] == 0)
							break;
						num += poss[ur][upc][htf] * Math.pow(10, p++);
					}
					posnum[ur][upc] = num;
				}
				if (poss[ur][upc][uph] == mn && upc == uc) {
					int hp = -1;
					while (poss[ur][upc][++hp] != 0)
						poss[ur][upc][hp] = 0;
					posnum[ur][upc] = 0;
				}
				uph++;
			}
		}
		if (ur >= 0 && ur < 3)
			boxr = 0;
		if (ur >= 3 && ur < 6)
			boxr = 3;
		if (ur >= 6 && ur < 9)
			boxr = 6;
		if (uc >= 0 && uc < 3)
			boxc = 0;
		if (uc >= 3 && uc < 6)
			boxc = 3;
		if (uc >= 6 && uc < 9)
			boxc = 6;
		for (x = boxr; x < boxr + 3; x++) {
			for (y = boxc; y < boxc + 3; y++) {
				while (poss[x][y][uph] != 0) {
					if (poss[x][y][uph] == mn && (x != ur || y != uc)) {
						uh = uph;
						while (poss[x][y][uh] != 0 && uh < 8)
							poss[x][y][uh] = poss[x][y][++uh];
						if (uh == 8)
							poss[x][y][8] = 0;
						p = 0;
						num = 0;
						for (int htf = 0; htf < 9; htf++) {
							if (poss[x][y][htf] == 0)
								break;
							num += poss[x][y][htf] * Math.pow(10, p++);
						}
						posnum[x][y] = num;
					}
					if (poss[x][y][uph] == mn && (x == ur && y == uc)) {
						int hp = -1;
						while (poss[x][y][++hp] != 0)
							poss[x][y][hp] = 0;
						posnum[x][y] = 0;
					}
					uph++;
				}
			}
		}

	}

	public static boolean update() {
		boolean result = false;
		while (solve()) {
			updateCandidates();
		}
		return result;
	}

	public static void removeRow() {
		int s = 0, num, p, samecand = 1, posnm = 0, rh, mmc, drh;
		for (int mr = 0; mr < 9; mr++) {
			for (int mc = 0; mc < 9; mc++) {
				for (mmc = mc + 1; mmc < 9; mmc++) {
					if (posnum[mr][mc] == 0)
						break;
					if (posnum[mr][mc] == posnum[mr][mmc]) {
						samecand = 2;
						for (int nc = mmc + 1; nc < 9; nc++)
							if (posnum[mr][mc] == posnum[mr][nc])
								samecand++;
						posnm = posnum[mr][mc];
						for (int ps = 0; ps < s; ps++)
							single[ps] = 0;
						s = 0;
						do {
							single[s++] = posnm % 10;
							posnm = posnm / 10;
						} while (posnm != 0);
					}

					if (s == samecand) {
						for (int nc = 0; nc < 9; nc++) {
							rh = 0;
							drh = 0;
							if (posnum[mr][nc] != posnum[mr][mc]) {
								drh = 0;
								while (poss[mr][nc][drh] != 0) {
									for (int ss = 0; ss < s; ss++) {
										if (poss[mr][nc][drh] == single[ss]) {
											rh = drh;
											while (poss[mr][nc][rh] != 0)
												poss[mr][nc][rh] = poss[mr][nc][++rh];
											p = 0;
											num = 0;
											for (int htf = 0; htf < 9; htf++) {
												if (poss[mr][nc][htf] == 0)
													break;
												num += poss[mr][nc][htf]
														* Math.pow(10, p++);
											}
											posnum[mr][nc] = num;
										}
										if (poss[mr][nc][0] != 0
												&& poss[mr][nc][1] == 0) {
											mn = puzzle_map[mr][nc] = poss[mr][nc][0];
											poss[mr][nc][0] = 0;
											posnum[mr][nc] = 0;
											filled++;
											ur = mr;
											uc = nc;
											updateCandidates();
										}
									}
									drh++;
								}
							}
						}
						for (int ps = 0; ps < s; ps++)
							single[ps] = 0;
						s = 0;
						break;
					}
				}

			}
		}
	}

	public static void removeColumn() {
		int s = 0, num, p, samecand = 1, posnm = 0, rh, mmr, drh;
		for (int mc = 0; mc < 9; mc++) {
			for (int mr = 0; mr < 9; mr++) {
				for (mmr = mr + 1; mmr < 9; mmr++) {
					if (posnum[mr][mc] == 0)
						break;
					if (posnum[mr][mc] == posnum[mmr][mc]) {
						samecand = 2;
						for (int nr = mmr + 1; nr < 9; nr++)
							if (posnum[mr][mc] == posnum[nr][mc])
								samecand++;
						posnm = posnum[mr][mc];
						for (int ps = 0; ps < s; ps++)
							single[ps] = 0;
						s = 0;
						do {
							single[s++] = posnm % 10;
							posnm = posnm / 10;
						} while (posnm != 0);
					}

					if (s == samecand) {
						for (int nr = 0; nr < 9; nr++) {
							rh = 0;
							drh = 0;
							if (posnum[nr][mc] != posnum[mr][mc]) {
								while (poss[nr][mc][drh] != 0) {
									for (int ss = 0; ss < s; ss++) {
										if (poss[nr][mc][drh] == single[ss]) {
											rh = drh;
											while (poss[nr][mc][rh] != 0)
												poss[nr][mc][rh] = poss[nr][mc][++rh];
											p = 0;
											num = 0;
											for (int htf = 0; htf < 9; htf++) {
												if (poss[nr][mc][htf] == 0)
													break;
												num += poss[nr][mc][htf]
														* Math.pow(10, p++);
											}
											posnum[nr][mc] = num;

											if (poss[nr][mc][0] != 0
													&& poss[nr][mc][1] == 0) {
												mn = puzzle_map[nr][mc] = poss[nr][mc][0];
												poss[nr][mc][0] = 0;
												posnum[nr][mc] = 0;
												filled++;
												ur = nr;
												uc = mc;
												updateCandidates();
											}
										}
									}
									drh++;
								}
							}
						}
						for (int ps = 0; ps < s; ps++)
							single[ps] = 0;
						s = 0;
						break;
					}
				}

			}
		}
	}

	public static void removeBox() {
		int box, ri = 0, ci, rri, cci = 0, samecand = 1, posnm = 0, s = 0, rt = 0, ct, rh, drh, num, p, kr = 0, kci, kri;
		for (box = 0; box < 9; box++) {
			if (box % 3 == 0) {
				ri = box;
				rt = box;
			}
			kr = ri;
			kri = ri;
			for (; ri < kr + 3; ri++) {
				ci = (box % 3) * 3;
				kci = ci;
				for (; ci < kci + 3; ci++) {
					samecand = 1;
					rri = ri;
					switch (ci) {
					case 0:
					case 1:
					case 2:
						if (ci < 2)
							cci = ci + 1;
						else {
							cci = 0;
							rri++;
						}
						break;
					case 3:
					case 4:
					case 5:
						if (ci < 5)
							cci = ci + 1;
						else {
							cci = 3;
							rri++;
						}
						break;
					case 6:
					case 7:
					case 8:
						if (ci < 8)
							cci = ci + 1;
						else {
							cci = 6;
							rri++;
						}
					}
					for (; rri < kri + 3; rri++) {
						for (; cci < kci + 3; cci++) {
							if (posnum[ri][ci] == 0)
								break;
							if (posnum[ri][ci] == posnum[rri][cci]) {
								samecand++;
								posnm = posnum[ri][ci];
								for (int ps = 0; ps < s; ps++)
									single[ps] = 0;
								s = 0;
								do {
									single[s++] = posnm % 10;
									posnm = posnm / 10;
								} while (posnm != 0);
							}
						}
						if (posnum[ri][ci] == 0)
							break;
						cci = kci;
					}
					if (s == samecand) {
						for (rt = kr; rt < kr + 3; rt++) {
							for (ct = kci; ct < kci + 3; ct++) {
								rh = 0;
								drh = 0;
								if (posnum[rt][ct] != posnum[ri][ci]) {
									while (poss[rt][ct][drh] != 0) {
										for (int ss = 0; ss < s; ss++) {
											if (poss[rt][ct][drh] == single[ss]) {
												rh = drh;
												while (poss[rt][ct][rh] != 0)
													poss[rt][ct][rh] = poss[rt][ct][++rh];
												p = 0;
												num = 0;
												for (int htf = 0; htf < 9; htf++) {
													if (poss[rt][ct][htf] == 0)
														break;
													num += poss[rt][ct][htf]
															* Math.pow(10, p++);
												}
												posnum[rt][ct] = num;
											}
											if (poss[rt][ct][0] != 0
													&& poss[rt][ct][1] == 0) {
												mn = puzzle_map[rt][ct] = poss[rt][ct][0];
												poss[rt][ct][0] = 0;
												posnum[rt][ct] = 0;
												filled++;
												ur = rt;
												uc = ct;
												updateCandidates();
											}
										}
										drh++;
									}
								}
							}
						}
						for (int ps = 0; ps < s; ps++)
							single[ps] = 0;
						s = 0;
						break;
					}
				}
			}
			ri = ri - 3;
		}
	}
}
