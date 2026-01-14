package fr.huiitre.tools.application.core.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.application.core.user.ports.UserRepository;

@Service
@Transactional
public class RequestPasswordResetUseCase {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final SendPasswordResetUseCase sendPasswordResetUseCase;

    public RequestPasswordResetUseCase(
        UserRepository userRepository,
        UserAuthProviderRepository userAuthProviderRepository,
        SendPasswordResetUseCase sendPasswordResetUseCase
    ) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.sendPasswordResetUseCase = sendPasswordResetUseCase;
    }

    public void execute(String email) {

        userRepository.findByEmail(email)
            .filter(user ->
                userAuthProviderRepository.existsByUserIdAndProvider(
                    user.getId(),
                    AuthProvider.PASSWORD
                )
            )
            .ifPresent(user ->
                sendPasswordResetUseCase.execute(
                    user.getId(),
                    user.getEmail()
                )
            );
    }
}