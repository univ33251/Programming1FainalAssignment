import java.util.Random;

import jp.ac.kobe_u.cs.cream.DefaultSolver;
import jp.ac.kobe_u.cs.cream.IntVariable;
import jp.ac.kobe_u.cs.cream.Network;
import jp.ac.kobe_u.cs.cream.Solution;
import jp.ac.kobe_u.cs.cream.Solver;

public class SliProblem {

    int nwidth;
    int nheight;
    int Cwidth;
    int Cheight;
    int[][] horline;
    int[][] verline;

    SliProblem(int width, int height, int Cwidth, int Cheight, int[][] horline, int[][] verline) {
        this.nwidth = width;
        this.nheight = height;
        this.Cwidth = Cwidth;
        this.Cheight = Cheight;
        this.horline = horline;
        this.verline = verline;
    }

    // ランダムな二次元配列生成メソッド
    private static int[][] generateRandomArray(int rows, int cols, int min, int max) {
        int[][] array = new int[rows][cols];
        Random random = new Random();
        int count;

        do {
            // 配列の各要素にランダムに0か1を代入
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    array[i][j] = random.nextInt(2);
                }
            }

            // 1の個数を数える
            count = 0;
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    count += array[i][j];
                }
            }
        } while (count < min || count > max || !isSingleCluster(array, count, 1)
                || !isSingleCluster(array, rows * cols - count, 0));
        return ArrayHandler.surroundWithMinusOne(array);
    }

    private int Solver(int[][] extendedinv, int nwidth, int nheight) {
        Network net = new Network();

        IntVariable[][] tileinside = new IntVariable[nwidth + 2][nheight + 2];
        int sum = 0;
        int onesolver = 0;

        for (int i = 0; i < nheight + 2; i++) {
            for (int j = 0; j < nwidth + 2; j++) {
                tileinside[j][i] = new IntVariable(net, 0, 1);
            }
        }

        // 盤面外は必ず外側
        for (int j = 0; j < nwidth + 2; j++) {
            tileinside[j][0].equals(0);
            tileinside[j][nheight + 1].equals(0);
        }
        for (int i = 0; i < nheight + 2; i++) {
            tileinside[0][i].equals(0);
            tileinside[nwidth + 1][i].equals(0);
        }

        for (int i = 1; i < nheight + 1; i++) {
            for (int j = 1; j < nwidth + 1; j++) {
                if (extendedinv[j][i] != -1) {
                    tileinside[j - 1][i].add(tileinside[j + 1][i].add(tileinside[j][i - 1].add(tileinside[j][i + 1])))
                            .equals(((tileinside[j][i].subtract(1)).negate()).multiply(extendedinv[j][i])
                                    .add(tileinside[j][i].multiply(4 - extendedinv[j][i])));
                }

                if (1 <= i && i <= nheight - 1 && 1 <= j && j <= nwidth - 1) {
                    // 線は分岐や交差をしない
                    tileinside[j][i].add(((tileinside[j + 1][i].subtract(1)).negate())
                            .add(((tileinside[j][i + 1].subtract(1)).negate())
                                    .add(tileinside[j + 1][i + 1])))
                            .le(3);
                    tileinside[j][i].add(((tileinside[j + 1][i].subtract(1)).negate())
                            .add(((tileinside[j][i + 1].subtract(1)).negate())
                                    .add(tileinside[j + 1][i + 1])))
                            .ge(1);
                }
            }
        }

        Solver solver = new DefaultSolver(net);
        solverloop: for (solver.start(); solver.waitNext(); solver.resume()) {
            Solution solution = solver.getSolution();

            for (int i = 0; i < nheight + 2; i++) {
                for (int j = 0; j < nwidth + 2; j++) {
                    sum += solution.getIntValue(tileinside[j][i]);
                }
            }
            checkloop: for (int i = 0; i < nheight + 2; i++) {
                for (int j = 0; j < nwidth + 2; j++) {
                    if (solution.getIntValue(tileinside[j][i]) == 1) {
                        if (!SliSolver.isSingleCluster(tileinside, sum, solution, 1)
                                || !SliSolver.isSingleCluster(tileinside, (nheight + 2) * (nwidth + 2) - sum, solution, 0)) {
                            sum = 0;
                            continue solverloop;
                        } else {
                            break checkloop;
                        }
                    }
                }
            }
            onesolver++;
            if (onesolver == 2) {
                break solverloop;
            }
        }

        return onesolver;
    }

    private static boolean isSingleCluster(int[][] matrix, int totalOnes, int num) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];

        int countOnes = 0;

        roop: for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if ((matrix[j][i]) == num && !visited[j][i]) {
                    countOnes += dfs(matrix, visited, j, i, num);
                    break roop;
                }
            }
        }

        return totalOnes > 0 && countOnes == totalOnes;
    }

    private static int dfs(int[][] matrix, boolean[][] visited, int j, int i, int num) {

        if ((matrix[j][i]) != num | visited[j][i]) {
            return 0;
        }

        visited[j][i] = true;
        int count = 1;

        // 上下左右を探索
        if (j != 0)
            count += dfs(matrix, visited, j - 1, i, num);
        if (j != matrix.length - 1)
            count += dfs(matrix, visited, j + 1, i, num);
        if (i != 0)
            count += dfs(matrix, visited, j, i - 1, num);
        if (i != matrix[0].length - 1)
            count += dfs(matrix, visited, j, i + 1, num);

        return count;
    }

    int[][] problem(int nw, int nh) {
        // パズルの問題と解のための配列を初期化
        int[][] hans = new int[nw][nh + 1];
        int[][] vans = new int[nw + 1][nh];
        boolean[][] visitedtile = new boolean[nw + 2][nh + 2];
        int[][] solvedtile = new int[nw][nh];
        int[][] tileins = new int[nw + 2][nh + 2];
        int[][] extendedinv;

        // 無限ループ開始
        do {
            // ランダムなタイル配置を生成
            tileins = generateRandomArray(nw, nh, nw * nh * 7 / 14, nw * nh * 4 / 5);
            // タイル配置から横方向と縦方向のラインを解く
            hans = SolveredLine(tileins, true, nw, nh);
            vans = SolveredLine(tileins, false, nw, nh);

            // タイルの配置から解を構成
            for (int i = 0; i < nh; i++) {
                for (int j = 0; j < nw; j++) {
                    solvedtile[j][i] = hans[j][i] + hans[j][i + 1] + vans[j][i] + vans[j + 1][i];
                }
            }

            // タイルの周りに-1を追加して制約充足問題を解く
            extendedinv = ArrayHandler.surroundWithMinusOne(solvedtile);
            int savenum = 0;
            int cnt = 0;

            // 初期状態で解が存在するか確認
            if (Solver(extendedinv, nw, nh) == 1) {
                Random random = new Random();

                // ランダムな位置に-1を追加して再度解を確認
                roop: while (true) {
                    cnt++;
                    if (cnt > nw * nh * 4 / 5) {
                        break roop;
                    }
                    int randx = random.nextInt(nw + 2);
                    int randy = random.nextInt(nh + 2);

                    // 未訪問のセルに-1を追加
                    if (!visitedtile[randx][randy]) {
                        savenum = extendedinv[randx][randy];
                        extendedinv[randx][randy] = -1;
                        visitedtile[randx][randy] = true;

                        // 解が存在しない場合、元に戻す
                        if (Solver(extendedinv, nw, nh) != 1) {
                            extendedinv[randx][randy] = savenum;
                        }

                        // 全てのセルが訪問済みならばループを抜ける
                        for (int i = 1; i < nh + 1; i++) {
                            for (int j = 1; j < nw + 1; j++) {
                                if (!visitedtile[j][i]) {
                                    continue roop;
                                }
                            }
                        }
                        break;
                    }
                }
                // ループを抜ける（無限ループを抜けるため）
                break;
            }
        } while (true);

        // 生成された拡張された配列の表示
        for (int i = 0; i < nh + 2; i++) {
            for (int j = 0; j < nw + 2; j++) {
                System.out.print(extendedinv[j][i]);
            }
            System.out.println();
        }

        // 生成された拡張された配列を返す
        return extendedinv;
    }

    public int[][] SolveredLine(int[][] tile, boolean h, int nwidth, int nheight) {
        int[][] hline = new int[nwidth][nheight + 1];
        int[][] vline = new int[nwidth + 1][nheight];

        for (int i = 1; i < nheight + 1; i++) {
            for (int j = 1; j < nwidth + 1; j++) {
                if ((tile[j][i]) + (tile[j - 1][i]) == 1) {
                    vline[j - 1][i - 1] = 1;
                }
                if ((tile[j][i]) + (tile[j][i - 1]) == 1) {
                    hline[j - 1][i - 1] = 1;
                }
                if ((tile[j][i]) == 1) {
                    if (j == nwidth) {
                        vline[j][i - 1] = 1;
                    }
                    if (i == nheight) {
                        hline[j - 1][i] = 1;
                    }
                }
            }
        }
        if (h) {
            return hline;
        } else {
            return vline;
        }
    }
}