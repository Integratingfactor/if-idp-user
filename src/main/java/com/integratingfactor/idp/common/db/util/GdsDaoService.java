package com.integratingfactor.idp.common.db.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
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
import com.google.gcloud.datastore.Key;
import com.google.gcloud.datastore.KeyFactory;
import com.google.gcloud.datastore.PathElement;
import com.google.gcloud.datastore.Query;
import com.google.gcloud.datastore.QueryResults;
import com.google.gcloud.datastore.StructuredQuery.PropertyFilter;
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
            // register getter method for this field
            getters.put(field, findMethod(type, field, "get"));
            // register getter method for this field
            setters.put(field, findMethod(type, field, "set"));

            if (field.isAnnotationPresent(IdpDaoParent.class)) {
                // this type has an ancestor, register key(s) for them
                // registerDaoEntity(field.getType());

                // TODO actually we need to register each parent class
                // explicitly in this approach, or
                // change to use reference to parent entity directly, instead of
                // parent's key
                // but in that case we may end up creating the parent entity in
                // GDS, which may not be desirable
                // do lets just use current approach, viz., to register each
                // parent entity class explicitly and in here just skip any
                // registration for parents
                continue;
            } else if (field.isAnnotationPresent(IdpDaoId.class)) {
                idCount++;
            }
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
        // TODO need to add ancestor, most likely during saving the entity
        // (because needs name/id of parent, which is only available during
        // saving), so may have to add some indication here about using ancestor
        factory.put(type.getSimpleName(), gds().newKeyFactory().kind(type.getSimpleName()));
        // register entity's methods
        this.getters.put(type.getSimpleName(), getters);
        this.setters.put(type.getSimpleName(), setters);
        // register class itself
        classes.put(type.getSimpleName(), type);
        fields.put(type.getSimpleName(), type.getDeclaredFields());
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
        throw new GenericDbException("did not find " + action + " method for property " + field.getName()
                + " in entity type " + type.getSimpleName());
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

    public <T> List<Object> readByAncestorKey(IdpDaoKey key, Class<T> type) throws DbException {
        List<Object> entities = new ArrayList<Object>();
        KeyFactory keyF = factory.get(key.type);
        if (keyF == null) {
            LOG.warning("attempt to read an unregistered entity: " + key.type);
            throw new GenericDbException("entity not registered");
        }
        Query<Entity> query = Query.entityQueryBuilder().kind(type.getSimpleName())
                // .filter(PropertyFilter.hasAncestor(keyF.newKey(key.key)))
                .filter(PropertyFilter.hasAncestor(keyF.newKey(key.key)))
                .build();
        QueryResults<Entity> result = gds().run(query);
        while (result.hasNext()) {
            entities.add(getFromEntity(result.next(), type.getSimpleName()));
        }
        return entities;
    }

    public <T> T readByEntityKey(IdpDaoKey<T> key) throws DbException {
        KeyFactory keyF = factory.get(key.type);
        if (keyF == null) {
            LOG.warning("attempt to read an unregistered entity: " + key.type);
            throw new GenericDbException("entity not registered");
        }
        return (T) getFromEntity(gds().get(keyF.newKey(key.key)), key.type);
        // // now construct the class instance from entity field by field
        // try {
        // Object instance = classes.get(key.type).newInstance();
        // for (Field field : fields.get(key.type)) {
        // if (entity.contains(field.getName())) {
        // Object value =
        // SerializationUtils.deserialize(entity.getBlob(field.getName()).toByteArray());
        // // field.set(instance, value);
        // setters.get(key.type).get(field).invoke(instance, value);
        // }
        // }
        // return (T) instance;
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // throw new GenericDbException("failed to read entity");
        // }
    }

    // private <T> Object getFromEntity(Entity entity, IdpDaoKey<T> key) throws
    // DbException {
    // if (entity == null) {
    // throw new NotFoundDbException(key.key + " not found");
    // }
    // // now construct the class instance from entity field by field
    // try {
    // Object instance = classes.get(key.type).newInstance();
    // for (Field field : fields.get(key.type)) {
    // if (entity.contains(field.getName())) {
    // Object value =
    // SerializationUtils.deserialize(entity.getBlob(field.getName()).toByteArray());
    // // field.set(instance, value);
    // setters.get(key.type).get(field).invoke(instance, value);
    // }
    // }
    // return instance;
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // throw new GenericDbException("failed to read entity");
    // }
    // }

    private <T> Object getFromEntity(Entity entity, String type) throws DbException {
        if (entity == null) {
            throw new NotFoundDbException("not found");
        }
        // now construct the class instance from entity field by field
        try {
            Object instance = classes.get(type).newInstance();
            for (Field field : fields.get(type)) {
                if (field.isAnnotationPresent(IdpDaoParent.class)) {
                    continue;
                }
                Object value = null;
                if (field.isAnnotationPresent(IdpDaoId.class)) {
                    // id fields get copied as is
                    value = entity.key().nameOrId();
                } else if (entity.contains(field.getName())) {
                    // non id field is saved/retrieved as blob
                    value = SerializationUtils.deserialize(entity.getBlob(field.getName()).toByteArray());
                }
                // field.set(instance, value);
                setters.get(type).get(field).invoke(instance, value);
            }
            return instance;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new GenericDbException("failed to read entity");
        }
    }

    private <T> void addAncestory(Deque<PathElement> ancestors, T entity, Field field)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method getter = getters.get(entity.getClass().getSimpleName()).get(field);
        IdpDaoKey pk = (IdpDaoKey) getter.invoke(entity);
        ancestors.push(PathElement.of(pk.type, pk.key));
        Class<? extends Object> pkType = classes.get(pk.type);
        for (Field pkField : pkType.getDeclaredFields()) {
            if (pkField.isAnnotationPresent(IdpDaoParent.class)) {
                // keep adding to ancestor path
                addAncestory(ancestors, pk, pkField);
            }
        }
    }

    // TODO need to add ancestor, most likely during saving the entity
    // (because needs name/id of parent, which is only available during
    // saving)
    public <T> void save(T entity) throws DbException {
        KeyFactory keyF = factory.get(entity.getClass().getSimpleName());
        if (keyF == null) {
            LOG.warning("attempt to save an unregistered entity: " + entity.getClass());
            throw new GenericDbException("entity not registered");
        }
        try {
            // process fields of the entity to create save query
            String key = null;
            // Class<? extends Object> type =
            // classes.get(entity.getClass().getSimpleName());
            Map<String, BlobValue> props = new HashMap<String, BlobValue>();
            Deque<PathElement> ancestors = new ArrayDeque<PathElement>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                Method getter = getters.get(entity.getClass().getSimpleName()).get(field);
                if (field.isAnnotationPresent(IdpDaoId.class)) {
                    // take note of @IdpDaoId annotated key name for the entity
                    key = (String) getter.invoke(entity);
                } else if (field.isAnnotationPresent(IdpDaoParent.class)) {
                    // build ancestor path for the entity
                    addAncestory(ancestors, entity, field);
                } else {
                    props.put(field.getName(),
                            BlobValue.builder(Blob.copyFrom(SerializationUtils.serialize(getter.invoke(entity))))
                                    .excludeFromIndexes(true).build());
                }
            }
            // build a new key with ancestory
            Key entityKey = null;
            if (!ancestors.isEmpty()) {
                entityKey = gds().newKeyFactory().ancestors(ancestors).kind(entity.getClass().getSimpleName())
                        .newKey(key);
            } else {
                entityKey = keyF.newKey(key);
            }
            // build a GDS save query from key and properties of the entity
            Builder builder = Entity.builder(entityKey);
            for (Entry<String, BlobValue> kv : props.entrySet()) {
                builder.set(kv.getKey(), kv.getValue());
            }
            gds().put(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenericDbException(e.getMessage());
        }
    }

    public <T> void save(List<T> entities) throws DbException {
        for (T entity : entities) {
            save(entity);
        }
    }
}
