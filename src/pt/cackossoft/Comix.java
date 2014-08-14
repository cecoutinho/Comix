package pt.cackossoft;

import org.apache.commons.lang.ArrayUtils;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Comix {
    private final String[] fComicNames = {
            "Adam@home",
            "For Better or For Worse",
            "Pickles",
            "Ben (except Sundays)",
            "Baldo",
            "Doonesbury",
            "Red and Rover",
            "Garfield",
            "Calvin & Hobbes",
            "Dilbert Classics",
            "Non Sequitur",
            "The Argyle Sweater",
            "Reality Check",
            "Speed Bump",
            "Ripley\"s Believe It or Not",
            "Trivquiz (except Sundays)",
            "Truth Facts",
            "Biographic (Sundays)",
            "Magic in a Minute (Sundays)",
            "Ask Shagg (Sundays)",
            "Kid Spot (except Sundays)" };
    private final String[] fComicUrls = {
            "http://www.gocomics.com/adamathome",
            "http://www.gocomics.com/forbetterorforworse",
            "http://www.gocomics.com/pickles",
            "http://www.gocomics.com/ben",
            "http://www.gocomics.com/baldo",
            "http://www.gocomics.com/doonesbury",
            "http://www.gocomics.com/redandrover",
            "http://www.gocomics.com/garfield",
            "http://www.gocomics.com/calvinandhobbes",
            "http://www.gocomics.com/dilbert-classics",
            "http://www.gocomics.com/nonsequitur",
            "http://www.gocomics.com/theargylesweater",
            "http://www.gocomics.com/realitycheck",
            "http://www.gocomics.com/speedbump",
            "http://www.gocomics.com/ripleysbelieveitornot",
            "http://www.gocomics.com/trivquiz",
            "http://www.gocomics.com/truth-facts",
            "http://www.gocomics.com/biographic",
            "http://www.gocomics.com/magicinaminute",
            "http://www.gocomics.com/askshagg",
            "http://www.gocomics.com/kidspot" };

    private String baseUrl;
    private String comicId;
    private String comicName;
    private String minDate;
    private String maxDate;
    private String description;

    public void main(String[] args) {
        for (;;) {
            // Get Comic from Combobox
            final String fComicName = (String) JOptionPane.showInputDialog(null, "Comic to be retrieved:", "Comix", JOptionPane.QUESTION_MESSAGE, null, fComicNames, fComicNames[0]);
            if (null == fComicName) {
                JOptionPane.showMessageDialog(null, "No Comic was selected. Exiting...", "Comix", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            final String fComicUrl = fComicUrls[ArrayUtils.indexOf(fComicNames, fComicName)];
            getPageInfo(fComicUrl);

            // Get the start and end dates
            String startDate = minDate;
            String endDate = startDate.substring(0, 4) + "/12/31";

            final SimpleDateFormat fStr2Date = new SimpleDateFormat("yyyy/MM/dd");
            final SimpleDateFormat fDate2Str = new SimpleDateFormat("yyyy/MM/dd");
            final SimpleDateFormat fDate2Print = new SimpleDateFormat("EEEE, yyyy-MM-dd");
            Calendar lCalendarStt = Calendar.getInstance();
            Calendar lCalendarEnd = Calendar.getInstance();

            try {
                lCalendarStt.setTime(fStr2Date.parse(startDate));   // Start date
                lCalendarEnd.setTime(fStr2Date.parse(endDate));     // End date
            } catch (ParseException e) {
                e.printStackTrace();
            }

            BufferedWriter out;
            try {
                final String lPageTitle = "cackos-comics-" + comicId + "-" + startDate.replaceAll("/", "") + "_" + endDate.replaceAll("/", "");
                out = new BufferedWriter(new FileWriter(lPageTitle + "-urls.htm"));
                out.write("<html><head><title>" + lPageTitle + "</title></head><body><b>Cackos Comics: " + comicName + "<br>From: " +
                        fDate2Print.format(lCalendarStt.getTime()) + "<br>To: " + fDate2Print.format(lCalendarEnd.getTime()) + "</b><br><br>" + description);
                lCalendarEnd.add(Calendar.DATE, 1); // Need to add 1 day to the final date so that it cycles through the final date inclusive
                for (; lCalendarStt.before(lCalendarEnd); lCalendarStt.add(Calendar.DATE, 1)) {
                    String lTargetUrl = baseUrl + "/" + comicId + "/" + fDate2Str.format(lCalendarStt.getTime());
                    String[] lComicUrls = WebPageReader.getComicPictureURL(lTargetUrl);
                    System.out.println(fDate2Print.format(lCalendarStt.getTime()) + ": " + lComicUrls[0] + " + " + lComicUrls[1]);
                    String lComicUrl = null == lComicUrls[1] ? lComicUrls[0] : lComicUrls[1];
                    if (null != lComicUrl) {
                        out.write("<hr>Comic: " + comicName + " - Date: " + fDate2Print.format(lCalendarStt.getTime()) + "<br><a href=" + lTargetUrl + "><img src=\"" + lComicUrl + "\"></a>");
                    }
                }
                out.write("</body></html>");
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPageInfo(String aUrl) {
        String[] pageInfo = WebPageReader.getComicInfo(aUrl);
        if (6 == pageInfo.length) {
            baseUrl = pageInfo[0];
            comicId = pageInfo[1];
            comicName = pageInfo[2];
            minDate = pageInfo[3];
            maxDate = pageInfo[4];
            description = pageInfo[5];
        }
    }
}
