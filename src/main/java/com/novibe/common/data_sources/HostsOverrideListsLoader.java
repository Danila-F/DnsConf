package com.novibe.common.data_sources;

import com.novibe.common.util.EnvParser;

import static com.novibe.common.config.EnvironmentVariables.REDIRECT_EXCLUDE;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HostsOverrideListsLoader extends ListLoader<HostsOverrideListsLoader.BypassRoute> {

    public record BypassRoute(String ip, String website) {
    }

    private static final List<String> REDIRECT_EXCLUDE_DOMAINS = EnvParser.parse(REDIRECT_EXCLUDE).stream()
                                                                          .map(String::toLowerCase)
                                                                          .distinct()
                                                                          .collect(Collectors.toUnmodifiableList());

    @Override
    protected String listType() {
        return "Override";
    }

    @Override
    protected Predicate<String> filterRelatedLines() {
        return line -> !HostsBlockListsLoader.isBlock(line) &&
                        REDIRECT_EXCLUDE_DOMAINS.stream().noneMatch(line::contains);
    }

    @Override
    protected BypassRoute toObject(String line) {
        int delimiter = line.indexOf(" ");
        String ip = line.substring(0, delimiter++);
        String website = removeWWW(line.substring(delimiter).strip());
        return new BypassRoute(ip, website);
    }

}
