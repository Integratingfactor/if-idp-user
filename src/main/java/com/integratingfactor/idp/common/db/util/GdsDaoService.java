package com.integratingfactor.idp.common.db.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.SerializationUtils;

import com.google.gcloud.datastore.Blob;
import com.google.gcloud.datastore.BlobValue;
import com.google.gcloud.datastore.Datastore;
import com.google.gcloud.datastore.DatastoreOptions;
import com.google.gcloud.datastore.Entity;
import com.google.gcloud.datastore.Entity.Builder;
import com.google.gcloud.datastore.KeyFactory;
import com.integratingfactor.idp.common.exceptions.db.DbException;
import com.integratingfactor.idp.common.exceptions.db.GenericDbException;
import com.integratingfactor.idp.common.exceptions.db.NotFoundDbException;

public class GdsDaoService implements InitializingBean {

    private static Logger LOG = Logger.getLogger(GdsDaoService.class.getName());

    @Autowired
    private Environment env;

    String serviceNameSpace = null;

    static final String GdsDaoNameSpaceEnvKey = "idp.service.db.keyspace.name";

    ConcurrentHashMap<String, KeyFactory> factory = new ConcurrentHashMap<String, KeyFactory>();

    ConcurrentHashMap<String, Field[]> fields = new ConcurrentHashMap<String, Field[]>();

    ConcurrentHashMap<String, Map<Field, Method>> getters = new ConcurrentHashMap<String, Map<Field, Method>>();

    ConcurrentHashMap<String, Map<Field, Method>> setters = new ConcurrentHashMap<String, Map<Field, Method>>();

    ConcurrentHashMap<String, Class<? extends Object>> classes = new ConcurrentHashMap<String, Class<? extends Object>>();

    private Datastore datastore;

    synchronized protected Datastore gds() {
        if (datastore == null) {
            datastore = DatastoreOptions.builder().namespace(serviceNameSpace).build().service();
        }
        return datastore;
    }

    // protected GdsDaoService() {
    //
    // }

    @Override
    public void afterPropertiesSet() throws Exception {
        serviceNameSpace = env.getProperty(GdsDaoNameSpaceEnvKey);
        assert (serviceNameSpace != null);
    }

    public <T> void registerDaoEntity(Class<T> type) throws DbException {
        // walk through the fields of the entity type to make sure we have
        // correct annotations
        int idCount = 0;
        Map<Field, Method> getters = new HashMap<Field, Method>();
        Map<Field, Method> setters = new HashMap<Field, Method>();
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(IdpDaoParent.class)) {
                // this type has an ancestor, and register key(s) for them
                registerDaoEntity(field.getType());
            } else if (field.isAnnotationPresent(IdpDaoId.class)) {
                idCount++;
            }
            if (!Serializable.class.isAssignableFrom(field.getType())) {
                LOG.warning("Unsupported entity field " + field.getName() + " of type: " + field.getType());
                throw new GenericDbException("unsupported field type: " + field.getType());
            }
            // register getter method for this field
            getters.put(field, findMethod(type, field, "get"));
            // register getter method for this field
            setters.put(field, findMethod(type, field, "set"));
        }
        // there should be exactly one ID
        if (idCount != 1) {
            LOG.warning("incorrect number of ID count: " + idCount);
            throw new GenericDbException("incorrect number of ID");
        }

        // everything is good, register this type
        LOG.info("Registering entity type " + type.getName());
        // register a key with this type
        factory.put(type.getName(), gds().newKeyFactory().kind(type.getName()));
        // register entity's methods
        this.getters.put(type.getName(), getters);
        this.setters.put(type.getName(), setters);
        // register class itself
        classes.put(type.getName(), type);
        fields.put(type.getName(), type.getDeclaredFields());

    }

    private <T> Method findMethod(Class<T> type, Field field, String action) throws DbException {
        for (Method method : type.getMethods()) {
            if ((method.getName().startsWith(action))
                    && (method.getName().length() == (field.getName().length() + 3))) {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                    return method;
                }
            }
        }
        throw new GenericDbException("did not find " + action + " method");
    }

    public <T> void delete(IdpDaoKey<T> key) throws DbException {
        KeyFactory keyF = factory.get(key.type);
        if (keyF == null) {
            LOG.warning("attempt to read an unregistered entity: " + key.type);
            throw new GenericDbException("entity not registered");
        }
        try {
            gds().delete(keyF.newKey(key.key));
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenericDbException("failed to delete");
        }
    }

    public <T> T read(IdpDaoKey<T> key) throws DbException {
        KeyFactory keyF = factory.get(key.type);
        if (keyF == null) {
            LOG.warning("attempt to read an unregistered entity: " + key.type);
            throw new GenericDbException("entity not registered");
        }
        Entity entity = gds().get(keyF.newKey(key.key));
        if (entity == null) {
            throw new NotFoundDbException(key.key + " not found");
        }
        // now construct the class instance from entity field by field
        try {
            Object instance = classes.get(key.type).newInstance();
            for (Field field : fields.get(key.type)) {
                if (entity.contains(field.getName())) {
                    Object value = SerializationUtils.deserialize(entity.getBlob(field.getName()).toByteArray());
                    // field.set(instance, value);
                    setters.get(key.type).get(field).invoke(instance, value);
                }
            }
            return (T) instance;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new GenericDbException("failed to read entity");
        }
    }

    public <T> void save(T entity) throws DbException {
        KeyFactory keyF = factory.get(entity.getClass().getName());
        if (keyF == null) {
            LOG.warning("attempt to save an unregistered entity: " + entity.getClass());
            throw new GenericDbException("entity not registered");
        }
        try {
            // process fields of the entity to create save query
            String key = null;
            Class<? extends Object> type = classes.get(entity.getClass().getName());
            Map<String, BlobValue> props = new HashMap<String, BlobValue>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                Method getter = getters.get(entity.getClass().getName()).get(field);
                if (field.isAnnotationPresent(IdpDaoId.class)) {
                    key = (String) getter.invoke(entity);
                } else {
                    props.put(field.getName(),
                            BlobValue.builder(Blob.copyFrom(SerializationUtils.serialize(getter.invoke(entity))))
                                    .excludeFromIndexes(true).build());
                }
            }
            // build a GDS save query from key and properties of the entity
            Builder builder = Entity.builder(keyF.newKey(key));
            for (Entry<String, BlobValue> kv : props.entrySet()) {
                builder.set(kv.getKey(), kv.getValue());
            }
            gds().put(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenericDbException(e.getMessage());
        }
    }
}
