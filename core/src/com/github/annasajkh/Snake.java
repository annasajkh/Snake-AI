package com.github.annasajkh;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Snake implements Comparable<Snake>
{
    double score = 0;
    int attempt = 0;
    boolean dead = false;
    Line[] visions = new Line[Game.visionLength];
    Vector2 position;
    Vector2 dir;
    List<Tail> tails;
    Color color = Color.BLUE;
    NeuralNetwork brain;
    Food food;

    public Snake()
    {
        this(new NeuralNetwork(Game.inputSize, 8, 4, 1));
    }

    public Snake(NeuralNetwork brain)
    {
        this.position = new Vector2(Game.collums / 2 * Game.scale, Game.rows / 2 * Game.scale);
        this.brain = brain;
        tails = new ArrayList<>();
        tails.add(new Tail(position));
        dir = new Vector2(Game.scale, 0);
        food = new Food(this);

    }

    public void setBorderPos(Line line, float Ax, float Ay, float Bx, float By)
    {

        line.pointA.x = Ax;
        line.pointA.y = Ay;
        line.pointB.x = Bx;
        line.pointB.y = By;
    }

    public void checkIfDead()
    {
        for(int j = 0; j < tails.size(); j++)
        {
            if(tails.get(j).position.equals(position))
            {
                dead = true;
                return;
            }
        }

        if(position.x >= Gdx.graphics.getWidth())
        {
            dead = true;
            return;
        }
        else if(position.x < 0)
        {
            dead = true;
            return;
        }
        else if(position.y >= Gdx.graphics.getHeight())
        {
            dead = true;
            return;
        }
        else if(position.y < 0)
        {
            dead = true;
            return;
        }

        if(attempt >= Game.maxAttempts)
        {
            dead = true;
            score -= 1000;
            return;
        }
    }

    public void move(List<Float> input)
    {
        float[] result = brain.process(input.toArray(new Float[0]));

        if(result[0] > 0.5f)
        {
            right();
        }
        else if(result[1] > 0.5f)
        {
            left();
        }
        else if(result[2] > 0.5f)
        {
            up();
        }
        else if(result[3] > 0.5f)
        {
            down();
        }
    }

    public List<Float> getInput()
    {
        List<Float> input = new ArrayList<>(Game.inputSize);

        for(int i = 0; i < visions.length; i++)
        {
            visions[i] = new Line(position.cpy().add(Game.scale * 0.5f, Game.scale * 0.5f),
                position.cpy().add(Game.scale * 0.5f, Game.scale * 0.5f).add(new Vector2(1, 0).rotateDeg(i * (360 / visions.length)).scl(2000)));

            for(int j = 0; j < tails.size(); j++)
            {
                for(int k = 0; k < 4; k++)
                {
                    Vector2 collisionPoint = tails.get(j).borders[k].collide(visions[i]);
                    if(collisionPoint != null)
                    {
                        visions[i].pointB = collisionPoint;
                        visions[i].collisionType = 0;
                    }
                }
            }

            for(int j = 0; j < 4; j++)
            {
                Vector2 collisionPoint = food.borders[j].collide(visions[i]);
                if(collisionPoint != null)
                {
                    visions[i].pointB = collisionPoint;
                    visions[i].collisionType = 1;
                }
            }

            for(int j = 0; j < 4; j++)
            {
                Vector2 collisionPoint = Game.borders[j].collide(visions[i]);
                if(collisionPoint != null)
                {
                    visions[i].pointB = collisionPoint;
                    visions[i].collisionType = 0;
                }
            }

            input.add(visions[i].length() / Game.maxLength);
            input.add(visions[i].collisionType);
        }

        input.add(dir.x > 0 ? 1f : 0f);
        input.add(dir.x > 0 ? 0f : 1f);
        input.add(dir.y > 0 ? 1f : 0f);
        input.add(dir.y > 0 ? 0f : 1f);

        return input;
    }

    public void update()
    {
        Vector2 tailDir = new Vector2();

        for(int i = tails.size() - 1; i > 0; i--)
        {
            if(i == tails.size() - 1)
            {
                tailDir.x = (tails.get(i).position.x - tails.get(i - 1).getX() + Game.scale) / (Game.scale * 2);
                tailDir.y = (tails.get(i).position.y - tails.get(i - 1).getY() + Game.scale) / (Game.scale * 2);
            }
            tails.get(i).position.x = tails.get(i - 1).getX();
            tails.get(i).position.y = tails.get(i - 1).getY();
        }

        tails.get(0).position = position.cpy();
        position.add(dir);
        
        

        for(Tail tail : tails)
        {
            setBorderPos(tail.borders[0], tail.position.x, tail.position.y, tail.position.x, tail.position.y + Game.scale);
            setBorderPos(tail.borders[1], tail.position.x, tail.position.y, tail.position.x + Game.scale, tail.position.y);
            setBorderPos(tail.borders[2], tail.position.x, tail.position.y + Game.scale, tail.position.x + Game.scale, tail.position.y + Game.scale);
            setBorderPos(tail.borders[3], tail.position.x + Game.scale, tail.position.y, tail.position.x + Game.scale, tail.position.y + Game.scale);
        }

        checkIfDead();

        if(dead)
        {
            score -= Math.pow((1 - attempt / Game.maxAttempts) * 10, 5);
            return;
        }

        List<Float> input = getInput();
        input.add(tailDir.x == 0.5f ? 1f : 0f);
        input.add(tailDir.x == 0.5f ? 0f : 1f);
        input.add(tailDir.y == 0.5f ? 1f : 0f);
        input.add(tailDir.y == 0.5f ? 0f : 1f);

        move(input);

        food.update();

        attempt++;
    }

    public void right()
    {
        if(dir.x != 0)
        {
            return;
        }
        dir.x = Game.scale;
        dir.y = 0;
    }

    public void left()
    {
        if(dir.x != 0)
        {
            return;
        }
        dir.x = -Game.scale;
        dir.y = 0;
    }

    public void up()
    {
        if(dir.y != 0)
        {
            return;
        }
        dir.y = Game.scale;
        dir.x = 0;
    }

    public void down()
    {
        if(dir.y != 0)
        {
            return;
        }
        dir.y = -Game.scale;
        dir.x = 0;
    }

    public void render(ShapeRenderer shapeRenderer)
    {
        for(int i = 0; i < visions.length; i++)
        {
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rectLine(visions[i].pointA, visions[i].pointB, 3);
        }

        shapeRenderer.setColor(color);
        for(Tail tail : tails)
        {
            shapeRenderer.rect(tail.position.x, tail.position.y, Game.scale, Game.scale);
        }

        food.draw(shapeRenderer);

        shapeRenderer.setColor(color);
        shapeRenderer.rect(position.x, position.y, Game.scale, Game.scale);

    }

    public float getX()
    {
        return position.x;
    }

    public float getY()
    {
        return position.y;
    }

    @Override
    public int compareTo(Snake other)
    {
        return Double.compare(score, other.score);

    }
}
