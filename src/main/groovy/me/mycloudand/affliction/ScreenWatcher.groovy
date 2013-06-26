package me.mycloudand.affliction

import java.awt.*
import java.awt.event.InputEvent
import java.awt.image.BufferedImage

/**
 * Created with IntelliJ IDEA.
 * User: pwagner
 * Date: 15/06/13
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
class ScreenWatcher {
    private static final int OFFSET_X = 35
    private static final int OFFSET_Y = 45

    Rectangle screenBounds
    Rectangle captureRect = new Rectangle(OFFSET_X, OFFSET_Y, 1600, 850)
    Robot robot

    Random rand = new Random()

    ScreenWatcher() {
        def ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        ge.screenDevices.each { GraphicsDevice screen ->
            screenBounds = screen.defaultConfiguration.bounds
            if (screenBounds.width != 1680) {
                return
            }
            robot = new Robot(screen)
        }
    }

    BufferedImage scanTappedOut() {
        robot.createScreenCapture(captureRect)
    }

    void clickTappedOut(int x, int y) {
        println "Move to ${OFFSET_X + x},${OFFSET_Y + y}"

        Point mouseLocation = MouseInfo.pointerInfo.location
//        println mouseLocation
//
        int end_x = OFFSET_X + x
        int end_y = OFFSET_Y + y
//
//        for (int i = 0; i < 100; i++) {
//            int mov_x = ((end_x * i) / 100) + (mouseLocation.x * (100 - i) / 100);
//            int mov_y = ((end_y * i) / 100) + (mouseLocation.y * (100 - i) / 100);

//            robot.delay(10);
//        }

        robot.mouseMove(end_x, end_y);
        robot.delay(rand.nextInt(10) + 10)
        robot.mousePress(InputEvent.BUTTON1_MASK)
        robot.delay(rand.nextInt(5) + 5)
        robot.mouseRelease(InputEvent.BUTTON1_MASK)
        robot.delay(200)
    }
}
