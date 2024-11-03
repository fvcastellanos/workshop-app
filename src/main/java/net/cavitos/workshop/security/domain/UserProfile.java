package net.cavitos.workshop.security.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UserProfile {

    private String tenant;
    private String userId;
    private String provider;
    private String username;
}
