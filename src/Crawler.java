import java.io.*;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class Crawler {
    //The class that contains url, depth pair.
    UrlDepthPairPool urlDepthPairPool;
	
    HttpURLConnection conn;

    int currentDepth;
	String currentUrl;

	//Crawler Constructor.
    //Add initial URL to UrlDepthPair pool with depth 0.
    public Crawler(String url, int maxDepth){
        urlDepthPairPool = new UrlDepthPairPool(maxDepth);
        //initial url has 0 depth.
        urlDepthPairPool.add(url, 0);
    }

    //establishing connection for URL which taking out from pool.
    public void getConnection() throws Exception {
            UrlDepthPair aUrlDepthPair = urlDepthPairPool.poll();
            URL aURL = new URL(aUrlDepthPair.getURLString());
            currentUrl = aURL.toString();
            if (aURL.toString().contains("https")) {
                conn = (HttpsURLConnection) aURL.openConnection();
            } else {
                conn = (HttpURLConnection) aURL.openConnection();
            }
            this.currentDepth = aUrlDepthPair.getDepth();
    }

    //read all html string from one URL.
    public void getStringOfHttpsConn() {
        try {
            getConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(3000);
            BufferedReader htmlReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            for (String line; (line = htmlReader.readLine()) != null; ) {
                findUrlInString(line);
            }
        }
        catch (Exception e){
            //just ignore and keep crawling.
        }
    }

    //find URLs in html String and add URLs to the URL pool.
    public void findUrlInString(String htmlLine) throws Exception{
        int urlStartIndex = 0;
        int urlEndIndex = 0;
        while ((urlStartIndex = htmlLine.indexOf("<a href=\"", urlStartIndex)) != -1) {
            urlStartIndex = urlStartIndex + 9;
            urlEndIndex = htmlLine.indexOf("\"", urlStartIndex);
            urlDepthPairPool.add(htmlLine.substring(urlStartIndex, urlEndIndex), currentDepth + 1);
            urlStartIndex++;
        }
	}

	public void crawlingHttps(){
        while (urlDepthPairPool.size() != 0){
            getStringOfHttpsConn();
            System.out.print(currentUrl + " / ");
            System.out.println(currentDepth);
        }
        urlDepthPairPool.printAllVisitedUrls();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
        if (args.length != 2){
            System.out.println("there are wrong arguments! please check the correct usage of arguments.");
            System.out.println("java Clawler <URL> <Depth>");
            System.exit(0);
        }

        // Set up SSL context for HTTPS support
        try {
        TrustManager[] trustAllCerts =
                new TrustManager[]{ new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {} public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }};
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom()); HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) { System.exit(1); }

        Crawler C = new Crawler(args[0], Integer.parseInt(args[1]));

        C.crawlingHttps();
    }
}
