package dima.java.gameeliminiationdetector;

/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class ScreenScraper {
    public static void main(String[] args) {
        String name = "https://finance.yahoo.com/quote/";
        In in = new In(name + args[0] + "?ltr=1");
        String text = in.readAll();
        int start = text.indexOf("PREV_CLOSE-value", 0);
        int from = text.indexOf("data-reactid=\"15\">", start);
        int to = text.indexOf("</span>", from);
        String temp = text.substring(start, start + 100);
        String pricce = text.substring(from + 18, to);
        StdOut.println(pricce);
    }
}
