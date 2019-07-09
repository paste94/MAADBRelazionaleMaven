import utils.CompleteTweet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TweetManager {
    public List<CompleteTweet> getTweetsWithoutUserPwd(int sentimentId, String path) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        List<CompleteTweet> completeTweets = new ArrayList<>();
        allLines.forEach(l->{
            String l1 = l.replaceAll("URL", "");
            l1 = l1.replaceAll("USERNAME", "");
            completeTweets.add(new CompleteTweet(l1, sentimentId));
        });

        return completeTweets;
    }
}


