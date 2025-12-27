package fr.huiitre.tools.api.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.application.test.usecase.TestRightUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Core - Test")
@RestController
@RequestMapping("/test")
public class TestController {

    private final TestRightUseCase testRightUseCase;

    public TestController(TestRightUseCase testRightUseCase) {
        this.testRightUseCase = testRightUseCase;
    }

    @GetMapping("/protected")
    public String protectedEndpoint() {

        testRightUseCase.execute();

        return "You have access to this protected endpoint.";
    }
}
