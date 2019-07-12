import utils.CompleteTweet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TweetManager {
    public List<CompleteTweet> getTweetsWithoutUserUrl(int sentimentId, String path) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        List<CompleteTweet> completeTweets = allLines.stream().map(l -> new CompleteTweet(l, sentimentId)).collect(Collectors.toList());

        return completeTweets;
    }
}


