package pt.cackossoft;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by cecoutinho on 11-08-2014.
 */
public class WebPageReader {
    public static String[] getComicPictureURL(String aComicUrl) {
        final String[] fResult = new String[2];
        String fWebPage = readWebPage(aComicUrl);
        if (null == fWebPage) {
            return fResult;
        }

        // Search for the comic date in the page. Is it equal to the requested date?
        String lSearchString = "<meta property=\"og:url\" content=\"";
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
        lSearchString = "<meta property=\"og:image\" content=\"";
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
        String fWebPage = readWebPage(aComicUrl);
        if (null == fWebPage) {
            return fResult;
        }

        // Search for the Twitter tag in the page.
        String lSearchString = "link rel=\"canonical\" href=\"";
        int lIndexStt = fWebPage.indexOf(lSearchString);
        if (lIndexStt < 0) {
            return fResult;
        }
        int lIndexEnd = fWebPage.indexOf("\"", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        String lResultString = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd);
        // Expected format: <BaseURL>/<comicId>, e.g., https://www.gocomics.com/forbetterorforworse
//        lResultString = lResultString.substring(0, lResultString.length() - "/yyyy/MM/dd".length());
        lIndexStt = lResultString.lastIndexOf("/");
        // Expected value example: fResult[0] = "https://www.gocomics.com"
        fResult[0] = lResultString.substring(0, lIndexStt);
        // Expected value example: fResult[1] = "forbetterorforworse"
        fResult[1] = lResultString.substring(lIndexStt + 1);

        // Search for the Comic Name
        lSearchString = "<title>Today on ";
        lIndexStt = fWebPage.indexOf(lSearchString);
        if (lIndexStt < 0) {
            return fResult;
        }
        lIndexEnd = fWebPage.indexOf("</title>", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        // Expected value example: fResult[2] = "For Better or For Worse - Comics by Lynn Johnston - GoComics"
        fResult[2] = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd);

        // Search for the Maximum Date of the Comic
        lSearchString = "primary\" href=\"/" + fResult[1] + "/";
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
            // Expected value example: fResult[1] = "2023/07/18"
            fResult[4] = lResultString;
        }

        // Search for the Minimum Date of the Comic
        // The pattern happens twice, first with the current date and second with the minimum date
        lIndexStt = fWebPage.indexOf(lSearchString, lIndexStt + lSearchString.length());
        if (lIndexStt < 0) {
            return fResult;
        }
        lIndexEnd = fWebPage.indexOf("\"", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        // Expected value example: fResult[3] = "1979/09/10"
        fResult[3] = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd);

        // Read DESCRIPTION
        fWebPage = readWebPage(aComicUrl + "/about");
        if (null == fWebPage) {
            return fResult;
        }

        // Search for the Description of the Comic (the search string appears twice, the good description comes after the Twitter definition)
        lSearchString = "</h1>    <div class=\"content-section\">";
        lIndexStt = fWebPage.indexOf(lSearchString);
        if (lIndexStt < 0) {
            return fResult;
        }
        lIndexEnd = fWebPage.indexOf("</p>", lIndexStt + lSearchString.length());
        if (lIndexEnd < 0) {
            return fResult;
        }
        fResult[5] = fWebPage.substring(lIndexStt + lSearchString.length(), lIndexEnd).trim();

        return fResult;
    }

    private static String readWebPage(String aUrl) {
        StringBuilder result = new StringBuilder();
        try {
            URL myurl = new URL(aUrl);
            HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
            con.setRequestProperty ( "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0" );
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;

            while ((inputLine = in.readLine()) != null) result.append(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result.toString();
    }
}
