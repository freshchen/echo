package com.github.freshchen.echo.rpc.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author darcy
 * @since 2022/04/09
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadUtils {

    public static <T> T getOrDefault(T value, T defaultValue) {
        return Objects.nonNull(value) ? value : defaultValue;
    }

    public static String getOrDefault(String value, String defaultValue) {
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }
}
