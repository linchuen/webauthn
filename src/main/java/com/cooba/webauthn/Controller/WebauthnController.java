package com.cooba.webauthn.Controller;

import com.cooba.webauthn.Request.AuthenticateRequest;
import com.cooba.webauthn.Request.RegisterRequest;
import com.cooba.webauthn.Service.WebauthnService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.exception.AssertionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WebauthnController {
    @Autowired
    private WebauthnService webauthnService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/register/{name}")
    public ResponseEntity<String> startRegister(@PathVariable("name") String name) throws JsonProcessingException {
        PublicKeyCredentialCreationOptions credentialCreationOptions = webauthnService.startRegister(name);
        return ResponseEntity.ok(credentialCreationOptions.toCredentialsCreateJson());
    }

    @PostMapping("/register/finish")
    public ResponseEntity<String> finishRegister(@RequestBody RegisterRequest request) throws IOException {
        PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = PublicKeyCredential.parseRegistrationResponseJson(request.getCredential());
        try {
            RegisteredCredential credential = webauthnService.finishRegister(request.getName(), pkc);
            return ResponseEntity.ok(objectMapper.writeValueAsString(credential));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/authenticate/{name}")
    public ResponseEntity<String> startAuthentication(@PathVariable("name") String name) throws JsonProcessingException {
        AssertionRequest assertionRequest = webauthnService.startAuthentication(name);
        return ResponseEntity.ok(assertionRequest.toCredentialsGetJson());
    }

    @PostMapping("/authenticate/finish")
    public ResponseEntity<String> startAuthentication(@RequestBody AuthenticateRequest request) throws IOException, AssertionFailedException {
        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> publicKeyCredential = PublicKeyCredential.parseAssertionResponseJson(request.getCredential());
        try {
            AssertionResult result = webauthnService.finishAuthentication(request.getName(), publicKeyCredential);
            return ResponseEntity.ok(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
