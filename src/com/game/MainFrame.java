package com.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainFrame extends JFrame implements ActionListener {

    private GamePanel gamePanel;
    private JPanel configPanel;
    private JTextField sizeTF;
    private JButton startBtn;
    private JLabel stepLabel;

    // 方块
    private int[][] block;
    private  int[][] bk;
    // 阶数
    private int rows;
    // 游戏步数
    private int steps;
    // 游戏分数
    private int scores;
    // 输赢状态，-1 输；0 可继续移动；1 赢
    private int isWin;
    // 是否有方块移动过
    private boolean isMoved;
    // 三种线程，移动，闪烁，分数
    private Thread t1, t2, t3;
    // 三种线程是否存活
    private boolean t1Alive, t2Alive, t3Alive;
    // 储存是否合并至数组下标位置的方块，闪烁用
    private boolean merge[];
    // 分数和Y坐标
    private int currentScore, currentScoreY;
    // 方块大小，闪烁用
    private int currentSize;
    // 方块移动信息
    private List<BlockData> list;

    private Random random = new Random(System.currentTimeMillis());

    // 方块大小
    private static final int PRE_PIECE_SIZE = 70;
    // 最大阶数，根据屏幕大小设定
    private static final int MAX_PIECES_DEGREE = (Toolkit.getDefaultToolkit().getScreenSize().height - 250) / PRE_PIECE_SIZE;
    // 方块显示起始坐标
    private static final int GAMEAREA_X = 10, GAMEAREA_Y = 90;
    // 方块间距
    private static final int BORDER_SIZE = 10;
    // 方块移动次数
    private static final int MOVE_TIMES = 20;
    // 分数移动次数
    private static final int SCORE_MOVE_TIMES = 20;
    // 方块闪烁次数
    private static final int FLASH_TIMES = 10;
    // 所有方块数值和背景颜色
    private static final int BLOCKS[][] = {{0, 0xffccc0b4}, {2, 0xffeee4da}, {4, 0xffede0c8},
            {8, 0xfff2b179}, {16, 0xfff59563}, {32, 0xfff67c5f},
            {64, 0xfff65e3b}, {128, 0xffedcf72}, {256, 0xffedcc61},
            {512, 0xffedc850}, {1024, 0xffedc53f}, {2048, 0xffedc22e}};
    // 字体名
    private static final String FONTNAME = "Arial";
    // 圆角半径
    private static final int RADIUS = 4;

    @Override
    public void actionPerformed(ActionEvent e) {
        start();
    }

    public MainFrame(){
        this.setTitle("2048");
        this.setSize(315,450);
        this.setResizable(Boolean.FALSE);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addMouseListener(new GestureListener());

        gamePanel = new GamePanel();
        gamePanel.addKeyListener(new GameKeyListener());
        add(gamePanel, BorderLayout.CENTER);

        configPanel = new JPanel();
        add(configPanel, BorderLayout.SOUTH);

        sizeTF = new JTextField("4");
        sizeTF.setColumns(6);
        sizeTF.addKeyListener(new SizeTFListener());
        configPanel.add(sizeTF);

        startBtn = new JButton("start");
        startBtn.addActionListener(this);
        configPanel.add(startBtn);

        stepLabel = new JLabel();
        configPanel.add(stepLabel);
    }

    // 初始化方块数值
    public void init(){
        block = new int[rows][];
        for(int row = 0; row < rows; row++){
            block[row] = new int[rows];
        }
        createBlock(2);
    }

    // 创建指定数目的方块
    public void createBlock(int count){
        while(count-- > 0){
            createBlock();
        }
    }

    // 随机在空白区域创建方块，如果有空格
    public void createBlock(){
        int r = 0;
        boolean hasBlock = Boolean.FALSE;
        for(int row = 0; row < rows; row++){
            for(int col = 0; col < rows; col++){
                if(block[row][col] == 0){
                    hasBlock = Boolean.TRUE;
                    row = rows;
                    break;
                }
            }
        }
        if(!hasBlock){
            return;
        }
        do{
            r = Math.abs(random.nextInt() % (rows * rows));
        }while(block[r / rows][r % rows] != 0);
        block[r / rows][r % rows] = Math.abs(random.nextInt() % 2) + 1;
    }

    // 监听手势
    class GestureListener extends MouseAdapter{
        private int startX, startY;

        @Override
        public void mousePressed(MouseEvent e){
            startX = e.getX();
            startY = e.getY();
            // 获得焦点
            if(!gamePanel.isFocusOwner()){
                gamePanel.requestFocus();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e){
            if(startX < 10 || startX > getSize().width - 10 || startY < transForm(GAMEAREA_X + 70) || startY > getSize().height - 60){
                return;
            }
            int endX = e.getX();
            int endY = e.getY();
            int dx = (int)Math.abs(startX - endX);
            int dy = (int)Math.abs(startY - endY);
            if(dx - dy < 0 && dy > 20){
                if(startY > endY){
                    gameMove(Direct.UP);
                }else{
                    gameMove(Direct.DOWN);
                }
            }else if(dx - dy > 0 && dx > 20){
                if(startX > endX){
                    gameMove(Direct.LEFT);
                }else{
                    gameMove(Direct.RIGHT);
                }
            }
        }
    }

    class GamePanel extends JPanel{

        @Override
        public void paint(Graphics g){
            super.paint(g);
            if(block == null){
                return;
            }
            g.setColor(new Color(0xffbfafa2));
            g.fillRoundRect(transForm(GAMEAREA_X), transForm(GAMEAREA_Y), PRE_PIECE_SIZE * rows + GAMEAREA_X + BORDER_SIZE - 10,
                    PRE_PIECE_SIZE * rows + BORDER_SIZE, RADIUS, RADIUS);

            g.setColor(new Color(0xff776e65));
            g.setFont(new Font(FONTNAME, Font.PLAIN, transForm(40)));
            g.drawString("2048", transForm(25), transForm(60));

            g.setColor(new Color(0xffbfafa2));
            g.fillRoundRect(transForm(130), transForm(10), transForm(70), transForm(70), RADIUS, RADIUS);
            g.setColor(Color.WHITE);
            g.setFont(new Font(FONTNAME, Font.BOLD, transForm(12)));
            g.drawString("SCORE", transForm(130 + 15), transForm(35));
            int len = String.valueOf(scores).length();
            g.setFont(new Font(FONTNAME, Font.BOLD, transForm(16)));
            g.drawString("" + scores, transForm(130 + ((70 - len * 8) >> 1)), transForm(60));

            g.setColor(new Color(0xffbfafa2));
            g.fillRoundRect(transForm(220), transForm(10), transForm(70), transForm(70), RADIUS, RADIUS);
            g.setColor(Color.WHITE);
            g.setFont(new Font(FONTNAME, Font.BOLD, transForm(12)));
            g.drawString("STEPS", transForm(238), transForm(35));

            len = String.valueOf(steps).length();
            g.setFont(new Font(FONTNAME, Font.BOLD, transForm(16)));
            g.drawString("" + steps, transForm(220 + ((70 - len * 8) >> 1) - 8), transForm(60));

            if(t3Alive && currentScore != 0){
                g.setColor(new Color(0xff776e65));
                len = String.valueOf(currentScore).length();
                g.drawString("+" + currentScore, transForm(130 + ((70 - len * 8) >> 1) - 8), currentScoreY);
            }
            int x = 0, y = 0, size = 0, data;
            g.setColor(new Color(BLOCKS[0][1]));
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < rows; j++){
                    x = j * PRE_PIECE_SIZE + transForm(GAMEAREA_X) + BORDER_SIZE;
                    y = i * PRE_PIECE_SIZE + transForm(GAMEAREA_Y) + BORDER_SIZE;
                    g.fillRoundRect(x, y, PRE_PIECE_SIZE - BORDER_SIZE, PRE_PIECE_SIZE - BORDER_SIZE, RADIUS, RADIUS);
                }
            }
            g.setFont(new Font(FONTNAME, Font.BOLD, 24));
            if(t1Alive){
                for(BlockData bd : list){
                    if(bd.data == 0){
                        continue;
                    }
                    x = transForm(GAMEAREA_X) + BORDER_SIZE * bd.x;
                    y = transForm(GAMEAREA_Y) + BORDER_SIZE * bd.y;
                    size = PRE_PIECE_SIZE - BORDER_SIZE;
                    data = bd.data;
                    g.setColor(new Color(BLOCKS[data][1]));
                    g.fillRoundRect(x, y, size, size, RADIUS, RADIUS);
                    if(data <= 2){
                        g.setColor(new Color(0xff776e65));
                    }else{
                        g.setColor((Color.WHITE));
                    }
                    if(BLOCKS[data][0] != 0){
                        len = String.valueOf(BLOCKS[data][0]).length();
                        g.drawString("" + BLOCKS[data][0], x + ((size - len * 12) >> 1) -1, y + (PRE_PIECE_SIZE >> 1) + 4);
                    }
                }
            }else{
                for(int i = rows - 1; i >= 0; i--){
                    for(int j = rows - 1; j >= 0; j--){
                        x = j * PRE_PIECE_SIZE + transForm(GAMEAREA_X) + BORDER_SIZE;
                        y = i * PRE_PIECE_SIZE + transForm(GAMEAREA_Y) + BORDER_SIZE;
                        size = PRE_PIECE_SIZE - BORDER_SIZE;
                        data = block[i][j];
                        if(t2Alive){
                            if(merge[i * rows + j]){
                                 x -= currentSize;
                                 y -= currentSize;
                                 size += currentSize << 1;
                            }
                        }
                        g.setColor(new Color(BLOCKS[data][1]));
                        if(BLOCKS[data][0] != 0){
                            g.fillRoundRect(x, y, size, size, RADIUS, RADIUS);
                        }
                        if(data <= 2){
                            g.setColor(new Color(0xff776e65));
                        }else{
                            g.setColor(Color.WHITE);
                        }
                        if(BLOCKS[data][0] != 0){
                            len = String.valueOf(BLOCKS[data][0]).length();
                            g.drawString("" + BLOCKS[data][0], x + ((size - len * 12) >> 1) - 1, y + (PRE_PIECE_SIZE >> 1) +4);
                        }
                    }
                }
            }
            g.setColor(Color.GREEN);
            if(rows < 4){
                g.setFont(new Font(FONTNAME, Font.BOLD, 14));
            }
            if(isWin == 1){
                g.drawString("YOU WIN!SCORE:" + scores + " STEPS:" + steps, 20, transForm(200));
            }else if(isWin == -1){
                g.drawString("YOU DIE!", 20, transForm(200));
            }
        }
    }

    // 按键监听
    class GameKeyListener extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_UP:
                    gameMove(Direct.UP);
                    break;
                case KeyEvent.VK_DOWN:
                    gameMove(Direct.DOWN);
                    break;
                case KeyEvent.VK_LEFT:
                    gameMove(Direct.LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                    gameMove(Direct.RIGHT);
                    break;
                    default:
                        return;
            }
        }
    }

    // 监听输入框，Enter键
    class SizeTFListener extends KeyAdapter{

        @Override
        public void keyPressed(KeyEvent e){
            if(e.getKeyCode() == KeyEvent.VK_ENTER){
                start();
            }
        }

    }

    // 点击开始按钮
    public void start(){
        rows = 0;
        try {
            rows = Integer.valueOf(sizeTF.getText().trim());
            if (rows < 2) {
                JOptionPane.showMessageDialog(null, "so small?", "Y", JOptionPane.INFORMATION_MESSAGE);
                sizeTF.setText("");
                sizeTF.requestFocus();
                return;
            } else if (rows > MAX_PIECES_DEGREE) {
                JOptionPane.showMessageDialog(null, "so big?", "Y", JOptionPane.INFORMATION_MESSAGE);
                sizeTF.setText("");
                sizeTF.requestFocus();
                return;
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Exception,repeat input?", "Y", JOptionPane.INFORMATION_MESSAGE);
            sizeTF.setText("");
            sizeTF.requestFocus();
            return;
        }
        init();
        int width = PRE_PIECE_SIZE * rows + transForm(GAMEAREA_X) + 10;
        int height = PRE_PIECE_SIZE * rows + transForm(GAMEAREA_Y) + 10;
        gamePanel.setSize(width, height);

        steps = 0;
        scores = 0;
        isWin = 0;
        t1Alive = Boolean.FALSE;
        t2Alive = Boolean.FALSE;
        t3Alive = Boolean.FALSE;
        stepLabel.setText("");
        setSize(width + transForm(GAMEAREA_X + 5), height + 70);
        repaint();
        setLocationRelativeTo(null);
        gamePanel.repaint();
        gamePanel.requestFocus();
        merge = new boolean[rows * rows];

    }

    // 计算缩放后的坐标，适应当前界面的大小
    public int transForm(int src){
        if(rows == 4){
            return src;
        }
        return src * rows / 4;
    }

    // 按照方向移动方块，先将数组行列转成按上方向移动
    public void moveBlocks(Direct direct){
        switch (direct){
            case UP:
                isMoved = moveByUpDirect(Direct.UP);
                break;
            case DOWN:
                reverse();
                isMoved = moveByUpDirect(Direct.DOWN);
                reverse();
                break;
            case RIGHT:
                transpose();
                reverse();
                isMoved = moveByUpDirect(Direct.RIGHT);
                reverse();
                transpose();
                break;
            case LEFT:
                transpose();
                isMoved = moveByUpDirect(Direct.LEFT);
                transpose();
                break;
                default:
                    break;
        }
        if(isMoved){
            createBlock();
            steps++;
            stepLabel.setText("步数：" + steps);
        }
    }

    // 将转换后的数组按照上方向移动
    public boolean moveByUpDirect(Direct direct){
        int index;
        int r, c;
        for(int col = 0; col < rows; col++){
            index = 0;
            boolean hasNext = Boolean.FALSE, isFirst = Boolean.TRUE;
            if(block[0][col] != 0){
                for(int row = 1; row < rows; row++){
                    if(block[row][col] != 0){
                        hasNext = Boolean.TRUE;
                        break;
                    }
                }
                if(!hasNext){
                    list.add(calculate(0, col, 0, col, block[0][col], direct));
                }
            }
            for(int row = 1; row < rows; row++){
                if(block[row][col] == 0){
                    continue;
                }
                if(block[index][col] == block[row][col]){
                    if(isFirst){
                        list.add(calculate(index, col, index, col, block[index][col], direct));
                        isFirst = Boolean.FALSE;
                    }
                    scores += BLOCKS[block[row][col] + 1][0];
                    currentScore += BLOCKS[block[row][col] + 1][0];
                    list.add(calculate(row, col, index, col, block[index][col], direct));
                    block[index][col] ++;
                    block[row][col] = 0;

                    if(!isMoved){
                        isMoved = Boolean.TRUE;
                    }
                    r = index;
                    c = col;
                    switch (direct){
                        case DOWN:
                            r = rows - index -1;
                        case UP:
                            break;
                        case RIGHT:
                            r = rows - index -1;
                        case LEFT:
                            int tmp = r;
                            r = c;
                            c = tmp;
                            break;
                    }
                    merge[r * rows +c] = Boolean.TRUE;
                    index ++;
                }else {
                    if(block[index][col] != 0){
                        if(isFirst){
                            list.add(calculate(index, col, index, col, block[index][col], direct));
                        }
                        index ++;
                    }
                    block[index][col] = block[row][col];
                    list.add(calculate(row, col, index, col, block[index][col], direct));
                    isFirst = Boolean.FALSE;
                    if(index != row){
                        block[row][col] = 0;
                        if(!isMoved){
                            isMoved = Boolean.TRUE;
                        }
                    }
                }
            }
            for(int row = 0; row < rows; row++){
                if(block[row][col] == 0){
                    list.add(calculate(row, col, row, col, 0, direct));

                }
            }
        }
        return isMoved;
    }

    // 将移动信息从上方向转回原方向
    public BlockData calculate(int startRow, int startCol, int endRow, int endCol, int data, Direct direct){
        BlockData bd = new BlockData(startRow, startCol, data, direct,0);
        int tmp;
        switch (direct){
            case UP:
                bd.distance = Math.abs(endRow - startRow);
                break;
            case DOWN:
                bd.row = rows - bd.row - 1;
            case RIGHT:
                tmp = bd.row;
                bd.row = bd.col;
                bd.col = rows - tmp - 1;
                bd.distance = Math.abs(endRow - startRow);
                break;
            case LEFT:
                tmp = bd.row;
                bd.row = bd.col;
                bd.col = tmp;
                bd.distance = Math.abs(endRow - startRow);
                break;
                default:
                    break;
        }
        return bd;
    }

    // 将方块每列逆转
    public void reverse(){
        int tmp;
        for(int col = 0;col < rows; col++){
            for(int row = 0; row < rows / 2; row++){
                tmp = block[rows - row - 1][col];
                block[rows - row -1][col] = block[row][col];
                block[row][col] = tmp;
            }
        }
    }

    // 将所有方块行列置换
    public void transpose(){
        int tmp;
        for(int col = 0; col < rows; col++){
            for(int row = col; row < rows; row++){
                tmp = block[col][row];
                block[col][row] = block[row][col];
                block[row][col] = tmp;
            }
        }
    }

    // 按方向移动
    public void gameMove(Direct direct){
        if(isWin != 0 || block == null){
            return;
        }
        bk = copy(block);
        list = new LinkedList<>();
        Arrays.fill(merge, Boolean.FALSE);
        isMoved = Boolean.FALSE;
        currentScore = 0;
        currentScoreY = transForm(60);
        moveBlocks(direct);
        win();
        if(isMoved){
            if(t1 !=null && t1.isAlive()){
                t1.stop();
            }
            t1 = new MoveThread();
            t1.start();

            if(t2 != null && t2.isAlive()){
                t2.stop();
            }
            t2 = new FlashThread();
            t2.start();
        }
        if(currentScore != 0){
            if(t3 != null && t3.isAlive()){
                t3.stop();
            }
            t3 = new ScoreMoveThread();
            t3.start();
        }
    }

    // 复制二维数组
    public int[][] copy(int[][] src){
        int len = src.length;
        int[][] ret = new int[len][];
        for(int i = 0; i < len; i++){
            ret[i] = new int[src[0].length];
            System.arraycopy(src, 0, ret, 0, src[0].length);
        }
        return ret;
    }

    // 判断游戏输赢
    public int win(){
        boolean  canMove = Boolean.FALSE;
        if(isWin == 1){
            return 1;
        }
         for(int row = 0; row < rows; row++){
             for(int col = 0; col < rows; col++){
                 // 达到2048游戏结束
                 if(block[row][col] == BLOCKS.length - 1){
                     isWin = 1;
                     return 1;
                 }
                 if(!canMove && block[row][col] == 0){
                     canMove = Boolean.TRUE;
                 }
             }
         }
         // 没有空格
         if(!canMove){
             for(int row = 0; row < rows; row++){
                 for(int col = 0; col <rows; col++){
                     canMove = canMove(row, col);
                     if(canMove){
                         row = rows;
                         break;
                     }
                 }
             }
         }
         if(canMove){
             isWin = 0;
             return 0;
         }
         isWin = -1;
         return -1;
    }

    // 在没有空格的情况下，判断当前方块是否可以移动
    public boolean canMove(int row, int col){
        if(row > 0 && block[row - 1][col] == block[row][col]){
            return Boolean.TRUE;
        }
        if(row < rows - 1 && block[row + 1][col] == block[row][col]){
            return Boolean.TRUE;
        }
        if(col > 0 && block[row][col -1] == block[row][col]){
            return Boolean.TRUE;
        }
        if(col < rows && block[row][col + 1] == block[row][col]){
            return  Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    // 移动线程
    class MoveThread extends Thread{
        @Override
        public void run(){
            t1Alive = Boolean.TRUE;
            while(t1Alive){
                for(int time = 1; time <= MOVE_TIMES; time++){
                    for(BlockData bd : list){
                        bd.x = bd.col * PRE_PIECE_SIZE;
                        bd.y = bd.row * PRE_PIECE_SIZE;
                        if(bd.distance == 0){
                            continue;
                        }
                        switch (bd.direct){
                            case UP:
                                bd.y -= (bd.distance * PRE_PIECE_SIZE * time) / MOVE_TIMES;
                                break;
                            case DOWN:
                                bd.y += (bd.distance * PRE_PIECE_SIZE * time) / MOVE_TIMES;
                                break;
                            case LEFT:
                                bd.x -= (bd.distance * PRE_PIECE_SIZE * time) / MOVE_TIMES;
                                break;
                            case RIGHT:
                                bd.x += (bd.distance * PRE_PIECE_SIZE * time) / MOVE_TIMES;
                                break;
                        }
                    }
                    gamePanel.repaint();
                    try {
                        Thread.sleep(10);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                t1Alive = Boolean.FALSE;
            }
        }
    }

    // 闪烁线程
    class FlashThread extends Thread {
        @Override
        public void run(){
            try {
                Thread.sleep(10 * MOVE_TIMES);
            }catch (Exception e){
                e.printStackTrace();
            }
            t2Alive = Boolean.TRUE;
            while(t2Alive){
                for(int time = 1; time <= FLASH_TIMES; time++){
                    currentSize = PRE_PIECE_SIZE - BORDER_SIZE;
                    if(time < (FLASH_TIMES >>> 2)){
                        currentSize = currentSize * time / (FLASH_TIMES << 3);
                    }else{
                        currentSize = currentSize * (FLASH_TIMES - time) / (FLASH_TIMES << 3);
                    }
                    gamePanel.repaint();
                    try {
                        Thread.sleep(20);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                t2Alive = Boolean.FALSE;
            }

        }
    }

    // 分数移动线程
    class ScoreMoveThread extends Thread{
        @Override
        public void run(){
            t3Alive = Boolean.TRUE;
            while(t3Alive){
                for(int  time = 0; time <= SCORE_MOVE_TIMES; time++){
                    if(rows < 4){
                        currentScoreY = transForm(((currentScoreY << 2) / rows) - (20 / SCORE_MOVE_TIMES));
                    }else{
                        currentScoreY -= transForm(20 / SCORE_MOVE_TIMES);
                    }
                    gamePanel.repaint(transForm(130), transForm(10), transForm(70), transForm(70));
                    try {
                        Thread.sleep(20);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                t3Alive = Boolean.FALSE;
                gamePanel.repaint(transForm(130), transForm(10), transForm(70), transForm(70));
            }
        }
    }
}