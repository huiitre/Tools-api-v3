package fr.huiitre.tools.application.core.user.ports;

import fr.huiitre.tools.application.core.auth.AuthProvider;

public interface UserAuthProviderRepository {

    boolean existsByProviderAndProviderUserId(
        AuthProvider provider,
        String providerUserId
    );

    boolean existsByUserIdAndProvider(
        Long userId,
        AuthProvider provider
    );

    void save(
        Long userId,
        AuthProvider provider,
        String providerUserId,
        String providerEmail
    );
}
