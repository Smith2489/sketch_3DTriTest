package Actions.ObjectActions;
import processing.core.PApplet;

//A wrapper class to allow Java projects or files that have not imported Processing's input handling to still use it
//All you have to do is to create a class which extends this one
//Most methods simply pass off handling to the PApplet class's own equivilants
public class PInputHandler{
    protected static final int CODED = PApplet.CODED;
    protected static final int LEFT = PApplet.LEFT;
    protected static final int RIGHT = PApplet.RIGHT;
    protected static final int UP = PApplet.UP;
    protected static final int DOWN = PApplet.DOWN;
    protected static final int ALT = PApplet.ALT;
    protected static final int CONTROL = PApplet.CONTROL;
    protected static final int SHIFT = PApplet.SHIFT;
    protected static final int CENTER = PApplet.CENTER;

    protected static final char ESC = PApplet.ESC;
    protected static final char BACKSPACE = PApplet.BACKSPACE;
    protected static final char ENTER = PApplet.ENTER;
    protected static final char RETURN = PApplet.RETURN;
    protected static final char DELETE = PApplet.DELETE;

    private static float speed = 1;
    private static float expectedFrameRate = 60;

    private static PApplet pApplet = null;
    protected static int width(){
        return pApplet.width;
    }
    public static void setExpectedFrameRate(int expected){
        expectedFrameRate = Math.max(1, expectedFrameRate);
    }
    public static void setSpeed(){
        speed = expectedFrameRate/pApplet.frameRate;
    }
    public static float speed(){
        return speed;
    }
    protected static int height(){
        return pApplet.height;
    }
    protected static boolean keyPressed(){
        return pApplet.keyPressed;
    }
    protected static boolean keyPressed(char button){
        return pApplet.keyPressed && pApplet.key == button;
    }
    protected static boolean key(char button){
        return pApplet.key == button;
    }
    protected static boolean codedKey(){
        return pApplet.key == PApplet.CODED;
    }
    protected static boolean leftKey(){
        return pApplet.keyCode == PApplet.LEFT;
    }
    protected static boolean rightKey(){
        return pApplet.keyCode == PApplet.RIGHT;
    }
    protected static boolean upKey(){
        return pApplet.keyCode == PApplet.UP;
    }
    protected static boolean downKey(){
        return pApplet.keyCode == PApplet.DOWN;
    }
    protected static boolean altKey(){
        return pApplet.keyCode == PApplet.ALT;
    }
    protected static boolean ctrlKey(){
        return pApplet.keyCode == PApplet.CONTROL;
    }
    protected static boolean shiftKey(){
        return pApplet.keyCode == PApplet.SHIFT;
    }
    protected static boolean escKey(){
        return pApplet.key == PApplet.ESC;
    }
    protected static boolean backSpaceKey(){
        return pApplet.key == PApplet.BACKSPACE;
    }
    protected static boolean enterKey(){
        return pApplet.key == PApplet.ENTER;
    }
    protected static boolean returnKey(){
        return pApplet.key == PApplet.RETURN;
    }
    protected static boolean deleteKey(){
        return pApplet.key == PApplet.DELETE;
    }


    protected static char key(){
        return pApplet.key;
    }
    protected static int keyCode(){
        return pApplet.keyCode;
    }

    protected static int mouseButton(){
        return pApplet.mouseButton;
    }

    protected static boolean leftClick(){
        return pApplet.mousePressed && pApplet.mouseButton == PApplet.LEFT;
    }
    protected static boolean rightClick(){
        return pApplet.mousePressed && pApplet.mouseButton == PApplet.RIGHT;
    }

    protected static boolean leftButton(){
        return pApplet.mouseButton == PApplet.LEFT;
    }
    protected static boolean rightButton(){
        return pApplet.mouseButton == PApplet.RIGHT;
    }
    protected static boolean mousePressed(){
        return pApplet.mousePressed;
    }
    protected static boolean mouseMoved(){
        return pApplet.pmouseX != pApplet.mouseX || pApplet.pmouseY != pApplet.mouseY; 
    }
    protected static boolean mouseNotMoved(){
        return pApplet.pmouseX == pApplet.mouseX && pApplet.pmouseY == pApplet.mouseY;
    }
    protected static boolean mouseDragged(){
        return pApplet.mousePressed && (pApplet.pmouseX != pApplet.mouseX || pApplet.pmouseY != pApplet.mouseY);
    }
    protected static boolean mouseNotDragged(){
        return !pApplet.mousePressed || (pApplet.pmouseX == pApplet.mouseX && pApplet.pmouseY == pApplet.mouseY);
    }
    protected static boolean mouseLeft(){
        return pApplet.mouseX < pApplet.pmouseX;
    }
    protected static boolean mouseRight(){
        return pApplet.mouseX > pApplet.pmouseX;
    }
    protected static boolean mouseNotMovedX(){
        return pApplet.mouseX == pApplet.pmouseX;
    }
    protected static boolean mouseUp(){
        return pApplet.mouseY < pApplet.pmouseY;
    }
    protected static boolean mouseDown(){
        return pApplet.mouseY > pApplet.pmouseY;
    }
    protected static boolean mouseNotMovedY(){
        return pApplet.mouseY == pApplet.pmouseY;
    }
    protected static int mouseChangeX(){
        return pApplet.mouseX-pApplet.pmouseX;
    }
    protected static int mouseChangeY(){
        return pApplet.mouseY-pApplet.pmouseY;
    }
    protected static int mouseX(){
        return pApplet.mouseX;
    }
    protected static int mouseY(){
        return pApplet.mouseY;
    }
    protected static int prevMouseX(){
        return pApplet.pmouseX;
    }
    protected static int prevMouseY(){
        return pApplet.pmouseY;
    }


    protected static int millis(){
        return pApplet.millis();
    }
    protected static int second(){
        return PApplet.second();
    }
    protected static int minute(){
        return PApplet.minute();
    }
    protected static int hour(){
        return PApplet.hour();
    }
    protected static int day(){
        return PApplet.day();
    }
    protected static int month(){
        return PApplet.month();
    }
    protected static int year(){
        return PApplet.year();
    }
    //Required in order for the whole class to work, but only needs to be called once
    public static void setPApplet(PApplet newApplet){
        pApplet = newApplet;
    }
}