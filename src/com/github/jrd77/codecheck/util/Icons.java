package com.github.jrd77.codecheck.util;

import javax.swing.*;
import java.awt.*;

/**
 * @author zhen.wang
 * @description icon util
 * @date 2021/9/8 10:05
 */
public class Icons {

    public static Icon pluginIcon;

    static {
        Image image = Toolkit.getDefaultToolkit().getImage("img/icon-blue.svg");
        pluginIcon = new ImageIcon(image);

    }

    public static void main(String[] args) {
        System.out.println(pluginIcon);
    }
}
