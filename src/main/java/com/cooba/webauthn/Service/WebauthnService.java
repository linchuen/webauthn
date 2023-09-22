package com.cooba.webauthn.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class WebauthnService {
    @Autowired
    private RelyingParty relyingParty;
    @Autowired
    private CredentialManager credentialManager;

    private final Map<String, PublicKeyCredentialCreationOptions> cacheMap = new HashMap<>();
    private final Map<String, AssertionRequest> cacheAuthMap = new HashMap<>();
    private final Random random = new SecureRandom();

    public PublicKeyCredentialCreationOptions startRegister(String name) throws JsonProcessingException {
        UserIdentity userIdentity = UserIdentity.builder()
                .name(name)
                .displayName(name)
                .id(generateRandom(32))
                .build();

        AuthenticatorSelectionCriteria authenticatorSelection = AuthenticatorSelectionCriteria.builder()
                .residentKey(ResidentKeyRequirement.DISCOURAGED)
                .build();

        StartRegistrationOptions registrationOptions = StartRegistrationOptions.builder()
                .user(userIdentity)
                .authenticatorSelection(authenticatorSelection)
                .timeout(60000L)
                .build();
        PublicKeyCredentialCreationOptions credentialCreationOptions = relyingParty.startRegistration(registrationOptions);
        cacheMap.put(name, credentialCreationOptions);
        return credentialCreationOptions;
    }

    public RegisteredCredential finishRegister(String name, PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> publicKeyCredential) throws RegistrationFailedException {
        PublicKeyCredentialCreationOptions credentialCreationOptions = cacheMap.get(name);
        UserIdentity userIdentity = credentialCreationOptions.getUser();

        FinishRegistrationOptions options = FinishRegistrationOptions.builder()
                .request(credentialCreationOptions)
                .response(publicKeyCredential)
                .build();

        RegistrationResult registration = relyingParty.finishRegistration(options);

        RegisteredCredential registeredCredential = RegisteredCredential.builder()
                .credentialId(registration.getKeyId().getId())
                .userHandle(userIdentity.getId())
                .publicKeyCose(registration.getPublicKeyCose())
                .signatureCount(registration.getSignatureCount())
                .build();

        credentialManager.insertRegisteredCredential(userIdentity, registeredCredential);
        return registeredCredential;
    }

    public AssertionRequest startAuthentication(String name) {
        AssertionRequest assertionRequest = relyingParty.startAssertion(StartAssertionOptions.builder().username(name).build());
        cacheAuthMap.put(name, assertionRequest);
        return assertionRequest;
    }

    public AssertionResult finishAuthentication(String name, PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> publicKeyCredential) throws AssertionFailedException {
        AssertionRequest assertionRequest = cacheAuthMap.get(name);

        FinishAssertionOptions options = FinishAssertionOptions.builder()
                .request(assertionRequest)
                .response(publicKeyCredential)
                .build();

        AssertionResult result = relyingParty.finishAssertion(options);
        return result;
    }

    private ByteArray generateRandom(int length) {
        byte[] bytes = new byte[length];
        this.random.nextBytes(bytes);
        return new ByteArray(bytes);
    }
}
