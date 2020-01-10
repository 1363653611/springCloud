package com.zbcn.adminclient.common;

import com.zbcn.common.utils.MD5Utils;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MyPasswordEncoder implements PasswordEncoder {
	@Override
	public String encode(CharSequence rawPassword) {
		return MD5Utils.encrypt((String)rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encodedPassword.equals(MD5Utils.encrypt((String)rawPassword));
	}
}
