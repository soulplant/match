package com.matchgame;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DefaultBlockFactory implements BlockFactory {
  private final Random random;
  private final List<Texture> textures;

  public DefaultBlockFactory(Random random, List<Texture> textures) {
    this.random = random;
    this.textures = textures;
  }

  @Override
  public Block createBlock(int x, int y, Block.Delegate delegate) {
    int colorIndex = random.nextInt(textures.size());
    Block block = new Block(x, y, colorIndex, textures.get(colorIndex),
        new ShapeRenderer(), delegate);
    return block;
  }
}
