package gamestates;

import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utilz.LoadSave.MENU_BACKGROUND_IMAGE;
import static utilz.LoadSave.PAUSE_BACKGROUND_MENU;

public class Settings extends State implements Statemethods{

    private BufferedImage background, settings;
    private int bgX,bgY,bgW,bgH;
    public Settings(Game game) {
        super(game);
        loadImages();

    }

    private void loadImages() {
        settings = LoadSave.GetSpriteAtlas(PAUSE_BACKGROUND_MENU);
        background = LoadSave.GetSpriteAtlas(MENU_BACKGROUND_IMAGE);
    }


    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(background,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT,null);
        g.drawImage(settings,bgX,bgY,bgW,bgH,null);
        bgW = settings.getWidth();
        bgH = settings.getHeight();
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int) (55 * Game.SCALE);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                Gamestate.state = Gamestate.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
