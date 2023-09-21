package com.cooba.webauthn.Request;

import lombok.Data;

@Data
public class RegisterRequest {
    String name;
    String credential;
}
