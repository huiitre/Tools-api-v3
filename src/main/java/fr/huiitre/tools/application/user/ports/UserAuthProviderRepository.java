package fr.huiitre.tools.application.user.ports;

import java.util.Optional;

import fr.huiitre.tools.application.auth.Authprovider;

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
