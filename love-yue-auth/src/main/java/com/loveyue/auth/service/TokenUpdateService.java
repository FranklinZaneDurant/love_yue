package com.loveyue.auth.service;

import com.loveyue.auth.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 令牌更新服务
 *
 * @author LoveYue
 * @since 2025/07/13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenUpdateService {

    private final UserTokenRepository userTokenRepository;

    /**
     * 更新令牌最后使用时间
     *
     * @param accessToken 访问令牌
     */
    @Transactional
    public void updateTokenLastUsedTime(String accessToken) {
        try {
            userTokenRepository.updateLastUsedTime(accessToken, LocalDateTime.now());
            log.debug("更新令牌最后使用时间成功: accessToken={}", accessToken);
        } catch (Exception e) {
            log.error("更新令牌最后使用时间失败: accessToken={}, error={}", accessToken, e.getMessage());
            throw e;
        }
    }
}