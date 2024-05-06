package utilz;

import main.Game;

import java.awt.geom.Rectangle2D;

public class HelpMethods {
    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        if (!IsSolid(x, y, lvlData))
            if (!IsSolid(x + width, y + height, lvlData))
                if (!IsSolid(x + width, y, lvlData))
                    if (!IsSolid(x, y + height, lvlData))
                        return true;
        return false;
    }

     private static boolean IsSolid(float x, float y, int [][] lvlData){
         int maxWidth = lvlData[0].length * Game.TILES_SIZE;
         if (x < 0 || x >= maxWidth)
             return true;
         if (y < 0 || y >= Game.GAME_HEIGHT)
             return true;

         float xIndex = x / Game.TILES_SIZE;
         float yIndex = y / Game.TILES_SIZE;

         return IsTileSolid((int) xIndex, (int) yIndex,lvlData);
     }
    public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData){
        int value = lvlData[(int)yTile][(int) xTile];

        return value >= 48 || value < 0 || value != 11;
    }
    //Como dice ahi para obtener la posicion
     public static float GetEntityXposNextToWall(Rectangle2D.Float hitbox, float xSpeed){
         int currentTile = (int)( hitbox.x / Game.TILES_SIZE);
         if (xSpeed > 0){
             //Derecha
             int tileXPos = currentTile * Game.TILES_SIZE;
             int xOffset = (int)(Game.TILES_SIZE- hitbox.width);
             return tileXPos + xOffset -1;
         }else {
             //Izquierda
             return currentTile * Game.TILES_SIZE;
         }
     }
    //Como dice ahi para obtener la posicion
    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed){
        int currentTile = (int)( hitbox.y / Game.TILES_SIZE);
        if (airSpeed > 0){
            //Cayendo
            int tileYPos = currentTile * Game.TILES_SIZE;
            int yOffset = (int)(Game.TILES_SIZE- hitbox.height);
            return tileYPos + yOffset -1;
        }else {
            //Saltando
            return currentTile * Game.TILES_SIZE;
        }
    }
    // Basicamente revisa si los pixeles de las esquinas estan tocando piso mediante el metodo isSolid
    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int [][] lvlData) {
        //Filtra los pixeles del x
        if (!IsSolid(hitbox.x, hitbox.y+hitbox.height+1,lvlData ))//Filtra los pixeles del y
            return IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData);
        return true ;
    }


    // Metodo para saber si lo que se va a pisar es suelo (Para enemigos)
    public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
        if (xSpeed > 0)
            return IsSolid(hitbox.x + hitbox.width +  xSpeed,  hitbox.y + hitbox.height + 1, lvlData);
        else
            return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
    }
    // Con la funcion isTileSolid reccorremos cada casilla dada en los parametros buscando algun obstaculo
    public static boolean IsTileWalkable(int xStart, int xEnd, int y ,int [][] lvlData){
        for (int i = 0; i < xEnd - xStart; i++) {
            if (IsTileSolid(xStart + i, y , lvlData))
                return false;
            if (!IsTileSolid(xStart+i,y+1,lvlData))
                return false;
        }   // en caso de que encuentre una casilla solida devolvera falso por que no es caminable
        return true;
        // resumidamente es una funcion para que el codigo se vea mas limpio
    }

    // Funcion que dedice si la en el camino no hay nada
    public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int tileY) {
        int firstXTile = (int)(firstHitbox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

        if (firstXTile > secondXTile){
            return IsTileWalkable(secondXTile,firstXTile,tileY,lvlData);// Atiende el caso en que el secondtile sea mas grande que el primero
        }else {
            return IsTileWalkable(firstXTile,secondXTile,tileY,lvlData);// Atiende el caso en que el fristTile sea mas grande que el segundo
        }
    }
}
