package com.matchgame;

public interface BlockFactory {
  public Block createBlock(int x, int y, Block.Delegate delegate);
}
