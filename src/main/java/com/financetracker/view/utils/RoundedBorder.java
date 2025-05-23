package com.financetracker.view.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

/**
 * 圆角边框实现，为面板提供圆角外观
 */
public class RoundedBorder extends AbstractBorder {
    private Color color;
    private int arc;
    private int thickness;
    private Insets insets;

    /**
     * 创建一个新的圆角边框
     * 
     * @param color     边框颜色
     * @param arc       圆角半径
     * @param thickness 边框厚度
     */
    public RoundedBorder(Color color, int arc, int thickness) {
        this.color = color;
        this.arc = arc;
        this.thickness = thickness;
        this.insets = new Insets(thickness, thickness, thickness, thickness);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);

        // 绘制圆角边框
        for (int i = 0; i < thickness; i++) {
            g2d.drawRoundRect(x + i, y + i, width - 1 - i * 2, height - 1 - i * 2, arc, arc);
        }
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = this.insets.left;
        insets.top = this.insets.top;
        insets.right = this.insets.right;
        insets.bottom = this.insets.bottom;
        return insets;
    }
}
