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
    protected static int width() throws NullPointerException{
        return pApplet.width;
    }
    public static void setExpectedFrameRate(int expected){
        expectedFrameRate = Math.max(1, expectedFrameRate);
    }
    public static void setFrameRateNorm() throws NullPointerException{
        speed = expectedFrameRate/pApplet.frameRate;
    }
    public static float frameRateNorm(){
        return speed;
    }
    protected static int height() throws NullPointerException{
        return pApplet.height;
    }
    protected static boolean keyPressed() throws NullPointerException{
        return pApplet.keyPressed;
    }
    protected static boolean keyPressed(char button) throws NullPointerException{
        return pApplet.keyPressed && pApplet.key == button;
    }
    protected static boolean key(char button) throws NullPointerException{
        return pApplet.key == button;
    }
    protected static boolean codedKey() throws NullPointerException{
        return pApplet.key == PApplet.CODED;
    }
    protected static boolean leftKey() throws NullPointerException{
        return pApplet.keyCode == PApplet.LEFT;
    }
    protected static boolean rightKey() throws NullPointerException{
        return pApplet.keyCode == PApplet.RIGHT;
    }
    protected static boolean upKey() throws NullPointerException{
        return pApplet.keyCode == PApplet.UP;
    }
    protected static boolean downKey() throws NullPointerException{
        return pApplet.keyCode == PApplet.DOWN;
    }
    protected static boolean altKey() throws NullPointerException{
        return pApplet.keyCode == PApplet.ALT;
    }
    protected static boolean ctrlKey() throws NullPointerException{
        return pApplet.keyCode == PApplet.CONTROL;
    }
    protected static boolean shiftKey() throws NullPointerException{
        return pApplet.keyCode == PApplet.SHIFT;
    }
    protected static boolean escKey() throws NullPointerException{
        return pApplet.key == PApplet.ESC;
    }
    protected static boolean backSpaceKey() throws NullPointerException{
        return pApplet.key == PApplet.BACKSPACE;
    }
    protected static boolean enterKey() throws NullPointerException{
        return pApplet.key == PApplet.ENTER;
    }
    protected static boolean returnKey() throws NullPointerException{
        return pApplet.key == PApplet.RETURN;
    }
    protected static boolean deleteKey() throws NullPointerException{
        return pApplet.key == PApplet.DELETE;
    }


    protected static char key() throws NullPointerException{
        return pApplet.key;
    }
    protected static int keyCode() throws NullPointerException{
        return pApplet.keyCode;
    }

    protected static int mouseButton() throws NullPointerException{
        return pApplet.mouseButton;
    }

    protected static boolean leftClick() throws NullPointerException{
        return pApplet.mousePressed && pApplet.mouseButton == PApplet.LEFT;
    }
    protected static boolean rightClick() throws NullPointerException{
        return pApplet.mousePressed && pApplet.mouseButton == PApplet.RIGHT;
    }

    protected static boolean leftButton() throws NullPointerException{
        return pApplet.mouseButton == PApplet.LEFT;
    }
    protected static boolean rightButton() throws NullPointerException{
        return pApplet.mouseButton == PApplet.RIGHT;
    }
    protected static boolean mousePressed() throws NullPointerException{
        return pApplet.mousePressed;
    }
    protected static boolean mouseMoved() throws NullPointerException{
        return pApplet.pmouseX != pApplet.mouseX || pApplet.pmouseY != pApplet.mouseY; 
    }
    protected static boolean mouseNotMoved() throws NullPointerException{
        return pApplet.pmouseX == pApplet.mouseX && pApplet.pmouseY == pApplet.mouseY;
    }
    protected static boolean mouseDragged() throws NullPointerException{
        return pApplet.mousePressed && (pApplet.pmouseX != pApplet.mouseX || pApplet.pmouseY != pApplet.mouseY);
    }
    protected static boolean mouseNotDragged() throws NullPointerException{
        return !pApplet.mousePressed || (pApplet.pmouseX == pApplet.mouseX && pApplet.pmouseY == pApplet.mouseY);
    }
    protected static boolean mouseLeft() throws NullPointerException{
        return pApplet.mouseX < pApplet.pmouseX;
    }
    protected static boolean mouseRight() throws NullPointerException{
        return pApplet.mouseX > pApplet.pmouseX;
    }
    protected static boolean mouseNotMovedX() throws NullPointerException{
        return pApplet.mouseX == pApplet.pmouseX;
    }
    protected static boolean mouseUp() throws NullPointerException{
        return pApplet.mouseY < pApplet.pmouseY;
    }
    protected static boolean mouseDown() throws NullPointerException{
        return pApplet.mouseY > pApplet.pmouseY;
    }
    protected static boolean mouseNotMovedY() throws NullPointerException{
        return pApplet.mouseY == pApplet.pmouseY;
    }
    protected static int mouseChangeX() throws NullPointerException{
        return pApplet.mouseX-pApplet.pmouseX;
    }
    protected static int mouseChangeY() throws NullPointerException{
        return pApplet.mouseY-pApplet.pmouseY;
    }
    protected static int mouseX() throws NullPointerException{
        return pApplet.mouseX;
    }
    protected static int mouseY() throws NullPointerException{
        return pApplet.mouseY;
    }
    protected static int prevMouseX() throws NullPointerException{
        return pApplet.pmouseX;
    }
    protected static int prevMouseY() throws NullPointerException{
        return pApplet.pmouseY;
    }


    protected static int millis() throws NullPointerException{
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
        if(newApplet != null)
            pApplet = newApplet;
        else{
            System.out.println("ERROR: NULL POINTER");
            System.exit(-1);
        }
    }
}