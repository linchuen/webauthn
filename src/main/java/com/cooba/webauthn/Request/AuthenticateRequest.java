package com.cooba.webauthn.Request;

import lombok.Data;

@Data
public class AuthenticateRequest {
    String name;
    String credential;
}
