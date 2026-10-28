package dev.hail.create_fantasizing.block.sturdy_girder;

/**
 * By Deepseek V4.1 Flash 1017:
 * Duck interface implemented on Create's {@code BezierConnection} by a mixin.
 * It stores whether the decorative girder built underneath a curved train
 * track should use Sturdy Girders instead of Metal Girders.
 */
public interface CFABezierGirder {

    boolean isSturdyGirder();

    void setSturdyGirder(boolean sturdy);
}
