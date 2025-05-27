package Renderer.Objects;
import java.util.*;
import Wrapper.*;
import Actions.*;
import Maths.LinearAlgebra.*;
import Renderer.ScreenDraw.MVP;
//A class for defining single-pixel objects which exist in 3-dimensional space
public class Dot {
    private float[] pos = {0, 0, 0};
    private float[] rot = {0, 0, 0};
    private float[] scale = {1, 1, 1};
    private float[][] shear = {{0, 0}, {0, 0}, {0, 0}};
    private Matrix dotModel = new Matrix();
    private int stroke = 0xFF000000;
    private byte flags = 0; //0 = depth disable, 1 = is attached to camera, 2 = always perform actions
    private LinkedList<Action> actionList = new LinkedList<Action>();
    private FloatWrapper uniTint = new FloatWrapper();
    private Action tempAction;
    public Dot(){
        dotModel = new Matrix();
        pos[0] = 0;
        pos[1] = 0;
        pos[2] = 0;
        rot[0] = 0;
        rot[1] = 0;
        rot[2] = 0;
        scale[0] = 0;
        scale[1] = 0;
        scale[2] = 0;
        shear[0][0] = 0;
        shear[0][1] = 0;
        shear[1][0] = 0;
        shear[1][1] = 0;
        shear[2][0] = 0;
        shear[2][1] = 0;
        stroke = 0xFF000000;
        uniTint.val = 1;
        actionList = new LinkedList<Action>();
    }
    public Dot(float[] newPos, int rgba){
        dotModel = new Matrix();
        rot[0] = 0;
        rot[1] = 0;
        rot[2] = 0;
        scale[0] = 0;
        scale[1] = 0;
        scale[2] = 0;
        shear[0][0] = 0;
        shear[0][1] = 0;
        shear[1][0] = 0;
        shear[1][1] = 0;
        shear[2][0] = 0;
        shear[2][1] = 0;
        if(newPos.length >= 3){
            pos[0] = newPos[0];
            pos[1] = newPos[1];
            pos[2] = newPos[2];
        }
        else{
            System.out.println("ERROR: TOO FEW DIMENSIONS");
            System.exit(1);
        }
        if((rgba >>> 24) == 0){
            if(rgba <= 0xFF)
                stroke = 0xFF000000 | (rgba << 16) | (rgba << 8) | rgba;
            else if(rgba <= 0xFFFF)
                stroke = ((rgba & 0xFF00) << 16) | ((rgba & 0xFF) << 16) | ((rgba & 0xFF) << 8) | (rgba & 0xFF);
            else
                stroke = 0xFF000000 | rgba;
        }
        else
            stroke = rgba;
        uniTint.val = 1;
        actionList = new LinkedList<Action>();
    }
    public void addAction(ModelAction newAction){
        if(newAction != null){
            newAction.setPos(pos);
            newAction.setRot(rot);
            newAction.setScale(scale);
            newAction.setShear(shear);
            newAction.setMatrix(dotModel);
            newAction.setModelTint(uniTint);
            actionList.add(newAction);
        }
        else
            System.out.println("ERROR: ACTION CANNOT BE NULL");
    }
    public Action removeFirstAction(){
        return actionList.removeFirst();
    }
    public Action removeLastAction(){
        return actionList.removeLast();
    }
    public Action removeAction(int i){
        return actionList.remove(i);
    }
    public void clearActionList(){
        actionList.clear();
    }
    public boolean hasActions(){
        return !actionList.isEmpty();
    }
    public int numOfActions(){
        return actionList.size();
    }
    public void executeActions(){
        int length = actionList.size();
        for(int i = 0; i < length; i++){
            tempAction = actionList.removeFirst();
            actionList.add(tempAction);
            tempAction.perform();
        }
    }
    public void alwaysPerform(boolean perform){
        if(perform)
            flags|=4;
        else
            flags&=-5;
    }
    public boolean alwaysPerform(){
        return ((flags & 4) == 4);
    }
    public void setModelTint(float newTint){
        uniTint.val = Math.max(0, Math.min(newTint, 1));
    }
    public float returnModelTint(){
        return uniTint.val;
    }
    public void setPosition(float[] newPos){
        if(newPos.length >= 3){
            pos[0] = newPos[0];
            pos[1] = newPos[1];
            pos[2] = newPos[2];
        }
        else{
            System.out.println("ERROR: TOO FEW DIMENSIONS");
            System.exit(1);
        }
    }
    public void setPosition(float x, float y, float z){
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
    }
    public void setRotation(float alpha, float beta, float gamma){
        rot[0] = alpha;
        rot[1] = beta;
        rot[2] = gamma;
    }
    public void setRotation(float[] angles){
        rot[0] = angles[0];
        rot[1] = angles[1];
        rot[2] = angles[2];
    }

