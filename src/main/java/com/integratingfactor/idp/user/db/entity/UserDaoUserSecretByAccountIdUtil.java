package com.integratingfactor.idp.user.db.entity;

import com.integratingfactor.idp.common.db.gds.Entity;
import com.integratingfactor.idp.common.db.gds.Id;
import com.integratingfactor.idp.common.db.gds.Key;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;

public class UserDaoUserSecretByAccountIdUtil {
    
    @Entity
    public static class UserDaoUserSecretByAccountId {
        @Id
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

    public static Key<UserDaoUserSecretByAccountId> toKey(String accountId) {
        UserDaoUserSecretByAccountId key = new UserDaoUserSecretByAccountId();
        key.accountId = accountId;
        return Key.create(key, UserDaoUserSecretByAccountId.class);
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
