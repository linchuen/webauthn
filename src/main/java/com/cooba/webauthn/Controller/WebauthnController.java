package com.cooba.webauthn.Controller;

import com.cooba.webauthn.Request.AuthenticateRequest;
import com.cooba.webauthn.Request.RegisterRequest;
import com.cooba.webauthn.Service.WebauthnService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WebauthnController {
    @Autowired
    private WebauthnService webauthnService;

    @PostMapping("/register/{name}")
    public ResponseEntity<String> startRegister(@PathVariable("name") String name) throws JsonProcessingException {
        PublicKeyCredentialCreationOptions credentialCreationOptions = webauthnService.startRegister(name);
        return ResponseEntity.ok(credentialCreationOptions.toCredentialsCreateJson());
    }

    @PostMapping("/register/finish")
    public ResponseEntity<String> finishRegister(@RequestBody RegisterRequest request) throws IOException {
        PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = PublicKeyCredential.parseRegistrationResponseJson(request.getCredential());
        try {
            webauthnService.finishRegister(request.getName(), pkc);
            return ResponseEntity.ok().build();
        }catch (RegistrationFailedException registrationFailedException){
            return ResponseEntity.internalServerError().body(registrationFailedException.getMessage());
        }
    }

    @PostMapping("/authenticate/finish")
    public void startAuthentication(@PathVariable("name") String name) {
        webauthnService.startAuthentication(name);
    }

    @PostMapping("/authenticate")
    public void startAuthentication(@RequestBody AuthenticateRequest request) throws IOException, AssertionFailedException {
        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> publicKeyCredential = PublicKeyCredential.parseAssertionResponseJson(request.getCredential());
        webauthnService.finishAuthentication(request.getName(), publicKeyCredential);
    }
}
