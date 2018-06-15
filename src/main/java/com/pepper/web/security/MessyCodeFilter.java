package com.pepper.web.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 过滤请求中的非法字符
 */
public class MessyCodeFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(MessyCodeFilter.class);

    private static String sql = ".~`·!！-@#$￥%……“”？《》^+*&\\/?|:：[]【】‘’。{}（）()';；,， =——_\"";

    private static Pattern patternBlank = Pattern.compile("\n|\t|\r");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(new ParameterRequestWrapper(request), response);
    }

    private static class ParameterRequestWrapper extends HttpServletRequestWrapper {
        public ParameterRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        // 重写getParameter，过滤非法字符
        public String getParameter(String name) {
            return filterMessyCode(super.getParameter(name));
        }

        @Override
        // getParameterValues，过滤非法字符
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return values;
            }

            for (int i = 0; i < values.length; i++) {
                values[i] = filterMessyCode(values[i]);
            }
            return values;
        }

        private static String filterMessyCode(String value) {
            if (StringUtils.isBlank(value)) {
                return value;
            }

            Matcher m = patternBlank.matcher(value);
            String after = m.replaceAll("");
            char[] ch = after.trim().toCharArray();
            for (int i = 0; i < ch.length; i++) {
                char c = ch[i];
                if (!Character.isLetterOrDigit(c)) {
                    if (!isChinese(c)) {
                        String cc = String.valueOf(c);
                        if (sql.indexOf(cc) == -1) {
                            ch[i] = ' ';
                            logger.error("截取乱码,原值: {}>>，乱码{}", value,(int)ch[i]);
                        }
                    }
                }
            }
            return String.valueOf(ch);
        }

        private static boolean isChinese(char c) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                return true;
            }
            return false;
        }
    }
}
