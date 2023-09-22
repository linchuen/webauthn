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
    private final Map<String, ByteArray> userNameHandleMap = new HashMap<>();

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        Set<PublicKeyCredentialDescriptor> result = new HashSet<>();
        Optional<ByteArray> userHandle = getUserHandleForUsername(username);
        if (userHandle.isPresent()) {
            RegisteredCredential credential = userHandleCredentialMap.get(userHandle.get());
            PublicKeyCredentialDescriptor descriptor = PublicKeyCredentialDescriptor.builder()
                    .id(credential.getCredentialId())
                    .build();
            result.add(descriptor);
        }
        return result;
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return Optional.ofNullable(userNameHandleMap.get(username));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        for (Map.Entry<String, ByteArray> entry : userNameHandleMap.entrySet()) {
            ByteArray userId = entry.getValue();
            if (userId.equals(userHandle)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        RegisteredCredential registeredCredential = userHandleCredentialMap.get(userHandle);
        return credentialId.equals(registeredCredential.getCredentialId())
                ? Optional.of(registeredCredential)
                : Optional.empty();
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        Set<RegisteredCredential> result = new HashSet<>();
        for (Map.Entry<ByteArray, RegisteredCredential> entry : userHandleCredentialMap.entrySet()) {
            RegisteredCredential registeredCredential = entry.getValue();

            if (credentialId.equals(registeredCredential.getCredentialId())) {
                result.add(registeredCredential);
            }
        }
        return result;
    }

    @Override
    public void insertRegisteredCredential(UserIdentity userIdentity, RegisteredCredential registeredCredential) {
        userHandleCredentialMap.put(userIdentity.getId(), registeredCredential);
        userNameHandleMap.put(userIdentity.getName(), userIdentity.getId());
    }
}
