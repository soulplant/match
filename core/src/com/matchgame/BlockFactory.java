package com.matchgame;

public interface BlockFactory {
  public Block createBlock(int x, int y, Block.Delegate delegate);
  public Block createColoredBlock(int x, int y, int colorIndex, Block.Delegate delegate);
}
