import java.util.Arrays;

public class ArrayHandler {
    ArrayHandler(){}
    
    // 配列を-1で囲むメソッド
    public static int[][] surroundWithMinusOne(int[][] array) {
        int rows = array.length;
        int cols = array[0].length;

        // 新しい配列を作成し、すべての要素を-1で初期化
        int[][] newArray = new int[rows + 2][cols + 2];
        for (int[] row : newArray)
            Arrays.fill(row, -1);

        // 元の配列の要素を新しい配列の内側にコピー
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newArray[i + 1][j + 1] = array[i][j];
            }
        }

        return newArray;
    }

    public int[][] removeBorder(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        // 端の行と列を除いた新しいサイズの配列を作成
        int[][] result = new int[rows - 2][cols - 2];

        // 端の数字を削除して新しい配列にコピー
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                result[i - 1][j - 1] = matrix[i][j];
            }
        }

        return result;
    }

    public int[][] transposeMatrix(int[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;

        int[][] transposedMatrix = new int[columns][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                transposedMatrix[j][i] = matrix[i][j];
            }
        }

        return transposedMatrix;
    }
}
