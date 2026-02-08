package PushingBoxGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.sound.sampled.*;

/**
 * 推箱子大师 - Java Swing 版
 * 包含 10 个关卡和自研音效系统
 */
public class MovingBox extends JFrame {
    private GamePanel gamePanel;
    private final int CELL_SIZE = 50;
    private int currentLevel = 0;
    private char[][] gameState;
    private int steps = 0;
    private int playerX, playerY;
    private boolean isMuted = false;

    // 图片资源
    private Image playerImg;
    private Image boxImg;

    // 关卡数据 (W:墙, T:目标, B:箱子, P:人, .:地板, *:完成的箱子)
    private final String[][] levels = {
        {"  WWW  ","  WTW  ","  W.W  ","WWW.WWW","WT.B.TW","W..P..W","WWWWWWW"},
        {"WWWWWW","W.T..W","W.B.WW","W.B.TW","W.P..W","WWWWWW"},
        {" WWWWW "," W...W "," WBT.W "," WTB.W "," W..PW "," WWWWW "},
        {"WWWWWWWW","W......W","W.TBT..W","W.B*B..W","W.TBT..W","W...P..W","WWWWWWWW"},
       
    };

    public MovingBox() {
        setTitle("推点儿啥呢 - Level 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 加载图片资源
        loadImages();

        gamePanel = new GamePanel();
        add(gamePanel);
        initLevel(0);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> movePlayer(0, -1);
                    case KeyEvent.VK_DOWN -> movePlayer(0, 1);
                    case KeyEvent.VK_LEFT -> movePlayer(-1, 0);
                    case KeyEvent.VK_RIGHT -> movePlayer(1, 0);
                    case KeyEvent.VK_R -> initLevel(currentLevel);
                    case KeyEvent.VK_M -> isMuted = !isMuted;
                }
            }
        });

        // 启动简易 BGM 线程
        startBGM();
    }

    private void initLevel(int index) {
        String[] layout = levels[index];
        gameState = new char[layout.length][layout[0].length()];
        steps = 0;
        for (int y = 0; y < layout.length; y++) {
            for (int x = 0; x < layout[y].length(); x++) {
                gameState[y][x] = layout[y].charAt(x);
                if (gameState[y][x] == 'P') {
                    playerX = x;
                    playerY = y;
                }
            }
        }
        updateSize();
        gamePanel.repaint();
    }

    private void updateSize() {
        int h = gameState.length * CELL_SIZE;
        int w = gameState[0].length * CELL_SIZE;
        gamePanel.setPreferredSize(new Dimension(w, h + 40));
        pack();
        setLocationRelativeTo(null);
        setTitle("推箱子大师 - Level " + (currentLevel + 1) + " (Steps: " + steps + ")");
    }

    private void movePlayer(int dx, int dy) {
        int nx = playerX + dx;
        int ny = playerY + dy;

        if (ny < 0 || ny >= gameState.length || nx < 0 || nx >= gameState[0].length) return;
        char target = gameState[ny][nx];

        if (target == 'W') {
            playTone(80, 30); // 撞墙音效
            return;
        }

        if (target == 'B' || target == '*') {
            int bx = nx + dx;
            int by = ny + dy;
            if (by < 0 || by >= gameState.length || bx < 0 || bx >= gameState[0].length) {
                playTone(80, 30); // 箱子推不动音效
                return;
            }
            char behind = gameState[by][bx];

            if (behind == '.' || behind == 'T') {
                gameState[by][bx] = (behind == 'T') ? '*' : 'B';
                gameState[ny][nx] = 'P';
                resetOldPos(playerX, playerY);
                playerX = nx;
                playerY = ny;
                steps++;
                playTone(100, 50); // 推箱子音效
            } else {
                // 箱子后面有障碍物，推不动，播放撞墙音效
                playTone(80, 30);
            }
        } else {
            gameState[ny][nx] = 'P';
            resetOldPos(playerX, playerY);
            playerX = nx;
            playerY = ny;
            steps++;
            playTone(500, 40); // 移动音效
        }

        setTitle("推箱子大师 - Level " + (currentLevel + 1) + " (Steps: " + steps + ")");
        gamePanel.repaint();
        checkVictory();
    }

    private void resetOldPos(int x, int y) {
        char original = levels[currentLevel][y].charAt(x);
        gameState[y][x] = (original == 'T' || original == '*') ? 'T' : '.';
    }

    private void checkVictory() {
        boolean won = true;
        for (char[] row : gameState) {
            for (char c : row) {
                if (c == 'B') {
                    won = false;
                    break;
                }
            }
        }
        if (won) {
            playTone(600, 200);
            JOptionPane.showMessageDialog(this, "挑战成功！步数: " + steps);
            currentLevel = (currentLevel + 1) % levels.length;
            initLevel(currentLevel);
        }
    }

    // 游戏绘制面板，使用Swing双缓冲
    private class GamePanel extends JPanel {
        public GamePanel() {
            setFocusable(true);
            setBackground(new Color(30, 41, 59));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGame(g);
        }
    }

    private void drawGame(Graphics g) {
        g.setColor(new Color(30, 41, 59));
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int y = 0; y < gameState.length; y++) {
            for (int x = 0; x < gameState[y].length; x++) {
                int px = x * CELL_SIZE;
                int py = y * CELL_SIZE + 40;
                char cell = gameState[y][x];

                switch (cell) {
                    case 'W' -> {
                        g.setColor(new Color(71, 85, 105));
                        g.fillRect(px + 2, py + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                    }
                    case 'T' -> {
                        g.setColor(new Color(51, 65, 85));
                        g.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                        g.setColor(new Color(250, 204, 21));
                        g.fillOval(px + CELL_SIZE/2 - 5, py + CELL_SIZE/2 - 5, 10, 10);
                    }
                    case 'P' -> {
                        g.setColor(new Color(51, 65, 85));
                        g.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                        if (playerImg != null) {
                            g.drawImage(playerImg, px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10, null);
                        } else {
                            g.setColor(new Color(96, 165, 250));
                            g.fillOval(px + 10, py + 10, CELL_SIZE - 20, CELL_SIZE - 20);
                        }
                    }
                    case 'B' -> {
                        // 绘制箱子：用图片"download.png"
                        if (boxImg != null) {
                            g.drawImage(boxImg, px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10, null);
                        } else {
                            // 备用绘制：如果图片加载失败，使用颜色绘制
                            g.setColor(new Color(146, 64, 14));
                            g.fillRect(px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                            g.setColor(new Color(120, 53, 15));
                            g.drawRect(px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                        }
                    }
                    case '*' -> {
                        // 绘制完成的箱子：用图片"kirby.jpg"
                        if (boxImg != null) {
                            g.drawImage(boxImg, px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10, null);
                        } else {
                            // 备用绘制：如果图片加载失败，使用颜色绘制
                            g.setColor(new Color(21, 128, 61));
                            g.fillRect(px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                            g.setColor(new Color(74, 222, 128));
                            g.drawRect(px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                        }
                    }
                    default -> {
                        g.setColor(new Color(51, 65, 85));
                        g.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }
    }

    // 加载图片资源
    private void loadImages() {
        try {
            // 方法1: 尝试从类路径加载（图片与类文件在同一目录）
            java.net.URL playerUrl = getClass().getResource("kirby.jpg");
            java.net.URL boxUrl = getClass().getResource("download.png");
            
            if (playerUrl != null) {
                ImageIcon icon = new ImageIcon(playerUrl);
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    playerImg = icon.getImage();
                    System.out.println("成功加载 kirby.jpg (从类路径)");
                }
            }
            
            if (boxUrl != null) {
                ImageIcon icon = new ImageIcon(boxUrl);
                if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                    boxImg = icon.getImage();
                    System.out.println("成功加载 download.png (从类路径)");
                }
            }
            
            // 方法2: 如果类路径加载失败，尝试从文件系统加载
            if (playerImg == null) {
                java.io.File playerFile = new java.io.File("PushingBoxGame/kirby.jpg");
                if (!playerFile.exists()) {
                    playerFile = new java.io.File("kirby.jpg");
                }
                if (playerFile.exists()) {
                    ImageIcon icon = new ImageIcon(playerFile.getAbsolutePath());
                    if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                        playerImg = icon.getImage();
                        System.out.println("成功加载 kirby.jpg (从文件系统: " + playerFile.getAbsolutePath() + ")");
                    }
                }
            }
            
            if (boxImg == null) {
                java.io.File boxFile = new java.io.File("PushingBoxGame/download.png");
                if (!boxFile.exists()) {
                    boxFile = new java.io.File("download.png");
                }
                if (boxFile.exists()) {
                    ImageIcon icon = new ImageIcon(boxFile.getAbsolutePath());
                    if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                        boxImg = icon.getImage();
                        System.out.println("成功加载 download.png (从文件系统: " + boxFile.getAbsolutePath() + ")");
                    }
                }
            }
            
            // 调试信息
            if (playerImg == null) {
                System.out.println("警告: 无法加载 kirby.jpg，将使用默认绘制");
            }
            if (boxImg == null) {
                System.out.println("警告: 无法加载 download.png，将使用默认绘制");
            }
        } catch (Exception e) {
            System.out.println("图片加载错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- 音频发生器（异步播放，避免阻塞主线程）---
    private void playTone(int freq, int ms) {
        if (isMuted) return;
        // 在后台线程播放音频，避免阻塞UI
        new Thread(() -> {
            try {
                byte[] buf = new byte[ms * 8];
                for (int i = 0; i < buf.length; i++) {
                    double angle = i / (8000.0 / freq) * 2.0 * Math.PI;
                    buf[i] = (byte) (Math.sin(angle) * 127.0 * (1.0 - (double)i/buf.length));
                }
                AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
                SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();
                sdl.write(buf, 0, buf.length);
                sdl.drain();
                sdl.close();
            } catch (Exception ignored) {}
        }).start();
    }

    private void startBGM() {
        new Thread(() -> {
            int[] melody = {261, 293, 329, 392, 440};
            while (true) {
                if (!isMuted) {
                    playTone(melody[(int)(Math.random() * melody.length)] / 2, 400);
                }
                try { Thread.sleep(800); } catch (InterruptedException e) { break; }
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MovingBox().setVisible(true));
    }
}