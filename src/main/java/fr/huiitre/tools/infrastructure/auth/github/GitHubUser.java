package fr.huiitre.tools.infrastructure.auth.github;

public record GitHubUser(
    String providerUserId,
    String email,
    String name
) {}