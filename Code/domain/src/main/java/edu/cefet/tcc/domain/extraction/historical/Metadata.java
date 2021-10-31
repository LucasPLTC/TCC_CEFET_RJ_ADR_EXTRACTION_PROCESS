package edu.cefet.tcc.domain.extraction.historical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
    private String newest_id;
    private String oldest_id;
    private Integer result_count;
    private String next_token;
}
