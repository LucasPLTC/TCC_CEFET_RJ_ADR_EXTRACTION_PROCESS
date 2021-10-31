package edu.cefet.tcc.domain.extraction.stream;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.cefet.tcc.domain.shared.Data;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterAPIStreamResponse {
    private Data data;
    private Collection<MatchingRule> matching_rules;
}
