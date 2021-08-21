package com.github.annasajkh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Game extends ApplicationAdapter
{

    static ShapeRenderer shapeRenderer;
    static float scale = 100;
    static List<Snake> snakes;
    static List<Snake> snakeDies;
    static Random random = new Random();
    static int visionLength = 10;
    static int inputSize = visionLength * 2 + 8;
    static int rows;
    static int collums;
    static int populationSize = 3000;
    static int bestSize = (int) (populationSize * 0.4f);
    static int index;
    static int generation = 0;
    static BitmapFont font;
    static SpriteBatch spriteBatch;
    static Snake bestSnake;
    static boolean isRendering = false;
    static Line[] borders = new Line[4];
    public static float generationToRenderEach = 1;
    public static int maxAttempts;
    public static float maxLength;

    public static void removeSnake(int index)
    {
        snakeDies.add(snakes.remove(index));
    }

    @Override
    public void create()
    {
        font = new BitmapFont();
        spriteBatch = new SpriteBatch();

        rows = (int) (Gdx.graphics.getHeight() / scale);
        collums = (int) (Gdx.graphics.getWidth() / scale);
        maxAttempts = rows * collums;

        borders[0] = new Line(new Vector2(0, 0), new Vector2(0, Gdx.graphics.getHeight()));
        borders[1] = new Line(new Vector2(0, 0), new Vector2(Gdx.graphics.getWidth(), 0));

        borders[2] = new Line(new Vector2(0, Gdx.graphics.getHeight()), new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        borders[3] = new Line(new Vector2(Gdx.graphics.getWidth(), 0), new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        snakeDies = new ArrayList<>(populationSize);
        snakes = new ArrayList<>(populationSize);

        index = 0;
        shapeRenderer = new ShapeRenderer();

        for(int i = 0; i < populationSize; i++)
        {
            snakes.add(new Snake());
        }
        
        maxLength = new Vector2(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()).len();
    }

    @Override
    public void render()
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(snakes.isEmpty())
        {
            if(!isRendering)
            {
                bestSnake = snakeDies.get(0);
                for(Snake snake : snakeDies)
                {
                    if(snake.score > bestSnake.score)
                    {
                        bestSnake = snake;
                    }
                }
                bestSnake.brain.save("bestSnakeBrain.txt");
                bestSnake = new Snake(bestSnake.brain.clone());
                
            }

            if(!bestSnake.dead && generation % generationToRenderEach == 0 && generation != 0)
            {
                isRendering = true;
                bestSnake.update();
                
                if(!bestSnake.dead)
                {
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    bestSnake.render(shapeRenderer);
                    shapeRenderer.end();
                }
                
                try
                {
                    Thread.sleep(100);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }


            }
            else
            {

                Collections.sort(snakeDies);

                Random random = new Random();

                List<Snake> bestSnakes = snakeDies.subList(snakeDies.size() - bestSize, snakeDies.size());
                List<Snake> bestSnakesBreedAndMutates = new ArrayList<>(bestSnakes);

                for(Snake bestSnakesBreedAndMutate : bestSnakesBreedAndMutates)
                {
                    Snake otherSnakes = bestSnakesBreedAndMutates.get(random.nextInt(bestSnakesBreedAndMutates.size()));

                    bestSnakesBreedAndMutate.brain.crossover(otherSnakes.brain);
                    bestSnakesBreedAndMutate.brain.mutate(0.1f);
                }

                bestSnakes.addAll(bestSnakesBreedAndMutates);

                for(int i = 0; i < bestSize / 2; i++)
                {
                    bestSnakes.add(new Snake());
                }

                for(Snake snake : bestSnakes)
                {
                    snakes.add(new Snake(snake.brain.clone()));
                }

                snakeDies.clear();
                generation++;
                isRendering = false;
                snakeDies.clear();
            }
        }
        else
        {
            for(int i = snakes.size() - 1; i >= 0; i--)
            {
                snakes.get(i).update();
                if(snakes.get(i).dead)
                {
                    removeSnake(i);
                }
            }
        }
        

        spriteBatch.begin();
        font.draw(spriteBatch, "generation : " + generation, 10, 20);
        spriteBatch.end();
    }

    @Override
    public void dispose()
    {
        shapeRenderer.dispose();
    }
}
