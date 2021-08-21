package com.github.annasajkh;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Food
{
    public Vector2 position;
    Snake snake;
    Line[] borders = new Line[4];

    public Food(Snake snake)
    {
        this.snake = snake;
        
        position = new Vector2(Game.random.nextInt(Game.collums) * Game.scale, Game.random.nextInt(Game.rows) * Game.scale);
        
        borders[0] = new Line(new Vector2(position.x, position.y), new Vector2(position.x, position.y + Game.scale));
        borders[1] = new Line(new Vector2(position.x, position.y), new Vector2(position.x + Game.scale, position.y));
        borders[2] = new Line(new Vector2(position.x, position.y + Game.scale), new Vector2(position.x + Game.scale, position.y + Game.scale));
        borders[3] = new Line(new Vector2(position.x + Game.scale, position.y), new Vector2(position.x + Game.scale, position.y + Game.scale));
        
        while (true)
        {
            boolean insideSnake = false;
            
            for(Tail tail : snake.tails)
            {
                if(position.equals(tail.position))
                {
                    insideSnake = true;
                }
            }
            
            if(snake.position.equals(position))
            {
                insideSnake = true;
            }
                
            
            if(!insideSnake)
            {
                break;
            }
            else
            {
                position = new Vector2(Game.random.nextInt(Game.collums) * Game.scale, Game.random.nextInt(Game.rows) * Game.scale);
                
                borders[0] = new Line(new Vector2(position.x, position.y), new Vector2(position.x, position.y + Game.scale));
                borders[1] = new Line(new Vector2(position.x, position.y), new Vector2(position.x + Game.scale, position.y));
                borders[2] = new Line(new Vector2(position.x, position.y + Game.scale), new Vector2(position.x + Game.scale, position.y + Game.scale));
                borders[3] = new Line(new Vector2(position.x + Game.scale, position.y), new Vector2(position.x + Game.scale, position.y + Game.scale));                    
            }
        }
    }

    public void update()
    {

        if(snake.position.equals(position))
        {
            snake.score += Math.pow((1 - snake.attempt / Game.maxAttempts) * 10, 3);
            snake.attempt = 0;
            snake.tails.add(new Tail(new Vector2(snake.tails.get(snake.tails.size() - 1).position.x, snake.tails.get(snake.tails.size() - 1).position.y)));
            
            position = new Vector2(Game.random.nextInt(Game.collums) * Game.scale, Game.random.nextInt(Game.rows) * Game.scale);
            
            borders[0] = new Line(new Vector2(position.x, position.y), new Vector2(position.x, position.y + Game.scale));
            borders[1] = new Line(new Vector2(position.x, position.y), new Vector2(position.x + Game.scale, position.y));
            borders[2] = new Line(new Vector2(position.x, position.y + Game.scale), new Vector2(position.x + Game.scale, position.y + Game.scale));
            borders[3] = new Line(new Vector2(position.x + Game.scale, position.y), new Vector2(position.x + Game.scale, position.y + Game.scale));
            
            while (true)
            {
                boolean insideSnake = false;
                
                for(Tail tail : snake.tails)
                {
                    if(position.equals(tail.position))
                    {
                        insideSnake = true;
                    }
                }
                
                if(snake.position.equals(position))
                {
                    insideSnake = true;
                }
                
                if(!insideSnake)
                {
                    break;
                }
                else
                {
                    position = new Vector2(Game.random.nextInt(Game.collums) * Game.scale, Game.random.nextInt(Game.rows) * Game.scale);
                    
                    borders[0] = new Line(new Vector2(position.x, position.y), new Vector2(position.x, position.y + Game.scale));
                    borders[1] = new Line(new Vector2(position.x, position.y), new Vector2(position.x + Game.scale, position.y));
                    borders[2] = new Line(new Vector2(position.x, position.y + Game.scale), new Vector2(position.x + Game.scale, position.y + Game.scale));
                    borders[3] = new Line(new Vector2(position.x + Game.scale, position.y), new Vector2(position.x + Game.scale, position.y + Game.scale));                    
                }
            }
        }
    }
    
    public void draw(ShapeRenderer shapeRenderer)
    {
        shapeRenderer.setColor(Color.RED);
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
}
