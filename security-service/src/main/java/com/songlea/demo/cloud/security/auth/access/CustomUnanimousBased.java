package com.songlea.demo.cloud.security.auth.access;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;

/**
 * 自定义的决策器
 *
 * @author Song Lea
 * @see org.springframework.security.access.vote.UnanimousBased
 * @since 2018-10-09 新建
 */
public class CustomUnanimousBased extends AbstractAccessDecisionManager {

    public CustomUnanimousBased(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        int grant = 0;
        if (configAttributes != null) {
            for (AccessDecisionVoter voter : getDecisionVoters()) {
                int result = voter.vote(authentication, object, configAttributes);

                if (logger.isDebugEnabled()) {
                    logger.debug("Voter: " + voter + ", returned: " + result);
                }

                switch (result) {
                    case AccessDecisionVoter.ACCESS_GRANTED:
                        grant++;
                        break;
                    case AccessDecisionVoter.ACCESS_DENIED:
                        throw new AccessDeniedException(messages.getMessage(
                                "AbstractAccessDecisionManager.accessDenied",
                                "Access is denied"));
                    default:
                        break;
                }
            }
        }
        // To get this far, there were no deny votes
        if (grant > 0) {
            return;
        }
        // To get this far, every AccessDecisionVoter abstained
        checkAllowIfAllAbstainDecisions();
    }
}
