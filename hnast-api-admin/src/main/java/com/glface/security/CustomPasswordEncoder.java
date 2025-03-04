package com.glface.security;

import com.glface.base.utils.Encodes;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * spring security 密码验证类
 * @author maowei
 */
public class CustomPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        return Encodes.md5(charSequence.toString(),null);
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(Encodes.md5(charSequence.toString(),null));
    }
}
