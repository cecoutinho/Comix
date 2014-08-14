package pt.cackossoft;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by cecoutinho on 11-08-2014.
 */
public class WebPageReader {
    public static String[] getComicPictureURL(String aComicUrl) {
        final String[] fResult = new String[2];
        final String fWebPage = readWebPage(aComicUrl);
        if (null == fWebPage) {
            return fResult;
        }

        // Search for the comic date in the page. Is it equal to the requested date?
        String lSearchString = "<meta name=\"twitter:url\" content=\"";
        int lIndexStt = fWebPage.indexOf(lSearchString);
        if (lIndexStt < 0) {
            return fResult;
        }
        int lIndexEnd = fWebPage.indexOf("\"", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        if (!fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd).equals(aComicUrl)) {
            return fResult; // If the dates are different, it is because the comic was not published in that date. Do not return the URIs.
        }

        // Search for the comic pictures URIs (first one is usually the smaller, the second is the real sized one (zoomed))
        lSearchString = "class=\"strip\" src=\"";
        lIndexStt = fWebPage.indexOf(lSearchString);
        if (lIndexStt < 0) {
            return fResult;
        }
        lIndexEnd = fWebPage.indexOf("\"", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        fResult[0] = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd); // Main result
        // Go for the second image (zoomed image)
        lIndexStt = fWebPage.indexOf(lSearchString, lIndexEnd);
        if (lIndexStt > 0) {
            lIndexEnd = fWebPage.indexOf("\"", lIndexStt + lSearchString.length());
            if (lIndexEnd > 0) {
                fResult[1] = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd); // Zoomed result
            }
        }
        return fResult;
    }

    public static String[] getComicInfo(String aComicUrl) {
        final String[] fResult = new String[6];
        final String fWebPage = readWebPage(aComicUrl);
        if (null == fWebPage) {
            return fResult;
        }

        // Search for the Twitter tag in the page.
        String lSearchString = "<meta name=\"twitter:url\" content=\"";
        int lIndexStt = fWebPage.indexOf(lSearchString);
        if (lIndexStt < 0) {
            return fResult;
        }
        int lIndexEnd = fWebPage.indexOf("\"", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        String lResultString = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd);
        // Expected format: <BaseURL>/<comicId>/yyyy/MM/dd
        lResultString = lResultString.substring(0, lResultString.length() - "/yyyy/MM/dd".length());
        lIndexStt = lResultString.lastIndexOf("/");
        fResult[0] = lResultString.substring(0, lIndexStt);
        fResult[1] = lResultString.substring(lIndexStt + 1);

        // Search for the Comic Name
        lSearchString = "<meta name=\"twitter:title\" content=\"";
        lIndexStt = fWebPage.indexOf(lSearchString);
        if (lIndexStt < 0) {
            return fResult;
        }
        lIndexEnd = fWebPage.indexOf(",", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        fResult[2] = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd);

        // Search for the Description of the Comic (the search string appears twice, the good description comes after the Twitter definition)
        lSearchString = "<meta name=\"description\" content=\"";
        lIndexStt = fWebPage.indexOf(lSearchString, lIndexEnd);
        if (lIndexStt < 0) {
            return fResult;
        }
        lIndexEnd = fWebPage.indexOf("\"/>", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        fResult[5] = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd);

        // Search for the Minimum Date of the Comic
        lSearchString = "minDate: \"";
        lIndexStt = fWebPage.indexOf(lSearchString);
        if (lIndexStt < 0) {
            return fResult;
        }
        lIndexEnd = fWebPage.indexOf("\"", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        fResult[3] = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd);

        // Search for the Maximum Date of the Comic
        lSearchString = "maxDate: \"";
        lIndexStt = fWebPage.indexOf(lSearchString);
        if (lIndexStt < 0) {
            return fResult;
        }
        lIndexEnd = fWebPage.indexOf("\"", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        lResultString = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd);
        if (!lResultString.equals("c")) {
            fResult[4] = lResultString;
        }

        return fResult;
    }

    private static String readWebPage(String aUrl) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream in = new URL(aUrl).openStream();
            byte[] buffer = new byte[256];
            while (true) {
                int byteRead = in.read(buffer);
                if (byteRead == -1)
                    break;
                for (int i = 0; i < byteRead; i++){
                    sb.append((char)buffer[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }
}
