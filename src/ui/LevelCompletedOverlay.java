package ui;

import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utilz.Constans.UI.URMButtons.*;

public class LevelCompletedOverlay {

    private Playing playing;
    private UrnButtons menu, next;
    private BufferedImage img;
    private int bgX,bgY,bgW,bgH;

    public LevelCompletedOverlay(Playing playing){
        this.playing = playing;
        initImage();
        initButtons();
    }

    private void initButtons() {
        int menuX = (int) (330 * Game.SCALE);
        int nextX = (int) (445 * Game.SCALE);
        int y = (int) (195 * Game.SCALE);
        next = new UrnButtons(nextX, y, URM_SIZE, URM_SIZE, 1);
        menu = new UrnButtons(menuX, y, URM_SIZE, URM_SIZE, 0);
    }

    private void initImage() {
        img = LoadSave.GetSpriteAtlas(LoadSave.COMPLETED_IMG);
        bgW = (int) (img.getWidth() * Game.SCALE);
        bgH = (int) (img.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW /2;
        bgY = (int) (75 * Game.SCALE);
    }
    public void draw(Graphics g){
        g.drawImage(img,bgX,bgY,bgW,bgH,null);
        next.draw(g);
        menu.draw(g);
    }
    public void update(){

    }
    private boolean isIn(MouseEvent e, UrnButtons b){
        return (b.getBounds().contains(e.getX(),e.getY()));
    }

    public void mousePressed(MouseEvent e){
        if (isIn(e,next)) {
            next.setMousePressed(true);
        } else if (isIn(e,menu)){
            menu.setMousePressed(true);
        }
    }
    public void mouseReleased(MouseEvent e) {
        if (isIn(e,next)) {
            if(next.isMousePressed())
                System.out.println("next");
        } else if (isIn(e,menu)){
            if(menu.isMousePressed())
                System.out.println("menu");
        }
        menu.resetBooleans();
        next.resetBooleans();
    }
}
