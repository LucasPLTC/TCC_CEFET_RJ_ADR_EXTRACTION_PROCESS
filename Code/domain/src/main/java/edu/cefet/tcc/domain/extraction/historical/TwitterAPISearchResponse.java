package edu.cefet.tcc.domain.extraction.historical;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.cefet.tcc.domain.shared.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterAPISearchResponse {
    Collection<Data> data;
    Metadata meta;

    public boolean hasNextPage(){
        return this.meta.getNext_token() != null;
    }
}
