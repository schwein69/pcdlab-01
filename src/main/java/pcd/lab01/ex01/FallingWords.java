package pcd.lab01.ex01;

import org.fusesource.jansi.Ansi;

import static pcd.lab01.ex01.AuxLib.*;


class CustomThread extends Thread {
    private final Screen screen;
    private final String word;
    private final int pos_x;
    private int pos_y;
    private static final int Y_BOTTOM = 20; // The consistent stopping height
    private static final int SPEED = 100; // The consistent stopping height

    public CustomThread(Screen screen, String word, int pos_x, int pos_y) {
        this.screen = screen;
        this.word = word;
        this.pos_x = pos_x + 10;
        this.pos_y = pos_y;
    }

    //TODO Spostamento diverso
    public void run() {
        while (this.pos_y <= Y_BOTTOM) {
            try {
                Thread.sleep(SPEED + (int) (Math.random() * 500));
                this.pos_y++;
                log();
                screen.writeStringAt(pos_y - 1, pos_x, Ansi.Color.BLACK, word);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        }
    }

    private void log() {
        screen.writeStringAt(this.pos_y, this.pos_x, Ansi.Color.RED, this.word);
    }

}


public class FallingWords extends Thread {

    public static void main(String[] args) {
        Screen screen = Screen.getInstance();
        screen.clear();
        var y0 = 10;
        var sentence = "This is a simple sentence with words ready to fall";
        var wordList = getWordsPos(sentence);
        for (var wp : wordList) {
            Thread t = new CustomThread(screen, wp.word(), wp.pos(), y0);
            t.start();
        }
    }
}
