package com.example.demo.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@Getter
@ToString
@RequiredArgsConstructor
public class TopicProp {
    @NotBlank
    private final String topic;
}
