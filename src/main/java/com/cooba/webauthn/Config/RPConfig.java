package com.cooba.webauthn.Config;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.Set;

@Configuration
public class RPConfig {
    @Autowired
    CredentialRepository credentialRepository;

    @Bean
    public RelyingParty relyingParty() {
        return RelyingParty.builder()
                .identity(RelyingPartyIdentity.builder().id("localhost").name("Yubico WebAuthn demo").build())
                .credentialRepository(credentialRepository)
                .origins(Set.of("https://localhost:8080"))
                .attestationConveyancePreference(Optional.of(AttestationConveyancePreference.DIRECT))
                .allowOriginPort(false)
                .allowOriginSubdomain(false)
                .allowUntrustedAttestation(true)
                .validateSignatureCounter(true)
                .build();
    }
}
