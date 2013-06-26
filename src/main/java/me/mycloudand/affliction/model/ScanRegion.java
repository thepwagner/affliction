package me.mycloudand.affliction.model;

import javax.inject.Singleton;
import java.awt.*;

/**
 * Where the game is on the screen.
 */
@Singleton
public class ScanRegion {
    private int x;
    private int y;
    private int w;
    private int h;

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Rectangle asRectangle() {
        return new Rectangle(x, y, w, h);
    }
}
