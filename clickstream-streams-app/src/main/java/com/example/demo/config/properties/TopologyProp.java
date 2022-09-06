package com.example.demo.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@Getter
@ToString
@RequiredArgsConstructor
public class TopologyProp {
    @Pattern(regexp = "(true|false)")
    private final String enabled;
    @NotBlank
    private final String applicationId;
    @NotBlank
    private final String clientId;
    private final String storeName;
}
