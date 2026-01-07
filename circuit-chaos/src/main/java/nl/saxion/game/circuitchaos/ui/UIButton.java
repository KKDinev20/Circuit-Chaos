package nl.saxion.game.circuitchaos.ui;

import nl.saxion.gameapp.GameApp;

public class UIButton {
    float x, y, w, h;
    String text;
    String normalColor;
    String hoverColor;

    public UIButton(float x, float y, float w, float h,
                    String text, String normalColor, String hoverColor) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = text;
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
    }


    public boolean isHovered(float mx, float my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    public void render(float mx, float my) {
        boolean hover = isHovered(mx, my);
        float pad = hover ? 5 : 0;

        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(
                x - pad,
                y - pad,
                w + pad * 2,
                h + pad * 2,
                hover ? hoverColor : normalColor
        );
        GameApp.endShapeRendering();

        GameApp.startSpriteRendering();
        GameApp.drawTextCentered(
                "levelSelectFont",
                text,
                x + w / 2f,
                y + h / 2f,
                "white"
        );
        GameApp.endSpriteRendering();
    }
}
