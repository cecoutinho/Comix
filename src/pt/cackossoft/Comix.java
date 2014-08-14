package pt.cackossoft;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Comix {
    String baseUrl;
    String comicId;
    String comicName;
    String minDate;
    String maxDate;
    String description;
    String startDate;
    String endDate;

    public void main(String[] args) {
        getPageInfo("http://www.gocomics.com/nonsequitur/2014/08/13#.U-weyXVdXQ0");

        startDate = minDate;
        endDate = startDate.substring(0, 4) + "/12/31";
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

    void getPageInfo(String aUrl) {
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
