package com.matchgame;

public interface Phase {
  /**
   * @param delta seconds since last call to act().
   * @return true if still in this phase, false if the phase is done.
   */
  boolean act(float delta);
  void enter();
  void exit();
}
