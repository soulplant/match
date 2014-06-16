package com.matchgame;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Util {
  public static void centerActorInStage(Actor actor, Stage stage) {
    float stageWidth = stage.getWidth();
    float stageHeight = stage.getHeight();
    float hpadding = (stageWidth - actor.getWidth()) / 2;
    float vpadding = (stageHeight - actor.getHeight()) / 2;
    actor.setPosition(hpadding, vpadding);
  }
}
