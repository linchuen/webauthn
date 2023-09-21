package com.cooba.webauthn.Service;

import com.cooba.webauthn.Interface.CredentialService;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.UserIdentity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class CredentialManager implements CredentialService {
    private final Map<ByteArray, RegisteredCredential> userHandleCredentialMap = new HashMap<>();
    private final Map<ByteArray, String> userHandleNameMap = new HashMap<>();

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        return new HashSet<>();
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        return Optional.ofNullable(userHandleNameMap.get(userHandle));
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        RegisteredCredential registeredCredential = userHandleCredentialMap.get(userHandle);
        return Optional.ofNullable(registeredCredential);
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return new HashSet<>();
    }

    @Override
    public void insertRegisteredCredential(RegisteredCredential registeredCredential) {
        userHandleCredentialMap.put(registeredCredential.getUserHandle(), registeredCredential);
    }

    public void bindUserHandleWithName(UserIdentity userIdentity) {
        userHandleNameMap.put(userIdentity.getId(), userIdentity.getName());
    }
}
