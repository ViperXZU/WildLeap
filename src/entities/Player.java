package entities;

import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utilz.Constans.PlayerConstants.*;
import static utilz.HelpMethods.*;

public class Player extends Entity{

    private Playing playing;

    // Variables para cambiar / guardar animaciones
    private BufferedImage[][] animations;
    private int aniTick;
    private int aniIndex;
    private final int aniSpeed = 25;
    private int playerAction = IDLE;


    // Variables para movimiento / Ataque
    private boolean right,left,up,down, jump;
    private boolean moving = false, attacking= false;

    private final float playerSpeed = 2.0f; // Velocidad del Personaje

    private int[][] lvlData;

    // Variables encuadrar la hitbox en el personaje
    private final float xDrawOffset = 15 * Game.SCALE;
    private final float yDrawOffset= 3 * Game.SCALE;

    //Variables Gravedad / SALTO
    private float airSpeed = 0f;
    private final float gravity = 0.04f * Game.SCALE;
    private final float jumpSpeed = -2.25f * Game.SCALE;
    private final float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;

    // Estatus del jugador
    private BufferedImage statusBarImg;

    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    private int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthBarHeight = (int) (4 * Game.SCALE);
    private int healthBarXStart = (int) (34 * Game.SCALE);
    private int healthBarYStart = (int) (14 * Game.SCALE);

    private int maxHealt = 100;
    private int currentHealth = maxHealt;
    private int healthWidth = healthBarWidth;

    // AttackBox
    private Rectangle2D.Float attackBox;

    private int flipX =0;
    private int flipW =1;

    private boolean attackChecked;


    //Clase principal del jugador
    public Player(float x, float y,int width , int height, Playing playing) {
        super(x, y , width, height);
        loadAnimations();
        initHitbox(x,y,(int) (16 * Game.SCALE), (int) (27 * Game.SCALE)); // Iniciacion principal de la hitbox
        initAttackBox();
        // 32x32 width 12 y 28 height
        // xDrawOffset  10
        // yDrawOffset= 2
        this.playing = playing;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x,y,(int)25*Game.SCALE, (int)20*Game.SCALE);
    }

    public void update(){
        updateHealthBar();
        if (currentHealth <= 0){
            playing.setGameOver(true);
            return;
        }

        updateAttackBox();

        updatePos();
        if (attacking)
            checkAttack();
        updateAnimationTick();
        setAnimation();
    }

    private void checkAttack() {
        if (attackChecked || aniIndex != 1)
            return;
        attackChecked = true;
        playing.checkEnemyHit(attackBox);
    }

    private void updateAttackBox() {
        if (right){
            attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 10);
        } else if (left) {
            attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 10);
        }
        attackBox.y = hitbox.y +(int) (10 * Game.SCALE);
    }

    private void updateHealthBar() {
        healthWidth = (int) ((currentHealth / (float) maxHealt) * healthBarWidth);
    }

    // Para dibujar el jugador con sus respectivas animaciones
    public void render(Graphics g, int xLvlOffset){
        g.drawImage(animations[playerAction][aniIndex],
                (int) (hitbox.x - xDrawOffset) - xLvlOffset + flipX,
                (int) ((hitbox.y - yDrawOffset)-24),
                width * flipW, height, null);
        // drawHitBox(g,xLvlOffset);
        // drawAttackBox(g,xLvlOffset);
        drawUI(g);
    }

    private void drawAttackBox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int)attackBox.x - xLvlOffset,(int)attackBox.y, (int)attackBox.width,(int)attackBox.height);
    }

    private void drawUI(Graphics g){
        g.drawImage(statusBarImg,statusBarX,statusBarY,statusBarWidth,statusBarHeight,null);
        g.setColor(Color.red);
        g.fillRect(healthBarXStart + statusBarX,healthBarYStart + statusBarY,healthWidth  ,healthBarHeight);
    }

    private void loadAnimations() {

            BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);


            animations = new BufferedImage[5][12];
            for (int j = 0; j < animations.length; j++) {
                for (int i = 0; i < animations[j].length; i++) {
                    animations[j][i] = img.getSubimage(i * 64, j * 64, 64, 64);
                }
            }

            statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitbox,lvlData)){
            inAir = true;
        }
    }

    private void updateAnimationTick() {
        aniTick++;
        if(aniTick >= aniSpeed){
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(playerAction)){
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
            }
        }
    }

    public void resetDirBooleans(){
        up = false;
        down = false;
        left= false;
        right=false;
    }

    public void setAttack(boolean attack){
        this.attacking = attack;
    }

    private void setAnimation() {
        int startAni = playerAction;


        if (moving)
            playerAction = RUNNING;
        else
            playerAction = IDLE;

        if (inAir){
            if (airSpeed < 0) {
                playerAction = JUMP;
            }else
                playerAction = FALLING;
        }

        if (attacking) {
            playerAction = ATTACK;
            if (startAni != ATTACK){
                aniIndex=1;
                aniTick =0;
            }
        }

        if (startAni != playerAction)
            resetAniTick();
    }

    private void resetAniTick() {
        aniTick =0;
        aniIndex =0;
    }


    //Funcion para actualizar la posicion del personaje dependiendo de la tecla que se presione
    private void updatePos(){
        moving= false;

        if (jump){
            jump();
        }

        if (!left && !right && !inAir ){
            return;
        }

        float xSpeed =0;

       if(left) {
           xSpeed -= playerSpeed;
           flipX = width;
           flipW = -1;
       }
       if(right) {
           xSpeed += playerSpeed;
           flipX = 0;
           flipW = 1;
       }

       if (!inAir){
           if (!IsEntityOnFloor(hitbox, lvlData)){
               inAir= true;
           }
       }

       if (inAir){

        if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)){
            hitbox.y +=airSpeed;
            airSpeed += gravity;
            updateXPos(xSpeed);
        }else {
        //Condicional que tiene dentro una funcion condicional que detectara si el personaje se puede mover en una direccion u otra
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox,airSpeed);
            if (airSpeed > 0)
                resetInAir();
            else
                airSpeed = fallSpeedAfterCollision;
            updateXPos(xSpeed);
        }

       }else
           updateXPos(xSpeed);
       moving = true;


    }

    private void jump() {
        if(inAir)
            return;
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)){
            hitbox.x +=xSpeed;
        }else {
            hitbox.x = GetEntityXposNextToWall(hitbox, xSpeed);
        }
    }

    public void changeHealth(int value){
        currentHealth += value;

        if (currentHealth <=0){
            currentHealth=0;
            //gameOver();
        }else if (currentHealth >= maxHealt)
            currentHealth = maxHealt;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void resetAll() {
        resetDirBooleans();
        resetInAir();
        resetAniTick();
        moving = false;
        jump = false;
        playerAction = IDLE;
        currentHealth = maxHealt;

        hitbox.x = 200;
        hitbox.y = 200;
        if (!IsEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
        }
    }
}
