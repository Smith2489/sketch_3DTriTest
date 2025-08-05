package Renderer.Objects.SceneEntities.SceneObjects;
import Renderer.Objects.Parents.*;
//A class for defining single-pixel objects which exist in 3-dimensional space
public class Dot extends SceneObject{
    private int stroke = 0xFF000000;
    public Dot(){
        super();
        stroke = 0xFF000000;
    }
    public Dot(float[] newPos, int rgba){
        super(newPos);
        if((rgba >>> 24) == 0){
            if(rgba <= 0xFF)
                stroke = 0xFF000000 | (rgba << 16) | (rgba << 8) | rgba;
            else if(rgba <= 0xFF00)
                stroke = ((rgba & 0xFF00) << 16) | ((rgba & 0xFF) << 16) | ((rgba & 0xFF) << 8) | (rgba & 0xFF);
            else
                stroke = 0xFF000000 | rgba;
        }
        else
            stroke = rgba;
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
            else if(rgba <= 0xFF00)
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
    public int returnStroke(){
        return stroke;
    }

    public boolean equals(Object o){
        if(o instanceof Dot){
            Dot d = (Dot)o;
            boolean isEqual = super.equals(d);
            isEqual&=(stroke == d.stroke);
            return isEqual;
        }
        else
            return false;
    }

    public boolean equals(Dot d){
        boolean isEqual = super.equals(d);
        isEqual&=(stroke == d.stroke);
        return isEqual;
    }
    public void copy(Object o){
        if(o instanceof Dot){
            Dot d = (Dot)o;
            super.copy(d);
            stroke = d.stroke;
        }
    }
    public void copy(Dot d){
        super.copy(d);
        stroke = d.stroke;
    }

    public String toString(){
        String output = "POSITION: ("+pos[0]+", "+pos[1]+", "+pos[2]+")\n";
        output+="STROKE: "+(stroke & 0xFFFFFF)+"\nALPHA: "+(stroke >>> 24);
        return output;
    }
}