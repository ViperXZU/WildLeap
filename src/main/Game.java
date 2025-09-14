package main;

import gamestates.Gamestate;
import gamestates.Menu;
import gamestates.Playing;
import gamestates.Settings;
import utilz.LoadSave;

import java.awt.*;

public class Game implements Runnable {

     private final GameWindow gameWindow;
     private final GamePanel gamePanel;

     private Thread gameThread;
     //Frames por segundo
     private final int FPS_SET = 120;
     //ACTUALIZACIONES POR SEGUNDO
     private final int UPS_SET = 200;

     private Playing playing;
     private Menu menu;
     private Settings settings;


     //Variables que determinan el tamano del juego
     public final static int TILES_DEFAULT_SIZE =32;
     public final static float SCALE = 1.6f; // Muy importante esta variable dependiendo de que tan grande queremos que se vea el juego se va modificando
     public final static int TILES_IN_WIDTH = 26;
     public final static int TILES_IN_HEIGHT = 14;
     public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
     public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
     public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

     public Game() {
         LoadSave.getAllLevels();
         initClasses();

         gamePanel = new GamePanel(this);
         gameWindow = new GameWindow(gamePanel);
         gamePanel.setFocusable(true);
         gamePanel.requestFocus();

         startGameLoop();
     }

    private void initClasses() {
         menu = new Menu(this);
         playing = new Playing(this);
         settings = new Settings(this);
    }

    private void startGameLoop(){
         gameThread = new Thread(this);
         gameThread.start();
     }

     private void update() {
         switch (Gamestate.state){
             case MENU:
                 menu.update();
                 break;
             case PLAYING:
                 playing.update();
                 break;
             case QUIT:
                 System.exit(0);
             case OPTIONS:
                 settings.update();
             default:
                 break;
         }

     }

     public void render(Graphics g){
         switch (Gamestate.state){
             case MENU:
                 menu.draw(g);
                 break;
             case PLAYING:
                 playing.draw(g);
                 break;
             case OPTIONS:
                 settings.draw(g);
             default:
                 break;
         }
     }

     @Override
     public void run() {

         double timePerFrame = 1000000000.0 / FPS_SET;
         double timePerUpdate = 1000000000.0 / UPS_SET;
         long previusTime = System.nanoTime();

         int frames = 0;
         int updates = 0;
         long lastCheck = System.currentTimeMillis();

         double deltaU = 0;
         double deltaF = 0;


        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previusTime) / timePerUpdate;
            deltaF += (currentTime - previusTime) / timePerFrame;
            previusTime = currentTime;


            if (deltaU >= 1){
                update();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1){
                gamePanel.repaint();
                frames++;
                deltaF--;
            }


            if (System.currentTimeMillis() - lastCheck >= 1000){
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS " + frames + "/ UPS"+updates);
                frames=0;
                updates=0;
            }
        }
     }



    public void windowFocusLost() {
         if (Gamestate.state == Gamestate.PLAYING){
            playing.getPlayer().resetDirBooleans();
            playing.setPaused(true); }
     }



    public gamestates.Settings getSettings(){
        return settings;
    }

    public gamestates.Menu getMenu(){
         return menu;
    }

    public Playing getPlaying() {
        return playing;
    }
}
