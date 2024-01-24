import javax.swing.*;
import java.awt.event.*;

// クリックされた時の処理用のクラス
public class MouseProc implements MouseListener {
    private JPanel canvas;
    int nwidth;
    int nheight;
    int Cwidth;
    int Cheight;
    int mode;
    int[][] horline;
    int[][] verline;

    MouseProc(JPanel canvas, int nw, int nh, int cw, int ch, int md, int[][] hl, int[][] vl) {
        this.canvas = canvas;
        this.nwidth = nw;
        this.nheight = nh;
        this.Cwidth = cw;
        this.Cheight = ch;
        this.mode = md;
        this.horline = hl;
        this.verline = vl;
    }

    int x;
    int y;
    int startX;
    int startY;
    int endX;
    int endY;

    @Override
    public void mouseClicked(MouseEvent e) {
        // mouseClickedの実装
        x = e.getX();
        y = e.getY();
        for (int i = 0; i < nheight + 1; i++) {
            for (int j = 0; j < nwidth + 1; j++) {
                if (j < nwidth) {
                    startX = Cwidth * j + 3;
                    startY = Cheight * i - Cheight / 10 + 3;
                    endX = Cwidth * (j + 1) + 3;
                    endY = Cheight * i + Cheight / 10 + 3;

                    if (x >= startX && x <= endX && y >= startY && y <= endY) {
                        // マウスクリックが線上にある場合のアクション
                        horline[j][i] = (horline[j][i] + 1) % 2;
                        if (mode == 2)
                            horline[j][i] *= -1;
                        canvas.repaint();
                    }
                }

                if (i < nheight) {
                    startX = Cwidth * j - Cwidth / 10 + 3;
                    startY = Cheight * i + 3;
                    endX = Cwidth * j + Cwidth / 10 + 3;
                    endY = Cheight * (i + 1) + 3;

                    if (x >= startX && x <= endX && y >= startY && y <= endY) {
                        // マウスクリックが線上にある場合のアクション
                        verline[j][i] = (verline[j][i] + 1) % 2;
                        if (mode == 2)
                            verline[j][i] *= -1;
                        canvas.repaint();
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // mousePressedの実装
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // mouseReleasedの実装
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // mouseEnteredの実装
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // mouseExitedの実装
    }
}
