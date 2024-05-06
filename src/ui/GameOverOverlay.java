package ui;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class GameOverOverlay {

    private Playing playing;
    private BufferedImage game_Over, game_Over_Background;



    public GameOverOverlay(Playing playing){
        this.playing = playing;
        loadImages();
    }

    private void loadImages() {
        game_Over = LoadSave.GetSpriteAtlas(LoadSave.GAME_OVER);
        game_Over_Background = LoadSave.GetSpriteAtlas(LoadSave.GAME_OVER_BACKGROUND);
    }

    public void draw(Graphics g){
        g.drawImage(game_Over_Background,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT,null);
        g.drawImage(game_Over,Game.GAME_WIDTH/2 - 390,Game.GAME_HEIGHT/2-200,800,400,null);
    }

    public void  keyPressed(KeyEvent e){
            playing.resetAll();
            Gamestate.state = Gamestate.MENU;

    }
}
