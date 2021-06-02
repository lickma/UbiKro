package fr.imt.albi.pacman.model;

import fr.imt.albi.pacman.main.PacManLauncher;
import fr.imt.albi.pacman.utils.ArcCircle;
import fr.imt.albi.pacman.utils.Figure;
import fr.imt.albi.pacman.utils.Food;
import fr.imt.albi.pacman.utils.Wall;

public class Pacman extends Creature {
	/* L'angle d'ouverture mini de sa bouche quand il avance */
	public static final int MIN_MOUTH_ANGLE = 10;
	/* L'angle d'ouverture mini de sa bouche quand il avance */
	public static final int MAX_MOUTH_ANGLE = 40;
	/* Sa vitesse sur la grille */
	public static final int SPEED_PACMAN = 10;
	/* La couleur de Pacman */
	private static final String PACMAN_COLOR = "yellow";
	/* Le nombre initial de vies de Pacman */
	private static final int LIFE_START = 3;
	/* Le nombre de points pour qu'il obtienne une vie */
	private static final int LIFE_POINT_THRESHOLD = 10000;

	private final ArcCircle pacman;
	private int mouthAngle;
	private boolean isMouthOpen;
	private boolean isEmpowered;
	private String lastPosition;
	private String lastMovement;
	private int currentLife;
	private int currentScore;
	private int nextLifeThreshold;

	public Pacman(int size, int x, int y) {
		this.pacman = new ArcCircle(size, x, y, PACMAN_COLOR, 0, 360);

		this.lastPosition = PacManLauncher.LEFT;
		this.mouthAngle = MIN_MOUTH_ANGLE;
		this.handleMouthOpening(PacManLauncher.LEFT);
		this.currentLife = LIFE_START;
		this.isEmpowered = false;
		this.lastMovement = PacManLauncher.LEFT;
		this.nextLifeThreshold = Pacman.LIFE_POINT_THRESHOLD;
		
	}

	public void removeLife() {
		this.currentLife -= 1;
	}

	public int getCurrentLife() {
		return this.currentLife;
	}

	public void updateScoreFood(Food f) {
		if (f.isPowerUp()) {
			this.currentScore += Food.POWER_UP_SCORE;
		} else {
			this.currentScore += 1;
		}
	}

	private void checkIfNewLife() {
		if (this.currentScore >= this.nextLifeThreshold) {
			this.currentLife += 1;
			this.nextLifeThreshold += Pacman.LIFE_POINT_THRESHOLD;
		}
	}

	public void updateScoreGhost() {
		this.currentScore += Ghost.GHOST_SCORE;
	}

	public int getCurrentScore() {
		return this.currentScore;
	}

	@Override
	public int getSpeed() {
		return Pacman.SPEED_PACMAN;
	}

	@Override
	public int getX() {
		return this.pacman.getX();
	}

	@Override
	public int getY() {
		return this.pacman.getY();
	}

	@Override
	public int getWidth() {
		return this.pacman.getWidth();
	}

	@Override
	public void draw() {
		this.pacman.draw();
	}

	@Override
	public void move(String direction) {
		int xMove = 0;
		int yMove = 0;
		
		if (this.isMovePossible(direction)) {
	
			// Animation du pacman
			if (direction == "UP") {this.handleMouthOpening(PacManLauncher.UP);}
			if (direction == "DOWN") {this.handleMouthOpening(PacManLauncher.DOWN);}
			if (direction == "RIGHT") {this.handleMouthOpening(PacManLauncher.RIGHT);}
			if (direction == "LEFT") {this.handleMouthOpening(PacManLauncher.LEFT);}
			isMouthOpen = !isMouthOpen;
			if (isMouthOpen) {mouthAngle = MAX_MOUTH_ANGLE;}
			else {mouthAngle = MIN_MOUTH_ANGLE;}
	
			//Deplacement du pacman
			int[] crossMap1 = this.navigateInMap(direction);
			xMove = crossMap1[0];
			yMove = crossMap1[1];
	
			int[] crossMap2 = this.checkCollision(direction, xMove, yMove);
			xMove = crossMap2[0];
			yMove = crossMap2[1];
			
			this.pacman.move(xMove, yMove);
			this.lastMovement = direction;
			System.out.println("OUI");
			
		} else {

			// Animation du pacman
			if (this.lastMovement == "UP") {this.handleMouthOpening(PacManLauncher.UP);}
			if (this.lastMovement == "DOWN") {this.handleMouthOpening(PacManLauncher.DOWN);}
			if (this.lastMovement == "RIGHT") {this.handleMouthOpening(PacManLauncher.RIGHT);}
			if (this.lastMovement == "LEFT") {this.handleMouthOpening(PacManLauncher.LEFT);}
			isMouthOpen = !isMouthOpen;
			if (isMouthOpen) {mouthAngle = MAX_MOUTH_ANGLE;}
			else {mouthAngle = MIN_MOUTH_ANGLE;}

			//Deplacement du pacman
			int[] crossMap = this.navigateInMap(this.lastMovement);
			xMove = crossMap[0];
			yMove = crossMap[1];

			crossMap = this.checkCollision(this.lastMovement, xMove, yMove);
			xMove = crossMap[0];
			yMove = crossMap[1];
			
			this.pacman.move(xMove, yMove);
			System.out.println("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOPE");
		}
	}

