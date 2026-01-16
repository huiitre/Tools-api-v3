package fr.huiitre.tools.infrastructure.core.user;

import org.springframework.stereotype.Component;

import fr.huiitre.tools.application.core.auth.AuthProvider;
import fr.huiitre.tools.application.core.user.ports.AvatarResolver;
import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.domain.core.user.User;

@Component
public class DefaultAvatarResolver implements AvatarResolver {

    private final UserAuthProviderRepository userAuthProviderRepository;

    public DefaultAvatarResolver(UserAuthProviderRepository userAuthProviderRepository) {
        this.userAuthProviderRepository = userAuthProviderRepository;
    }

    @Override
    public String resolve(User user) {
        return switch (user.getAvatarSource()) {
            case GOOGLE -> resolveGoogleAvatar(user);
            default -> resolveDefaultAvatar();
        };
    }

    private String resolveDefaultAvatar() {
        return null;
    }

    private String resolveGoogleAvatar(User user) {
        return userAuthProviderRepository.findProviderAvatarUrl(user.getId(), AuthProvider.GOOGLE).orElse(null);
    }
}
