package util;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

@Slf4j
public class CrisUtil {

    private static final Random RANDOM = new Random();

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "HOST"
    };

    private CrisUtil() {
    }

    public static String parseHttpStatus(int statusCode) {
        String status = "status=";
        if (statusCode == HttpStatus.SC_OK) {
            status += "Ok";
        } else {
            status += "Failed";
        }
        return status;
    }

    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Queue<String> convertToCharQueue(String value) {
        Queue<String> queue = new LinkedList<>();
        for (char c : value.toCharArray()) {
            queue.add(String.valueOf(c));
        }
        return queue;
    }

    public static String makeUppercaseFirstChar(String str) {
        String name = str.toLowerCase();
        return String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
    }

    public static int getRandom(int min, int max) {
        return Math.abs(RANDOM.nextInt()) % (max - min + 1) + min;
    }

    public static void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static String getMyPublicIP() {
        try {
            Document doc = Jsoup.connect("http://whatsmyuseragent.org")
                    .header("Content-type", "text/*")
                    .get();
            Element body = doc.body();
            TextNode node = (TextNode) body.select(".intro-text").get(1).unwrap();
            String message = node.getWholeText();
            return message.substring(message.indexOf(":") + 1).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static String getMD5Hash(String data) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash); // make it printable
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    public static String bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }

    public static HttpResponse<String> sendGoogleRequest(String url, String keywords) {
        long uniqueId = System.currentTimeMillis();
        try {
            String encodedKeywords = URLEncoder.encode(keywords, StandardCharsets.UTF_8.name());

            return Unirest.post("https://www.google.com/url?sa=t&source=web&rct=j")
                    .queryString("ved", "2ahUKEwjQ6ayW7PnsAhVS3KQKHTFFAT0QFjAAegQIARAC" + uniqueId)
                    .queryString("url", url)
                    .header("authority", "www.google.com")
                    .header("ping-from", "https://www.google.com/search?q=" + encodedKeywords + "&rlz=1C1CHBF_enRO818RO818&oq=" + keywords + "&aqs=chrome.0.69i59l3" + uniqueId + "j69i60l3.3374j0j7&sourceid=chrome&ie=UTF-8")
                    .header("ping-to", url)
                    .header("dnt", "1")
                    .header("cache-control", "max-age=0")
                    .header("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1" + uniqueId)
                    .header("content-type", "text/ping")
                    .header("accept", "*/*")
                    .header("origin", "https://www.google.com")
                    .header("sec-fetch-site", "same-origin")
                    .header("sec-fetch-mode", "no-cors")
                    .header("sec-fetch-dest", "empty")
                    .header("accept-language", "ro,en;q=0.9,en-GB;q=0.8")
                    .body("PINGu0000")
                    .asString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
