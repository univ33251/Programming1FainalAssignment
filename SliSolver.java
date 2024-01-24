import jp.ac.kobe_u.cs.cream.*;

public class SliSolver {

    int Fwidth;
    int Fheight;
    int tile[][];
    int nwidth;
    int nheight;
    int cwidth; // マスの横幅
    int cheight; // マスの縦幅
    int horline[][];
    int verline[][];
    int[][] extendedtileinv;

    SliSolver(int nw, int nh, int cw, int ch, int[][] hl, int[][] vl, int[][] tileinv) {
        this.nwidth = nw;
        this.nheight = nh;
        this.cwidth = cw;
        this.cheight = ch;
        this.horline = hl;
        this.verline = vl;
        this.extendedtileinv = tileinv;
    }

    public int[][] Solver(boolean h, int nwidth, int nheight, int[][] extendedtileinv) {
        // 制約充足問題を解くためのネットワークを作成
        Network net = new Network();

        // 盤面内の各セルの変数を表す二次元配列
        IntVariable[][] tileinside = new IntVariable[nwidth + 2][nheight + 2];
        // 解の合計値を初期化
        int sum = 0;

        // 盤面内の各セルの変数を初期化
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

        // タイルの配置ルールを設定
        for (int i = 1; i < nheight + 1; i++) {
            for (int j = 1; j < nwidth + 1; j++) {
                if (extendedtileinv[j][i] != -1) {
                    // タイルの配置条件を数学的に表現
                    tileinside[j - 1][i].add(tileinside[j + 1][i].add(tileinside[j][i - 1].add(tileinside[j][i + 1])))
                            .equals(((tileinside[j][i].subtract(1)).negate()).multiply(extendedtileinv[j][i])
                                    .add(tileinside[j][i].multiply(4 - extendedtileinv[j][i])));
                }

                if (1 <= i && i <= nheight - 1 && 1 <= j && j <= nwidth - 1) {
                    // 線は分岐や交差をしない条件を設定
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

        // 制約充足問題を解くためのソルバーを設定
        Solver solver = new DefaultSolver(net);
        // 解の一時的な格納用
        Solution result = new Solution(net);

        // ソルバーループ
        solverloop: for (solver.start(); solver.waitNext(); solver.resume()) {
            Solution solution = solver.getSolution();

            // 解の合計を計算
            for (int i = 0; i < nheight + 2; i++) {
                for (int j = 0; j < nwidth + 2; j++) {
                    sum += solution.getIntValue(tileinside[j][i]);
                }
            }

            // 解の検証
            checkloop: for (int i = 0; i < nheight + 2; i++) {
                for (int j = 0; j < nwidth + 2; j++) {
                    if (solution.getIntValue(tileinside[j][i]) == 1) {
                        // 単一のクラスタを形成しているか検証
                        if (!isSingleCluster(tileinside, sum, solution, 1)
                                || !isSingleCluster(tileinside, (nheight + 2) * (nwidth + 2) - sum, solution, 0)) {
                            sum = 0;
                            // 解が無効ならばソルバーループへ戻る
                            continue solverloop;
                        } else {
                            break checkloop;
                        }
                    }
                }
            }
            result = solution;
        }

        // 解の出力と処理
        return SolveredLine(tileinside, result, h, nwidth, nheight);
    }

    public int[][] SolveredLine(IntVariable[][] tile, Solution solution, boolean h, int nwidth, int nheight) {
        int[][] hline = new int[nwidth][nheight + 1];
        int[][] vline = new int[nwidth + 1][nheight];

        for (int i = 1; i < nheight + 1; i++) {
            for (int j = 1; j < nwidth + 1; j++) {
                if (solution.getIntValue(tile[j][i]) + solution.getIntValue(tile[j - 1][i]) == 1) {
                    vline[j - 1][i - 1] = 1;
                }
                if (solution.getIntValue(tile[j][i]) + solution.getIntValue(tile[j][i - 1]) == 1) {
                    hline[j - 1][i - 1] = 1;
                }
                if (solution.getIntValue(tile[j][i]) == 1) {
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

    public int Solver(int[][] extendedinv, int nwidth, int nheight) {
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
                        if (!isSingleCluster(tileinside, sum, solution, 1)
                                || !isSingleCluster(tileinside, (nheight + 2) * (nwidth + 2) - sum, solution, 0)) {
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

    public static boolean isSingleCluster(IntVariable[][] matrix, int totalOnes, Solution solution, int num) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];

        int countOnes = 0;

        roop: for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (solution.getIntValue(matrix[j][i]) == num && !visited[j][i]) {
                    countOnes += dfs(matrix, visited, j, i, solution, num);
                    break roop;
                }
            }
        }

        return totalOnes > 0 && countOnes == totalOnes;
    }

    private static int dfs(IntVariable[][] matrix, boolean[][] visited, int j, int i, Solution solution, int num) {

        if (solution.getIntValue(matrix[j][i]) != num | visited[j][i]) {
            return 0;
        }

        visited[j][i] = true;
        int count = 1;

        // 上下左右を探索
        if (j != 0)
            count += dfs(matrix, visited, j - 1, i, solution, num);
        if (j != matrix.length - 1)
            count += dfs(matrix, visited, j + 1, i, solution, num);
        if (i != 0)
            count += dfs(matrix, visited, j, i - 1, solution, num);
        if (i != matrix[0].length - 1)
            count += dfs(matrix, visited, j, i + 1, solution, num);

        return count;
    }
}
