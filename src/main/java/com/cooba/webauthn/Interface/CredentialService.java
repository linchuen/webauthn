package com.cooba.webauthn.Interface;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.UserIdentity;


public interface CredentialService extends CredentialRepository {

    void insertRegisteredCredential(UserIdentity userIdentity, RegisteredCredential registeredCredential);
}
