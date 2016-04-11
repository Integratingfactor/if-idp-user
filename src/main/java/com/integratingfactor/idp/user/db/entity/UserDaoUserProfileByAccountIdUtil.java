package com.integratingfactor.idp.user.db.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.integratingfactor.idp.common.db.gds.Entity;
import com.integratingfactor.idp.common.db.gds.Id;
import com.integratingfactor.idp.common.db.gds.Key;
import com.integratingfactor.idp.common.db.gds.Parent;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.core.model.IdpUserProfileFields;

public class UserDaoUserProfileByAccountIdUtil {

    public static class UserDaoUserProfileByAccountIdPk {
        @Id
        String accountId;

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }
    }
    
    public static class UserDaoUserProfileByAccountId {
        @Parent
        Key<UserDaoUserProfileByAccountIdPk> pk;

        @Id
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

        public Key<UserDaoUserProfileByAccountIdPk> getPk() {
            return pk;
        }

        public void setPk(Key<UserDaoUserProfileByAccountIdPk> pk) {
            this.pk = pk;
        }

    }

    public static Key<UserDaoUserProfileByAccountIdPk> toPk(String accountId) {
        UserDaoUserProfileByAccountIdPk pk = new UserDaoUserProfileByAccountIdPk();
        pk.accountId = accountId;
        return Key.create(pk, UserDaoUserProfileByAccountIdPk.class);
    }

    public static Key<UserDaoUserProfileByAccountId> toKey(String accountId, String field) {
        UserDaoUserProfileByAccountId entity = new UserDaoUserProfileByAccountId();
        entity.field = field;
        entity.pk = toPk(accountId);
        return Key.create(entity, UserDaoUserProfileByAccountId.class);
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

    public static IdpUserProfile toModel(List<Entity<UserDaoUserProfileByAccountId>> entities) {
        IdpUserProfile profile = new IdpUserProfile();
        for (Entity<UserDaoUserProfileByAccountId> entity : entities) {

            profile.put(IdpUserProfileFields.as(entity.getValue().field), entity.getValue().value);
        }
        return profile;
    }
}
