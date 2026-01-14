package com.equipment.management;

import com.equipment.management.common.utils.JwtUtil;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT Token 属性测试
 * Property 1: JWT Token 验证一致性
 * Property 2: 无效 Token 拒绝访问
 */
class JwtPropertyTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    /**
     * Property 1: 生成的Token应该能被正确验证
     */
    @Property(tries = 100)
    void generatedTokenShouldBeValid(
            @ForAll @LongRange(min = 1, max = 10000) Long userId,
            @ForAll @StringLength(min = 3, max = 20) @AlphaChars String username) {
        
        String token = jwtUtil.generateToken(userId, username);
        
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(userId, jwtUtil.getUserIdFromToken(token));
        assertEquals(username, jwtUtil.getUsernameFromToken(token));
    }

    /**
     * Property 2: 无效Token应该被拒绝
     */
    @Property(tries = 100)
    void invalidTokenShouldBeRejected(
            @ForAll @StringLength(min = 10, max = 100) String randomString) {
        
        assertFalse(jwtUtil.validateToken(randomString));
    }

    /**
     * Property 3: 空Token应该被拒绝
     */
    @Property(tries = 10)
    void emptyTokenShouldBeRejected() {
        assertFalse(jwtUtil.validateToken(null));
        assertFalse(jwtUtil.validateToken(""));
        assertFalse(jwtUtil.validateToken("   "));
    }

    /**
     * Property 4: 篡改的Token应该被拒绝
     */
    @Property(tries = 100)
    void tamperedTokenShouldBeRejected(
            @ForAll @LongRange(min = 1, max = 10000) Long userId,
            @ForAll @StringLength(min = 3, max = 20) @AlphaChars String username) {
        
        String token = jwtUtil.generateToken(userId, username);
        // 篡改token的最后一个字符
        String tamperedToken = token.substring(0, token.length() - 1) + 
                (token.charAt(token.length() - 1) == 'a' ? 'b' : 'a');
        
        assertFalse(jwtUtil.validateToken(tamperedToken));
    }
}
