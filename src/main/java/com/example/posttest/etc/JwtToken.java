package com.example.posttest.etc;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtToken {
    private String accesstoken;
    private String refreshtoken;
    private String grantType;

}
