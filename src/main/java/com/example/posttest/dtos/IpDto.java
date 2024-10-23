package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IpDto {




    private String ip_addr;


    public IpDto(String ip_addr) {
        this.ip_addr = ip_addr;
    }
}
