package com.github.annasajkh;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Tail
{

    Vector2 position;
    Color color;
    Line[] borders = new Line[4];

    public Tail(Vector2 position)
    {
        this.position = position;
        borders[0] = new Line(new Vector2(position.x, position.y), new Vector2(position.x, Game.scale));
        borders[1] = new Line(new Vector2(position.x, position.y), new Vector2(Game.scale, position.y));
        borders[2] = new Line(new Vector2(position.x, Game.scale), new Vector2(Game.scale, Game.scale));
        borders[3] = new Line(new Vector2(Game.scale, position.y), new Vector2(Game.scale, Game.scale));
        
        color = Color.GREEN;
    }

    public float getX()
    {
        return position.x;
    }

    public float getY()
    {
        return position.y;
    }
}
