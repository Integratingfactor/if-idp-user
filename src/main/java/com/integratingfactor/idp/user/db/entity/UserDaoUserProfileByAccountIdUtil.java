package com.integratingfactor.idp.user.db.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.integratingfactor.idp.common.db.util.IdpDaoEntity;
import com.integratingfactor.idp.common.db.util.IdpDaoId;
import com.integratingfactor.idp.common.db.util.IdpDaoKey;
import com.integratingfactor.idp.common.db.util.IdpDaoParent;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.core.model.IdpUserProfileFields;

public class UserDaoUserProfileByAccountIdUtil {

    @IdpDaoEntity
    public static class UserDaoUserProfileByAccountIdPk {
        @IdpDaoId
        String accountId;

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }
    }
    
    @IdpDaoEntity
    public static class UserDaoUserProfileByAccountId {
        @IdpDaoParent
        IdpDaoKey<UserDaoUserProfileByAccountIdPk> pk;

        @IdpDaoId
        String field;

        String value;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public IdpDaoKey<UserDaoUserProfileByAccountIdPk> getPk() {
            return pk;
        }

        public void setPk(IdpDaoKey<UserDaoUserProfileByAccountIdPk> pk) {
            this.pk = pk;
        }

    }

    public static IdpDaoKey<UserDaoUserProfileByAccountIdPk> toPk(String accountId) {
        return IdpDaoKey.create(accountId, UserDaoUserProfileByAccountIdPk.class);
    }

    public static IdpDaoKey<UserDaoUserProfileByAccountId> toKey(String accountId) {
        return IdpDaoKey.create(accountId, UserDaoUserProfileByAccountId.class);
    }

    public static List<UserDaoUserProfileByAccountId> toEntities(String accountId, IdpUserProfile profile) {
        List<UserDaoUserProfileByAccountId> entities = new ArrayList<UserDaoUserProfileByAccountId>();
        for (Entry<IdpUserProfileFields, String> kv : profile.entrySet()) {
            entities.add(toEntity(accountId, kv.getKey(), kv.getValue()));
        }
        return entities;
    }

    public static UserDaoUserProfileByAccountId toEntity(String accountId, IdpUserProfileFields field, String value) {
        UserDaoUserProfileByAccountId entity = new UserDaoUserProfileByAccountId();
        entity.pk = toPk(accountId);
        entity.field = field.getValue();
        entity.value = value;
        return entity;
    }

    public static IdpUserProfile toModel(List<UserDaoUserProfileByAccountId> entities) {
        IdpUserProfile profile = new IdpUserProfile();
        for (UserDaoUserProfileByAccountId entity : entities) {
            profile.put(IdpUserProfileFields.as(entity.field), entity.value);
        }
        return profile;
    }
}
