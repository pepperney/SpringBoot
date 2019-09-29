package com.pepper.web.security;

import com.pepper.common.consts.Code;
import com.pepper.common.consts.Const;
import com.pepper.common.consts.RedisKey;
import com.pepper.common.exception.CustomException;
import com.pepper.common.util.DateUtil;
import com.pepper.common.util.IPUtil;
import com.pepper.common.util.JsonUtil;
import com.pepper.common.util.RandomUtil;
import com.pepper.web.config.ApiConfig;
import com.pepper.web.helper.RedisHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthFilter extends OncePerRequestFilter {

    private static final String key_timestamp = "timestamp";

    private static final String key_token = "token";

    private static final String key_sign = "sign";

    private static final String match_all = "/**";

    protected static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    protected static final AntPathMatcher antMatcher = new AntPathMatcher();

    private Map<String, String> urlAuthMap = new HashMap<>();

    private MappingJackson2JsonView jackson2JsonView;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private ApiConfig apiConfig;


    public AuthFilter(){
        this.jackson2JsonView = new MappingJackson2JsonView();
    }

    @Override
    protected void initFilterBean() {
        if(!urlAuthMap.isEmpty()) return;
        for (String securityPattern : apiConfig.getSecurityChains()) {
            String[] strs = StringUtils.splitPreserveAllTokens(securityPattern, "=");
            String pattern = strs[0];
            String checks = strs[1];
            if (isNotAntPattern(pattern)) {
                pattern = pattern.substring(0, pattern.length() - 3);
            }
            urlAuthMap.put(pattern, checks);
        }
        logger.info("Init security maps : {}", JsonUtil.toJson(urlAuthMap));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (apiConfig.isNeedValidateTimestamp()) {
            validateTimestamp(request);
        }
        String path = getRequestPath(request);
        boolean isMatch = false;
        MDC.clear();
        MDC.put(Const.MDC_KEY,RandomUtil.uuid());
        try {
            for (Map.Entry<String, String> entry : urlAuthMap.entrySet()) {
                String pattern = entry.getKey();
                String checks = entry.getValue();
                if (isNotAntPattern(pattern)) {
                    assert !pattern.contains("*");
                    isMatch = path.startsWith(pattern) && (path.length() == pattern.length() || path.charAt(pattern.length()) == '/');
                } else {
                    isMatch = antMatcher.match(entry.getKey(), path);
                }
                if (isMatch) {
                    if (checks.contains(key_sign)) {
                        validateSign(request);
                    }
                    if (checks.contains(key_token)) {
                        validateToken(request, response);
                    }
                    break;
                }
            }
            filterChain.doFilter(request, response);
        } catch(CustomException e){
            Map<String,String> map = new HashMap<>();
            map.put("code",e.getCode());
            map.put("message",e.getMessage());
            try {
                jackson2JsonView.render(map,request,response);
            } catch (Exception e1) {
                logger.warn("Jackson to json error.", e);
            }
        } catch (Exception e) {
            Map<String,String> map = new HashMap<>();
            map.put("code",Code.SYSTEM_ERROR.getCode());
            map.put("message",Code.SYSTEM_ERROR.getMsg());
            try {
                jackson2JsonView.render(map,request,response);
            } catch (Exception e1) {
                logger.warn("Jackson to json error.", e);
            }
        }

    }


    /**
     * 验证签名
     */
    public void validateSign(HttpServletRequest request) {
        String sign = getKeyFromRequest(request, key_sign);
        if (StringUtils.isBlank(sign)) {
            logger.warn("==> The request {} from {}, token is null.", getRequestPath(request), IPUtil.getClientIP(request));
            throw new CustomException(Code.CHECK_SIGN_FAIL);
        }
        // TODO 根据签名生成的规则验证签名
    }

    /**
     * 验证时间戳
     *
     * @param request
     */
    public void validateTimestamp(HttpServletRequest request) {
        if (this.getEnvironment().getActiveProfiles() != null && this.getEnvironment().getActiveProfiles().length == 1 && "local".equals(this.getEnvironment().getActiveProfiles()[0])) {
            return;
        }
        String timestamp = getKeyFromRequest(request, key_timestamp);
        if (StringUtils.isBlank(timestamp)) {
            logger.warn("==> The request {} from {}, timestamp is null.", getRequestPath(request), IPUtil.getClientIP(request));
            throw new CustomException(Code.CHECK_TIMESTAMP_FAIL);
        }
        long reqTime = DateUtil.strToDate(timestamp, "yyyy-MM-dd HH:mm:SS").getTime();
        //检查timestamp 与系统时间是否相差在合理时间内，如10分钟。
        boolean flag = Math.abs(System.currentTimeMillis() - reqTime) < apiConfig.getTimestampMilliseconds();
        if (!flag) {
            throw new CustomException(Code.CHECK_TIMESTAMP_FAIL);
        }
    }


    /**
     * 验证token
     *
     * @param request
     * @param response
     */

    public void validateToken(HttpServletRequest request, HttpServletResponse response) {
        String token = getKeyFromRequest(request, key_token);
        if (StringUtils.isBlank(token)) {
            logger.warn("==> The request {} from {}, token is null.", getRequestPath(request), IPUtil.getClientIP(request));
            throw new CustomException(Code.TOKEN_ERROR);
        }
        response.addHeader(key_token, token);
        String cacheValue = redisHelper.get(RedisKey.TOKEN.getKey() + token);
        if (StringUtils.isEmpty(cacheValue)) {
            logger.warn("==> The request {} from {}, token is {}.", getRequestPath(request), IPUtil.getClientIP(request), token);
            throw new CustomException(Code.TOKEN_ERROR);
        }
    }
















    /*  =================================================  私有方法  ======================================================= */


    /**
     * 获取请求路径
     *
     * @param request
     * @param key
     * @return
     */
    protected String getKeyFromRequest(HttpServletRequest request, String key) {
        if (request.getParameterMap().containsKey(key)) {
            return request.getParameter(key);
        } else {
            return request.getHeader(key);
        }
    }

    /**
     * 获取请求路径
     *
     * @param request
     * @return
     */
    public static String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();
        if (request.getPathInfo() != null) {
            url += request.getPathInfo();
        }
        return url;
    }


    /**
     * 验证是否ant风格的路径
     *
     * @param pattern
     * @return
     */
    protected boolean isNotAntPattern(String pattern) {
        boolean flag = pattern.endsWith(match_all) && pattern.indexOf('?') == -1 && pattern.indexOf("*") == pattern.length() - 2;
        return flag;
    }


}
