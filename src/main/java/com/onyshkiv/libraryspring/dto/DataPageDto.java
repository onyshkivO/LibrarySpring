package com.onyshkiv.libraryspring.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.onyshkiv.libraryspring.entity.Views;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonView(Views.Id.class)
public class DataPageDto<T> {
    private List<T> data;
    private int currentPage;
    private int pageNumbers;

}
