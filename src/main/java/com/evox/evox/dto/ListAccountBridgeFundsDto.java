package com.evox.evox.dto;

import com.evox.evox.model.BridgeFundsAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListAccountBridgeFundsDto {
    private Integer id;
    private String title;
    private List<BridgeFundsAccount> bridgeFundsAccounts;
    private Boolean state;
}