    public void setScale(float scX, float scY, float scZ){
        scale[0] = scX;
        scale[1] = scY;
        scale[2] = scZ;
    }
    public void setScale(float[] newScale){
        scale[0] = newScale[0];
        scale[1] = newScale[1];
        scale[2] = newScale[2];
    }
    public void setShear(float[][] shr){
        shear[0][0] = shr[0][0];
        shear[0][1] = shr[0][1];
        shear[1][0] = shr[1][0];
        shear[1][1] = shr[1][1];
        shear[2][0] = shr[2][0];
        shear[2][1] = shr[2][1];
    }
    public void setShear(float[] shearX, float[] shearY, float[] shearZ){
        shear[0][0] = shearX[0];
        shear[0][1] = shearX[1];
        shear[1][0] = shearY[0];
        shear[1][1] = shearY[1];
        shear[2][0] = shearZ[0];
        shear[2][1] = shearZ[1];
    }

    public void setShearX(float[] shearX){
        shear[0][0] = shearX[0];
        shear[0][1] = shearX[1];
    }
    public void setShearX(float shearX1, float shearX2){
        shear[0][0] = shearX1;
        shear[0][1] = shearX2;
    }
    public void setShearY(float[] shearY){
        shear[1][0] = shearY[0];
        shear[1][1] = shearY[1];
    }
    public void setShearY(float shearY1, float shearY2){
        shear[1][0] = shearY1;
        shear[1][1] = shearY2;
    }
    public void setShearZ(float[] shearZ){
        shear[2][0] = shearZ[0];
        shear[2][1] = shearZ[1];
    }
    public void setShearZ(float shearZ1, float shearZ2){
        shear[2][0] = shearZ1;
        shear[2][1] = shearZ2;
    }

    public void setModelMatrix(Matrix newModel){
        if(Objects.nonNull(newModel) && newModel.returnWidth() == 4 && newModel.returnHeight() == 4)
            dotModel.copy(newModel);
        else
            System.out.println("ERROR: MATRIX MUST BE A VALID 4x4 MATRIX");
    }
    public void setModelMatrix(){
        dotModel.copy(MVP.inverseViewMatrix(pos, rot, scale, shear));
    }

