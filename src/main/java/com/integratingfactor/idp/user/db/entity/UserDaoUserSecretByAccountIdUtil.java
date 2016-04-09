package com.integratingfactor.idp.user.db.entity;

import com.integratingfactor.idp.common.db.util.IdpDaoEntity;
import com.integratingfactor.idp.common.db.util.IdpDaoId;
import com.integratingfactor.idp.common.db.util.IdpDaoKey;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;

public class UserDaoUserSecretByAccountIdUtil {
    
    @IdpDaoEntity
    public static class UserDaoUserSecretByAccountId {
        @IdpDaoId
        String accountId;

        IdpUserSecret secret;

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public IdpUserSecret getSecret() {
            return secret;
        }

        public void setSecret(IdpUserSecret secret) {
            this.secret = secret;
        }
    }

    public static IdpDaoKey<UserDaoUserSecretByAccountId> toPk(String accountId) {
        return IdpDaoKey.create(accountId, UserDaoUserSecretByAccountId.class);
    }

    public static UserDaoUserSecretByAccountId toEntity(IdpUserSecret secret) {
        UserDaoUserSecretByAccountId entity = new UserDaoUserSecretByAccountId();
        entity.accountId = secret.getAccountId();
        entity.secret = secret;
        return entity;
    }

    public static IdpUserSecret toModel(UserDaoUserSecretByAccountId entity) {
        return entity.secret;
    }

}
