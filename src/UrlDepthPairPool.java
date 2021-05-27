import java.net.MalformedURLException;
import java.util.*;

public class UrlDepthPairPool {

    //container to store url, depth pairs.
    //contains urls that going to visit.
    Queue<UrlDepthPair> urlDepthPairsList;

    //container to store url that already crawled.
    Queue<String> visitedUrl;

    //container to store url, depth pairs for printing results.
    Queue<UrlDepthPair> resultUrl;

    //max depth of url crawling.
    int maxDepth;

    public UrlDepthPairPool(int maxDepth){
        urlDepthPairsList = new LinkedList<UrlDepthPair>();
        visitedUrl = new LinkedList<String>();
        resultUrl = new LinkedList<UrlDepthPair>();
        this.maxDepth = maxDepth;
    }

    //Store new url, depth pair in the container.
    //If URL's depth is over the max depth, drop it.
    public boolean add(String url, int depth) {
        try {
            if (depth > this.maxDepth){
                throw new Exception("InvalidUrlException");
            }
            else if (url == null || !url.contains("http")){
                throw new MalformedURLException();
            }
            urlDepthPairsList.add(new UrlDepthPair(url, depth));
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    //Retrieve one urlDepthPair from the container.
    //And add that urlDepthPair to visited url list.
    //Only returns a url that doesn't existed in the visited list.
    public UrlDepthPair poll() throws NullPointerException{
        if (urlDepthPairsList.peek() != null) {
            //only when dequeued URL is never visited, add in visited list and return it.
            while (true) {
                if (!visitedUrl.contains(urlDepthPairsList.peek().getURLString())) {
                    visitedUrl.add(urlDepthPairsList.peek().getURLString());
                    resultUrl.add(urlDepthPairsList.peek());
                    return urlDepthPairsList.poll();
                } else {
                    urlDepthPairsList.poll();
                }
            }
        }
        else {
            throw new NullPointerException();
        }
    }

    public int size(){
        return urlDepthPairsList.size();
    }

    public void printAllVisitedUrls(){
        System.out.println("\n---------------------------");
        System.out.println("Print all visited URLs");
        System.out.println("---------------------------");
        for (UrlDepthPair url : resultUrl) {
            System.out.println(url.getURLString() + "  -  Depth: " + url.getDepth());
        }
    }

}
