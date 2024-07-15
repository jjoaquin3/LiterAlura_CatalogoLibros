package com.jjoaquin3.literalura.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookResponseDTO
{
    private String title;
    private List<AuthorResponseDTO> authors;
    private List<String> languages;

    @JsonAlias("download_count")
    private int downloadCount;
}
