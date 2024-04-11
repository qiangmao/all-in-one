package com.sankuai.ee.mcode.share.service.impl.certificate;

import com.dianping.cat.message.Transaction;
import com.sankuai.ee.mcode.share.domain.Group;
import com.sankuai.ee.mcode.share.domain.PageItems;
import com.sankuai.ee.mcode.share.domain.Pagination;
import com.sankuai.ee.mcode.share.domain.User;
import com.sankuai.ee.mcode.share.domain.certificate.Certificate;
import com.sankuai.ee.mcode.share.domain.certificate.UserCertificate;
import com.sankuai.ee.mcode.share.lion.LionClient;
import com.sankuai.ee.mcode.share.monitor.MonitorUtil;
import com.sankuai.ee.mcode.share.service.api.auth.GroupService;
import com.sankuai.ee.mcode.share.service.api.certificate.CertificateService;
import com.sankuai.ee.mcode.share.service.api.certificate.CertificateTypeScopeService;
import com.sankuai.ee.mcode.share.service.api.certificate.UserCertificateService;
import com.sankuai.ee.mcode.share.service.impl.util.PageUtil;
import com.sankuai.ee.mcode.share.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserCertificateServiceImpl implements UserCertificateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCertificateServiceImpl.class);
    private static final String MonitorType = "UserCertificate";

    private final CertificateService certificateService;
    private final CertificateTypeScopeService certificateTypeScopeService;
    private final GroupService groupService;
    private final LionClient lionClient;

    @Autowired
    public UserCertificateServiceImpl(CertificateService certificateService,
                                      CertificateTypeScopeService certificateTypeScopeService,
                                      GroupService groupService,
                                      LionClient lionClient) {
        this.certificateService = certificateService;
        this.certificateTypeScopeService = certificateTypeScopeService;
        this.groupService = groupService;
        this.lionClient = lionClient;
    }

    @Override
    public Set<Certificate> listUserCertificatesWithScope(User user, boolean scope) {
        Transaction t = MonitorUtil.startMonitor(MonitorType, "listUserCertificates.scope." + scope);
        try {
            Set<Certificate> all = certificateService.listUserCertificates(user);
            Set<Certificate> res = all;
            if (scope) {
                Optional<Group> group = groupService.findGroupForUser(user.getName());
                if (group.isPresent()) {
                    res = all.stream().filter(r -> certificateTypeScopeService.isCertificateTypeIncludeScope(r.getCertificateType(), group.get()))
                            .collect(Collectors.toSet());
                } else {
                    res = Collections.emptySet();
                }
            }
            MonitorUtil.endMonitorWithDefaultDuration(t);
            return res;
        } catch (Exception ex) {
            LogUtil.LogException(LOGGER, LogUtil.InternalUser, "listUserCertificates", "", ex);
            MonitorUtil.endMonitorWithDefaultDuration(t, ex);
            return Collections.emptySet();
        }
    }

    @Override
    public PageItems<UserCertificate> listCertificatedUsersWithScope(String typeName, int minLevel, boolean scope, Pagination pagination) {
        Transaction t = MonitorUtil.startMonitor(MonitorType, "listCertificatedUsers.scope." + scope);
        try {
            PageItems<UserCertificate> res;
            if (scope) {
                // 需要列出与认证人组织匹配的认证数据
                // 用于解决用户转岗后，自动隐藏该用户转岗前组织授予的认证(但是系统不会删除该用户的认证)
                List<UserCertificate> totalInScope = certificateService.listCertificatedUsers(typeName, minLevel, Pagination.ALL_RECORDS)
                        .stream()
                        .filter(r -> certificateTypeScopeService.isCertificateTypeIncludeScope(r.getCertificateType(), r.getUser()))
                        .collect(Collectors.toList());
                res = PageUtil.cutAndConvertToPage(totalInScope, pagination);
            } else {
                res = certificateService.listCertificatedUsers(typeName, minLevel, pagination);
            }
            MonitorUtil.endMonitorWithDefaultDuration(t);
            return res;
        } catch (Exception ex) {
            LogUtil.LogException(LOGGER, LogUtil.InternalUser, "listCertificatedUsers", "", ex);
            MonitorUtil.endMonitorWithDefaultDuration(t, ex);
            return PageItems.emptyPageItems();
        }
    }
}
