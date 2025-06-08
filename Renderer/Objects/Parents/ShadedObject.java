package Renderer.Objects.Parents;
import Actions.StencilAction;
//Superclass for scene objects that have shading properties
public class ShadedObject extends SceneObject{
    //Flag bits: 3 = hasFill, 4 = hasStroke, 5 = isGauroud
    private float brightness = 1;
    private float shininess = 4;
    private float maxFizzel = 1;
    private float fizzelThreshold = 1.1f;
    private StencilAction stencil = new StencilAction();
    public ShadedObject(){
        super();
        brightness = 1;
        shininess = 4;
        maxFizzel = 1;
        fizzelThreshold = 1.1f;
        stencil = new StencilAction();
    }
    protected ShadedObject(byte defaultFlags){
        super(defaultFlags);
        brightness = 1;
        shininess = 4;
        maxFizzel = 1;
        fizzelThreshold = 1.1f;
        stencil = new StencilAction();
    }

    public void setStencilAction(StencilAction newAction){
        stencil = newAction;
    }

    public StencilAction returnStencilActionPtr(){
        return stencil;
    }

    public void setHasFill(boolean hasFill){
        if(hasFill)
            flags|=8;
        else
            flags&=-9;
    }
    public boolean returnHasFill(){
        return (flags & 8) == 8;
    }

    public void setHasStroke(boolean hasStroke){
        if(hasStroke)
            flags|=16;
        else
            flags&=-17;
    }
    public boolean returnHasStroke(){
        return (flags & 16) == 16;
    }

    public void setGauroud(boolean isGauroud){
        if(isGauroud)
            flags|=32;
        else
            flags&=-33;
    }
    public boolean isGauroud(){
        return (flags & 32) == 32;
    }

    public void setBrightness(float newBrightness){
        Math.max(0, Math.min(newBrightness, 1));
    }

    public float returnBrightness(){
        return brightness;
    }

    public void setShininess(float newShininess){
        shininess = Math.max(0, newShininess);
    }
    public float returnShininess(){
        return shininess;
    }
    public void setFizzelParameters(float newMax, float newThreshold){
        maxFizzel = newMax;
        fizzelThreshold = newThreshold;
    }
    public float returnMaxFizzel(){
        return maxFizzel;
    }
    public float returnFizzelThreshold(){
        return fizzelThreshold;
    }

    public void copy(Object o){
        if(o instanceof ShadedObject){
            ShadedObject s = (ShadedObject)o;
            super.copy(s);
            brightness = s.brightness;
            shininess = s.shininess;
            maxFizzel = s.maxFizzel;
            fizzelThreshold = s.fizzelThreshold;
            stencil = s.stencil;
        }
    }
    public void copy(ShadedObject s){
        super.copy(s);
        brightness = s.brightness;
        shininess = s.shininess;
        maxFizzel = s.maxFizzel;
        fizzelThreshold = s.fizzelThreshold;
        stencil = s.stencil;
    }

    public boolean equals(Object o){
        if(o instanceof ShadedObject){
            ShadedObject s = (ShadedObject)o;
            boolean isEquals = super.equals(s);
            isEquals&=(Math.abs(brightness-s.brightness) <= EPSILON);
            isEquals&=(Math.abs(shininess-s.shininess) <= EPSILON);
            isEquals&=(Math.abs(maxFizzel-s.maxFizzel) <= EPSILON);
            isEquals&=(Math.abs(fizzelThreshold-s.fizzelThreshold) <= EPSILON);
            isEquals&=(stencil == s.stencil);
            return isEquals;
        }
        else
            return false;
    }
    public boolean equals(ShadedObject s){
        boolean isEquals = super.equals(s);
        isEquals&=(Math.abs(brightness-s.brightness) <= EPSILON);
        isEquals&=(Math.abs(shininess-s.shininess) <= EPSILON);
        isEquals&=(Math.abs(maxFizzel-s.maxFizzel) <= EPSILON);
        isEquals&=(Math.abs(fizzelThreshold-s.fizzelThreshold) <= EPSILON);
        isEquals&=(stencil == s.stencil);
        return isEquals;
    }
}