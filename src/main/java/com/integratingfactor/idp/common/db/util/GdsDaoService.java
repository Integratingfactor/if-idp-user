package com.integratingfactor.idp.common.db.util;

import java.io.Serializable;
import java.lang.reflect.Field;
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

public class GdsDaoService implements InitializingBean {

    private static Logger LOG = Logger.getLogger(GdsDaoService.class.getName());

    @Autowired
    private Environment env;

    String serviceNameSpace = null;

    static final String GdsDaoNameSpaceEnvKey = "idp.service.db.keyspace.name";

    ConcurrentHashMap<String, KeyFactory> factory = new ConcurrentHashMap<String, KeyFactory>();

    private Datastore datastore;

    synchronized protected Datastore gds() {
        if (datastore == null) {
            datastore = DatastoreOptions.builder().namespace(serviceNameSpace).build().service();
        }
        return datastore;
    }

    protected GdsDaoService() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serviceNameSpace = env.getProperty(GdsDaoNameSpaceEnvKey);
        assert (serviceNameSpace != null);
    }

    // ThreadLocal<GdsDaoService> instances = new ThreadLocal<GdsDaoService>();

    // public static GdsDaoService instance() {
    // GdsDaoService util = instances.get();
    // if (util == null) {
    // LOG.info("Creating new instance of IdpGdsUtil for thread: " +
    // Thread.currentThread());
    // util = new GdsDaoService();
    // instances.set(util);
    // }
    // return util;
    // }

    public <T> void registerDaoEntity(Class<T> type) throws DbException {
        // walk through the fields of the entity type to make sure we have
        // correct annotations
        int idCount = 0;
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(IdpDaoParent.class)) {
                // this type has an ancestor, and register key(s) for them
                registerDaoEntity(field.getType());
            } else if (field.isAnnotationPresent(IdpDaoId.class)) {
                idCount++;
            }
            // make sure that field is supported datastore type
            // if (Integer.class.isAssignableFrom(field.getType()) ||
            // String.class.isAssignableFrom(field.getType())
            // || Serializable.class.isAssignableFrom(field.getType())) {
            // // NO OP
            // } else {
            // LOG.warning("Unsupported entity field " + field.getName() + " of
            // type: " + field.getType());
            // throw new GenericDbException("unsupported field type: " +
            // field.getType());
            // }
            if (!Serializable.class.isAssignableFrom(field.getType())) {
                LOG.warning("Unsupported entity field " + field.getName() + " of type: " + field.getType());
                throw new GenericDbException("unsupported field type: " + field.getType());
            }

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
            Map<String, BlobValue> props = new HashMap<String, BlobValue>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(IdpDaoId.class)) {
                    key = (String) field.get(entity);
                } else {
                    props.put(field.getName(),
                            BlobValue.builder(Blob.copyFrom(SerializationUtils.serialize(field.get(entity))))
                                    .excludeFromIndexes(true).build());
                }
            }
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
