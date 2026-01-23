package fr.huiitre.tools.modules.core.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.modules.core.auth.api.dto.UserProfileDto;
import fr.huiitre.tools.modules.core.user.application.usecase.GetCurrentUserProfileUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Core - User")
@RestController
@RequestMapping("/user")
public class UserController {

    private final GetCurrentUserProfileUseCase getCurrentUserProfileUseCase;

    public UserController(
            GetCurrentUserProfileUseCase getCurrentUserProfileUseCase) {
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> me() {

        return ResponseEntity.ok(getCurrentUserProfileUseCase.execute());
    }
}
