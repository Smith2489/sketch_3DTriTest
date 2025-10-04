package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateLongModel extends ModelAction{
  float[] rotatePoint = {0, 0, 0};
  float[] angle = {0, 2, 0};
  boolean xPressed = false;
    public void init(){

    }
    public void perform(){
      restoreRotation();
      restoreAndSavePosition();
      matrixTransform();
      float[] modelForward = getForward();
      if(keyPressed()){
        switch(key()){
          case 'x':
            if(!xPressed){
              rotatePoint = getPos();
              rotatePoint[0]-=modelForward[0];
              rotatePoint[2]-=2*modelForward[2];
            }
            moveAroundPoint(rotatePoint, angle);
            addToRotation(2, (byte)1);
            xPressed = true;
            break;
          case 'w':
            addToPosition(0.5f*speed, modelForward);
            break;
          case 's':
            addToPosition(-0.5f*speed, modelForward);
            break;
          case 'd':
            addToRotation(0.5f*speed, (byte)1);
            break;
          case 'a':
            addToRotation(-0.5f*speed, (byte)1);
            break;
          case 'g':
            addToRotation(0.5f*speed, (byte)0);
            break;
          case 'h':
            addToRotation(-0.5f*speed, (byte)0);
            break;
          case '=':
            initPositionShake(0.6f, 50);
            initRotationShake(15, 50);
            break;
        }
      }
      else
        xPressed = false;

      savePosition();
      saveRotation();

      shakePosition();
      shakeRotation();
      float[] tempRot = getRotDegrees();
      if(tempRot[1] < 0)
        rotatePlus360((byte)1);
      else if(tempRot[1] >= 360)
        rotateMinus360((byte)1);
      if(tempRot[0] < 0)
        rotatePlus360((byte)0);
      else if(tempRot[0] >= 360)
        rotateMinus360((byte)0);
    }
  }