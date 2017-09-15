import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
public class Breakout extends GraphicsProgram {
/**Starts random generator*/
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
/**Sets random number*/
	private double setRandomNumb(double min, double max){
		return rgen.nextDouble(min, max);
	}

/**Determines how many bricks have been removed*/
	private int counter=0;

/**Determines whether ball has collided with the paddle*/
	private boolean interaction=false;

/** Width of application window in pixels */
	private static final int APPLICATION_WIDTH = 417;
/** Height of application window in pixels */
	private static final int APPLICATION_HEIGHT = 700;

/** Dimensions of game board  */
	private static final int WIDTH = 400;
	private static final int HEIGHT = 600;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;
	
/** Speed of the ball */
	private static final double SPEED = 3;
	private double speed=SPEED;
	private static final double acceleration=1.5;
	private boolean accelerate;
	private GLine a1;
	private GLine a2;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Number of turns */
	private int tries = 3;
	
/** Array of bricks */
	private GRect[] row = new GRect[NBRICKS_PER_ROW*NBRICK_ROWS];
	private GImage[] health = new GImage[10];
	private GImage[] bonuses = new GImage[3];
	private boolean ghost;
	private long endTimeG;
	private long endTimeA;
	private static final long BONUS=10000;
	
/** Draws a ball */
	private GOval ball = new GOval(WIDTH/2-BALL_RADIUS/2,HEIGHT/2-BALL_RADIUS/2,BALL_RADIUS,BALL_RADIUS);

/** Determines delay with which refreshes are shown */
	private final static int DELAY=10;

/** Sets start angle of ball's direction */
	private double angle = setRandomNumb(0.3,Math.PI/2-0.3);


/** Draws a paddle */
	private GRect paddle;

/** Checks whether the ball collided with paddle by upper side*/
	private GObject touchU;
/** Checks whether the ball collided with paddle by down side*/
	private GObject touchD;
/** Checks whether the ball collided with paddle by left side*/
	private GObject touchL;
/** Checks whether the ball collided with paddle by right side*/
	private GObject touchR;
	
