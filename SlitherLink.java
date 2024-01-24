import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class SlitherLink extends JComponent implements ActionListener {

    private JFrame frame; // メインのフレーム
    private int Fwidth = 1000; // フレームの幅
    private int Fheight = 650; // フレームの高さ
    private int nwidth; // タイルの横の数
    private int nheight; // タイルの縦の数
    private int Cwidth; // タイル1つの横幅
    private int Cheight; // タイル1つの縦幅
    private int[][] tile; // ゲームボード上の数字配置
    private int[][] horline; // 横の線の状態
    private int[][] verline; // 縦の線の状態
    private int[][] extendedtileinv; // 拡張されたタイル配置
    private JButton bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9; // GUI上のボタン
    private int mode = 1; // ゲームのモード（0: 問題生成画面, 1: ゲームプレイ画面）
    private int gmmode = 0; // ゲームモード（0: タイトル, 1: ゲーム中）
    boolean ac = true; // ゲームの状態を制御するフラグ
    Image titleImage; // タイトル画面の画像
    boolean geneproblem = false; // 問題が生成されたかどうかのフラグ
    boolean fin = false; // ゲームがクリアされたかどうかのフラグ
    long startTime; // ゲームスタート時の時間

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SlitherLink();
        });
    }

    SlitherLink() {
        ArrayHandler handler = new ArrayHandler();
        // フレームの初期設定
        this.frame = new JFrame("SlitherLink");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Fwidth + 200, Fheight + 100);

        // タイトル画像の読み込み
        try {
            titleImage = getToolkit().getImage("img/title.jpg");
        } catch (Exception e) {
            System.out.println("error");
        }

        // GUI上のボタンの初期化と配置
        bt1 = new JButton("ー");
        bt2 = new JButton("×");
        bt3 = new JButton("解答");
        bt4 = new JButton("タイトルへ");
        bt5 = new JButton("リセット");
        bt6 = new JButton("初級");
        bt7 = new JButton("中級");
        bt8 = new JButton("上級");
        bt9 = new JButton("再生成");

        // ゲームボードを描画するキャンバスの初期化と配置
        JPanel canvas = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                // キャンバスの描画メソッド
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                btnvisible(gmmode);

                switch (gmmode) {
                    case 0:
                        // タイトル画面の描画
                        g2d.drawImage(titleImage, 0, 0, Fwidth, Fheight, this);
                        break;

                    case 1:
                        // ゲームプレイ画面の描画
                        if (!geneproblem) {
                            // 問題生成
                            SliProblem sliproblem = new SliProblem(nwidth, nheight, Cwidth, Cheight, horline, verline);
                            extendedtileinv = sliproblem.problem(nwidth, nheight);
                            Cwidth = Fwidth / nwidth; // マスの横幅
                            Cheight = Fheight / nheight; // マスの縦幅
                            horline = new int[nwidth][nheight + 1];
                            verline = new int[nwidth + 1][nheight];
                            tile = handler.removeBorder(extendedtileinv);
                            geneproblem = true;

                            // マウスイベントの処理を追加
                            startTime = System.currentTimeMillis();
                        }
                        MouseProc mouseProc = new MouseProc(this, nwidth, nheight, Cwidth, Cheight, mode, horline,
                                verline);
                        this.addMouseListener(mouseProc);

                        if (ac) {
                            // 初回のみ実行する処理
                            for (int[] row : horline)
                                Arrays.fill(row, 0);

                            for (int[] row : verline)
                                Arrays.fill(row, 0);
                            ac = false;
                        }

                        // ゲームボードの描画
                        g.setColor(Color.WHITE);
                        g2d.fillRect(0, 0, Fwidth + 6, Fheight + 6);

                        g.setColor(Color.BLACK);
                        g2d.setStroke(new BasicStroke(3));
                        g.setFont(new Font("Arial", Font.BOLD, (Cwidth) / 2));
                        for (int i = 0; i < nheight + 1; i++) {
                            for (int j = 0; j < nwidth + 1; j++) {
                                if (i < nwidth && j < nheight && tile[i][j] != -1) {
                                    g2d.drawString("" + tile[i][j], (Cwidth) * i + (Cwidth) * 2 / 5,
                                            (Cheight) * j + (Cheight) * 23 / 30);
                                }
                                g2d.fillOval((Cwidth) * j, (Cheight) * i, 6, 6);
                            }
                        }

                        SliSolver slisolver = new SliSolver(nwidth, nheight, Cwidth, Cheight, horline, verline,
                                extendedtileinv);

                        if (mode != 0) {
                            g.setColor(Color.BLUE);
                            for (int i = 0; i < nheight + 1; i++) {
                                for (int j = 0; j < nwidth + 1; j++) {
                                    if (j < nwidth) {
                                        if (horline[j][i] == 1) {
                                            g.setFont(new Font("Arial", Font.BOLD, 8));
                                            g2d.drawLine((Cwidth) * j + 3, (Cheight) * i + 3,
                                                    (Cwidth) * (j + 1) + 3, (Cheight) * i + 3);
                                        } else if (horline[j][i] == -1) {
                                            g.setFont(new Font("Arial", Font.BOLD, Cwidth / 2));
                                            g2d.drawString("×", (Cwidth) * j + (Cwidth) * 11 / 30 + 3,
                                                    (Cheight) * i + (Cheight) * 3 / 14 + 3);
                                        }
                                    }
                                    if (i < nheight) {
                                        if (verline[j][i] == 1) {
                                            g2d.drawLine((Cwidth) * j + 3, (Cheight) * i + 3,
                                                    (Cwidth) * j + 3, (Cheight) * (i + 1) + 3);
                                        } else if (verline[j][i] == -1) {
                                            g.setFont(new Font("Arial", Font.BOLD, Cwidth / 2));
                                            g2d.drawString("×", (Cwidth) * j - (Cwidth) / 7 + 3,
                                                    (Cheight) * i + (Cheight) * 2 / 3 + 3);
                                        }
                                    }
                                }
                            }
                        } else {
                            drawsolver(g2d, slisolver.Solver(true, nwidth, nheight, extendedtileinv),
                                    slisolver.Solver(false, nwidth, nheight, extendedtileinv), nwidth, nheight, Cwidth,
                                    Cheight,
                                    horline,
                                    verline);
                        }
                        if (!fin && anscheck(horline, verline, slisolver.Solver(true, nwidth, nheight, extendedtileinv),
                                slisolver.Solver(false, nwidth, nheight, extendedtileinv))) {
                            long endTime = System.currentTimeMillis();

                            // 実行時間を計算して表示（分と秒）
                            long executionTimeMillis = endTime - startTime;
                            long executionTimeSeconds = executionTimeMillis / 1000;
                            long minutes = executionTimeSeconds / 60;
                            long seconds = executionTimeSeconds % 60;
                            Window containerWindow = SwingUtilities.getWindowAncestor(this);
                            JDialog dialog = new JDialog(containerWindow);
                            dialog.setLayout(new BorderLayout());
                            dialog.setLocationRelativeTo(containerWindow);
                            JPanel panel = new JPanel(new BorderLayout());
                            JLabel label = new JLabel("   Clear!\n" + "タイムは" + minutes + "分" + seconds + "秒です。");
                            panel.add(label, BorderLayout.CENTER);
                            dialog.setContentPane(panel);
                            dialog.setTitle("Clear!");
                            dialog.setSize(300, 200);
                            dialog.setVisible(true);
                        }
                }
            }
        };

        // ボタンとキャンバスの配置
        bt1.setBounds(Fwidth + 10, 150, 170, 100); // ボタンの位置とサイズを設定
        bt1.addActionListener(this);
        bt2.setBounds(Fwidth + 10, 250, 170, 100); // ボタンの位置とサイズを設定
        bt2.addActionListener(this);
        bt3.setBounds(Fwidth + 10, 350, 170, 100); // ボタンの位置とサイズを設定
        bt3.addActionListener(this);
        bt4.setBounds(Fwidth + 10, 450, 170, 100); // ボタンの位置とサイズを設定
        bt4.addActionListener(this);
        bt5.setBounds(Fwidth + 10, 50, 170, 100); // ボタンの位置とサイズを設定
        bt5.addActionListener(this);
        bt6.setBounds(Fwidth + 10, 50, 170, 100); // ボタンの位置とサイズを設定
        bt6.addActionListener(this);
        bt7.setBounds(Fwidth + 10, 150, 170, 100); // ボタンの位置とサイズを設定
        bt7.addActionListener(this);
        bt8.setBounds(Fwidth + 10, 250, 170, 100); // ボタンの位置とサイズを設定
        bt8.addActionListener(this);
        bt9.setBounds(Fwidth + 10, 550, 170, 100); // ボタンの位置とサイズを設定
        bt9.addActionListener(this);

        canvas.setBounds(0, 0, Fwidth + 6, Fheight + 6);
        frame.setLayout(null); // レイアウトマネージャーを無効にする
        frame.getContentPane().add(canvas);
        frame.getContentPane().add(bt1);
        frame.getContentPane().add(bt2);
        frame.getContentPane().add(bt3);
        frame.getContentPane().add(bt4);
        frame.getContentPane().add(bt5);
        frame.getContentPane().add(bt6);
        frame.getContentPane().add(bt7);
        frame.getContentPane().add(bt8);
        frame.getContentPane().add(bt9);

        // フレームの表示
        frame.setSize(Fwidth + 200, Fheight + 50);
        frame.setVisible(true);
    }

    private void btnvisible(int mode) {
        if (mode == 1) {
            bt1.setVisible(true);
            bt2.setVisible(true);
            bt3.setVisible(true);
            bt4.setVisible(true);
            bt5.setVisible(true);
            bt6.setVisible(false);
            bt7.setVisible(false);
            bt8.setVisible(false);
            bt9.setVisible(true);
        } else if (mode == 0) {
            bt1.setVisible(false);
            bt2.setVisible(false);
            bt3.setVisible(false);
            bt4.setVisible(false);
            bt5.setVisible(false);
            bt6.setVisible(true);
            bt7.setVisible(true);
            bt8.setVisible(true);
            bt9.setVisible(false);
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == bt1) {
            mode = 1;
            frame.repaint();
        } else if (e.getSource() == bt2) {
            mode = 2;
            frame.repaint();
        } else if (e.getSource() == bt3) {
            int result = JOptionPane.showConfirmDialog(null, "解答を表示します", "確認", JOptionPane.YES_NO_OPTION);

            // ユーザーが「はい」を選択した場合のみアクションを実行
            if (result == JOptionPane.YES_OPTION) {
                mode = 0;
                frame.repaint();
            }
        } else if (e.getSource() == bt4) {
            int result = JOptionPane.showConfirmDialog(null, "タイトル画面に戻ります", "確認", JOptionPane.YES_NO_OPTION);

            // ユーザーが「はい」を選択した場合のみアクションを実行
            if (result == JOptionPane.YES_OPTION) {
                mode = 1;
                gmmode = 0;
                geneproblem = false;
                ac = true;
                fin = false;
                frame.repaint();
            }
        } else if (e.getSource() == bt5) {
            int result = JOptionPane.showConfirmDialog(null, "すべて消去します", "確認", JOptionPane.YES_NO_OPTION);

            // ユーザーが「はい」を選択した場合のみアクションを実行
            if (result == JOptionPane.YES_OPTION) {
                mode = 1;
                ac = true;
                frame.repaint();
            }
        } else if (e.getSource() == bt6) {
            gmmode = 1;
            nwidth = 4;
            nheight = 3;
            frame.repaint();
        } else if (e.getSource() == bt7) {
            gmmode = 1;
            nwidth = 5;
            nheight = 4;
            frame.repaint();
        } else if (e.getSource() == bt8) {
            gmmode = 1;
            nwidth = 6;
            nheight = 5;
            frame.repaint();
        } else if (e.getSource() == bt9) {
            int result = JOptionPane.showConfirmDialog(null, "問題を再生成します", "確認", JOptionPane.YES_NO_OPTION);

            // ユーザーが「はい」を選択した場合のみアクションを実行
            if (result == JOptionPane.YES_OPTION) {
                mode = 1;
                geneproblem = false;
                ac = true;
                fin = false;
                frame.repaint();
            }
        }
    }

    private void drawsolver(Graphics2D g2d, int[][] answerh, int[][] answerv, int nwidth, int nheight, int Cwidth,
            int Cheight, int horline[][], int[][] verline) {
        for (int i = 0; i < nheight + 1; i++) {
            for (int j = 0; j < nwidth + 1; j++) {
                if (j < nwidth) {
                    if (answerh[j][i] == 1) {
                        if (horline[j][i] == 1) {
                            g2d.setColor(Color.BLUE);
                        } else {
                            g2d.setColor(Color.RED);
                        }
                        g2d.setFont(new Font("Arial", Font.BOLD, 8));
                        g2d.drawLine((Cwidth) * j + 3, (Cheight) * i + 3,
                                (Cwidth) * (j + 1) + 3, (Cheight) * i + 3);
                    } else if (horline[j][i] == 1) {
                        g2d.setColor(Color.BLUE);
                        g2d.setFont(new Font("Arial", Font.BOLD, 8));
                        g2d.drawLine((Cwidth) * j + 3, (Cheight) * i + 3,
                                (Cwidth) * (j + 1) + 3, (Cheight) * i + 3);
                        g2d.setColor(Color.RED);
                        g2d.setFont(new Font("Arial", Font.BOLD, Cwidth / 2));
                        g2d.drawString("×", (Cwidth) * j + (Cwidth) * 11 / 30 + 3,
                                (Cheight) * i + (Cheight) * 3 / 14 + 3);
                    }
                }
                if (i < nheight) {
                    if (answerv[j][i] == 1) {
                        if (verline[j][i] == 1) {
                            g2d.setColor(Color.BLUE);
                        } else {
                            g2d.setColor(Color.RED);
                        }
                        g2d.setFont(new Font("Arial", Font.BOLD, 8));
                        g2d.drawLine((Cwidth) * j + 3, (Cheight) * i + 3,
                                (Cwidth) * j + 3, (Cheight) * (i + 1) + 3);
                    } else if (verline[j][i] == 1) {
                        g2d.setColor(Color.BLUE);
                        g2d.setFont(new Font("Arial", Font.BOLD, 8));
                        g2d.drawLine((Cwidth) * j + 3, (Cheight) * i + 3,
                                (Cwidth) * j + 3, (Cheight) * (i + 1) + 3);
                        g2d.setColor(Color.RED);
                        g2d.setFont(new Font("Arial", Font.BOLD, Cwidth / 2));
                        g2d.drawString("×", (Cwidth) * j - (Cwidth) / 7 + 3,
                                (Cheight) * i + (Cheight) * 2 / 3 + 3);
                    }
                }
            }
        }
    }

    private boolean anscheck(int[][] hline, int[][] vline, int[][] hans, int[][] vans) {
        for (int i = 0; i < nwidth + 1; i++) {
            for (int j = 0; j < nheight + 1; j++) {
                if (i != nwidth && hline[i][j] != hans[i][j] && !(hline[i][j] == -1 && hans[i][j] == 0))
                    return false;
                if (j != nheight && vline[i][j] != vans[i][j] && !(vline[i][j] == -1 && vans[i][j] == 0))
                    return false;
            }
        }
        return true;
    }

}
