package ui;


import com.sun.management.GarbageCollectionNotificationInfo;
import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;

import utilz.Constans;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


import static utilz.Constans.UI.PauseButtons.*;
import static utilz.Constans.UI.URMButtons.*;


public class PauseOverlay {


    private BufferedImage backgroundImg;
    private int bgX,bgY,bgW,bgH;
    private SoundButton musicButton, sfxButton;
    private UrnButtons menuB, replayB, unpauseB;
    private Playing playing;

    public PauseOverlay(Playing playing){

        loadBackground();
        createSoundButtons();
        createUrmButtons();

        this.playing = playing;
    }

    private void createUrmButtons() {
        int menuX = (int) (327*Game.SCALE);
        int replayX = (int) (401*Game.SCALE);
        int unpauseX = (int) (473*Game.SCALE);
        int bY = (int) (325*Game.SCALE);

        menuB = new UrnButtons(menuX,bY,URM_SIZE,URM_SIZE,0);
        replayB = new UrnButtons(replayX,bY,URM_SIZE,URM_SIZE,2);
        unpauseB = new UrnButtons(unpauseX,bY,URM_SIZE,URM_SIZE,1);

    }

    private void createSoundButtons() {
        int soundX = (int) (450*Game.SCALE);
        int musicY = (int) (140*Game.SCALE);
        int sfxY = (int) (186*Game.SCALE);

        musicButton = new SoundButton(soundX,musicY,SOUND_SIZE,SOUND_SIZE,0);
        sfxButton = new SoundButton(soundX,sfxY,SOUND_SIZE,SOUND_SIZE, 1);
    }

    // Carga de la imagen y establecer las medidas y lugar
    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND_MENU);
        bgW = (int) ((backgroundImg.getWidth()/1.5) * Game.SCALE);
        bgH = (int) ((backgroundImg.getHeight()/1.5) * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int) (55 * Game.SCALE);

    }

    public void update(){
        musicButton.update();
        sfxButton.update();

        menuB.update();
        replayB.update();
        unpauseB.update();


    }
    // Dibujar la imagen dentro del panel
    public void draw(Graphics g){
        g.drawImage(backgroundImg,bgX,bgY,bgW,bgH,null);

        musicButton.draw(g);
        sfxButton.draw(g);

        menuB.draw(g);
        replayB.draw(g);
        unpauseB.draw(g);

    }

    public void mousePressed(MouseEvent e) {
        if(isIn(e,musicButton))
            musicButton.setMousePressed(true);
        else if (isIn(e,sfxButton))
            sfxButton.setMousePressed(true);
        else if (isIn(e,menuB))
            menuB.setMousePressed(true);
        else if (isIn(e,replayB))
            replayB.setMousePressed(true);
        else if (isIn(e,unpauseB))
            unpauseB.setMousePressed(true);
    }

    public void mouseReleased(MouseEvent e) {
        if (isIn(e, musicButton)) {
            if (musicButton.isMousePressed())
                musicButton.setMuted(!musicButton.isMuted());

        } else if (isIn(e, sfxButton)){
            if (sfxButton.isMousePressed())
                sfxButton.setMuted(!sfxButton.isMuted());
        }else if (isIn(e, menuB)){
            if (menuB.isMousePressed())
                Gamestate.state = Gamestate.MENU;
                playing.setPaused(false);
        } else if (isIn(e,replayB)) {
            if (replayB.isMousePressed())
                playing.resetAll();

        }else if (isIn(e, unpauseB)){
            if (unpauseB.isMousePressed())
                playing.setPaused(false);

        }

        musicButton.resetBooleans();
        sfxButton.resetBooleans();

        menuB.resetBooleans();
        replayB.resetBooleans();
        unpauseB.resetBooleans();
    }

    public void mouseDragged(MouseEvent e){}

    private boolean isIn(MouseEvent e, PauseButtons b){
        return (b.getBounds().contains(e.getX(),e.getY()));
    }
}
