package gamestates;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utilz.LoadSave;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static utilz.Constans.Enviroment.Clouds.*;

// Clase
public class Playing extends State implements Statemethods{
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private LevelCompletedOverlay levelCompletedOverlay;
    private boolean paused = false;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
    private int lvlTilesWide = LoadSave.GetLevelData()[0].length;
    private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
    private int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;

    private BufferedImage backgroundImg, bigClouds, smallClouds;
    private int[] smallCloudsPos;
    private Random rnd = new Random();
    private Clip clip;

    private boolean gameOver;
    private boolean lvlCompleted = false;

    public Playing(Game game) {
        super(game);
        initClasses();
        loadMusic();
        loadBackground();
        setRandomY();
    }

    private void loadMusic() {
        File file = new File("src/res/level.wav");
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            System.out.println("HOLA");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void setRandomY() {
        smallCloudsPos = new int[16];
        for (int i=0; i < smallCloudsPos.length; i++)
            smallCloudsPos[i] = (int) (90*Game.SCALE + rnd.nextInt((int) (100*Game.SCALE)));
    }

    // Como bien dice para cargar las imagenes del fondo/Ambiente
    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMAGE);
        bigClouds = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallClouds = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
    }

    //
    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        player = new Player(200,200,(int)(48*Game.SCALE),(int)(48*Game.SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverlay(this);
    }

    @Override
    public void update() {
        if (paused){
            pauseOverlay.update();
        }else if (lvlCompleted){
            levelCompletedOverlay.update();
        }else if (!gameOver){
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLvlData(), player);
            checkCloseToBorder();
        }

    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }
    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff<leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        drawClouds(g);


        levelManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);
        if (paused) {
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver){
            gameOverOverlay.draw(g);
        }else if (lvlCompleted)
            levelCompletedOverlay.draw(g);
    }

    private void drawClouds(Graphics g) {
        // El limite del for determina cuantas nubes van a haber, las grandes no se tocan por que ya esta listo.
        for (int i= 0; i < 6; i++)
            g.drawImage(bigClouds, i*BIG_CLOUD_WIDTH - (int)(xLvlOffset * 0.3), (int) (204*Game.SCALE),BIG_CLOUD_WIDTH,BIG_CLOUD_HEIGHT,null);
        for (int i= 0; i < smallCloudsPos.length; i++)
            g.drawImage(smallClouds, SMALL_CLOUD_WIDTH*4*i - (int)(xLvlOffset * 0.5),smallCloudsPos[i],SMALL_CLOUD_WIDTH,SMALL_CLOUD_HEIGHT,null);
    }


    public void resetAll() {
        clip.setMicrosecondPosition(0);
        gameOver = false;
        paused = false;
        player.resetAll();
        enemyManager.resetAllEnemies();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameOver)
        if (e.getButton() == MouseEvent.BUTTON1){
            player.setAttack(true);
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver) {
            if (paused) {
                pauseOverlay.mousePressed(e);
            }else if (lvlCompleted){
                levelCompletedOverlay.mousePressed(e);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!gameOver) {
            if (paused) {
                pauseOverlay.mouseReleased(e);
            }else if (lvlCompleted) {
                levelCompletedOverlay.mouseReleased(e);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver)
            gameOverOverlay.keyPressed(e);
        else
        switch (e.getKeyCode()){
            case KeyEvent.VK_W:
                player.setUp(true);
                break;
            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(true);
                break;
            case KeyEvent.VK_ESCAPE:
                paused = !paused;

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver)
        switch (e.getKeyCode()){
            case KeyEvent.VK_W:
                player.setUp(false);
                break;
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(false);
                break;

        }
    }



    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Player getPlayer() {
        return player;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
