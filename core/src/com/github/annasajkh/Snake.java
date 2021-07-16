package com.github.annasajkh;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Snake
{
    double score = 0;
    int attempt = 0;
    boolean die = false;
    Line[] visions = new Line[8];
    Vector2 position;
    Vector2 dir;
    List<Tail> tails;
    Color color;
    NeuralNetwork brain;
    Food food;
    float colorHue;
    boolean forward;

    public Snake()
    {
        this(new NeuralNetwork(Game.inputSize, 20, 4, 2));
    }

    public Snake(NeuralNetwork brain)
    {
        this.position = new Vector2(Game.collums / 2 * Game.scale, Game.rows / 2 * Game.scale);
        colorHue = 0.2f;
        forward = true;
        this.brain = brain;
        tails = new ArrayList<>();
        tails.add(new Tail(position, colorHue, forward));
        color = Game.hsvToRgba(colorHue, 1, 1, 1);
        dir = new Vector2(Game.scale, 0);
        colorHue += 0.01f;
        food = new Food(this);

    }
    
    public void setBorderPos(Line line,float Ax, float Ay, float Bx, float By)
    {

    	line.pointA.x = Ax;
    	line.pointA.y =	Ay;
    	line.pointB.x = Bx;
    	line.pointB.y = By;
    }

    public void update()
    {

        food.update();
        for (int i = tails.size() - 1; i > 0; i--)
        {
            tails.get(i).position.x = tails.get(i - 1)
                                           .getX();
            tails.get(i).position.y = tails.get(i - 1)
                                           .getY();
        }

        tails.get(0).position = position.cpy();
        position.add(dir);

        for (Tail tail : tails)
        { 
        	setBorderPos(tail.borders[0],tail.position.x,tail.position.y,tail.position.x,tail.position.y + Game.scale);
        	setBorderPos(tail.borders[1],tail.position.x, tail.position.y,tail.position.x +Game.scale, tail.position.y);
        	setBorderPos(tail.borders[2],tail.position.x, tail.position.y + Game.scale
        								,tail.position.x + Game.scale, tail.position.y + Game.scale);
        	setBorderPos(tail.borders[3],tail.position.x + Game.scale, tail.position.y
        								,tail.position.x +Game.scale, tail.position.y + Game.scale);
        }


        double[] input = new double[Game.inputSize];
        List<Float> inputTemp = new ArrayList<>(Game.inputSize);

        for (int i = 0; i < visions.length; i++)
        {
            visions[i] = new Line(position.cpy().add(Game.scale * 0.5f, Game.scale * 0.5f),
            						position.cpy().add(Game.scale * 0.5f, Game.scale * 0.5f)
            							.add(new Vector2(1,0).rotateDeg(i * 45).scl(2000)));

            for (int j = 0; j < 4; j++)
            {
                Vector2 collisionPoint = Game.borders[j].collide(visions[i]);
                if (collisionPoint != null)
                {
                    visions[i].pointB = collisionPoint;
                }
            }
            if(visions[i].pointB != null)
            {
                inputTemp.add(visions[i].length2() / Game.maxVisionLength2);
            }
            else
            {
                inputTemp.add(1f);
            }


            for (int j = 0; j < 4; j++)
            {
                Vector2 collisionPoint = food.borders[j].collide(visions[i]);
                if (collisionPoint != null)
                {
                    visions[i].pointB = collisionPoint;
                }
            }

            if(visions[i].pointB != null)
            {
                inputTemp.add(visions[i].length2() / Game.maxVisionLength2);
            }
            else
            {
                inputTemp.add(0f);
            }

            for (int j = 0; j < tails.size(); j++)
            {
                for (int k = 0; k < 4; k++)
                {
                    Vector2 collisionPoint = tails.get(j).borders[k].collide(visions[i]);
                    if (collisionPoint != null)
                    {
                        visions[i].pointB = collisionPoint;
                    }
                }
            }

            if(visions[i].pointB != null)
            {
                inputTemp.add(visions[i].length2() / Game.maxVisionLength2);
            }
            else
            {
                inputTemp.add(1f);
            }
        }


        for (int j = 0; j < tails.size(); j++)
        {
            if (tails.get(j).position.equals(position))
            {
                die = true;
                return;
            }
        }

        if (position.x >= Gdx.graphics.getWidth())
        {
            die = true;
            return;
        }
        else if (position.x < 0)
        {
            die = true;
            return;
        }
        else if (position.y >= Gdx.graphics.getHeight())
        {
            die = true;
            return;
        }
        else if (position.y < 0)
        {
            die = true;
            return;
        }

        if (attempt >= Game.rows * Game.collums)
        {
            die = true;
            return;
        }

        for (int i = 0; i < input.length; i++)
        {
            input[i] = inputTemp.get(i);
        }
        inputTemp = null;

        double[] result = brain.process(input);

        if (result[0] > 0.5)
        {
            right();
        }
        else if (result[1] > 0.5)
        {
            left();
        }
        else if (result[2] > 0.5)
        {
            up();
        }
        else if (result[3] > 0.5)
        {
            down();
        }
        attempt++;
    }

    public void right()
    {
        if (dir.x != 0)
        {
            return;
        }
        dir.x = Game.scale;
        dir.y = 0;
    }

    public void left()
    {
        if (dir.x != 0)
        {
            return;
        }
        dir.x = -Game.scale;
        dir.y = 0;
    }

    public void up()
    {
        if (dir.y != 0)
        {
            return;
        }
        dir.y = Game.scale;
        dir.x = 0;
    }

    public void down()
    {
        if (dir.y != 0)
        {
            return;
        }
        dir.y = -Game.scale;
        dir.x = 0;
    }

    public void render(ShapeRenderer shapeRenderer)
    {
        shapeRenderer.setColor(color);
        for (Tail tail : tails)
        {
            shapeRenderer.rect(tail.position.x, tail.position.y, Game.scale, Game.scale);
        }
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(food.position.x, food.position.y, Game.scale, Game.scale);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(position.x, position.y, Game.scale, Game.scale);

        for (int i = 0; i < visions.length; i++)
        {
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rectLine(visions[i].pointA, visions[i].pointB,5);
        }
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