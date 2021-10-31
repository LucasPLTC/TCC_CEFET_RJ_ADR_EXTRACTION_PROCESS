package edu.cefet.tcc.domain.extraction.historical;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
public class SearchItem {
    private String drug;
    private LocalDate expirationDate;
    private boolean active;
    
    public SearchItem(String[] data){
        this.drug = data[0];
        this.expirationDate = LocalDate.parse(data[1]);
        this.active = !expirationDate.isBefore(LocalDate.now());
    }
}
