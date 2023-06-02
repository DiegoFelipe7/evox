package com.evox.evox.dto;

import com.evox.evox.model.Synthetics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListSyntheticUsers {
    private String username;
    private String password;
}