	/** Prepares game for start*/
	private void setup(){
		this.setSize(APPLICATION_WIDTH,APPLICATION_HEIGHT);
		makeRows();
		paddle = new GRect(WIDTH/2-PADDLE_WIDTH/2,HEIGHT-PADDLE_Y_OFFSET-PADDLE_HEIGHT,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
		for(int i=0;i<health.length;i++){
			health[i]=new GImage("heart.png", WIDTH-40*(i+1), HEIGHT);
			if(i<3)add(health[i]);
		}
		bonuses[0]=new GImage("heart.png");
		bonuses[1]=new GImage("accelerate.png");
		bonuses[2]=new GImage("ghost.png");
		a1=new GLine(ball.getX(), ball.getY()+BALL_RADIUS/2, ball.getX()-20*Math.sin(angle), ball.getY()-20*Math.cos(angle)+BALL_RADIUS/2);
		a2=new GLine(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS/2, ball.getX()+BALL_RADIUS-20*Math.sin(angle), ball.getY()+BALL_RADIUS/2-20*Math.cos(angle));
		a1.setVisible(false);
		a2.setVisible(false);
		a1.setColor(Color.GRAY);
		a2.setColor(Color.GRAY);
		addMouseListeners(); 
	}

/** Get colliding object */	
	private GObject getCollidingObject(char det, GObject check){
					if(det=='v'){
					    if(getElementAt(ball.getX()+BALL_RADIUS/2,ball.getY()-check.getHeight()/2) != null){
						    return getElementAt(ball.getX()+BALL_RADIUS/2,ball.getY()-check.getHeight()/2);
					    } 
					    if (getElementAt(ball.getX()+BALL_RADIUS/2,ball.getY()+BALL_RADIUS+check.getHeight()/2) != null){
						    return getElementAt(ball.getX()+BALL_RADIUS/2,ball.getY()+BALL_RADIUS+check.getHeight()/2);
					    }
					}
					if(det=='h'){
					    if (getElementAt(ball.getX()-check.getWidth()/7,ball.getY()+BALL_RADIUS/2) != null){
						    return getElementAt(ball.getX()-check.getWidth()/7,ball.getY()+BALL_RADIUS/2);
					    } 
					    if (getElementAt(ball.getX()+BALL_RADIUS+check.getWidth()/7,ball.getY()+BALL_RADIUS/2) != null){
						    return getElementAt(ball.getX()+BALL_RADIUS+check.getWidth()/7,ball.getY()+BALL_RADIUS/2);
					    }
				}
					return null;
			}


/** Draws rows of bricks with color depending on its number */
	private void makeRows(){
		for (int i=0,m=0; i<NBRICK_ROWS; i++){
			for (int j=0; j<NBRICKS_PER_ROW; j++,m++){
				double x = BRICK_SEP/2+BRICK_WIDTH*j+BRICK_SEP*j ;
				double y = 50.0+(BRICK_HEIGHT+BRICK_SEP)*i;

				row[m] = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				row[m].setFilled(true);
				if (i < 2) {
					row[m].setColor(Color.RED);
				} else if (i == 2 || i == 3) {
					row[m].setColor(Color.ORANGE);
				} else if (i == 4 || i == 5) {
					row[m].setColor(Color.YELLOW);
				} else if (i == 6 || i == 7) {
					row[m].setColor(Color.GREEN);
				} else if (i == 8 || i == 9) {
					row[m].setColor(Color.CYAN);
				}
				add(row[m]);
			}
		}
	}
	
	/**Appears after the end of the game to hide paddle and ball*/
	private GRect hider(){
		GRect hider=new GRect(0,0,WIDTH,HEIGHT);
		hider.setColor(Color.WHITE);
		hider.setFilled(true);
		hider.setFillColor(Color.WHITE);
		return hider;
	}
	
/**Determines whether the game is over and prints text appropriate to the situation at the end of the game*/
	private boolean gameOver(){
		boolean result=false;
		if(counter==NBRICK_ROWS*NBRICKS_PER_ROW){
			add(hider());
			GLabel victory = new GLabel("YOU'VE WON!",WIDTH/4+14,HEIGHT/2+14);
			victory.setFont("Times New Roman-28");
			add(victory);
			result = true;
		}
		if( tries==0){
			add(hider());
			GLabel lost = new GLabel("YOU'VE LOST",WIDTH/4+14,HEIGHT/2+14);
			lost.setFont("Times New Roman-28");
			add(lost);
			result = true;
		}
		return result;
	}
	
	/**Moves ball across game table*/
	private void moveBall(){
		ball.move(Math.sin(angle)*speed, Math.cos(angle)*speed);
		if(a1!=null)a1.move(Math.sin(angle)*speed, Math.cos(angle)*speed);
		if(a2!=null)a2.move(Math.sin(angle)*speed, Math.cos(angle)*speed);
	}
	
	/**Checks with what ball has interacted with something*/
	private void interaction(){
		touchWithPaddle();
		touchWithBrick();
		touchWithWall();
		touchWithBonus();
	}

	/** Determines what happens if mouse has moved */
	public void mouseMoved(MouseEvent e){
		if(!gameOver() && !interaction){
			if((e.getX()<=PADDLE_WIDTH/2)||e.getX()+PADDLE_WIDTH/2>=WIDTH){
				paddle.move(0,0);
			}
			else{
		        paddle.move(e.getX()-paddle.getX()-PADDLE_WIDTH/2,0);
		    }
		}
	}
	
	/** Determines what happens if ball touches with a paddle */
	private void touchWithPaddle(){
		double x=0.35;
		for(double i=0.3;i<=0.7;i+=0.002){
		    touchU=getElementAt(ball.getX()+i*BALL_RADIUS, ball.getY()-PADDLE_HEIGHT/6);
		    touchD=getElementAt(ball.getX()+i*BALL_RADIUS, ball.getY()+BALL_RADIUS+BRICK_HEIGHT/2);
		    touchL=getElementAt(ball.getX()-PADDLE_WIDTH/9, ball.getY()+x*BALL_RADIUS);
		    touchR=getElementAt(ball.getX()+BALL_RADIUS+PADDLE_WIDTH/9, ball.getY()+x*BALL_RADIUS);
            if((touchD==paddle||touchU==paddle)&& !interaction){
			    interaction=true;
			    if(touchD==paddle){
					pause(DELAY);
				    ball.move(0,-BALL_RADIUS);
			    }
			    else{
				    if(touchU==paddle){
						pause(DELAY);
					    ball.move(0,BALL_RADIUS);
				    }
			    }
			    angle=Math.PI-angle;
				if(a1.isVisible() && a2.isVisible()){
					remove(a1);
					remove(a2);
					a1=new GLine(ball.getX(), ball.getY()+BALL_RADIUS/2, ball.getX()-20*Math.sin(angle), ball.getY()-20*Math.cos(angle)+BALL_RADIUS/2);
					a2=new GLine(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS/2, ball.getX()+BALL_RADIUS-20*Math.sin(angle), ball.getY()+BALL_RADIUS/2-20*Math.cos(angle));
					a1.setColor(Color.GRAY);
					a2.setColor(Color.GRAY);
					add(a1);
					add(a2);
				}
			    break;
		    }
		    if((touchR==paddle)||(touchL==paddle)&& !interaction){
			    interaction=true;
                if(touchR==paddle){
					pause(DELAY);
            	    ball.move(-BALL_RADIUS,0);
			    }
			    else{
				    if(touchL==paddle){
						pause(DELAY);
					    ball.move(BALL_RADIUS,0);
				    }
			    }
			    angle=(-1)*angle;
				if(a1.isVisible() && a2.isVisible()){
					remove(a1);
					remove(a2);
					a1=new GLine(ball.getX(), ball.getY()+BALL_RADIUS/2, ball.getX()-20*Math.sin(angle), ball.getY()-20*Math.cos(angle)+BALL_RADIUS/2);
					a2=new GLine(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS/2, ball.getX()+BALL_RADIUS-20*Math.sin(angle), ball.getY()+BALL_RADIUS/2-20*Math.cos(angle));
					a1.setColor(Color.GRAY);
					a2.setColor(Color.GRAY);
					add(a1);
					add(a2);
				}
			    break;
		    }
		    if((touchR!=paddle)&&(touchL!=paddle)&&(touchD!=paddle)&&(touchU!=paddle)&&(touchD!=paddle)){
			    interaction=false;
		    }
		    if(x<=0.65){
		        x+=0.002;
		    }
		}
	}
		
/**Determines what happens if ball touches with a brick*/
	private void touchWithBrick(){
					GObject collisionV;
					GObject collisionH;
					for(int i=0;i<row.length;i++) {
						collisionV=getCollidingObject('v', row[i]);
						collisionH=getCollidingObject('h', row[i]);;
							if (collisionV == row[i]) {
								remove(collisionV);
								pause(DELAY);
								if(!ghost)angle = Math.PI - angle;
								if(a1.isVisible() && a2.isVisible()){
									remove(a1);
									remove(a2);
									a1=new GLine(ball.getX(), ball.getY()+BALL_RADIUS/2, ball.getX()-20*Math.sin(angle), ball.getY()-20*Math.cos(angle)+BALL_RADIUS/2);
									a2=new GLine(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS/2, ball.getX()+BALL_RADIUS-20*Math.sin(angle), ball.getY()+BALL_RADIUS/2-20*Math.cos(angle));
									a1.setColor(Color.GRAY);
									a2.setColor(Color.GRAY);
									add(a1);
									add(a2);
								}
								counter++;
							}
							if (collisionH == row[i]) {
								remove(collisionH);
								pause(DELAY);
								if(!ghost)angle = (-1) * angle;
								if(a1.isVisible() && a2.isVisible()){
									remove(a1);
									remove(a2);
									a1=new GLine(ball.getX(), ball.getY()+BALL_RADIUS/2, ball.getX()-20*Math.sin(angle), ball.getY()-20*Math.cos(angle)+BALL_RADIUS/2);
									a2=new GLine(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS/2, ball.getX()+BALL_RADIUS-20*Math.sin(angle), ball.getY()+BALL_RADIUS/2-20*Math.cos(angle));
									a1.setColor(Color.GRAY);
									a2.setColor(Color.GRAY);
									add(a1);
									add(a2);
								}
								counter++;
							}
					}
				}

	
/**Determines what happens if ball moves off the screen*/
	private void touchWithWall(){
		if(ball.getX()<=0||ball.getX()+BALL_RADIUS>=WIDTH){
            angle=(-1)*angle;
            if(ball.getX()<=0){
				pause(DELAY);
                ball.move(BALL_RADIUS,0);
            }
            else{
				pause(DELAY);
                ball.move(-BALL_RADIUS,0);
            }
			if(a1.isVisible() && a2.isVisible()){
				remove(a1);
				remove(a2);
				a1=new GLine(ball.getX(), ball.getY()+BALL_RADIUS/2, ball.getX()-20*Math.sin(angle), ball.getY()-20*Math.cos(angle)+BALL_RADIUS/2);
				a2=new GLine(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS/2, ball.getX()+BALL_RADIUS-20*Math.sin(angle), ball.getY()+BALL_RADIUS/2-20*Math.cos(angle));
				a1.setColor(Color.GRAY);
				a2.setColor(Color.GRAY);
				add(a1);
				add(a2);
			}
        }
		if(ball.getY()<=0){
			pause(DELAY);
			angle=Math.PI-angle;
			ball.move(0,BALL_RADIUS);
			if(a1.isVisible() && a2.isVisible()){
				remove(a1);
				remove(a2);
				a1=new GLine(ball.getX(), ball.getY()+BALL_RADIUS/2, ball.getX()-20*Math.sin(angle), ball.getY()-20*Math.cos(angle)+BALL_RADIUS/2);
				a2=new GLine(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS/2, ball.getX()+BALL_RADIUS-20*Math.sin(angle), ball.getY()+BALL_RADIUS/2-20*Math.cos(angle));
				a1.setColor(Color.GRAY);
				a2.setColor(Color.GRAY);
				add(a1);
				add(a2);
			}
		}
		if(ball.getY()+BALL_RADIUS>=HEIGHT){
			pause(DELAY);
			angle=Math.PI-angle;
			ball.move(0,-BALL_RADIUS);
			if(a1.isVisible() && a2.isVisible()){
				remove(a1);
				remove(a2);
				a1=new GLine(ball.getX(), ball.getY()+BALL_RADIUS/2, ball.getX()-20*Math.sin(angle), ball.getY()-20*Math.cos(angle)+BALL_RADIUS/2);
				a2=new GLine(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS/2, ball.getX()+BALL_RADIUS-20*Math.sin(angle), ball.getY()+BALL_RADIUS/2-20*Math.cos(angle));
				a1.setColor(Color.GRAY);
				a2.setColor(Color.GRAY);
				add(a1);
				add(a2);
			}
			remove(health[tries-1]);
			tries--;
		}
	}

	private void touchWithBonus(){
		if(getCollidingObject('v', bonuses[0])==bonuses[0] || getCollidingObject('h', bonuses[0])==bonuses[0]){
			tries++;
			remove(bonuses[0]);
			add(health[tries-1]);
		}
		if(getCollidingObject('v', bonuses[1])==bonuses[1] || getCollidingObject('h', bonuses[1])==bonuses[1]){
			speed+=acceleration;
			accelerate=true;
			a1=new GLine(ball.getX(), ball.getY()+BALL_RADIUS/2, ball.getX()-20*Math.sin(angle), ball.getY()-20*Math.cos(angle)+BALL_RADIUS/2);
			a2=new GLine(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS/2, ball.getX()+BALL_RADIUS-20*Math.sin(angle), ball.getY()+BALL_RADIUS/2-20*Math.cos(angle));
			a1.setColor(Color.GRAY);
			a2.setColor(Color.GRAY);
			add(a1);
			add(a2);
			endTimeA=System.currentTimeMillis()+BONUS;
			remove(bonuses[1]);
		}
		if(getCollidingObject('v', bonuses[2])==bonuses[2] || getCollidingObject('h', bonuses[2])==bonuses[2]){
			ghost=true;
			endTimeG=System.currentTimeMillis()+BONUS;
			ball.setFillColor(Color.LIGHT_GRAY);
			ball.setColor(Color.LIGHT_GRAY);
			remove(bonuses[2]);
		}
	}
	private void createBonus(){
		double p;
		p=setRandomNumb(0,3);
		if(p>=0.495 && p<=0.5 && tries<10){
			remove(bonuses[0]);
			add(bonuses[0], setRandomNumb(0, WIDTH-30), setRandomNumb(row[99].getY()+BRICK_WIDTH, HEIGHT-60-PADDLE_HEIGHT));
		}
		if(p>=1.495 && p<=1.5 && !accelerate){
			remove(bonuses[1]);
			add(bonuses[1], setRandomNumb(0, WIDTH-30), setRandomNumb(row[99].getY()+BRICK_WIDTH, HEIGHT-60-PADDLE_HEIGHT));
		}
		if(p>=2.495 && p<=2.5 && !ghost){
			remove(bonuses[2]);
			add(bonuses[2], setRandomNumb(0, WIDTH-30), setRandomNumb(row[99].getY()+BRICK_WIDTH, HEIGHT-60-PADDLE_HEIGHT));
		}
	}
/** Runs the Breakout program. */
	public void run() {
		setup();
		while(!gameOver()){
			if(System.currentTimeMillis()>endTimeG && ghost) {
				ghost=false;
				ball.setFillColor(Color.BLACK);
				ball.setColor(Color.BLACK);
			}
			if(System.currentTimeMillis()>endTimeA && accelerate){
				speed-=acceleration;
				accelerate=false;
				a1.setVisible(false);
				a2.setVisible(false);
			}
			createBonus();
			moveBall();
			interaction();
			pause(DELAY);
		}
	}
	public static void main(String[] args) {
		new Breakout().start(args);
	}
}