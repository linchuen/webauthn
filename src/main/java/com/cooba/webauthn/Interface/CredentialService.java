package com.cooba.webauthn.Interface;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;


public interface CredentialService extends CredentialRepository {

    void insertRegisteredCredential(RegisteredCredential registeredCredential);
}
