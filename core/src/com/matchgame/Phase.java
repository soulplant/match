package com.matchgame;

public interface Phase {
  boolean act(float delta);
  void enter();
  void exit();
}
