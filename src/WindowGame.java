package lesson1;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

// Boucle infinie
public class WindowGame extends BasicGame {
    public WindowGame() {
        super("WindowGame");
    }

    // Plateau de jeu
    private GameContainer container;
    
    // Map générée avec Tiled
    private TiledMap map;
    
    // Position initiale
    private float x = 320 , y = 320;
    
    // Direction initiale
    private int direction = 2;
    
    // Booleen vrai si en mouvement
    private boolean moving = false ;
    
    // Huit animations differentes selon direction
    private Animation[] animations = new Animation[8];
    
    // Calcul du possible futur x
    private float getFuturX(int delta){
    	float futurX = this.x;
    	switch (this.direction) {
        case 1: futurX = this.x - .1f * delta; break;
        case 3: futurX = this.x + .1f * delta; break;
        }
        return futurX;
    }
    
    // Calcul du possible futur y
    private float getFuturY(int delta) {
        float futurY = this.y;
        switch (this.direction) {
        case 0: futurY = this.y - .1f * delta; break;
        case 2: futurY = this.y + .1f * delta; break;
        }
        return futurY;
    }
    
    // Test de collision
    private boolean isCollision(float x, float y) {
        int tileW = this.map.getTileWidth();
        int tileH = this.map.getTileHeight();
        int logicLayer = this.map.getLayerIndex("logic");
        Image tile = this.map.getTileImage((int) x / tileW, (int) y / tileH, logicLayer);
        boolean collision = tile != null;
        if (collision) {
            Color color = tile.getColor((int) x % tileW, (int) y % tileH);
            collision = color.getAlpha() > 0;
        }
        return collision;
    }
    
    // Initialisation contenu du jeu, graphismes ...
    @Override
    public void init(GameContainer container) throws SlickException {
        this.container = container;
        this.map = new TiledMap("src/main/resources/map/carte.tmx");
        SpriteSheet spriteSheet = new SpriteSheet("src/main/resources/sprites/tuto-slick2d-024-character-exemple.png", 64, 64);
        this.animations[0] = loadAnimation(spriteSheet, 0, 1, 0);
        this.animations[1] = loadAnimation(spriteSheet, 0, 1, 1);
        this.animations[2] = loadAnimation(spriteSheet, 0, 1, 2);
        this.animations[3] = loadAnimation(spriteSheet, 0, 1, 3);
        this.animations[4] = loadAnimation(spriteSheet, 1, 9, 0);
        this.animations[5] = loadAnimation(spriteSheet, 1, 9, 1);
        this.animations[6] = loadAnimation(spriteSheet, 1, 9, 2);
        this.animations[7] = loadAnimation(spriteSheet, 1, 9, 3);
    }
    
    // Charge les sprites
    private Animation loadAnimation(SpriteSheet spriteSheet, int startX, int endX, int y) {
        Animation animation = new Animation();
        for (int x = startX; x < endX; x++) {
            animation.addFrame(spriteSheet.getSprite(x, y), 50);
        }
        return animation;
    }
    
    // Affichage contenu du jeu
    // NB Dessin d'une ombre : la collision correspond au coin inferieur gauche du sprite
    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        this.map.render(0, 0);
        g.setColor(new Color(0, 0, 0, .5f));
        g.fillOval(x - 16, y - 8, 32, 16);
        g.drawAnimation(animations[direction + (moving ? 4 : 0)], x-32, y-60);
    }
    
    // Gestion evolution temps reel
    @Override
	public void update(GameContainer container, int delta) throws SlickException {
		updateCharacter(delta);
	}
    
    // Gestion evolution deplacement personnage
	private void updateCharacter(int delta) {
		if (this.moving) {
			float futurX = getFuturX(delta);
			float futurY = getFuturY(delta);
			boolean collision = isCollision(futurX, futurY);
			if (collision) {
				this.moving = false;
			} else {
				this.x = futurX;
				this.y = futurY;
			}
		}
	}

	// Association fleche clavier avec direction
	@Override
    public void keyPressed(int key, char c) {
        switch (key) {
            case Input.KEY_UP:    this.direction = 0; this.moving = true; break;
            case Input.KEY_LEFT:  this.direction = 1; this.moving = true; break;
            case Input.KEY_DOWN:  this.direction = 2; this.moving = true; break;
            case Input.KEY_RIGHT: this.direction = 3; this.moving = true; break;
        }
    }

	// Relachement de la touche : a regler car problème de lors de changement rapide
    @Override
    public void keyReleased(int key, char c) {
        if (Input.KEY_ESCAPE == key) {
            container.exit();
        }
        this.moving = false;
    }
    // Fonction main generant la fenetre
    public static void main(String[] args) throws SlickException {
        new AppGameContainer(new WindowGame(), 640, 480, false).start();
    }
    
   
}