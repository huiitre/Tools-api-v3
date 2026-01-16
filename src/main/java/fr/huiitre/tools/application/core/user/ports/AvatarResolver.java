package fr.huiitre.tools.application.core.user.ports;

import fr.huiitre.tools.domain.core.user.User;

public interface AvatarResolver {
    
    String resolve(User user);
}