    public void stroke(short r, short g, short b){
        stroke = 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
    public void stroke(short r, short g, short b, short a){
        stroke = ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
    public void stroke(int rgb, short a){
        if((rgb & 0xFFFFFF) <= 0xFF)
            stroke = ((a & 255) << 24) | (rgb << 16) | (rgb << 8) | rgb;
        else
            stroke = ((a & 255) << 24) | (rgb & 0xFFFFFF);
    }
    public void stroke(int rgba){
        if((rgba >>> 24) == 0){
            if(rgba <= 0xFF)
                stroke = 0xFF000000 | (rgba << 16) | (rgba << 8) | rgba;
            else if(rgba <= 0xFFFF)
                stroke = ((rgba & 0xFF00) << 16) | ((rgba & 0xFF) << 16) | ((rgba & 0xFF) << 8) | (rgba & 0xFF);
            else
                stroke = 0xFF000000 | rgba;
        }
        else
            stroke = rgba;
    }

    public void setAlpha(short alpha){
        stroke&=0xFFFFFF;
        alpha&=0xFF;
        stroke = (alpha << 24)|stroke;
    }
    public void setDepthWrite(boolean depthDisable){
        if(depthDisable)
            flags|=1;
        else
            flags&=-2;
    }
    public void setAttachedToCamera(boolean attachedToCamera){
        if(attachedToCamera)
            flags|=2;
        else
            flags&=-3;
    }

    public float[] returnPosition(){
        return pos;
    }
      
    public int returnStroke(){
        return stroke;
    }
    public boolean returnDepthDisable(){
        return (flags & 1) == 1;
    }
    public boolean returnAttachedToCamera(){
        return (flags & 2) == 2;
    }

    public boolean equals(Object o){
        if(o instanceof Dot){
            Dot d = (Dot)o;
            boolean isEqual = true;
            isEqual&=(Math.abs(uniTint.val - d.uniTint.val) <= 0.0001);
            for(byte i = 0; i < 3; i++){
                isEqual&=(Math.abs(pos[i] - d.pos[i]) <= 0.0001);
                isEqual&=(Math.abs(rot[i] - d.rot[i]) <= 0.0001);
                isEqual&=(Math.abs(scale[i] - d.scale[i]) <= 0.0001);
                isEqual&=(Math.abs(shear[i][0] - d.shear[i][0]) <= 0.0001);
                isEqual&=(Math.abs(shear[i][1] - d.shear[i][1]) <= 0.0001);
            }
            isEqual&=(stroke == d.stroke);
            isEqual&=(flags == d.flags);
            isEqual&=dotModel.equals(d.dotModel);
            return isEqual;
        }
        else
            return false;
    }

    public boolean equals(Dot d){
        boolean isEqual = true;
        isEqual&=(Math.abs(uniTint.val - d.uniTint.val) <= 0.0001);
        for(byte i = 0; i < 3; i++){
            isEqual&=(Math.abs(pos[i] - d.pos[i]) <= 0.0001);
            isEqual&=(Math.abs(rot[i] - d.rot[i]) <= 0.0001);
            isEqual&=(Math.abs(scale[i] - d.scale[i]) <= 0.0001);
            isEqual&=(Math.abs(shear[i][0] - d.shear[i][0]) <= 0.0001);
            isEqual&=(Math.abs(shear[i][1] - d.shear[i][1]) <= 0.0001);
        }
        isEqual&=(stroke == d.stroke);
        isEqual&=(flags == d.flags);
        isEqual&=dotModel.equals(d.dotModel);
        return isEqual;
    }
    public void copy(Object o){
        if(o instanceof Dot){
            Dot d = (Dot)o;
            uniTint.val = d.uniTint.val;
            for(byte i = 0; i < 3; i++){
                pos[i] = d.pos[i];
                rot[i] = d.rot[i];
                scale[i] = d.scale[i];
                shear[i][0] = d.shear[i][0];
                shear[i][1] = d.shear[i][1];
            }
            stroke = d.stroke;
            flags = d.flags;
            dotModel.copy(d.dotModel);
        }
    }
    public void copy(Dot d){
        uniTint.val = d.uniTint.val;
        for(byte i = 0; i < 3; i++){
            pos[i] = d.pos[i];
            rot[i] = d.rot[i];
            scale[i] = d.scale[i];
            shear[i][0] = d.shear[i][0];
            shear[i][1] = d.shear[i][1];
        }
        stroke = d.stroke;
        flags = d.flags;
        dotModel.copy(d.dotModel);
    }

    public String toString(){
        String output = "POSITION: ("+pos[0]+", "+pos[1]+", "+pos[2]+")\n";
        output+="STROKE: "+(stroke & 0xFFFFFF)+"\nALPHA: "+(stroke >>> 24);
        return output;
    }
}
