package com.mr.mymr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data

public class ItemsDTO {
    @JsonProperty("items")
    List<ItemDTO> items;
}
