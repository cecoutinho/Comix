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
            "https://www.gocomics.com/adamathome",
            "https://www.gocomics.com/forbetterorforworse",
            "https://www.gocomics.com/pickles",
            "https://www.gocomics.com/ben",
            "https://www.gocomics.com/baldo",
            "https://www.gocomics.com/doonesbury",
            "https://www.gocomics.com/redandrover",
            "https://www.gocomics.com/garfield",
            "https://www.gocomics.com/calvinandhobbes",
            "https://www.gocomics.com/dilbert-classics",
            "https://www.gocomics.com/nonsequitur",
            "https://www.gocomics.com/theargylesweater",
            "https://www.gocomics.com/realitycheck",
            "https://www.gocomics.com/speedbump",
            "https://www.gocomics.com/ripleysbelieveitornot",
            "https://www.gocomics.com/trivquiz",
            "https://www.gocomics.com/truth-facts",
            "https://www.gocomics.com/biographic",
            "https://www.gocomics.com/magicinaminute",
            "https://www.gocomics.com/askshagg",
            "https://www.gocomics.com/kidspot" };

    private String baseUrl;
    private String comicId;
    private String comicName;
    private String minDate;
    private String maxDate;
    private String description;

    public void main(String[] args) {
        for (;;) {
            // Select Comic Name from Combobox
            final String fComicName = (String) JOptionPane.showInputDialog(null, "Comic to be retrieved:", "Comix", JOptionPane.QUESTION_MESSAGE, null, fComicNames, fComicNames[0]);
            if (null == fComicName) {
                JOptionPane.showMessageDialog(null, "No Comic was selected. Exiting...", "Comix", JOptionPane.INFORMATION_MESSAGE);
                break;
            }

            // Get Information from Comics Site
            final String fComicUrl = fComicUrls[ArrayUtils.indexOf(fComicNames, fComicName)];
            getPageInfo(fComicUrl);

            // Select Year to retrieve Comics. This needs to be done otherwise we will get all years (too much information) and the Comics site will shut us down (thinking it is an attack)
            int lComicYear = -1;
            String lComicYearStr = (String) JOptionPane.showInputDialog(null, "Year to be retrieved:", "Comix", JOptionPane.QUESTION_MESSAGE, null, null, minDate.substring(0,4));
            if (null == lComicYearStr) {
                JOptionPane.showMessageDialog(null, "No Comic Year was selected. Exiting...", "Comix", JOptionPane.INFORMATION_MESSAGE);
                break;
            } else {
                lComicYear = Integer.parseInt(lComicYearStr);
            }

            final SimpleDateFormat fStr2Date = new SimpleDateFormat("yyyy/MM/dd");
            final SimpleDateFormat fDate2Str = new SimpleDateFormat("yyyy/MM/dd");
            final SimpleDateFormat fDate2Print = new SimpleDateFormat("EEEE, yyyy-MM-dd");
            Calendar lCalendarStt = Calendar.getInstance();
            Calendar lCalendarEnd = Calendar.getInstance();
            Calendar lCalendarMaxDate = Calendar.getInstance();

            try {
                lCalendarStt.setTime(fStr2Date.parse(minDate));   // Start date
                lCalendarMaxDate.setTime(fStr2Date.parse(maxDate)); // Max date
                if (lComicYear > 0) {
                    if (lComicYear > lCalendarStt.get(Calendar.YEAR)) lCalendarStt.setTime(fStr2Date.parse(lComicYear + "/01/01"));   // Start date
                    if (lComicYear < lCalendarMaxDate.get(Calendar.YEAR)) lCalendarMaxDate.setTime(fStr2Date.parse(lComicYear + "/12/31"));   // Max date
                }
                lCalendarMaxDate.add(Calendar.DATE, 1); // Need to add 1 day to the final date so that it cycles through the final date inclusive
            } catch (ParseException e) {
                e.printStackTrace();
            }

            BufferedWriter out;
            try {
                while (lCalendarStt.before(lCalendarMaxDate)) {
                    lCalendarEnd.setTime(lCalendarStt.getTime());     // End date
                    lCalendarEnd.add(Calendar.YEAR, 1);      // Add 1 Year
                    lCalendarEnd.set(Calendar.MONTH, 0);        // 0 = January
                    lCalendarEnd.set(Calendar.DAY_OF_MONTH, 1); // new year
                    lCalendarEnd.setTime((lCalendarMaxDate.before(lCalendarEnd) ? lCalendarMaxDate : lCalendarEnd).getTime());
                    lCalendarEnd.add(Calendar.DATE, -1);
                    String lPageTitle = "cackos-comics-" + comicId + "-" + fDate2Str.format(lCalendarStt.getTime()).replaceAll("/", "") + "_" + fDate2Str.format(lCalendarEnd.getTime()).replaceAll("/", "");
                    out = new BufferedWriter(new FileWriter(lPageTitle + "-urls.htm"));
                    out.write("<html><head><title>" + lPageTitle + "</title></head><body><b>Cackos Comics: " + comicName + "<br>From: " +
                            fDate2Print.format(lCalendarStt.getTime()) + "<br>To: " + fDate2Print.format(lCalendarEnd.getTime()) + "</b><br><br>" + description);
                    lCalendarEnd.add(Calendar.DATE, 1); // Need to add 1 day to the final date so that it cycles through the final date inclusive
                    for (; lCalendarStt.before(lCalendarEnd); lCalendarStt.add(Calendar.DATE, 1)) {
                        String lTargetUrl = baseUrl + "/" + comicId + "/" + fDate2Str.format(lCalendarStt.getTime());
                        String[] lComicUrls = WebPageReader.getComicPictureURL(lTargetUrl);
                        //                    System.out.println(fDate2Print.format(lCalendarStt.getTime()) + ": " + lComicUrls[0] + " + " + lComicUrls[1]);
                        String lComicUrl = null == lComicUrls[1] ? lComicUrls[0] : lComicUrls[1];
                        if (null != lComicUrl) {
                            out.write("<hr>Comic: " + comicName + " - Date: " + fDate2Print.format(lCalendarStt.getTime()) + "<br><a href=" + lTargetUrl + "><img src=\"" + lComicUrl + "\"></a>");
                        } else {
                            out.write("<hr>Comic: " + comicName + " - Date: " + fDate2Print.format(lCalendarStt.getTime()) + "<br><a href=" + lTargetUrl + "><img src=\"" + lComicUrl + "\"></a>");
                        }
                    }
                    out.write("</body></html>");
                    out.flush();
                    out.close();
                }
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
