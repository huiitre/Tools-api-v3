package fr.huiitre.tools.application.core.user.ports;

import fr.huiitre.tools.application.core.auth.Authprovider;

public interface UserAuthProviderRepository {

    boolean existsByProviderAndProviderUserId(
        Authprovider provider,
        String providerUserId
    );

    boolean existsByUserIdAndProvider(
        Long userId,
        Authprovider provider
    );

    void save(
        Long userId,
        Authprovider provider,
        String providerUserId,
        String providerEmail
    );
}
