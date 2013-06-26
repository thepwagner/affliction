package me.mycloudand.affliction

import org.junit.Test

import javax.swing.*

/**
 * Created with IntelliJ IDEA.
 * User: pwagner
 * Date: 15/06/13
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
class TappedOutScannerTest {

    private Random rand = new Random()

    @Test
    void f() {
        ScreenWatcher screenWatcher = new ScreenWatcher()
        TappedOutScanner screenScanner = new TappedOutScanner(watcher: screenWatcher)

        def imageIcon = new ImageIcon()
        def jLabel = new JLabel(imageIcon);
        jLabel.setVisible(true)

        def jWindow = new JFrame()
        jWindow.add(jLabel)
        jWindow.pack()
        jWindow.setVisible(true)
        jWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        long ticks = Long.MIN_VALUE;
        while (true) {
            def captureImage = screenWatcher.scanTappedOut()
            def markedImage = screenScanner.scanScreen(captureImage)


            if ((ticks++ % 20) == 0) {
                markedImage = screenScanner.findHotPixels(markedImage)
            }

            imageIcon.setImage(markedImage)
            jWindow.pack()
            jWindow.repaint()
            Thread.sleep(rand.nextInt(100) + 50)
        }
    }
}