	/**
	 * Cette méthode permet de vérifier si le déplacement demandé est effectivement
	 * faisable.
	 *
	 * @param direction La direction choisie
	 * @return true si possible, false sinon
	 */

	private boolean isMovePossible(String direction) {
        boolean canMove = false;
        Figure[][] map = this.gameMap.getMap();

        if (this.getX() % this.gameMap.getSizeCase() == 0 && this.getY() % this.gameMap.getSizeCase() == 0) {
            int[] position = this.getColumnAndRow();
            int xPosition = position[0];
            int yPosition = position[1];

            Figure fUp = map[yPosition - 1][xPosition];
            Figure fDown = map[yPosition + 1][xPosition];
            Figure fleft = map[yPosition][xPosition - 1];
            Figure fRight = map[yPosition][xPosition + 1];

            switch (direction) {
                case PacManLauncher.UP:
                    if (!(fUp instanceof Wall)) {
                        canMove = true;
                    }
                    break;
                case PacManLauncher.DOWN:
                    if (!(fDown instanceof Wall)) {
                        canMove = true;
                    }
                    break;
                case PacManLauncher.LEFT:
                    if (!(fleft instanceof Wall)) {
                        canMove = true;
                    }
                    break;
                case PacManLauncher.RIGHT:
                    if (!(fRight instanceof Wall)) {
                        canMove = true;
                    }
                    break;
            }
        }

        return canMove;
    }

	@Override
	public void move(int xMove, int yMove) {
		this.pacman.move(xMove, yMove);
	}

	/**
	 * Anime la bouche du petit aussi, mais avec les calculs qui vont bien
	 *
	 * @param direction La direction à laquelle pointe Pacman
	 */
	private void handleMouthOpening(String direction) {
		int startAngle = 0;
		int extentAngle = 0;

		if (direction.equals(PacManLauncher.UP)) {
			startAngle = 90 - this.mouthAngle;
			extentAngle = -360 + 2 * this.mouthAngle;
		} else if (direction.equals(PacManLauncher.LEFT)) {
			startAngle = 180 - this.mouthAngle;
			extentAngle = -360 + 2 * this.mouthAngle;
		} else if (direction.equals(PacManLauncher.DOWN)) {
			startAngle = 270 - this.mouthAngle;
			extentAngle = -360 + 2 * this.mouthAngle;
		} else if (direction.equals(PacManLauncher.RIGHT)) {
			startAngle = -this.mouthAngle;
			extentAngle = -360 + 2 * this.mouthAngle;
		}

		this.pacman.setAngleStart(startAngle);
		this.pacman.setAngleExtent(extentAngle);
		this.lastPosition = direction;
	}

	@Override
	protected void interactWithFood(Figure[][] map, int i, int j) {
		Figure f = map[i][j];
		if (f instanceof Food) {
			Food food = (Food) f;
			if (food.getFood() != null) {
				
				food.setFood(null);
				food.draw();
				this.gameMap.pickFood();
				this.updateScoreFood(food);
				this.checkIfNewLife();
				if (food.isPowerUp()) {
					this.isEmpowered = true;
				}
			}
		}
	}

	public boolean getIsEmpowered() {
		return this.isEmpowered;
	}

	public void resetIsEmpowered() {
		this.isEmpowered = false;
	}

	@Override
	public boolean checkCaseType(Figure f) {
		return f instanceof Wall || f instanceof Food;
	}

	/**
	 * Anime la bouche du petit.
	 */
	public void animateMouth() {
		if (this.isMouthOpen) {
			this.mouthAngle = MIN_MOUTH_ANGLE;
		} else {
			this.mouthAngle = MAX_MOUTH_ANGLE;
		}
		this.handleMouthOpening(this.lastPosition);
		this.isMouthOpen = !this.isMouthOpen;
	}

	/**
	 * Méthode qui permet de dire s'il se pète la gueule avec un fantome.
	 *
	 * @param f Le fantome en question
	 * @return true ou false
	 */
	public boolean isPacmanCollidingWithGhost(Ghost f) {
		int xGhost = f.getX();
		int yGhost = f.getY();
		int sizeGhost = f.getWidth();

		int xPacman = this.getX();
		int yPacman = this.getY();
		int sizePacman = this.getWidth();

		boolean posMinX = xPacman < xGhost + sizeGhost || xPacman + sizePacman < xGhost + sizeGhost;
		boolean posMaxX = xPacman > xGhost || xPacman + sizePacman > xGhost;
		boolean posMinY = yPacman < yGhost + sizeGhost || yPacman + sizePacman < yGhost + sizeGhost;
		boolean posMaxY = yPacman > yGhost || yPacman + sizePacman > yGhost;

		return posMinX && posMaxX && posMinY && posMaxY;
	}
}
