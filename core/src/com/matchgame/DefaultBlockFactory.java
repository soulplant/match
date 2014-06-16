package com.matchgame;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.matchgame.Block.Delegate;

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
    return createColoredBlock(x, y, colorIndex, delegate);
  }

  @Override
  public Block createColoredBlock(int x, int y, int colorIndex,
      Delegate delegate) {
    return new Block(x, y, colorIndex, textures.get(colorIndex),
        new ShapeRenderer(), delegate);
  }
}
